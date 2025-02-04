package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.taskbazaar.model.Post;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin")
public class adminServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UserDao userDao = null;
        Connection conn = null;
        try {
            userDao = userDao.getInstance();
            conn = userDao.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String query = "select * from posts";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            List<Post> posts = new ArrayList<>();
            while (resultSet.next()) {
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("title"));
                posts.add(post);
            }

            req.setAttribute("posts", posts);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
