package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;

@WebServlet("/bid")
public class BidServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get the post ID from the form
        String postId = request.getParameter("postId");
        System.out.println(postId);

        // Fetch the username from session (assuming the user is logged in)
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        UserDao userDao = null;
        try {
            userDao = UserDao.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Connection connection = null;
        try {
            connection  = userDao.connect();
            String query = "select * from users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String role = resultSet.getString("role");

            if (role.equals("freelancer")) {
                String insertQuery = "INSERT INTO bid (postId,username) VALUES (?, ?)";
                PreparedStatement stmt = connection.prepareStatement(insertQuery);
                stmt.setString(1, postId);
                stmt.setString(2, username);


                stmt.executeUpdate();

                stmt.close();
                response.sendRedirect("/home");
            }
            else{
                response.setContentType("text/html");
                response.getWriter().println("<script type='text/javascript'>"
                        + "alert('You are not authorized to access this page!');"
                        + "window.location='/home';"
                        + "</script>");
            }

            resultSet.close();


            connection.close();

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
}
