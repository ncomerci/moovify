<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.passwordResetSuccess.title" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div class="uk-text-center uk-margin-auto">
    <h2 class="userTitle"><spring:message code="user.passwordResetSuccess" arguments="${loggedUser.name}" /></h2>
</div>

</body>
</html>
