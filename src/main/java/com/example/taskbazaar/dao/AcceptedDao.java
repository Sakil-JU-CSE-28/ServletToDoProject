package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.service.DbConnectionService;
import com.example.taskbazaar.utility.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AcceptedDao {

    public static synchronized boolean addBidderToAccepted(String postId, String buyerUsername, String workerUsername) throws BidException {

        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.ADD_BIDER)) {
            preparedStatement.setString(1, postId);
            preparedStatement.setString(2, buyerUsername);
            preparedStatement.setString(3, workerUsername);
            preparedStatement.executeUpdate();
            return true;
        }catch (Exception e) {
            throw new BidException(e.getMessage());
        }
    }

    public static synchronized int existInAccepted(String postId) throws Exception {

        try(Connection connection = DbConnectionService.getConnection();
            PreparedStatement statement = connection.prepareStatement(Queries.EXIST_IN_ACCEPTED)){
            statement.setString(1, postId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public static synchronized List<String> getAllAcceptedBid(String username) {

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