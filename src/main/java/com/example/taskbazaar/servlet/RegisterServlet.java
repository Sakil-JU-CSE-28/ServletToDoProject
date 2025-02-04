package com.example.taskbazaar.servlet;

import dao.UserDao;
import model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "RegisterServlet", value = "/reg")
public class RegisterServlet  extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
       String name = request.getParameter("username");
       String password = request.getParameter("password");
       String role = request.getParameter("role");

       User user = new User(name, password, role);

        UserDao userDao = null;
        try {
            userDao = UserDao.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Connection connection = null;
        try {
            connection = userDao.connect();
            boolean registerd = userDao.register(user,connection.createStatement());
            if(registerd){
                response.sendRedirect("index.jsp");
            }
            else{
                response.sendRedirect("register.jsp");
            }
            
        } catch (Exception e) {
            response.sendRedirect("register.jsp");
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

}
