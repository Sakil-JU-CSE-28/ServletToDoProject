package com.example.taskbazaar.service;

import com.example.taskbazaar.query.Queries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WorkService {

    private static WorkService workService = null;
    private WorkService() {}
    public static WorkService getInstance() {
        return workService==null?workService=new WorkService():workService;
    }

    public List<String> getAllWork(String username) {

        try(Connection connection = DbConnectionService.getConnection();
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
