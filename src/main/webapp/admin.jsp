<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/5/25
  Time: 9:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.taskbazaar.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Panel - Manage Posts</title>
    <link rel="stylesheet" type="text/css" href="style/adminStyle.css">
</head>
<body>
<h2>Admin Panel - Manage Users</h2>

<table>
    <tr>
        <th>Username</th>
        <th>Role</th>
        <th>IsBlocked</th>
        <th>Action</th>
    </tr>
    <%
        List<User> users = (List<User>) request.getAttribute("users");
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                boolean isBlocked = user.isBlocked(); // Store in a variable to avoid EL issues
    %>
    <tr>
        <td><%= user.getUsername() %></td>
        <td><%= user.getRole() %></td>
        <td><%= isBlocked %></td>
        <td>
            <% if (!isBlocked) { %>
            <form action="block" method="post">
                <input type="hidden" name="username" value="<%= user.getUsername() %>">
                <button type="submit" class="delete-btn"
                        onclick="return confirm('Are you sure you want to block this account?');">
                    Block
                </button>
            </form>
            <% } else { %>
            <form action="unBlock" method="post">
                <input type="hidden" name="username" value="<%= user.getUsername() %>">
                <button type="submit" class="delete-btn"
                        onclick="return confirm('Are you sure you want to unblock this account?');">
                    UnBlock
                </button>
            </form>
            <% } %>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="4">No users available</td>
    </tr>
    <% } %>
</table>
</body>
</html>

