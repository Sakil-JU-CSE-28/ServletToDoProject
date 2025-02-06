package com.example.taskbazaar.servlet;

import java.io.*;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.AuthenticationService;
import com.example.taskbazaar.service.RegisterService;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "LoginServlet", value = {"/login","/logout","/reg"})
public class AuthenticationServlet extends HttpServlet {

    private final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private final RegisterService registerService = RegisterService.getInstance();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = new User(username, password);

        if(request.getServletPath().equals("/login")) {
            boolean isValid;
            try {
                isValid = authenticationService.authenticate(user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(isValid) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("/home");
            }
            else {
                response.sendRedirect("/login");
            }
        }
        else if(request.getServletPath().equals("/reg")){
            try {
                boolean registered = registerService.register(user);
                if(registered){
                    response.sendRedirect("index.jsp");
                }
                else{
                    response.sendRedirect("register.jsp");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if(request.getServletPath().equals("/logout")) {
            // Invalidate the current session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Redirect to the login page or home page
            response.sendRedirect("index.jsp");
        }

    }

}