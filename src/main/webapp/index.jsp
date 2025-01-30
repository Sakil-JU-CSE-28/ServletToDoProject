<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login Form</title>
</head>
<body>
<h1>Welcome To TaskBazaar</h1>
<br/>

<!-- Login Form -->
<form action="login" method="POST">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required>
    <br/><br/>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required>
    <br/><br/>

    <button type="submit">Login</button>
</form>

<!-- Register Button -->
<br/><br/>
<a href="register.jsp">
    <button type="button">Register</button>
</a>

</body>
</html>
