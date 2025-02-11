package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.ResponseService;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.TaskBazaarLogger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


@WebServlet({"/bid","/accept","/addBidder"})
public class BidServlet extends HttpServlet {

    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final Logger logger = TaskBazaarLogger.getLogger();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String postId = request.getParameter("postId");
        logger.info("working with postId: " + postId);

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            logger.info("user not logged in");
            ResponseService.sendAlertAndRedirect(response,"Please log in and try again!!","index.jsp");
            return;
        }

        String path = request.getServletPath();
        String role = null;
        int bidNumber = 0;

        try {
            logger.info("user role retrieved");
            role = userService.getUserRole(username);
        } catch (SQLException e) {
            logger.info("Error getting user role " + e.getMessage());
        }

        try {
            logger.info("bid number retrieved. " + bidNumber);
            bidNumber = bidService.existBid(username);
        } catch (Exception e) {
            logger.info("Error getting bid number " + e.getMessage());
        }


        if("/bid".equals(path)) {
            if("freelancer".equals(role)) {
                if (bidNumber > 0) {
                    logger.info("try to add previous bid " + username);
                    ResponseService.sendAlertAndRedirect(response, "You already bid this post!!", "/home");
                    return;
                    }

                    try {
                        logger.info("bid added" + username);
                        bidService.placeBid(postId, username);
                    } catch (SQLException e) {
                        logger.info("Error occurred while place bid: " + e.getMessage());
                    }
                    ResponseService.sendAlertAndRedirect(response,"Bid Added successfully!!","/home");
            } else {
                logger.info("try to add new bid " + username);
                ResponseService.sendAlertAndRedirect(response, "You are not authorized to access this page!", "/home");
            }
        }
        else if("/user".equals(path)) {
            try {
                if (!"buyer".equals(role)) {
                    logger.info(username + "try to access unauthorized role");
                    ResponseService.sendAlertAndRedirect(response, "You are not authorized to access this page!", "/home");
                    return;
                }
                logger.info("checking if " + username + " owned the post " + postId + ".....");
                boolean isAuthorized = bidService.isPostOwnedByUser(username, postId);
                if (!isAuthorized) {
                    logger.info(username + "try to access bid of other user");
                    ResponseService.sendAlertAndRedirect(response, "You are not authorized to accept this bid!", "/home");
                    return;
                }
                logger.info("checking if " + username + " not accepted this post : " + postId + ".....");
                int previousBidNumber = bidService.existInAccepted(postId);

                if(previousBidNumber > 0){
                    logger.info(username + "Bid already accepted");
                    ResponseService.sendAlertAndRedirect(response,"Bid Already Added!!","/home");
                    return;
                }
                logger.info("Retrieving all bids for user " + username + " ...");
                List<String> bidders = bidService.getBiddersForPost(postId);
                request.setAttribute("bidders", bidders);
                RequestDispatcher dispatcher = request.getRequestDispatcher("selectBidder.jsp");
                dispatcher.forward(request, response);

            }catch (Exception e) {
               logger.info("Exception occurred while accessing bidservice : " + e.getMessage());
            }
        }
        else if("/addBidder".equals(path)) {

            if(!"buyer".equals(role)){
                logger.info(username + "try to access unauthorized role");
                ResponseService.sendAlertAndRedirect(response,"You are not authorized to access this page!!","/home");
                return;
            }

            String selectedBidder = request.getParameter("selectedBidder");
            boolean isAdded = bidService.addBidderToAccepted(postId, username, selectedBidder);
            if (postId != null && isAdded) {
                logger.info(selectedBidder + " Bidder added for post " + postId);
                ResponseService.sendAlertAndRedirect(response,"Selected bidder added successfully!!","/home");
            } else {
                logger.info("bidder add failed for post " + postId);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add the bidder.");
            }
        }
        else{
            logger.info(username + "try to access unauthorized role");
            ResponseService.sendAlertAndRedirect(response,"You are not authorized to access this page!","/home");
        }

    }
}