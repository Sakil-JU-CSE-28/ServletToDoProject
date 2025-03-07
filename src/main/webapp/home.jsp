<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 1/29/25
  Time: 4:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>

<%
    // Redirect if user is not logged in
    if (session.getAttribute("username") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Page</title>
    <!-- Add Bootstrap for styling and navbar layout -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<!-- Navbar Section -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="home">TaskBazaar</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ml-auto">
                <c:if test="${sessionScope.role == 'buyer'}">
                    <li class="nav-item">
                        <a class="nav-link" href="createPost.jsp">Post_Job</a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.role == 'freelancer'}">
                    <li class="nav-item">
                        <a class="nav-link" href="works">Work_History</a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.role == 'admin'}">
                    <li class="nav-item">
                        <a class="nav-link" href="admin">Admin_Dashboard</a>
                    </li>
                </c:if>
                <li class="nav-item">
                    <a class="nav-link" href="logout">Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- Post Listing Section -->
<div class="container mt-4">
    <h2>Latest Posts</h2>

    <!-- Dynamically display posts -->

    <c:if test="${not empty posts}">
        <c:forEach var="post" items="${posts}">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">${post.title()}</h5>
                    <p class="card-text">${post.content()}</p>
                    <form action="view" method="post" style="display:inline;">
                        <input type="hidden" name="postId" value="${post.postId()}">
                        <button type="submit" class="btn btn-primary">View Post</button>
                    </form>
                    <c:if test="${sessionScope.role == 'admin'}">
                        <form action="delete" method="post">
                            <input type="hidden" name="postId" value="${post.postId()}">
                            <button type="submit" class="delete-btn"
                                    onclick="return confirm('Are you sure you want to delete this post?');">Delete
                            </button>
                        </form>
                    </c:if>

                </div>
            </div>
        </c:forEach>
    </c:if>
    <c:if test="${empty posts}">
        <p>No posts available.</p>
    </c:if>


</div>

<!-- Footer Section -->
<footer class="bg-light text-center py-4 mt-5">
    <p>&copy; 2025 TaskBazaar. All rights reserved.</p>
</footer>

<!-- Add Bootstrap JS and dependencies -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>

