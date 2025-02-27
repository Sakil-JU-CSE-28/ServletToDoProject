/*
 * author : Md. Sakil Ahmed
 */
package com.example.taskbazaar.servlet;

import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.service.AuthenticationService;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constant;
import com.example.taskbazaar.validation.Validator;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = {"/login", "/logout", "/register"})
public class AuthenticationServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final Validator validator = Validator.getInstance();

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String path = request.getServletPath();
            UserDTO user = new UserDTO(username, password, null, null, null,false);
            logger.info("User : {}", user);
            boolean isSuccess;
            if ("/login".equals(path)) {
                validator.validateLogin(user);
                logger.info("authenticating user:: {}", username);
                isSuccess = authenticationService.authenticate(user);
                if (isSuccess) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    String role = userService.getRole(username);
                    session.setAttribute("role", role);
                    logger.info("User: {} logged in", username);
                    response.sendRedirect("/home");
                } else {
                    logger.info("{} enter wrong password", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constant.INVALID_PASSWORD, "index.jsp");
                }
            } else if ("/register".equals(path)) {
                String confirmPassword = request.getParameter("confirmPassword");
                String role = request.getParameter("role");
                user = new UserDTO(username, password, confirmPassword, role, null,false);
                validator.validateRegistration(user);
                isSuccess = authenticationService.register(user);
                if (isSuccess) {
                    logger.info("{} registered", user);
                    PopUpAlert.sendAlertAndRedirect(response, Constant.SUCCESS, "index.jsp");

                } else {
                    logger.info("User {} is already registered", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constant.ALREADY_REGISTERED, "index.jsp");
                }

            } else {
                logger.warn("{} {}", username, Constant.INVALID_PAGE_ERROR);
                response.sendRedirect(request.getContextPath() + "pageNotFound.jsp");
            }
        } catch (ValidationException e) {
            logger.error("validation error: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (AuthenticationException e) {
            logger.error("authentication error: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (Exception e) {
            logger.error("error: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getServletPath();
            if ("/logout".equals(path)) {
                String username = request.getParameter("username");
                logger.info("{} logged out", username);
                HttpSession session = request.getSession(false);
                authenticationService.logOut(session);
                PopUpAlert.sendAlertAndRedirect(response, Constant.SUCCESS, "index.jsp");

            } else {
                logger.warn("{}", Constant.INVALID_PAGE_ERROR);
                response.sendRedirect(request.getContextPath() + "pageNotFound.jsp");
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