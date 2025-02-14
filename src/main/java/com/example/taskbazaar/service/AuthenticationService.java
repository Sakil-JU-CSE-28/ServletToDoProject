package com.example.taskbazaar.service;

import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.bytesToHex;
import static com.example.taskbazaar.utility.HexByteConverter.hexToBytes;
import static com.example.taskbazaar.utility.SaltGenerator.generateSalt;

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
        String userName = user.getUsername();
        try (Connection connection = DbConnectionService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constants.SELECT_PASSWORD_BY_USERNAME)) {
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHashedPassword = resultSet.getString("password");
                    String storedSalt = resultSet.getString("salt");

                    // Convert the stored salt to bytes
                    byte[] salt = hexToBytes(storedSalt);

                    // Hash the input password with the stored salt
                    String inputHashedPassword = hashPassword(user.getPassword(), salt);

                    if (storedHashedPassword.equals(inputHashedPassword)) {
                        logger.info("{} authenticated successfully", userName);
                        return true;
                    }
                }
                logger.info("{} not found or incorrect password", userName);
            }
        } catch (SQLException e) {
            logger.error("Database error during authentication for user: {}", userName, e);
            throw new AuthenticationException("Database error occurred:: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user: {}", userName, e);
            throw new AuthenticationException("Unexpected error occurred:: " + e.getMessage());
        }
        return false; // User not found or authentication failed
    }

    public boolean register(User user) throws AuthenticationException {
        String username = user.getUsername();
        String role = user.getRole();
        logger.info("Registering {} as {}", username, role);

        try (Connection connection = DbConnectionService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constants.FIND_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = resultSet.next() ? resultSet.getInt(1) : 0;
            logger.info("{} {} found", count, username);
            if (count > 0) {
                return false;
            }
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }

        try (Connection connection = DbConnectionService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constants.INSERT_USER)) {

            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(user.getPassword(), salt);
            String saltString = bytesToHex(salt);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, role);
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();
            logger.info("{} successfully registered in service", username);
        } catch (Exception e) {
            logger.info("error:: {}", e.getMessage());
            throw new AuthenticationException(e.getMessage());
        }

        return true;
    }

    public void logOut(HttpSession session) {
        if (session != null) {
            logger.info("{} Logged out of session", session.getId());
            session.invalidate();
        }
    }
}
