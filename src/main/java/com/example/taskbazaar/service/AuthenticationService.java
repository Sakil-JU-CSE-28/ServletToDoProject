/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.model.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

import static com.example.taskbazaar.utility.Common.hashPassword;


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

    public boolean authenticate(User user) throws AuthenticationException, NoSuchAlgorithmException {

        String userName = user.getUsername();
        String storedHashedPassword = UserDao.getPasswordByUserName(userName);
        String storedSalt = UserDao.getSaltByUsername(userName);
        String inputPassword = user.getPassword();
        logger.info("stored salt , input password:: {} , {}", storedSalt, inputPassword);
        String inputHashedPassword = hashPassword(inputPassword, storedSalt);

        return storedHashedPassword.equals(inputHashedPassword);
    }

    public boolean register(User user) throws AuthenticationException {
        String username = user.getUsername();
        String role = user.getRole();
        logger.info("Registering {} as {}", username, role);
        int totalUserInstance = UserDao.getUserCountByUsername(username);
        if (totalUserInstance > 0) {
            throw new AuthenticationException("User already exists");
        }
        return UserDao.insertUser(user);
    }

    public void logOut(HttpSession session) {
        if (session != null) {
            logger.info("{} Logged out of session", session.getId());
            session.invalidate();
        }
    }
}
