/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.utility;

public class Constants {

    public static class Error {
        public static final String REGEX_ERROR = "Password should contain at least one digit,one uppercase letter,one lowercase,one special character";
        public static final String PASSWORD_ERROR = "password cannot be empty";
        public static final String ERROR = "ERROR";
        public static final String INTERNAL_ERROR = "Internal error";
        public static final String USERNAME_ERROR = "username cannot be empty";
    }

    public static class Queries {

        public static class Bid {
            public static final String BID_USERNAME_BY_POSTID = "SELECT bidder_username FROM bid WHERE post_id = ? AND isAccepted = FALSE";
            public static final String INSERT_BID = "INSERT INTO bid (post_id, bidder_username) VALUES (?, ?)";
            public static final String ADD_BIDER = "UPDATE bid SET isAccepted = TRUE WHERE post_id = ? AND bidder_username = ?;";
            public static final String ALL_ACCEPTED_BID = "SELECT * FROM bid WHERE bidder_username = ? AND isAccepted = TRUE;";
            public static final String BID_BY_ID = "SELECT * FROM bid WHERE bidder_username = ? AND post_id = ?";
        }

        public static class Post {
            public static final String ALL_POSTS_BY_DESC = "select * from posts order by created_at desc";
            public static final String ADD_POST = "INSERT INTO posts (title, description, author_username) VALUES (?, ?, ?)";
            public static final String POST_BY_ID = "SELECT * FROM posts WHERE id = ?";
            public static final String DELETE_POST_BY_ID = "DELETE FROM posts WHERE id = ?";
            public static final String UPDATE_POST_BY_ID = "UPDATE posts SET title = ?, description = ? WHERE id = ?";
        }

        public static class User {
            public static final String INSERT_USER = "INSERT INTO users (username, password, role, salt) VALUES (?, ?, ?, ?)";
            public static final String FIND_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
            public static final String GET_ALL_USER = "SELECT * FROM users";
            public static final String BLOCK_USER = "UPDATE users SET isBlocked = ? WHERE username = ?";
            public static final String CHECK_BLOCK_STATUS_BY_USERNAME = "select * from users where username = ? and isBlocked = TRUE";
        }
    }

    public static class Constant {
        /// error messages for log
        public static final String PASSWORD_NOT_MATCH = "Passwords do not match";
        public static final String USER_EXISTS = "User already exists!";
        public static final String ACCOUNT_BLOCKED = "Account has been blocked";
        public static final String SUCCESS = "success";
        public static final String INVALID_PASSWORD = "Invalid Password!! Try again";
        public static final String ALREADY_REGISTERED = "Already registered!";
        public static final String UNAUTHORIZED = "Unauthorized!";
        public static final String ALREADY_ADDED = "Already added!";
        public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    }
}
