package com.example.taskbazaar.dao;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.query.Queries;
import com.example.taskbazaar.utility.ConfigLoader;
import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.bytesToHex;
import static com.example.taskbazaar.utility.SaltGenerator.generateSalt;

public class UserDao {

    /*
     * Configuration for Java database connectivity
     * 1. import ---> java.sql.*
     * 2. load and register the driver ---> com.mysql.jdbc.Driver
     * 3. Create connection ---> connection
     * 4. create a statement ----> statement
     * 5. execute the query
     * 6. process the result
     * 7. close
     */


    private String url = ConfigLoader.getProperty("db.url");
    private String user = ConfigLoader.getProperty("db.user");
    private String password = ConfigLoader.getProperty("db.password");
    private String driver = ConfigLoader.getProperty("db.driver");
    private static UserDao singleObject = null;
    private static Connection connection = null;
    private UserDao() throws Exception {
        Class.forName(driver);
    }

    public static UserDao getInstance() throws Exception {
        if (singleObject == null) {
            singleObject = new UserDao();
        }
        return singleObject;
    }

    public Connection connect() throws Exception {
        return connection = DriverManager.getConnection(url, user, password);
    }



    public boolean register(User user) throws SQLException, NoSuchAlgorithmException {
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