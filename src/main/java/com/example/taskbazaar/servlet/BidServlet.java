/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.servlet;

import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.PostException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constant;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet({"/bid", "/accept", "/addBidder"})
public class BidServlet extends HttpServlet {

    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();
    private Logger logger = LoggerFactory.getLogger(BidServlet.class);


    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            String postId = request.getParameter("postId");
            logger.info("working with postId: {}", postId);
            HttpSession session = request.getSession(false);
            String username = (session != null) ? (String) session.getAttribute("username") : null;
            String path = request.getServletPath();
            String role = userService.getRole(username);
            boolean isBidExist = bidService.isExist(username, Integer.parseInt(postId));
            logger.info("{} is {} has {} bid", username, role, isBidExist);
            boolean isSuccessful;
            switch (path) {
                case "/bid" -> {
                    if ("freelancer".equals(role)) {
                        if (isBidExist) {
                            logger.info("{} try to add past bid", username);
                            PopUpAlert.sendAlertAndRedirect(response, Constant.ALREADY_ADDED, "/home");
                            return;
                        }
                        isSuccessful = bidService.placeBid(postId, username);
                        if (!isSuccessful) {
                            PopUpAlert.sendAlertAndRedirect(response, Constant.ERROR, "/home");
                            return;
                        }

                        logger.info("{} bid added for {}", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.SUCCESS, "/home");
                    } else {
                        logger.info("{} try to add new bid {}", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                    }
                }
                case "/accept" -> {
                    if (!"buyer".equals(role)) {
                        logger.info("{} try to accept bid {}", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                        return;
                    }
                    logger.info("checking owner of bid {}", postId);
                    isSuccessful = bidService.isPostOwner(username, postId);
                    if (!isSuccessful) {
                        logger.info("{} try to access bid of other user", username);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                        return;
                    }
                    logger.info("checking if {} not accepted this post : {}", username, postId);
                    boolean acceptedStatus = bidService.isAccepted(postId);
                    if (acceptedStatus) {
                        logger.info("{} already accepted", username);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.ALREADY_ADDED, "/home");
                        return;
                    }
                    List<String> bidders = bidService.getBiddersForPost(postId);
                    logger.info("{} of {}", bidders, username);
                    request.setAttribute("bidders", bidders);
                    request.setAttribute("postId", postId);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("selectBidder.jsp");
                    dispatcher.forward(request, response);
                }
                case "/addBidder" -> {

                    if (!"buyer".equals(role)) {
                        logger.info("{} not valid for adding", username);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                        return;
                    }
                    postId = request.getParameter("postId");
                    String selectedBidder = request.getParameter("selectedBidder");
                    if (postId != null) {
                        logger.info("{} Bidder added for post {}", selectedBidder, postId);
                        bidService.addBidder(postId, selectedBidder);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.SUCCESS, "/home");
                    } else {
                        logger.info("bidder add failed for post {}", postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.ERROR, "/home");
                    }
                }
                case null, default -> {
                    logger.info("{} Page not found", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constant.ERROR, "pageNotFound.jsp");
                }
            }
        } catch (AuthenticationException e) {
            logger.error("error in bid authentication:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (ValidationException e) {
            logger.error("error validation in bid:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (BidException e) {
            logger.error("error in bid:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (PostException e) {
            logger.error("error occurred in postDao: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        }
    }

}