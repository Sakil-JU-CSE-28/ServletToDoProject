/*
 * author : Md. Sakil Ahmed
 * Date : 21 feb 2024
 */
package com.example.taskbazaar.servlet;

import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.enums.Role;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constant;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet({"/admin", "/block", "/unBlock"})
public class AdminServlet extends BaseServlet {

    private final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private final UserService userService = UserService.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        String path = req.getRequestURI();
        try {
            if ("/admin".equals(path)) {
                String username = (String) req.getSession().getAttribute("username");
                logger.info("{} try to retrieve admin page", username);
                UserDTO user = userService.getUser(username);
                logger.info("{} is logged in as {} and retrieve admin page", username, user.role());
                if (Role.ADMIN.getValue().equals(user.role())) {
                    List<UserDTO> users = userService.getUsers();
                    logger.info("Total {} users found for admin dashboard", users.size());
                    req.setAttribute("users", users);
                    RequestDispatcher dispatcher = req.getRequestDispatcher("admin.jsp");
                    dispatcher.forward(req, res);
                } else {
                    logger.warn("{} is not admin but try to retrieve admin dashboard", username);
                    PopUpAlert.sendAlertAndRedirect(res, Constant.UNAUTHORIZED, "/home");
                }
            }
        } catch (DbException e) {
            logger.error("database error in retrieving users:: {}", e.getMessage(), e);
            handleError(res, e.getMessage(), req);
        } catch (Exception e) {
            logger.error("error to access admin: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", Constant.Error.INTERNAL_ERROR);
            forwardToErrorPage(req, res);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        String path = req.getRequestURI();
        try {
            String username = (String) req.getSession().getAttribute("username");
            logger.info("{} try to access admin page to change dashboard info", username);
            UserDTO user = userService.getUser(username);
            logger.info("{} is logged in as {}", username, user.role());
            if (Role.ADMIN.getValue().equals(user.role())) {
                if ("/block".equals(path)) {
                    String candidate = req.getParameter("username");
                    boolean isBlocked = userService.blockUser(candidate);
                    if (isBlocked) {
                        logger.warn("{} is blocked", candidate);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.SUCCESS, "/admin");
                        return;
                    } else {
                        logger.error("{} block failed", candidate);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.Error.ERROR, "/admin");
                    }
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/admin");
                    dispatcher.forward(req, res);
                } else if ("/unBlock".equals(path)) {
                    String candidate = req.getParameter("username");
                    boolean isUnBlocked = userService.unBlockUser(candidate);
                    if (isUnBlocked) {
                        logger.warn("{} is unblocked", candidate);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.SUCCESS, "/admin");
                    } else {
                        logger.error("{} unblock failed", candidate);
                        PopUpAlert.sendAlertAndRedirect(res, Constant.Error.ERROR, "/admin");
                    }
                } else {
                    res.sendRedirect("pageNotFound.jsp");
                }
            } else {
                logger.warn("{} is not admin", username);
                PopUpAlert.sendAlertAndRedirect(res, Constant.UNAUTHORIZED, "/home");
            }

        } catch (DbException e) {
            logger.error("database error in admin post request:: {}", e.getMessage(), e);
            handleError(res, e.getMessage(), req);
        } catch (Exception e) {
            logger.error("error in blocking: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", Constant.Error.INTERNAL_ERROR);
            forwardToErrorPage(req, res);
        }
    }
}