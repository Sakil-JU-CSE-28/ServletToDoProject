package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.ResponseService;
import com.example.taskbazaar.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet({"/bid","/accept","/addBidder"})
public class BidServlet extends HttpServlet {

    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postId = request.getParameter("postId");
        System.out.println(postId);

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        if(request.getServletPath().equals("/bid")) {
            try {
                if (userService.getUserRole(username).equals("freelancer")) {
                    if (bidService.existBid(username) > 0) {
                        ResponseService.sendAlertAndRedirect(response, "You already bid this post!!", "/home");
                        return;
                    }
                    bidService.placeBid(postId, username);
                    response.sendRedirect("/home");
                } else {
                    ResponseService.sendAlertAndRedirect(response, "You are not authorized to access this page!", "/home");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error processing bid", e);
            }
        }
        else if(request.getServletPath().equals("/user")) {
            try {
                String role = userService.getUserRole(username);

                if (!"buyer".equals(role)) {
                    ResponseService.sendAlertAndRedirect(response, "You are not authorized to access this page!", "/home");
                    return;
                }

                if (!bidService.isPostOwnedByUser(username, postId)) {
                    ResponseService.sendAlertAndRedirect(response, "You are not authorized to accept this bid!", "/home");
                    return;
                }

                System.out.println("Val : " + bidService.existOrder(postId));

                if(bidService.existOrder(postId) > 0){

                    ResponseService.sendAlertAndRedirect(response,"Bid Already Added!!","/home");
                    return;
                }
                List<String> bidders = bidService.getBiddersForPost(postId);
                request.setAttribute("bidders", bidders);
                RequestDispatcher dispatcher = request.getRequestDispatcher("selectBidder.jsp");
                dispatcher.forward(request, response);

            } catch (SQLException e) {
                throw new RuntimeException("Error processing bid acceptance", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(request.getServletPath().equals("/addBidder")) {
            try {
                if(!userService.getUserRole(username).equals("buyer")){
                    ResponseService.sendAlertAndRedirect(response,"You are not authorized to access this page!!","/home");
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String selectedBidder = request.getParameter("selectedBidder");

            if (postId != null && bidService.addBidderToOrder(postId, username, selectedBidder)) {
                ResponseService.sendAlertAndRedirect(response,"Selected bidder added successfully!!","/home");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add the bidder.");
            }
        }

    }


}
