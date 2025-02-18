package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.model.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static volatile AuthenticationService authenticationService = null;

    private AuthenticationService() {
        logger.info("AuthenticationService created");
    }

    public static AuthenticationService getInstance() {
        if (authenticationService == null) {
            synchronized (AuthenticationService.class) {
                if (authenticationService == null) {
                    authenticationService = new AuthenticationService();
                }
            }
        }
        return authenticationService;
    }

    public boolean authenticate(User user) throws AuthenticationException {
        logger.info("authenticating : {}", user);
        boolean isSuccess = UserDao.authenticateUser(user);
        return isSuccess;
    }

    public boolean register(User user) throws AuthenticationException {
        String username = user.getUsername();
        String role = user.getRole();
        logger.info("Registering {} as {}", username, role);
        boolean isUserExists = UserDao.isUserExists(username);
        if (isUserExists) {
            return false;
        }
        boolean isSuceess = UserDao.insertUser(user);
        return isSuceess;
    }

    public void logOut(HttpSession session) {
        if (session != null) {
            logger.info("{} Logged out of session", session.getId());
            session.invalidate();
        }
    }
}
