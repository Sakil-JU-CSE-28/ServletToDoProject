/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dao;

import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.exception.PostException;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    private volatile static PostDao instance = null;
    private Logger logger = LoggerFactory.getLogger(PostDao.class);

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


    public PostDTO getByUsername(PostDTO postDTO) throws PostException {
        PostDTO post = null;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.POSTID_BY_AUTHOR)) {
            preparedStatement.setString(1, postDTO.author());
            preparedStatement.setInt(2, postDTO.postId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int postId = resultSet.getInt(1);
                String title = resultSet.getString(2);
                String description = resultSet.getString(3);
                String author = resultSet.getString(4);
                post = new PostDTO(author, title, description, postId);
            }
        } catch (Exception e) {
            logger.error("error in retrieving post by username:: {}", e.getMessage());
            throw new PostException(Constant.INTERNAL_ERROR);
        }
        return post;
    }

    public List<PostDTO> getPosts() throws PostException {
        List<PostDTO> posts = new ArrayList<>();
        try (Connection connection = DatabaseService.getConnection(); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(Constant.Queries.ALL_POSTS_BY_DESC)) {
            while (rs.next()) {
                int postId = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                posts.add(new PostDTO(postId, title, description));
            }
        } catch (Exception e) {
            logger.error("error in retrieving all post:: {}", e.getMessage());
            throw new PostException(Constant.INTERNAL_ERROR);
        }
        return posts;
    }

    public synchronized boolean add(PostDTO postDTO) throws PostException {
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement stmt = connection.prepareStatement(Constant.Queries.ADD_POST)) {
            stmt.setString(1, postDTO.title());
            stmt.setString(2, postDTO.content());
            stmt.setString(3, postDTO.author());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("error in adding post:: {}", e.getMessage());
            throw new PostException(Constant.INTERNAL_ERROR);
        }
    }

    public synchronized boolean delete(int postId) throws PostException {
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmt = conn.prepareStatement(Constant.Queries.DELETE_POST_BY_ID)) {
            stmt.setString(1, String.valueOf(postId));
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("error in deleting post:: {}", e.getMessage());
            throw new PostException(Constant.INTERNAL_ERROR);
        }
    }

    public synchronized boolean update(PostDTO postDTO) throws PostException {
        try (Connection conn = DatabaseService.getConnection(); PreparedStatement stmnt = conn.prepareStatement(Constant.Queries.UPDATE_POST_BY_ID)) {
            stmnt.setString(1, postDTO.title());
            stmnt.setString(2, postDTO.content());
            stmnt.setInt(3, postDTO.postId());
            int rowsUpdated = stmnt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.error("error in updating post:: {}", e.getMessage());
            throw new PostException(Constant.INTERNAL_ERROR);
        }
    }

    public PostDTO getById(int postId) throws PostException {
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Constant.Queries.POST_BY_ID)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    return new PostDTO(postId, title, description);
                }
            }
        } catch (Exception e) {
            logger.error("error in retrieving post by id:: {}", e.getMessage());
            throw new PostException(Constant.INTERNAL_ERROR);
        }
        return null;
    }
}
