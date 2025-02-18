package com.example.taskbazaar.servlet;

import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.AuthenticationService;
import com.example.taskbazaar.service.AlertService;
import com.example.taskbazaar.validation.Validator;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(value = {"/login", "/logout", "/register"})
public class AuthenticationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private final Validator validator = Validator.getInstance();

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String path = request.getServletPath();
            User user = new User(username, password);
            logger.info("User : {}", user);
            boolean isSuccess;
            if ("/login".equals(path)) {

                isSuccess = validator.validateLogin(user);

                if(!isSuccess) {
                    AlertService.sendAlertAndRedirect(response,Constants.VALIDATION_ERROR, "index.jsp");
                    return;
                }

                logger.info("authenticating user:: {}", username);
                isSuccess = authenticationService.authenticate(user);
                if (isSuccess) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    logger.info("User: {} logged in", username);
                    response.sendRedirect("/home");
                } else {
                    logger.info("{} enter wrong credentials", username);
                    AlertService.sendAlertAndRedirect(response, Constants.CREDENTIALS_ERROR, "index.jsp");
                }
            } else if ("/register".equals(path)) {
                String confirmPassword = request.getParameter("confirmPassword");
                String role = request.getParameter("role");
                user = new User(username, password, role);

                isSuccess = validator.validateRegistration(user,confirmPassword);
                if(!isSuccess) {
                    AlertService.sendAlertAndRedirect(response,Constants.VALIDATION_ERROR, "register.jsp");
                    return;
                }

                isSuccess = authenticationService.register(user);
                if (isSuccess) {
                    logger.info("{} registered", user);
                    AlertService.sendAlertAndRedirect(response, Constants.SUCCESS, "index.jsp");

                } else {
                    logger.info("User {} is already registered", username);
                    AlertService.sendAlertAndRedirect(response, Constants.ALREADY_REGISTERED, "index.jsp");
                }

            } else {
                logger.warn("{} {}", username, Constants.INVALID_PAGE_ERROR);
                response.sendRedirect(request.getContextPath() + "pageNotFound.jsp");
            }
        } catch (AuthenticationException e) {
            logger.error("authentication error: {}", e.getMessage());
            try {
                AlertService.sendAlertAndRedirect(response, Constants.VALIDATION_ERROR, request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constants.FORWARD_ERROR, ex.getMessage());
            }
        } catch (ValidationException e) {
            logger.error("validation error: {}", e.getMessage());
            try {
                AlertService.sendAlertAndRedirect(response, Constants.VALIDATION_ERROR, request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constants.FORWARD_ERROR, ex.getMessage());
            }
        } catch (Exception e) {
            logger.error("error: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constants.FORWARD_ERROR, ex.getMessage());
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
                AlertService.sendAlertAndRedirect(response, Constants.SUCCESS, "index.jsp");

            } else {
                logger.warn("{}", Constants.INVALID_PAGE_ERROR);
                response.sendRedirect(request.getContextPath() + "pageNotFound.jsp");
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constants.FORWARD_ERROR, ex.getMessage());
            }
        }
    }
}