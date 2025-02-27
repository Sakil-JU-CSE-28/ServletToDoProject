/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.BidDao;
import com.example.taskbazaar.dao.PostDao;
import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.PostException;

import java.util.List;


public class BidService {

    private static volatile BidService bidService = null;
    private final BidDao bidDao = BidDao.getInstance();
    private final PostDao postDao = PostDao.getInstance();

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

    public boolean isPostOwner(String username, String postId) throws PostException {
        PostDTO post = postDao.getByUsername(new PostDTO(username, Integer.parseInt(postId)));
        return post != null;
    }

    public List<String> getBiddersForPost(String postId) throws BidException {
        List<String> bidders = bidDao.getBiddersByPostId(postId);
        return bidders;
    }

    public boolean placeBid(String postId, String username) throws BidException {
        boolean isPlaced = bidDao.add(postId, username);
        return isPlaced;
    }

    public boolean addBidder(String postId, String workerId) throws BidException {
        boolean isAdded = bidDao.updateStatus(postId, workerId);
        return isAdded;
    }

    public boolean isAccepted(String postId) throws Exception {
        int totalInstanceOfAccepted = bidDao.getCountById(postId);
        return totalInstanceOfAccepted > 0;
    }

    public boolean isExist(String username, int post_id) throws Exception {
        int totalInstanceInBid = bidDao.getCountByUsername(username, post_id);
        return totalInstanceInBid > 0;
    }

    public List<String> getAllAccepted(String username) throws BidException {
        List<String> acceptedBids = bidDao.getAllAcceptedByUsername(username);
        return acceptedBids;
    }

}