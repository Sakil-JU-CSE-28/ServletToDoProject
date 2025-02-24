/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dao;

import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    public static synchronized int getPostCountByPostId(PostDTO postDTO) throws BidException {
        int count = 0;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.POSTID_BY_AUTHOR)) {
            preparedStatement.setString(1, postDTO.author);
            preparedStatement.setInt(2, postDTO.postId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (Exception e) {
            throw new BidException(e.getMessage());
        }
        return count;
    }

    public static synchronized List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DatabaseService.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(Constant.Queries.ALL_POSTS_BY_DESC)) {
            while (rs.next()) {
                posts.add(new Post(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    public static synchronized boolean addPost(PostDTO postDTO) {
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement stmt = connection.prepareStatement(Constant.Queries.ADD_POST)) {
            stmt.setString(1, postDTO.title);
            stmt.setString(2, postDTO.content);
            stmt.setString(3, postDTO.author);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized boolean deletePost(int postId) throws Exception {
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(Constant.Queries.DELETE_POST_BY_ID)) {
            stmt.setString(1, String.valueOf(postId));
            return stmt.executeUpdate() > 0;
        }
    }

    public static synchronized boolean updatePost(PostDTO postDTO) {
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmnt = conn.prepareStatement(Constant.Queries.UPDATE_POST_BY_ID)) {
            stmnt.setString(1, postDTO.title);
            stmnt.setString(2, postDTO.content);
            stmnt.setInt(3, postDTO.postId);
            int rowsUpdated = stmnt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized PostDTO getPostById(int postId){
        String query = Constant.Queries.POST_BY_ID;

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve post details from the result set
                    String title = rs.getString("title");
                    String description = rs.getString("description");

                    return new PostDTO(postId, title, description);
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null; // Return null if post not found or an error occurs
    }
}
