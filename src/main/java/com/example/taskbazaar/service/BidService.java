package com.example.taskbazaar.service;

import com.example.taskbazaar.query.Queries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidService {



    public boolean isPostOwnedByUser(String username, String postId) throws SQLException {

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.POSTID_BY_AUTHOR)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, Integer.parseInt(postId));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // True if the user owns the post
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> getBiddersForPost(String postId) throws SQLException {
        List<String> bidders = new ArrayList<>();


        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.BID_USERNAME_BY_POSTID)) {
            preparedStatement.setString(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                bidders.add(resultSet.getString("username"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bidders;
    }


    public void placeBid(String postId, String username) throws SQLException {
        try (Connection connection = DbConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_BID);
            stmt.setString(1, postId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getPostIdByBidder(String bidderUsername) {
        String postId = null;
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.POST_ID_BY_USER)) {
            preparedStatement.setString(1, bidderUsername);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                postId = resultSet.getString("postId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return postId;
    }

    public boolean addBidderToOrder(String postId, String buyerUsername, String workerUsername) {

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.ADD_BIDER)) {
            preparedStatement.setString(1, postId);
            preparedStatement.setString(2, buyerUsername);
            preparedStatement.setString(3, workerUsername);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int existOrder(String postId) throws Exception {

        try(Connection connection = DbConnection.getConnection();
           PreparedStatement statement = connection.prepareStatement(Queries.EXIST_ORDER)){
            statement.setString(1, postId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }

    }

    public int existBid(String username) throws Exception {
        try(Connection connection = DbConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(Queries.EXIST_BID)){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }


}

