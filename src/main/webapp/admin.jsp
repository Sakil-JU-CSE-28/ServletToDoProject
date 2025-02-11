<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/5/25
  Time: 9:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.taskbazaar.model.Post" %>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Panel - Manage Posts</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid black;
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .delete-btn {
            background-color: red;
            color: white;
            border: none;
            padding: 5px 10px;
            cursor: pointer;
        }
        .delete-btn:hover {
            background-color: darkred;
        }
    </style>
</head>
<body>
<h2>Admin Panel - Manage Posts</h2>

<table>
    <tr>
        <th>Post ID</th>
        <th>Title</th>
        <th>Content</th>
        <th>Action</th>
    </tr>
    <%
        List<Post> posts = (List<Post>) request.getAttribute("posts");
        if (posts != null && !posts.isEmpty()) {
            for (Post post : posts) {
    %>
    <tr>
        <td><%= post.getId() %></td>
        <td><%= post.getTitle() %></td>
        <td><%= post.getDescription() %></td>
        <td>
            <form action="delete" method="post">
                <input type="hidden" name="postId" value="<%= post.getId() %>">
                <button type="submit" class="delete-btn" onclick="return confirm('Are you sure you want to delete this post?');">Delete</button>
            </form>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="4">No posts available</td>
    </tr>
    <% } %>
</table>
</body>
</html>

