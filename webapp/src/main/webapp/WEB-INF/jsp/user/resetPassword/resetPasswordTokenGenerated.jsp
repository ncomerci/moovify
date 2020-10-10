<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.resetPasswordTokenGenerated" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <div class="uk-text-center uk-margin-auto uk-padding-large">
        <h1 class="uk-h1">
            <spring:message code="user.resetPasswordTokenGenerated.title" arguments="${loggedUser.email}"/>
        </h1>
        <p class="uk-text-light uk-text-lead">
            <spring:message code="mail.resetPasswordTokenGenerated.body" arguments="${loggedUser.email}"/>
        </p>
    </div>
</body>
</html>
