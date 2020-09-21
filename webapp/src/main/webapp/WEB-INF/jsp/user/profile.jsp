<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Profile</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <p><c:out value="${user.creationDate}" /></p>
    <p><c:out value="${user.username}" /></p>
    <p><c:out value="${user.password}" /></p>
    <p><c:out value="${user.name}" /></p>
    <p><c:out value="${user.email}" /></p>

    <sec:authorize access="hasRole('ADMIN')" >
        <p>Sos un admin!!!</p>
    </sec:authorize>

    <sec:authorize access="hasRole('NOT_VALIDATED')" >
        <p>Por favor no olvides de validar tu cuenta mediante el mail que te mandamos!</p>
        <a href="<c:url value="/user/resendConfirmation" /> ">Reenviar mail</a>
    </sec:authorize>

</body>
</html>
