package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.model.Post;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Post> posts = new ArrayList<>();

        UserDao userDao = null;
        try {
            userDao = UserDao.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Connection connection = null;
        try {
            connection = userDao.connect();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from posts order by created_at desc");
            while (rs.next()) {
                Post post = new Post(rs.getInt("id"), rs.getString("title"), rs.getString("description"));
                System.out.println(post.getDescription());
                posts.add(post);
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


        // Add posts list to the request and forward to the home.jsp
        request.setAttribute("posts", posts);

        RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");

        dispatcher.forward(request, response);

    }
}
