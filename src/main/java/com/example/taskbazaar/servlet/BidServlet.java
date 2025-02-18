package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.AlertService;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet({"/bid", "/accept", "/addBidder"})
public class BidServlet extends HttpServlet {

    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final Logger logger = LoggerFactory.getLogger(BidServlet.class);


    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            String postId = request.getParameter("postId");
            logger.info("working with postId: {}", postId);

            HttpSession session = request.getSession(false);
            String username = (session != null) ? (String) session.getAttribute("username") : null;
            String path = request.getServletPath();
            String role = userService.getUserRole(username);
            int bidNumber = bidService.existBid(username);
            logger.info("{} is {} has {} bid", username, role, bidNumber);
            boolean isSuccess;
            if ("/bid".equals(path)) {
                if ("freelancer".equals(role)) {
                    if (bidNumber > 0) {
                        logger.info("{} try to add past bid", username);
                        AlertService.sendAlertAndRedirect(response, Constants.ERROR, "/home");
                        return;
                    }
                    bidService.placeBid(postId, username);
                    logger.info("{} bid added for {}", username, postId);
                    AlertService.sendAlertAndRedirect(response, Constants.SUCCESS, "/home");
                } else {
                    logger.info("{} try to add new bid {}", username, postId);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                }
            } else if ("/accept".equals(path)) {
                if (!"buyer".equals(role)) {
                    logger.info("{} try to accept bid {}", username, postId);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                    return;
                }
                logger.info("checking owner of bid {}", postId);
                isSuccess = bidService.isPostOwnedByUser(username, postId);
                if (!isSuccess) {
                    logger.info("{} try to access bid of other user", username);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                    return;
                }
                logger.info("checking if {} not accepted this post : {}", username, postId);
                int previousBidNumber = bidService.existInAccepted(postId);
                if (previousBidNumber > 0) {
                    logger.info("{} already accepted", username);
                    AlertService.sendAlertAndRedirect(response, Constants.ERROR, "/home");
                    return;
                }

                List<String> bidders = bidService.getBiddersForPost(postId);
                logger.info("{} of {}", bidders, username);
                request.setAttribute("bidders", bidders);
                request.setAttribute("postId", postId);
                RequestDispatcher dispatcher = request.getRequestDispatcher("selectBidder.jsp");
                dispatcher.forward(request, response);

            } else if ("/addBidder".equals(path)) {

                if (!"buyer".equals(role)) {
                    logger.info("{} not valid for adding", username);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                    return;
                }
                postId = request.getParameter("postId");
                String selectedBidder = request.getParameter("selectedBidder");
                if (postId != null) {
                    logger.info("{} Bidder added for post {}", selectedBidder, postId);
                    bidService.addBidderToAccepted(postId, username, selectedBidder);
                    AlertService.sendAlertAndRedirect(response, Constants.SUCCESS, "/home");
                } else {
                    logger.info("bidder add failed for post {}", postId);
                    AlertService.sendAlertAndRedirect(response, Constants.ERROR, "/home");
                }
            } else {
                logger.info("{} Page not found", username);
                AlertService.sendAlertAndRedirect(response, Constants.ERROR, "pageNotFound.jsp");
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constants.FORWARD_ERROR, ex.getMessage());
            }
        }
    }
}