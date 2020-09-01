<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset=utf-8>
    <title>Hello World!</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>

<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <h2>Hello World!! <c:out value="${user.title}"/>!</h2>
</body>

</html>