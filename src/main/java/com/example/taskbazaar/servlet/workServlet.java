package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.WorkService;
import com.example.taskbazaar.utility.TaskBazaarLogger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/works")
public class workServlet extends HttpServlet {

    private final WorkService workService = WorkService.getInstance();
    private final static Logger logger = TaskBazaarLogger.getLogger();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        logger.info("username : " + username + " retrieving works......");
        List<String> works = workService.getAllWork(username);
        req.setAttribute("works", works);
        RequestDispatcher dispatcher = req.getRequestDispatcher("workHistory.jsp");
        dispatcher.forward(req, res);
    }

}
