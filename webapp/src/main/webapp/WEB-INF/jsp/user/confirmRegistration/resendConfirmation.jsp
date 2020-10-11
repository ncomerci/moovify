<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="mail.resendConfirmationEmail"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

    <sec:authorize access="isAuthenticated()">
        <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    </sec:authorize>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <div class="uk-text-center uk-margin-auto uk-padding-large">
        <h1 class="uk-h1">
            <spring:message code="mail.resendConfirmation.title" arguments="${loggedUser.name}"/>
        </h1>
        <p class="uk-text-bold">
            <spring:message code="mail.resendConfirmation.body" arguments="${loggedUser.email}"/>
        </p>
    </div>
</body>
</html>
