package com.example.taskbazaar.service;

import com.example.taskbazaar.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WorkService {

    private static Logger logger = LoggerFactory.getLogger(WorkService.class);
    private WorkService() {
        logger.info("WorkService created");
    }
    private static class Holder {
        private static final WorkService INSTANCE = new WorkService();
    }
    public static WorkService getInstance() {
        return Holder.INSTANCE;
    }

    public List<String> getAllWork(String username) {

        try(Connection connection = DbConnectionService.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(Constants.ORDER_BY_WORKER_USERNAME)) {
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
