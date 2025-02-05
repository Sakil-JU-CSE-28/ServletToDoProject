package com.example.taskbazaar.service;

import com.example.taskbazaar.model.User;
import com.example.taskbazaar.query.Queries;
import java.sql.Connection;
import java.sql.PreparedStatement;
import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.bytesToHex;
import static com.example.taskbazaar.utility.SaltGenerator.generateSalt;

public class RegisterService {
    public boolean register(User user) throws Exception {
        Connection connection = DbConnection.getConnection();
        // Generate a salt
        byte[] salt = generateSalt();

        // Hash the password with the salt
        String hashedPassword = hashPassword(user.getPassword(), salt);

        // Convert the salt to a string for storage
        String saltString = bytesToHex(salt);

        String username = user.getUsername();
        String role = user.getRole();

        try (PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_USER)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, role);
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();
        }

        return true;
    }
}
