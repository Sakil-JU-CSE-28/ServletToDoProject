package dao;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import model.User;

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


    private String url = "jdbc:mysql://localhost:3306/Test";
    private String user = "root";
    private String password = "1234";

    private static UserDao singleObject = null;
    private static Connection connection = null;
    private UserDao() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public static UserDao getInstance() throws Exception {
        if (singleObject == null) {
            singleObject = new UserDao();
        }
        return singleObject;
    }

    public Connection connect() throws Exception {
        return connection = DriverManager.getConnection(url,user,password);
    }

    public boolean authenticate(User user) throws SQLException, NoSuchAlgorithmException {

        String userName = user.getUsername();
        // Retrieve user information from the database
        String query = "SELECT password, salt FROM users WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
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

    public boolean register(User user, Statement statement) throws SQLException, NoSuchAlgorithmException {
        // Generate a salt
        byte[] salt = generateSalt();

        // Hash the password with the salt
        String hashedPassword = hashPassword(user.getPassword(), salt);

        // Convert the salt to a string for storage
        String saltString = bytesToHex(salt);

        String username = user.getUsername();
        String role = user.getRole();

        // Corrected SQL query (placeholders without quotes)
        String query = "INSERT INTO users (username, password, role, salt) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, role);
            preparedStatement.setString(4, saltString);

            // Execute the query
            preparedStatement.executeUpdate();
        }

        return true;
    }


    private String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Add the salt to the password
        digest.update(salt);

        // Hash the password with the salt
        byte[] hashedBytes = digest.digest(password.getBytes());

        // Convert the hashed bytes to a hex string
        return bytesToHex(hashedBytes);
    }

    private byte[] generateSalt() throws NoSuchAlgorithmException {
        // Create a secure random number generator
        SecureRandom random = new SecureRandom();

        // Generate a 16-byte salt
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

}
