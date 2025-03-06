/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.BidDao;
import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.model.Bid;

import java.util.List;


public class BidService {

    private static volatile BidService bidService = null;
    private final BidDao bidDao = BidDao.getInstance();

    private BidService() {
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

    public List<String> getAllByPostId(String postId) throws BidException {
        return bidDao.getAllByPostId(postId);
    }

    public boolean placeBid(String postId, String username) throws BidException {
        return bidDao.add(postId, username);
    }

    public void addBidder(String postId, String workerId) throws BidException {
        bidDao.addBidder(postId, workerId);
    }

    public Bid getById(String username, int post_id) throws BidException, DbException {
        Bid bid = bidDao.getById(username, post_id);
        if(bid == null) {
            throw new BidException("Bid not found");
        }
        return bid;
    }

    public List<String> getAllAcceptedByUsername(String username) throws BidException {
        return bidDao.getAllAcceptedByUsername(username);
    }
}