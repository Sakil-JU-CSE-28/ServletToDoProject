package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.service.DbConnectionService;
import com.example.taskbazaar.utility.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BidDao {
    public static synchronized List<String> getBiddersForPost(String postId) throws BidException {
        List<String> bidders = new ArrayList<>();

        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.BID_USERNAME_BY_POSTID)) {
            preparedStatement.setString(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                bidders.add(resultSet.getString("username"));
            }
        } catch (Exception e) {
            throw new BidException(e.getMessage());
        }
        return bidders;
    }

    public static synchronized boolean placeBid(String postId, String username) throws BidException {
        try (Connection connection = DbConnectionService.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_BID);
            stmt.setString(1, postId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new BidException(e.getMessage());
        }
    }

    public static synchronized int existBid(String username) throws Exception {
        try(Connection connection = DbConnectionService.getConnection();
            PreparedStatement statement = connection.prepareStatement(Queries.EXIST_BID)){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

}
