package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.service.DbConnectionService;
import com.example.taskbazaar.utility.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostDao {
    public static synchronized boolean isPostOwner(String username, String postId) throws BidException {
        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.POSTID_BY_AUTHOR)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, Integer.parseInt(postId));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            throw new BidException(e.getMessage());
        }
    }

    public static synchronized List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DbConnectionService.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(Queries.ALL_POSTS_BY_DESC)) {
            while (rs.next()) {
                posts.add(new Post(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    public static synchronized boolean createPost(String username, String title, String content) {
        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.ADD_POST)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setString(3, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized boolean deletePost(int postId) throws Exception {
        try (Connection conn = DbConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.DELETE_POST_BY_ID)) {
            stmt.setString(1, String.valueOf(postId));
            return stmt.executeUpdate() > 0;
        }
    }

    public static synchronized boolean updatePost(int postId, String newTitle, String newContent) {
        try (Connection conn = DbConnectionService.getConnection();
             PreparedStatement stmnt = conn.prepareStatement(Queries.UPDATE_POST_BY_ID)) {
            stmnt.setString(1, newTitle);
            stmnt.setString(2, newContent);
            stmnt.setInt(3, postId);
            int rowsUpdated = stmnt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
