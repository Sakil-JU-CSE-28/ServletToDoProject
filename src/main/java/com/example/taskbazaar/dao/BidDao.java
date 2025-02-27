/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constant;
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
    private BidDao() {}
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


    public List<String> getBiddersByPostId(String postId) throws BidException {
        List<String> bidders = new ArrayList<>();

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.BID_USERNAME_BY_POSTID)) {
            preparedStatement.setInt(1, Integer.parseInt(postId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                bidders.add(resultSet.getString("bidder_username"));
            }
        } catch (Exception e) {
            logger.error("error in retrieving bidders {}",e.getMessage());
            throw new BidException(Constant.INTERNAL_ERROR);
        }
        return bidders;
    }

    public synchronized boolean add(String postId, String username) throws BidException {
        try (Connection connection = DatabaseService.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(Constant.Queries.INSERT_BID);
            stmt.setString(1, postId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("error in adding bids {}",e.getMessage());
            throw new BidException(Constant.INTERNAL_ERROR);
        }
    }

    public int getCountByUsername(String username, int postId) throws DbException {
        int count = 0;
        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(Constant.Queries.BID_COUNT_BY_USERNAME)) {
            statement.setString(1, username);
            statement.setInt(2, postId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) count = resultSet.getInt(1);

        } catch (Exception e) {
            logger.error("error in retrieving bids {}",e.getMessage());
            throw new DbException(Constant.INTERNAL_ERROR);
        }
        return count;
    }

    public synchronized boolean updateStatus(String postId, String workerUsername) throws BidException {

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.ADD_BIDER)) {
            preparedStatement.setString(1, postId);
            preparedStatement.setString(2, workerUsername);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("error in updating bids {}",e.getMessage());
            throw new BidException(Constant.INTERNAL_ERROR);
        }
    }

    public int getCountById(String postId) throws BidException {

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(Constant.Queries.BID_COUNT_BY_ID)) {
            System.out.println("getBidCountById");
            statement.setInt(1, Integer.parseInt(postId));
            ResultSet resultSet = statement.executeQuery();
            int count = 0;
            if (resultSet.next()) count = resultSet.getInt(1);
            return count;
        }catch (Exception e) {
            logger.error("error in retrieving bids by postId {}",e.getMessage());
            throw new BidException(Constant.INTERNAL_ERROR);
        }
    }

    public List<String> getAllAcceptedByUsername(String username) throws BidException {

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.ALL_ACCEPTED_BID)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> works = new ArrayList<>();
            while (resultSet.next()) {
                works.add(resultSet.getString("post_id"));
            }
            return works;

        } catch (Exception e) {
            logger.error("error in retrieving accepted bids by postId {}",e.getMessage());
            throw new BidException(Constant.INTERNAL_ERROR);
        }
    }

}
