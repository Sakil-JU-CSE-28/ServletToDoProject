package com.example.taskbazaar.model;

public class Bid {
    private String bidderName;
    private int postId;
    private boolean isAccepted;
    public Bid(String bidderName, int postId, boolean isAccepted) {
        this.bidderName = bidderName;
        this.postId = postId;
        this.isAccepted = isAccepted;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
