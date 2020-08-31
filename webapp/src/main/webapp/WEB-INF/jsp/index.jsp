<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset=utf-8>
    <title>Hello World!</title>
</head>

<body>
    <h2>Hello World!! <c:out value="${user.username}"/>!</h2>
</body>

</html>