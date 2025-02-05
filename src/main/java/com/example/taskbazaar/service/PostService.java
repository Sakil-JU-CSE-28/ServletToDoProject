package com.example.taskbazaar.service;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.query.Queries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(Queries.ALL_POSTS_BY_DESC)) {
            while (rs.next()) {
                posts.add(new Post(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    public boolean createPost(String username, String title, String content) {
        if (username == null) return false;
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.ADD_POST)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setString(3, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePost(String postId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.DELETE_POST_BY_ID)) {
            stmt.setString(1, postId);
            return stmt.executeUpdate() > 0;
        }
    }

}
