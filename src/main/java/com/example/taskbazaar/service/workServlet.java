package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import jakarta.servlet.RequestDispatcher;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/works")
public class workServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
          HttpSession session = req.getSession();
          String username = (String) session.getAttribute("username");
        UserDao userDao = null;
        Connection connection = null;
        try {
            userDao = userDao.getInstance();
            connection = userDao.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String query = "select * from Orders where worker_username = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> works = new ArrayList<>();
            while (resultSet.next()) {
                works.add(resultSet.getString("postId"));
            }
            preparedStatement.close();
            req.setAttribute("works", works);
            RequestDispatcher dispatcher = req.getRequestDispatcher("workHistory.jsp");
            dispatcher.forward(req, res);
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
