/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.BidDao;
import com.example.taskbazaar.dao.PostDao;
import com.example.taskbazaar.dto.PostDTO;
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
        int postCount = PostDao.getPostCountByPostId(new PostDTO(username, Integer.parseInt(postId)));
        return postCount > 0;
    }

    public List<String> getBiddersForPost(String postId) throws BidException {
        List<String> bidders = BidDao.getBiddersByPostId(postId);
        return bidders;
    }

    public boolean placeBid(String postId, String username) throws BidException {
        boolean isPlaced = BidDao.addBid(postId, username);
        return isPlaced;
    }

    public boolean addSelectedBidder(String postId, String workerUsername) throws BidException {
        boolean isAdded = BidDao.updateBidStatus(postId, workerUsername);
        return isAdded;
    }

    public int isBidAccepted(String postId) throws Exception {
        int totalInstanceOfAccepted = BidDao.getBidCountById(postId);
        return totalInstanceOfAccepted;
    }

    public int getBidCountByUsername(String username, int post_id) throws Exception {
        int totaInstanceInBid = BidDao.getBidCountByUsername(username, post_id);
        return totaInstanceInBid;
    }

    public List<String> getAllAcceptedBid(String username) {
        List<String> acceptedBids = BidDao.getAllAcceptedBidByUsername(username);
        return acceptedBids;
    }

}