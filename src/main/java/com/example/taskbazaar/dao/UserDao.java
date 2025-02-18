package com.example.taskbazaar.dao;

import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.DbConnectionService;
import com.example.taskbazaar.utility.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.bytesToHex;
import static com.example.taskbazaar.utility.HexByteConverter.hexToBytes;
import static com.example.taskbazaar.utility.SaltGenerator.generateSalt;

public class UserDao {
    public static synchronized boolean isUserExists(String username) throws AuthenticationException {
        try (Connection connection = DbConnectionService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Queries.FIND_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = resultSet.next() ? resultSet.getInt(1) : 0;
            if (count > 0) {
                throw new AuthenticationException("user already exists");
            }
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
        return false;
    }
    public static synchronized boolean insertUser(User user) throws AuthenticationException {
        try (Connection connection = DbConnectionService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_USER)) {

            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(user.getPassword(), salt);
            String saltString = bytesToHex(salt);

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
        return true;
    }

    public static synchronized boolean authenticateUser(User user) throws AuthenticationException {
        String userName = user.getUsername();
        try (Connection connection = DbConnectionService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Queries.SELECT_PASSWORD_BY_USERNAME)) {
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
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Database error occurred:: " + e.getMessage());
        } catch (Exception e) {
            throw new AuthenticationException("Unexpected error occurred:: " + e.getMessage());
        }
        return false;
    }

    public static synchronized String getUserRole(String username) throws SQLException {
        String role;
        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.USER_ROLE_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            role = resultSet.getString("role");

        } catch (Exception e) {
            throw new SQLException(e);
        }
        return role;
    }

}