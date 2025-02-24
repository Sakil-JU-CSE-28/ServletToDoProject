/*
 * author : Md. Sakil Ahmed
 * Date : 21 feb 2024
 */
package com.example.taskbazaar.servlet;

import com.example.taskbazaar.model.User;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constant;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet({"/admin", "/block", "/unBlock"})
public class AdminServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private final UserService userService = UserService.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        String path = req.getRequestURI();
        try {
            if ("/admin".equals(path)) {
                String username = (String) req.getSession().getAttribute("username");
                logger.info("{} try to access admin page", username);
                String userRole = userService.getUserRole(username);
                logger.info("{} is logged in as {}", username, userRole);
                if ("admin".equals(userRole)) {
                    List<User> users = userService.getUsers();
                    logger.info("Total {} users", users.size());
                    req.setAttribute("users", users);
                    RequestDispatcher dispatcher = req.getRequestDispatcher("admin.jsp");
                    dispatcher.forward(req, res);
                } else {
                    logger.info("{} is not admin", username);
                    PopUpAlert.sendAlertAndRedirect(res, Constant.UNAUTHORIZED, "/home");
                }
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            req.setAttribute("errorMessage", Constant.INTERNAL_ERROR);
            try {
                req.getRequestDispatcher("error.jsp").forward(req, res);
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        String path = req.getRequestURI();
        try {

            String username = (String) req.getSession().getAttribute("username");
            logger.info("{} try to access admin page", username);
            String userRole = userService.getUserRole(username);
            logger.info("{} is logged in as {}", username, userRole);
            if ("admin".equals(userRole)) {
                if ("/block".equals(path)) {
                    String usernameForBlock = req.getParameter("username");
                    boolean isBlocked = userService.blockUser(usernameForBlock);
                    if (isBlocked) {
                        logger.info("{} is blocked", usernameForBlock);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.SUCCESS, "/admin");
                        return;
                    } else {
                        logger.info("{} block failed", usernameForBlock);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.ERROR, "/admin");
                    }
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/admin");
                    dispatcher.forward(req, res);
                } else if ("/unBlock".equals(path)) {
                    String usernameForUnBlock = req.getParameter("username");
                    boolean isUnBlocked = userService.unBlockUser(usernameForUnBlock);
                    if (isUnBlocked) {
                        logger.info("{} is unblocked", usernameForUnBlock);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.SUCCESS, "/admin");
                    } else {
                        logger.info("{} unblock failed", usernameForUnBlock);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.ERROR, "/admin");
                    }
                } else {
                    res.sendRedirect("pageNotFound.jsp");
                }
            } else {
                logger.info("{} is not admin", username);
                PopUpAlert.sendAlertAndRedirect(res, Constant.UNAUTHORIZED, "/home");
            }

        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            req.setAttribute("errorMessage", Constant.INTERNAL_ERROR);
            try {
                req.getRequestDispatcher("error.jsp").forward(req, res);
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        }
    }

}