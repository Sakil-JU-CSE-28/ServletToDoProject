package com.example.taskbazaar.servlet;

import java.io.*;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.Authentication;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "LoginServlet", value = {"/login","/logout"})
public class LoginServlet extends HttpServlet {

    public void init() {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getServletPath().equals("/login")) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            User user = new User(username, password);


            boolean isValid = false;
            try {
                isValid = Authentication.authenticate(user);
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

    public void destroy() {
    }
}