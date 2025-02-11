package com.example.taskbazaar.servlet;

import java.io.*;
import java.util.logging.Logger;

import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.AuthenticationService;
import com.example.taskbazaar.service.RegisterService;
import com.example.taskbazaar.service.ResponseService;
import com.example.taskbazaar.utility.LogMessage;
import com.example.taskbazaar.utility.TaskBazaarLogger;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "AuthenticationServlet", value = {"/login","/logout","/reg"})
public class AuthenticationServlet extends HttpServlet {

    private static final Logger logger = TaskBazaarLogger.getLogger();
    private final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private final RegisterService registerService = RegisterService.getInstance();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = new User(username, password);

        if(request.getServletPath().equals("/login")) {
            boolean isValid;

            try{
                logger.info("authenticating user......");
                isValid = authenticationService.authenticate(user);
            } catch (Exception e) {
                logger.info("error authenticating user......" + e.getMessage());
                ResponseService.sendAlertAndRedirect(response,LogMessage.tryAgain,"index.jsp");
                return;
            }

            if(isValid) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                logger.info("User " + username + " logged in");
                response.sendRedirect("/home");
            }
            else {
                logger.info("User " + username + " enter wrong credentials");
                ResponseService.sendAlertAndRedirect(response,"Wrong Credentials!! Try again","index.jsp");
            }
        }
        else if(request.getServletPath().equals("/reg")){
            String confirmPassword = request.getParameter("confirmPassword");
            String role = request.getParameter("role");
            if(!password.equals(confirmPassword)) {
                logger.info("Passwords do not match for user : " + username);
                ResponseService.sendAlertAndRedirect(response,"Password do not match. Try again","register.jsp");
            }
            try {
                user = new User(username, password,role);
                boolean registered = registerService.register(user);
                System.out.println(registered);
                if (registered) {
                    logger.info("User " + username + " registered");
                    response.sendRedirect("index.jsp");
                } else {
                    logger.info("User " + username + " is already registered" );
                    response.sendRedirect("register.jsp");
                }
            } catch (Exception e) {
                logger.info("error registering user......" + e.getMessage());
                ResponseService.sendAlertAndRedirect(response,"Some Error occurred in registration form!!! Please try again","register.jsp");
            }
        }
        else{
            logger.info("Internal server error...");
            ResponseService.sendAlertAndRedirect(response,LogMessage.tryAgain,"index.jsp");
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if(request.getServletPath().equals("/logout")) {
            logger.info("User " + request.getSession().getAttribute("username") + " logged out");
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            logger.info("Redirecting to login.......");
            response.sendRedirect("index.jsp");
        }
        else{
            logger.info("Internal server error...");
            ResponseService.sendAlertAndRedirect(response,LogMessage.tryAgain,"index.jsp");
        }

    }

}