<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="email.resendConfirmationEmail"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <div class="uk-text-center uk-margin-auto">
        <h2 class="userTitle"> <spring:message code="email.resendMessage" arguments="${loggedUser.name}, ${loggedUser.email}"/></h2>
    </div>
</body>
</html>
