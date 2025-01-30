package com.example.taskbazaar;

import dao.UserDao;
import data.Post;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        List<Post> posts = new ArrayList<>();

        UserDao userDao = new UserDao();
        Pair<Statement, Connection> con = null;
        try {
            con = userDao.connect();
            Statement stmt = con.getLeft();
            ResultSet rs = stmt.executeQuery("select * from posts order by created_at desc");
            while (rs.next()) {
                Post post = new Post(rs.getInt("id"), rs.getString("title"), rs.getString("description"));
                System.out.println(post.getDescription());
                posts.add(post);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Add posts list to the request and forward to the home.jsp
        request.setAttribute("posts", posts);

        RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");

        dispatcher.forward(request, response);

    }
}
