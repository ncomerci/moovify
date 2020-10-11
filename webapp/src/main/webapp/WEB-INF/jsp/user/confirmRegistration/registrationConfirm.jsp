<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="mail.emailConfirmation"/>></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/components/countdownRedirect.js"/>"></script>

    <sec:authorize access="isAuthenticated()">
        <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    </sec:authorize>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<c:if test="${success}" >
    <div class="uk-text-center uk-margin-auto uk-padding-large">
        <h1 class="uk-h1">
            <spring:message code="mail.registrationConfirm.title" arguments="${loggedUser.name}"/>
        </h1>
        <p class="uk-text-bold uk-text-lead">
            <spring:message code="mail.registrationConfirm.body"/>
        </p>
        <div class="uk-container-xsmall uk-flex uk-margin-auto">
            <div class="uk-width-1-2 uk-padding-small uk-padding-remove-vertical">
                <a class="uk-button uk-button-primary uk-border-rounded extended-button" href="<c:url value="/"/>">
                    <spring:message code="mail.registrationConfirm.goHome"/>
                </a>
            </div>
            <div class="uk-width-1-2 uk-padding-small uk-padding-remove-vertical">
                <a class="uk-button uk-button-primary uk-border-rounded extended-button" href="<c:url value="/post/create"/>">
                    <spring:message code="mail.registrationConfirm.createPost"/>
                </a>
            </div>
        </div>
        <p class="uk-text-bold">
            <spring:message code="mail.registrationConfirm.countdown"/>
            <span id="countdown" data-redirect-url="<c:url value="/"/>">10</span>
        </p>
    </div>
</c:if>

<c:if test="${!success}" >
<div class="uk-text-center uk-margin-auto uk-padding-large">
    <h1 class="uk-h1">
        <spring:message code="mail.errorResendEmail"/>
    </h1>
    <p class="uk-text-bold uk-text-lead">
        <spring:message code="mail.errorResendEmail.body"/>
        <a href="<c:url value="/user/resendConfirmation"/>">
            <spring:message code="mail.errorResendEmail.resendEmail"/>
        </a>
    </p>
<div>
</c:if>

</body>
</html>
