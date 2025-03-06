package com.example.taskbazaar.servlet;

import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.enums.Role;
import com.example.taskbazaar.exception.*;
import com.example.taskbazaar.model.Bid;
import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet({"/bid", "/accept", "/addBidder"})
public class BidServlet extends BaseServlet {

    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final PostService postService = PostService.getInstance();
    private final Logger logger = LoggerFactory.getLogger(BidServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            String postId = request.getParameter("postId");
            logger.info("Processing postId: {}", postId);
            HttpSession session = request.getSession(false);
            String username = (session != null) ? (String) session.getAttribute("username") : null;
            String path = request.getServletPath();
            UserDTO userDTO = userService.getByUsername(username);
            String role = userDTO.role();
            Bid bid = bidService.getById(username, Integer.parseInt(postId));
            logger.info("User {} with role {} has bid status: {}", username, role, bid);

            boolean isSuccessful;
            switch (path) {
                case "/bid" -> {
                    if (Role.FREELANCER.getValue().equals(role)) {
                        if (bid != null) {
                            logger.info("User {} attempted to place a duplicate bid for post {}", username, postId);
                            PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.ALREADY_ADDED, "/home");
                            return;
                        }
                        isSuccessful = bidService.placeBid(postId, username);
                        if (!isSuccessful) {
                            logger.error("Error placing bid for post {} by user {}", postId, username);
                            PopUpAlert.sendAlertAndRedirect(response, Constants.Error.ERROR, "/home");
                            return;
                        }
                        logger.info("User {} successfully placed a bid for post {}", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.SUCCESS, "/home");
                    } else {
                        logger.info("User {} attempted to place a bid but is not a freelancer", username);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                    }
                }
                case "/accept" -> {
                    if (!Role.BUYER.getValue().equals(role)) {
                        logger.info("User {} with role {} tried to accept a bid, but is not a buyer", username, role);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                        return;
                    }
                    logger.info("Checking if user {} is the owner of post {}", username, postId);
                    PostDTO postDTO = postService.getById(Integer.parseInt(postId));
                    if (!postDTO.author().equals(username)) {
                        logger.info("User {} attempted to access a bid for post {} that they do not own", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                        return;
                    }

                    if (bid.isAccepted()) {
                        logger.info("User {} has already accepted the bid for post {}", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.ALREADY_ADDED, "/home");
                        return;
                    }

                    List<String> bidders = bidService.getAllByPostId(postId);
                    logger.info("User {} is reviewing bidders for post {}", username, postId);
                    request.setAttribute("bidders", bidders);
                    request.setAttribute("postId", postId);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("selectBidder.jsp");
                    dispatcher.forward(request, response);

                }
                case "/addBidder" -> {
                    if (!Role.BUYER.getValue().equals(role)) {
                        logger.info("User {} with role {} attempted to add a bidder, but is not a buyer", username, role);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                        return;
                    }
                    postId = request.getParameter("postId");
                    String selectedBidder = request.getParameter("selectedBidder");
                    if (postId != null) {
                        logger.info("Adding bidder {} to post {}", selectedBidder, postId);
                        bidService.addBidder(postId, selectedBidder);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.SUCCESS, "/home");
                    } else {
                        logger.error("Failed to add bidder for post {}", postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Error.ERROR, "/home");
                    }
                }
                case null, default -> {
                    logger.warn("User {} accessed an invalid page or endpoint", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constants.Error.ERROR, "pageNotFound.jsp");
                }
            }
        } catch (BidException | PostException | DbException e) {
            logger.error("error:: {}", e.getMessage(), e);
            handleError(response, e.getMessage(), request);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            forwardToErrorPage(request, response);
        }
    }
}