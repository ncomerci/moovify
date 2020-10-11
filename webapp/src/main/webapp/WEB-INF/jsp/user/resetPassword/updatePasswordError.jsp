<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.updatePasswordError.title" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-text-center uk-margin-auto uk-padding-large">
    <h1 class="uk-h1">
        <spring:message code="user.updatePassword.failure.title"/>
    </h1>
    <p class="uk-text-bold uk-text-lead">
        <spring:message code="user.updatePassword.failure.body"/>
        <a href="<c:url value="/login"/>">
            <spring:message code="user.updatePassword.failure.loginPage"/>
        </a>
    </p>
</div>

</body>
</html>
