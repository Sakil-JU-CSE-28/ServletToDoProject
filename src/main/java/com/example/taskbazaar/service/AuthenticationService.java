/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.DbException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

import static com.example.taskbazaar.utility.Common.hashPassword;


public class AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static volatile AuthenticationService authenticationService = null;
    private final UserService userService = UserService.getInstance();

    private AuthenticationService() {
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


    public boolean authenticate(UserDTO user) throws NoSuchAlgorithmException, DbException {
        logger.info("authenticating user :: {}", user);
        String userName = user.username();
        UserDTO storedUser = userService.getByUsername(userName);
        String storedHashedPassword = storedUser.password();
        String storedSalt = storedUser.salt();
        String inputPassword = user.password();
        logger.info("Generating hash password");
        String inputHashedPassword = hashPassword(inputPassword, storedSalt);
        return storedHashedPassword.equals(inputHashedPassword);
    }

    public boolean register(UserDTO candidate) throws DbException {
        String username = candidate.username();
        String role = candidate.role();
        logger.info("Registering {} as {}", username, role);
        return userService.insert(candidate);
    }

    public void logOut(HttpSession session) {
        if (session != null) {
            logger.info("{} Logged out of session", session.getId());
            session.invalidate();
        }
    }
}