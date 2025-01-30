package com.example.taskbazaar;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import dao.UserDao;
import data.User;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.commons.lang3.tuple.Pair;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    public void init() {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
         String username = request.getParameter("username");
         String password = request.getParameter("password");
         User user = new User(username, password);
         String sessionId = request.getSession().getId();
        UserDao userDao = new UserDao();
        Pair<Statement, Connection> con = null;
        try {
            con = userDao.connect();
            boolean isValid = userDao.authenticate(user,con.getLeft());
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
                con.getRight().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroy() {
    }
}