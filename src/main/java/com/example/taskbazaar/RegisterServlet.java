package com.example.taskbazaar;

import dao.UserDao;
import data.User;
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

       UserDao userDao = new UserDao();
       Pair<Statement, Connection> con = null;
        try {
            con = userDao.connect();
            boolean registerd = userDao.register(user,con.getLeft());
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
                con.getRight().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
