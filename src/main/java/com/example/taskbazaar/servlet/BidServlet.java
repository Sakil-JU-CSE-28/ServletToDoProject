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


@WebServlet("/bid")
public class BidServlet extends HttpServlet {
    private final BidService bidService = new BidService();
    private final UserService userService = new UserService();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postId = request.getParameter("postId");
        System.out.println(postId);

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            if (userService.getUserRole(username).equals("freelancer")) {
                if(bidService.existBid(username) > 0){
                    Response.sendAlertAndRedirect(response,"You already bid this post!!","/home");
                    return;
                }
                bidService.placeBid(postId, username);
                response.sendRedirect("/home");
            } else {
                Response.sendAlertAndRedirect(response, "You are not authorized to access this page!", "/home");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing bid", e);
        }
    }
}
