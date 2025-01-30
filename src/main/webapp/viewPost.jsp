<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 1/29/25
  Time: 8:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*, java.sql.*" %>

<%
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setHeader("Expires", "0");

  // Redirect if user is not logged in
  if (session.getAttribute("username") == null) {
    response.sendRedirect("index.jsp");
  }
%>


<%
  // Fetch post ID from the request (assuming the ID is passed in the URL)
  String postId = request.getParameter("id");
  if (postId == null) {
    response.sendRedirect("home.jsp");  // If postId is not passed, redirect
    return;
  }

  // Connect to the database and retrieve the post details
  Connection conn = null;
  PreparedStatement stmt = null;
  ResultSet rs = null;

  try {
    // Database connection (use your own DB settings here)
    String dbURL = "jdbc:mysql://localhost:3306/Test";
    String dbUser = "root";
    String dbPassword = "1234";
    conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

    // SQL query to fetch post details
    String sql = "SELECT * FROM posts WHERE id = ?";
    stmt = conn.prepareStatement(sql);
    stmt.setInt(1, Integer.parseInt(postId));
    rs = stmt.executeQuery();

    if (rs.next()) {
      // Retrieve post details from the result set
      String title = rs.getString("title");
      String description = rs.getString("description");
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>View Post</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f4f4f4;
      margin: 0;
      padding: 0;
    }
    .container {
      width: 60%;
      margin: 50px auto;
      background-color: #fff;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    h1, h2 {
      color: #333;
    }
    .post-title {
      font-size: 24px;
      font-weight: bold;
      color: #2c3e50;
    }
    .post-description {
      font-size: 18px;
      color: #7f8c8d;
      margin-bottom: 20px;
    }
    .button {
      background-color: #3498db;
      color: white;
      padding: 10px 20px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      text-decoration: none;
    }
    .button:hover {
      background-color: #2980b9;
    }
    .bid-section {
      margin-top: 30px;
    }
    .bid-button {
      background-color: #e74c3c;
    }
    .bid-button:hover {
      background-color: #c0392b;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>Post Details</h1>
  <div class="post-title">
    <%= title %>
  </div>
  <div class="post-description">
    <%= description %>
  </div>

  <!-- Form for placing a bid -->
  <div class="bid-section">
    <form action="bid" method="post">
      <input type="hidden" name="postId" value="<%= postId %>" />
      <button class="button" type="submit">Place a Bid</button>
    </form>
  </div>

  <!-- Accept Bid Button (assuming you want a separate button to accept a bid) -->
  <div class="bid-section">
    <form action="accept" method="post">
      <input type="hidden" name="postId" value="<%= postId %>" />
      <button class="button bid-button" type="submit">Accept Bid</button>
    </form>
  </div>
</div>
</body>
</html>

<%
    } else {
      out.println("Post not found.");
    }
  } catch (SQLException e) {
    e.printStackTrace();
  } finally {
    try {
      if (rs != null) rs.close();
      if (stmt != null) stmt.close();
      if (conn != null) conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
%>

