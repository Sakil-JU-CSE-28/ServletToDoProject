/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dao;

import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.exception.PostException;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    private volatile static PostDao instance = null;
    private final Logger logger = LoggerFactory.getLogger(PostDao.class);

    private PostDao() {
    }

    public static PostDao getInstance() {
        if (instance == null) {
            synchronized (PostDao.class) {
                if (instance == null) {
                    instance = new PostDao();
                }
            }
        }
        return instance;
    }


    public List<PostDTO> getPosts() throws PostException {
        List<PostDTO> posts = new ArrayList<>();
        try (Connection connection = DatabaseService.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(Constants.Queries.Post.ALL_POSTS_BY_DESC)) {
            while (rs.next()) {
                int postId = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                posts.add(new PostDTO(postId, title, description));
            }
        } catch (Exception e) {
            logger.error("error in retrieving all post:: {}", e.getMessage());
            throw new PostException(Constants.Error.INTERNAL_ERROR);
        }
        return posts;
    }

    public boolean add(PostDTO postDTO) throws PostException {
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement stmt = connection.prepareStatement(Constants.Queries.Post.ADD_POST)) {
            stmt.setString(1, postDTO.title());
            stmt.setString(2, postDTO.content());
            stmt.setString(3, postDTO.author());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("error in adding post:: {}", e.getMessage());
            throw new PostException(Constants.Error.INTERNAL_ERROR);
        }
    }

    public boolean delete(int postId) throws PostException {
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(Constants.Queries.Post.DELETE_POST_BY_ID)) {
            stmt.setString(1, String.valueOf(postId));
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("error in deleting post:: {}", e.getMessage());
            throw new PostException(Constants.Error.INTERNAL_ERROR);
        }
    }

    public boolean update(PostDTO postDTO) throws PostException {
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmnt = conn.prepareStatement(Constants.Queries.Post.UPDATE_POST_BY_ID)) {
            stmnt.setString(1, postDTO.title());
            stmnt.setString(2, postDTO.content());
            stmnt.setInt(3, postDTO.postId());
            int rowsUpdated = stmnt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.error("error in updating post:: {}", e.getMessage());
            throw new PostException(Constants.Error.INTERNAL_ERROR);
        }
    }

    public PostDTO getById(int postId) throws PostException {
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Constants.Queries.Post.POST_BY_ID)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String author = rs.getString("author_username");
                    return new PostDTO(author, title, description,postId);
                }
            }
        } catch (Exception e) {
            logger.error("error in retrieving post by id:: {}", e.getMessage(),e);
            throw new PostException(Constants.Error.INTERNAL_ERROR);
        }
        return null;
    }
}
