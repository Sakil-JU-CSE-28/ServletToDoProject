package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.AcceptedDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class AcceptedBidService {

    private static Logger logger = LoggerFactory.getLogger(AcceptedBidService.class);
    private AcceptedBidService() {
        logger.info("WorkService created");
    }
    private static class Holder {
        private static final AcceptedBidService INSTANCE = new AcceptedBidService();
    }
    public static AcceptedBidService getInstance() {
        return Holder.INSTANCE;
    }

    public List<String> getAllAcceptedBid(String username) {
        List<String> acceptedBids = AcceptedDao.getAllAcceptedBid(username);
        return acceptedBids;
    }
}
