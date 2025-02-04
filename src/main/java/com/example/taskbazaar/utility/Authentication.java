package com.example.taskbazaar.utility;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.query.Queries;


import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.hexToBytes;

public class Authentication {

    public static boolean authenticate(User user) throws SQLException, NoSuchAlgorithmException {

        Connection connection;
        UserDao userDao;

        try {
            userDao = UserDao.getInstance();
            connection = userDao.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String userName = user.getUsername();
        // Retrieve user information from the database
        PreparedStatement preparedStatement = connection.prepareStatement(Queries.SELECT_PASSWORD_BY_USERNAME);
        preparedStatement.setString(1, userName);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String storedHashedPassword = resultSet.getString("password");
            String storedSalt = resultSet.getString("salt");

            // Convert the stored salt to bytes
            byte[] salt = hexToBytes(storedSalt);

            // Hash the input password with the stored salt
            String inputHashedPassword = hashPassword(user.getPassword(), salt);
            resultSet.close();
            // Compare the hashed input password with the stored password
            return storedHashedPassword.equals(inputHashedPassword);
        }
        resultSet.close();
        return false; // User not found
    }

}
