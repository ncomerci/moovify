<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Resend Confirmation Email</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <p><c:out value="${user.name}" />, tu email de confirmacion ha sido reenviado a la casilla <c:out value="${user.email}" /> exitosamente</p>
</body>
</html>
