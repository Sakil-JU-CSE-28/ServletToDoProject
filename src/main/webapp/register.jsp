<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 1/28/25
  Time: 8:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Register Form</title>
</head>
<body>
<h1>Register</h1>
<br/>

<!-- Registration Form -->
<form action="register" method="POST">
  <label for="username">Username:</label>
  <input type="text" id="username" name="username" required>
  <br/><br/>

  <label for="role">Role:</label>
  <select id="role" name="role" required>
    <option value="admin">admin</option>
    <option value="freelancer">freelancer</option>
    <option value="buyer">buyer</option>
    <!-- You can add more roles as needed -->
  </select>
  <br/><br/>

  <label for="password">Password:</label>
  <input type="password" id="password" name="password" required>
  <br/><br/>

  <label for="confirmPassword">Confirm Password:</label>
  <input type="password" id="confirmPassword" name="confirmPassword" required>
  <br/><br/>

  <button type="submit">Register</button>
</form>

<br/>
<a href="index.jsp">Back to Log in</a>
</body>
</html>
