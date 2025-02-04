package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import jakarta.servlet.RequestDispatcher;
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

@WebServlet("/accept")
public class acceptBidServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
             String postId = req.getParameter("postId");
             System.out.println("Post Id: " + postId);

        HttpSession session = req.getSession(false);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            res.sendRedirect("index.jsp");
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
            preparedStatement.close();
            if (role.equals("buyer")) {
                 String query2 = "select * from posts where author_username = ? and id = ?";
                 PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                 preparedStatement2.setString(1, username);
                 preparedStatement2.setString(2, postId);
                 ResultSet resultSet2 = preparedStatement2.executeQuery();
                 int length = resultSet2.getMetaData().getColumnCount();
                 if(length == 0){
                     res.setContentType("text/html");
                     res.getWriter().println("<script type='text/javascript'>"
                             + "alert('You are not authorized to accept this bid!');"
                             + "window.location='/home';"
                             + "</script>");
                 }
                 preparedStatement2.close();

                 String query3 = "select username from bid where postId = ?";
                 PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                 preparedStatement3.setString(1, postId);
                 ResultSet resultSet3 = preparedStatement3.executeQuery();
                List<String> bidders = new ArrayList<>();
                 while (resultSet3.next()) {
                     bidders.add(resultSet3.getString("username"));
                 }
                 preparedStatement3.close();

                 req.setAttribute("bidders", bidders);
                RequestDispatcher dispatcher = req.getRequestDispatcher("selectBidder.jsp");
                dispatcher.forward(req, res);

            }
            else{
                res.setContentType("text/html");
                res.getWriter().println("<script type='text/javascript'>"
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
