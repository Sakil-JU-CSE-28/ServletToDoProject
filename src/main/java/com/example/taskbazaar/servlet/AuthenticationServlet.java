/*
 * author : Md. Sakil Ahmed
 */
package com.example.taskbazaar.servlet;

import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.service.AuthenticationService;
import com.example.taskbazaar.utility.Constants;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.validation.Validator;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = {"/login", "/logout", "/register"})
public class AuthenticationServlet extends BaseServlet {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private final UserService userService = UserService.getInstance();
    private final Validator validator = Validator.getInstance();

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String path = request.getServletPath();
            UserDTO userDTO = new UserDTO(username, password, null, null, null, false);
            boolean isSuccess;

            if ("/login".equals(path)) {
                logger.info("Received login request for user: {}", username);
                validator.validateLogin(userDTO);
                logger.info("Attempting to authenticate user: {}", username);
                isSuccess = authenticationService.authenticate(userDTO);
                if (isSuccess) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    userDTO = userService.getByUsername(username);
                    session.setAttribute("role", userDTO.role());
                    logger.info("User {} successfully logged in", username);
                    response.sendRedirect("/home");
                } else {
                    logger.warn("User {} entered incorrect password", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.INVALID_PASSWORD, "index.jsp");
                }
            } else if ("/register".equals(path)) {
                logger.info("Received register request for user: {}", username);
                String confirmPassword = request.getParameter("confirmPassword");
                String role = request.getParameter("role");
                userDTO = new UserDTO(username, password, confirmPassword, role, null, false);
                validator.validateRegistration(userDTO);
                logger.info("Attempting to register user: {}", username);
                isSuccess = authenticationService.register(userDTO);
                if (isSuccess) {
                    logger.info("User {} registered successfully", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.SUCCESS, "index.jsp");
                } else {
                    logger.warn("User {} is already registered", username);
                    PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.ALREADY_REGISTERED, "index.jsp");
                }

            } else {
                logger.error("Invalid request path: {} for user {}", path, username);
                response.sendRedirect(request.getContextPath() + "pageNotFound.jsp");
            }
        } catch (ValidationException | AuthenticationException e) {
            logger.error("error:: {}", e.getMessage(), e);
            handleError(response, e.getMessage(), request);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            forwardToErrorPage(request, response);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getServletPath();
            if ("/logout".equals(path)) {
                String username = request.getParameter("username");
                logger.info("User {} logging out", username);
                HttpSession session = request.getSession(false);
                authenticationService.logOut(session);
                PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.SUCCESS, "index.jsp");
            } else {
                logger.error("Invalid request path: {} for logout", path);
                response.sendRedirect(request.getContextPath() + "pageNotFound.jsp");
            }
        } catch (Exception e) {
            logger.error("Error occurred during logout process: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            forwardToErrorPage(request, response);
        }
    }
}