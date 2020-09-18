<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>User Profile</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <p><c:out value="${user.creationDate}" /></p>
    <p><c:out value="${user.username}" /></p>
    <p><c:out value="${user.password}" /></p>
    <p><c:out value="${user.name}" /></p>
    <p><c:out value="${user.email}" /></p>


</body>
</html>
