/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.model.Bid;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BidDao {

    private final Logger logger = LoggerFactory.getLogger(BidDao.class);
    private volatile static BidDao bidDao = null;

    private BidDao() {
    }

    public static BidDao getInstance() {
        if (bidDao == null) {
            synchronized (BidDao.class) {
                if (bidDao == null) {
                    bidDao = new BidDao();
                }
            }
        }
        return bidDao;
    }


    public List<String> getAllByPostId(String postId) throws BidException {
        List<String> bidders = new ArrayList<>();

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.Queries.Bid.BID_USERNAME_BY_POSTID)) {
            preparedStatement.setInt(1, Integer.parseInt(postId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                bidders.add(resultSet.getString("bidder_username"));
            }
        } catch (Exception e) {
            logger.error("error in retrieving bidders {}", e.getMessage());
            throw new BidException(Constants.Error.INTERNAL_ERROR);
        }
        return bidders;
    }

    public boolean add(String postId, String username) throws BidException {
        try (Connection connection = DatabaseService.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Constants.Queries.Bid.INSERT_BID);
            stmt.setString(1, postId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("error in adding bids {}", e.getMessage());
            throw new BidException(Constants.Error.INTERNAL_ERROR);
        }
    }

    public Bid getById(String username, int postId) throws DbException {
        Bid bid = null;
        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(Constants.Queries.Bid.BID_BY_ID)) {
            statement.setString(1, username);
            statement.setInt(2, postId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String bidder_name = resultSet.getString("bidder_username");
                int post_id = resultSet.getInt("post_id");
                boolean isAccepted = resultSet.getBoolean("isAccepted");
                bid = new Bid(bidder_name, post_id, isAccepted);
            }

        } catch (Exception e) {
            logger.error("error in retrieving bids {}", e.getMessage(), e);
            throw new DbException(Constants.Error.INTERNAL_ERROR);
        }
        return bid;
    }

    public void addBidder(String postId, String workerUsername) throws BidException {

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.Queries.Bid.ADD_BIDER)) {
            preparedStatement.setString(1, postId);
            preparedStatement.setString(2, workerUsername);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            logger.error("error in updating bids {}", e.getMessage());
            throw new BidException(Constants.Error.INTERNAL_ERROR);
        }
    }


    public List<String> getAllAcceptedByUsername(String username) throws BidException {

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.Queries.Bid.ALL_ACCEPTED_BID)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> works = new ArrayList<>();
            while (resultSet.next()) {
                works.add(resultSet.getString("post_id"));
            }
            return works;

        } catch (Exception e) {
            logger.error("error in retrieving accepted bids by postId {}", e.getMessage());
            throw new BidException(Constants.Error.INTERNAL_ERROR);
        }
    }
}