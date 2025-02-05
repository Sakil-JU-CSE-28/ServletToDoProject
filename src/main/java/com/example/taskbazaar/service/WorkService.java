package com.example.taskbazaar.service;

import com.example.taskbazaar.query.Queries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WorkService {

    public List<String> getAllWork(String username) {

        try(Connection connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(Queries.ORDER_BY_WORKER_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> works = new ArrayList<>();
            while (resultSet.next()) {
                works.add(resultSet.getString("postId"));
            }
            return works;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
