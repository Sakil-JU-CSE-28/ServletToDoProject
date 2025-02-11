package com.example.taskbazaar.service;

import com.example.taskbazaar.model.User;
import com.example.taskbazaar.query.Queries;
import com.example.taskbazaar.utility.TaskBazaarLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import static com.example.taskbazaar.utility.HashGenerator.hashPassword;
import static com.example.taskbazaar.utility.HexByteConverter.bytesToHex;
import static com.example.taskbazaar.utility.SaltGenerator.generateSalt;

public class RegisterService {

    private static RegisterService registerService = null;
    private static final Logger logger = TaskBazaarLogger.getLogger();

    private RegisterService() {
        logger.info("RegisterService created");
    }

    public static RegisterService getInstance() {
        return registerService==null?registerService=new RegisterService():registerService;
    }

    public boolean register(User user) throws Exception {
        Connection connection = DbConnectionService.getConnection();
        String username = user.getUsername();
        String role = user.getRole();
        logger.info("Registering as in register service...." + username + " " + role);
        try(PreparedStatement preparedStatement = connection.prepareStatement(Queries.FIND_USER_BY_USERNAME)){
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = resultSet.next() ? resultSet.getInt(1) : 0;
            logger.info("total User found with this username " + count);
            if(count > 0){
                logger.info("User already exists");
                return false;
            }
        }
        catch (Exception e) {
           throw new Exception(e);
        }

        // Generate a salt
        byte[] salt = generateSalt();

        // Hash the password with the salt
        String hashedPassword = hashPassword(user.getPassword(), salt);

        // Convert the salt to a string for storage
        String saltString = bytesToHex(salt);

        try (PreparedStatement preparedStatement = connection.prepareStatement(Queries.INSERT_USER)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, role);
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();
            logger.info("User successfully registered in service");
        }
        catch (Exception e) {
            logger.info("Eligible but Error in registering user ....."+ e.getMessage());
            throw new Exception(e);
        }

        return true;

    }
}
