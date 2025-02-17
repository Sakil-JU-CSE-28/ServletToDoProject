package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.AcceptedBidService;
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

@WebServlet("/works")
public class WorkServlet extends HttpServlet {

    private final AcceptedBidService workService = AcceptedBidService.getInstance();
    private final static Logger logger = LoggerFactory.getLogger(WorkServlet.class);

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        try {
            HttpSession session = req.getSession();
            String username = (String) session.getAttribute("username");
            List<String> works = workService.getAllAcceptedBid(username);
            logger.info("{} has {} works", username, works.size());
            req.setAttribute("works", works);
            RequestDispatcher dispatcher = req.getRequestDispatcher("workHistory.jsp");
            dispatcher.forward(req, res);
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            req.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            try {
                req.getRequestDispatcher("error.jsp").forward(req, res);
            } catch (Exception ex) {
                logger.error(Constants.FORWARD_ERROR, ex.getMessage());
            }
        }
    }
}