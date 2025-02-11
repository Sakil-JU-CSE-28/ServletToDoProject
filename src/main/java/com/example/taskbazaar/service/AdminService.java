package com.example.taskbazaar.service;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.query.Queries;
import com.example.taskbazaar.utility.TaskBazaarLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AdminService {

    private static Logger logger = TaskBazaarLogger.getLogger();
    private static AdminService adminService = null;

    private AdminService(){
        logger.info("AdminService created");
    }

    public static AdminService getInstance(){
        return  adminService == null ? adminService = new AdminService() : adminService;
    }

    public List<Post> getAllPost() throws Exception {
        List<Post> posts = new ArrayList<>();
        try( Connection conn = DbConnectionService.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(Queries.ALL_POSTS_BY_DESC);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("title"),resultSet.getString("description"));
                posts.add(post);
            }
           logger.info("Retrieved posts " + posts.size());

        } catch (SQLException e) {
            logger.info("SQLException : " + e.getMessage());
            throw new Exception(e);
        }
        return posts;
    }
}
