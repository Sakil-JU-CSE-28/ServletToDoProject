package com.example.taskbazaar.service;

import com.example.taskbazaar.model.User;
import com.example.taskbazaar.query.Queries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.hexToBytes;

public class AuthenticationService {

    private static AuthenticationService authenticationService = null;

    private AuthenticationService() {}

    public static AuthenticationService getInstance() {
        return authenticationService==null ? authenticationService = new AuthenticationService() : authenticationService;
    }

    public  boolean authenticate(User user) throws Exception {

        Connection connection = DbConnectionService.getConnection();

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
