/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BidDao {
    public static synchronized List<String> getBiddersByPostId(String postId) throws BidException {
        List<String> bidders = new ArrayList<>();

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.BID_USERNAME_BY_POSTID)) {
            preparedStatement.setInt(1, Integer.parseInt(postId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                bidders.add(resultSet.getString("bidder_username"));
            }
        } catch (Exception e) {
            throw new BidException(e.getMessage());
        }
        return bidders;
    }

    public static synchronized boolean addBid(String postId, String username) throws BidException {
        try (Connection connection = DatabaseService.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Constant.Queries.INSERT_BID);
            stmt.setString(1, postId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new BidException(e.getMessage());
        }
    }

    public static synchronized int getBidCountByUsername(String username,int post_id) throws Exception {
        int count = 0;
        try(Connection connection = DatabaseService.getConnection();
            PreparedStatement statement = connection.prepareStatement(Constant.Queries.BID_COUNT_BY_USERNAME)){
            statement.setString(1, username);
            statement.setInt(2, post_id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) count = resultSet.getInt(1);

        }
        return count;
    }

    public static synchronized boolean updateBidStatus(String postId, String workerUsername) throws BidException {

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.ADD_BIDER)) {
            preparedStatement.setString(1, postId);
            preparedStatement.setString(2, workerUsername);
            preparedStatement.executeUpdate();
            return true;
        }catch (Exception e) {
            throw new BidException(e.getMessage());
        }
    }

    public static synchronized int getBidCountById(String postId) throws Exception {

        try(Connection connection = DatabaseService.getConnection();
            PreparedStatement statement = connection.prepareStatement(Constant.Queries.BID_COUNT_BY_ID)){
            System.out.println("getBidCountById");
            statement.setInt(1, Integer.parseInt(postId));
            ResultSet resultSet = statement.executeQuery();
            int count = 0;
            if(resultSet.next()) count = resultSet.getInt(1);
            return count;
        }
    }

    public static synchronized List<String> getAllAcceptedBidByUsername(String username) {

        try(Connection connection = DatabaseService.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.ALL_ACCEPTED_BID)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> works = new ArrayList<>();
            while (resultSet.next()) {
                works.add(resultSet.getString("post_id"));
            }
            return works;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
