package com.example.taskbazaar.query;

public class Queries {

    public static final String SELECT_PASSWORD_BY_USERNAME = "SELECT password, salt FROM users WHERE username = ?";
    public static final String INSERT_USER = "INSERT INTO users (username, password, role, salt) VALUES (?, ?, ?, ?)";
    public static final String USER_ROLE_BY_USERNAME = "SELECT role FROM users WHERE username = ?";
    public static final String POSTID_BY_AUTHOR = "SELECT * FROM posts WHERE author_username = ? AND id = ?";
    public static final String BID_USERNAME_BY_POSTID = "SELECT username FROM bid WHERE postId = ?";
    public static final String INSERT_BID = "INSERT INTO bid (postId, username) VALUES (?, ?)";
    public static final String ALL_POSTS_BY_DESC  = "select * from posts order by created_at desc";
    public static final String ADD_BIDER = "INSERT INTO acceptedbid(postId, buyer_username, worker_username) VALUES(?, ?, ?)";
    public static final String POST_ID_BY_USER = "SELECT postId FROM bid WHERE username = ?";
    public static final String ADD_POST = "INSERT INTO posts (title, description, author_username) VALUES (?, ?, ?)";
    public static final String  ORDER_BY_WORKER_USERNAME = "select * from acceptedbid where worker_username = ?";
    public static final String EXIST_IN_ACCEPTED = "SELECT COUNT(*) FROM acceptedbid WHERE postId = ?";
    public static final String EXIST_BID = "SELECT COUNT(*) FROM bid WHERE username = ?";
    public static final String POST_BY_ID = "SELECT * FROM posts WHERE id = ?";
    public static final String DELETE_POST_BY_ID = "DELETE FROM posts WHERE id = ?";
    public static final String UPDATE_POST_BY_ID = "UPDATE posts SET title = ?, description = ? WHERE id = ?";
    public static final String FIND_USER_BY_USERNAME = "SELECT COUNT(*) FROM users WHERE username = ?";

}