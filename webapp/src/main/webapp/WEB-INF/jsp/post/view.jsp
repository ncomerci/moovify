<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: tobias
  Date: 31/8/20
  Time: 17:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <h2>Hello World!! <c:out value="${post.title}"/> <c:out value="${post.id}"/>!</h2>
    <h3>By: <c:out value="${post.email}"/></h3>
    <h6>Word Count: <c:out value="${post.wordCount}"/></h6>
    <h6>Creation Date: <c:out value="${post.creationDate}"/></h6>
    <p><c:out value="${post.body}"/></p>
</body>
</html>
