package com.example.taskbazaar.service;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.query.Queries;
import com.example.taskbazaar.utility.TaskBazaarLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PostService {

    private static PostService postService = null;
    private static Logger logger = TaskBazaarLogger.getLogger();
    private PostService() {
        logger.info("PostService created");
    }
    public static PostService getInstance() {
        return postService == null ? postService = new PostService() : postService;
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DbConnectionService.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(Queries.ALL_POSTS_BY_DESC)) {
            while (rs.next()) {
                posts.add(new Post(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        } catch (Exception e) {
            logger.info("Error while retrieving posts");
            throw new RuntimeException(e);
        }
        return posts;
    }

    public boolean createPost(String username, String title, String content) {
        if (username == null) return false;
        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(Queries.ADD_POST)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setString(3, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.info("Error while creating post");
           throw new RuntimeException(e);
        }
    }

    public boolean deletePost(int postId) throws Exception {
        try (Connection conn = DbConnectionService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Queries.DELETE_POST_BY_ID)) {
            stmt.setString(1, String.valueOf(postId));
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePost(int postId,String newTitle, String newContent) {
        try(Connection conn = DbConnectionService.getConnection();
            PreparedStatement stmnt = conn.prepareStatement(Queries.UPDATE_POST_BY_ID)){
            stmnt.setString(1, newTitle);
            stmnt.setString(2, newContent);
            stmnt.setInt(3, postId);
            int rowsUpdated = stmnt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.info("Error while updating post");
            throw new RuntimeException(e);
        }
    }
}