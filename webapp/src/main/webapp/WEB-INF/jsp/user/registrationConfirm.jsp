<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Email Confirmation</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <c:if test="${success}" >
        <h1>You have successfully confirmed your account! Thank you for choosing Moovify</h1>
    </c:if>
    <c:if test="${!success}" >
        <h1>The confirmation link was invalid or had already expired :(</h1>
        <p>If you wish to resend the email, you can do it in your <a href="<c:url value="/user/profile" /> ">profile</a></p>
    </c:if>

</body>
</html>
