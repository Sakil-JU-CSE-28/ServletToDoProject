package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/addBidder")
public class AddBidderServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String selectedBidder = request.getParameter("selectedBidder");
            UserDao userDao = null;
            try{
                userDao = userDao.getInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        Connection connection = null;
        try {
            connection = userDao.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        String postId;
        String query = "SELECT * FROM bid WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedBidder);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            postId = resultSet.getString("postId");

            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String query2 = "INSERT INTO Orders(postId, buyer_username,worker_username) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setString(1, postId);
            preparedStatement2.setString(2, username);
            preparedStatement2.setString(3, selectedBidder);
            preparedStatement2.executeUpdate();
            preparedStatement2.close();
            response.setContentType("text/html");
            response.getWriter().println("<script type='text/javascript'>"
                    + "alert('Selected bidder added successfully!!');"
                    + "window.location='/home';"
                    + "</script>");
        } catch (SQLException e) {
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
