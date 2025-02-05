package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.Response;
import com.example.taskbazaar.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/addBidder")
public class AddBidderServlet extends HttpServlet {

    private final BidService bidService = new BidService();
    private final UserService userService = new UserService();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        if (username == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            if(!userService.getUserRole(username).equals("buyer")){
                Response.sendAlertAndRedirect(response,"You are not authorized to access this page!!","/home");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String selectedBidder = request.getParameter("selectedBidder");
        String postId = bidService.getPostIdByBidder(selectedBidder);

            if (postId != null && bidService.addBidderToOrder(postId, username, selectedBidder)) {
                Response.sendAlertAndRedirect(response,"Selected bidder added successfully!!","/home");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add the bidder.");
            }

    }
}