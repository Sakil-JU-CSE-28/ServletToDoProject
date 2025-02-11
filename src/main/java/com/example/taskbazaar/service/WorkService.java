package com.example.taskbazaar.service;

import com.example.taskbazaar.query.Queries;
import com.example.taskbazaar.utility.TaskBazaarLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WorkService {

    private static WorkService workService = null;
    private static Logger logger = TaskBazaarLogger.getLogger();

    private WorkService() {
        logger.info("WorkService created");
    }
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
            logger.info("Error getting all works");
            throw new RuntimeException(e);
        }
    }

}
