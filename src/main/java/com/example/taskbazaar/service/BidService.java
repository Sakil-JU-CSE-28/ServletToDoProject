package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.AcceptedDao;
import com.example.taskbazaar.dao.BidDao;
import com.example.taskbazaar.dao.PostDao;
import com.example.taskbazaar.exception.BidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        boolean isPostOwner = PostDao.isPostOwner(username, postId);
        return isPostOwner;
    }

    public List<String> getBiddersForPost(String postId) throws BidException {
        List<String> bidders = BidDao.getBiddersForPost(postId);
        return bidders;
    }

    public boolean placeBid(String postId, String username) throws BidException {
        boolean isPlaced = PostDao.isPostOwner(postId, username);
        return isPlaced;
    }

    public boolean addBidderToAccepted(String postId, String buyerUsername, String workerUsername) throws BidException {
        boolean isAdded = AcceptedDao.addBidderToAccepted(postId, buyerUsername, workerUsername);
        return isAdded;
    }

    public int existInAccepted(String postId) throws Exception {
        int totalInstanceInAccepted = AcceptedDao.existInAccepted(postId);
        return totalInstanceInAccepted;
    }

    public int existBid(String username) throws Exception {
       int totaInstanceInBid = BidDao.existBid(username);
       return totaInstanceInBid;
    }

}