package com.example.taskbazaar.service;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.query.Queries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private static AdminService adminService = null;

    private AdminService(){
    }

    public static AdminService getInstance(){
        return  adminService != null? adminService = new AdminService() : adminService;
    }

    public List<Post> getAllPost() throws Exception {

        try( Connection conn = DbConnectionService.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(Queries.ALL_POSTS_BY_DESC);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            List<Post> posts = new ArrayList<>();
            while (resultSet.next()) {
                Post post = new Post(resultSet.getInt("id"), resultSet.getString("title"),resultSet.getString("description"));
                posts.add(post);
            }

            return posts;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
