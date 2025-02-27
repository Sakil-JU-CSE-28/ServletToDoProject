<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 1/29/25
  Time: 8:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.example.taskbazaar.dto.PostDTO" %>


<%
    // Redirect if user is not logged in
    if (session.getAttribute("username") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>


<%
    PostDTO post = (PostDTO) session.getAttribute("post");
    String postId = (String) session.getAttribute("postId");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Post</title>
    <link rel="stylesheet" type="text/css" href="style/viewPostStyle.css">
</head>
<body>
<div class="container">
    <h1>Post Details</h1>
    <div class="post-title">
        <%= post.title() %>
    </div>
    <div class="post-description">
        <%= post.content() %>
    </div>

    <c:if test="${sessionScope.role=='buyer'}">
        <div class="bid-section">
            <form action="editPost.jsp" method="post">
                <input type="hidden" name="postId" value="<%= postId %>"/>
                <button class="button" type="submit">Edit Post</button>
            </form>
        </div>
    </c:if>

    <c:if test="${sessionScope.role=='freelancer'}">
        <!-- Form for placing a bid -->
        <div class="bid-section">
            <form action="bid" method="post">
                <input type="hidden" name="postId" value="<%= postId %>"/>
                <button class="button" type="submit">Place a Bid</button>
            </form>
        </div>
    </c:if>

    <c:if test="${sessionScope.role=='buyer'}">
        <!-- Accept Bid Button (assuming you want a separate button to accept a bid) -->
        <div class="bid-section">
            <form action="accept" method="post">
                <input type="hidden" name="postId" value="<%= postId %>"/>
                <button class="button bid-button" type="submit">Accept Bid</button>
            </form>
        </div>
    </c:if>
</div>
</body>
</html>
