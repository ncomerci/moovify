<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.passwordResetSuccess.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    <script src="<c:url value="/resources/js/components/countdownRedirect.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-text-center uk-margin-auto uk-padding-large">
    <h1 class="uk-h1">
        <spring:message code="user.passwordResetSuccess"/>
    </h1>
    <div class="uk-container-xsmall uk-flex uk-margin-auto">
        <div class="uk-width-1-2 uk-padding-small uk-padding-remove-vertical">
            <a class="uk-button uk-button-primary uk-border-rounded extended-button" href="<c:url value="/"/>">
                <spring:message code="user.passwordResetSuccess.goHome"/>
            </a>
        </div>
        <div class="uk-width-1-2 uk-padding-small uk-padding-remove-vertical">
            <a class="uk-button uk-button-primary uk-border-rounded extended-button" href="<c:url value="/post/create"/>">
                <spring:message code="user.passwordResetSuccess.createPost"/>
            </a>
        </div>
    </div>
    <p class="uk-text-bold">
        <spring:message code="user.passwordResetSuccess.countdown"/>
        <span id="countdown" data-redirect-url="<c:url value="/"/>">10</span>
    </p>
</div>

</body>
</html>
