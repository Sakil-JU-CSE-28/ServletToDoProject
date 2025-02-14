package com.example.taskbazaar.service;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class BidService {

    private static volatile BidService bidService = null;
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    private BidService() {
        logger.info("BidService created");
    }

    public static BidService getInstance() {
        if (bidService == null) {
            synchronized (BidService.class) {
                if (bidService == null) {
                    bidService = new BidService();
                }
            }
        }
        return bidService;
    }

    public boolean isPostOwnedByUser(String username, String postId) throws BidException {

        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.POSTID_BY_AUTHOR)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, Integer.parseInt(postId));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // True if the user owns the post
        } catch (Exception e) {
            logger.error("{} get exception for isPostOwnedByUser()", username);
            throw new BidException(e.getMessage());
        }
    }


    public List<String> getBiddersForPost(String postId) throws BidException {
        List<String> bidders = new ArrayList<>();

        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.BID_USERNAME_BY_POSTID)) {
            preparedStatement.setString(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                bidders.add(resultSet.getString("username"));
            }
        } catch (Exception e) {
            logger.error("{} create exception for getBiddersForPost",postId);
            throw new BidException(e.getMessage());
        }
        return bidders;
    }


    public void placeBid(String postId, String username) throws BidException {
        try (Connection connection = DbConnectionService.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Constants.INSERT_BID);
            stmt.setString(1, postId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            logger.error("{} not add into bid", postId);
            throw new BidException(e.getMessage());
        }
    }

    public boolean addBidderToAccepted(String postId, String buyerUsername, String workerUsername) throws BidException {

        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.ADD_BIDER)) {
            preparedStatement.setString(1, postId);
            preparedStatement.setString(2, buyerUsername);
            preparedStatement.setString(3, workerUsername);
            preparedStatement.executeUpdate();
            return true;
        }catch (Exception e) {
            logger.error("{} error add in acceptedBid table ", postId);
            throw new BidException(e.getMessage());
        }
    }

    public int existInAccepted(String postId) throws Exception {

        try(Connection connection = DbConnectionService.getConnection();
            PreparedStatement statement = connection.prepareStatement(Constants.EXIST_IN_ACCEPTED)){
            statement.setString(1, postId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public int existBid(String username) throws Exception {
        try(Connection connection = DbConnectionService.getConnection();
            PreparedStatement statement = connection.prepareStatement(Constants.EXIST_BID)){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

}