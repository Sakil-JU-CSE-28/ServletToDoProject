package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.Response;
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

@WebServlet("/accept")
public class acceptBidServlet extends HttpServlet {
    private final BidService bidService = new BidService();
    private final UserService userService = new UserService();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String postId = req.getParameter("postId");
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        try {
            String role = userService.getUserRole(username);

            if (!"buyer".equals(role)) {
                Response.sendAlertAndRedirect(res, "You are not authorized to access this page!", "/home");
                return;
            }

            if (!bidService.isPostOwnedByUser(username, postId)) {
                Response.sendAlertAndRedirect(res, "You are not authorized to accept this bid!", "/home");
                return;
            }

            System.out.println("Val : " + bidService.existOrder(postId));

            if(bidService.existOrder(postId) > 0){

                Response.sendAlertAndRedirect(res,"Bid Already Added!!","/home");
                return;
            }
            List<String> bidders = bidService.getBiddersForPost(postId);
            req.setAttribute("bidders", bidders);
            RequestDispatcher dispatcher = req.getRequestDispatcher("selectBidder.jsp");
            dispatcher.forward(req, res);

        } catch (SQLException e) {
            throw new RuntimeException("Error processing bid acceptance", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
