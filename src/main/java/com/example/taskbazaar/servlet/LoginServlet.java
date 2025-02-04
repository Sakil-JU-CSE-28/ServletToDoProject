package com.example.taskbazaar.servlet;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import dao.UserDao;
import model.User;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    public void init() {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
         String username = request.getParameter("username");
         String password = request.getParameter("password");
         User user = new User(username, password);
        UserDao userDao = null;
        try {
            userDao = UserDao.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Connection connection = null;
        try {
            connection = userDao.connect();
            boolean isValid = userDao.authenticate(user);
            if(isValid) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
               response.sendRedirect("/home");
            }
            else {
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroy() {
    }
}