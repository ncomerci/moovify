<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.updatePassword.title" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <link rel="stylesheet" href="<c:url value="/resources/css/extraStyle.css"/>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<c:url value="/user/updatePassword" var="action"/>
<form:form modelAttribute="updatePasswordForm" action="${action}" method="post">
    <div class="uk-container-xsmall uk-margin-auto">
        <h1 class="uk-h1">
            <spring:message code="user.updatePassword.success.title"/>
        </h1>
        <p class="uk-text-light">
            <spring:message code="user.updatePassword.success.body" arguments="${loggedUser.email}"/>
        </p>
        <form:errors path="password" element="p" cssClass="error uk-margin-remove-bottom"/>
        <c:set var="passwordError"><form:errors path="password"/></c:set>
        <div class="uk-inline">
            <form:label path="password">
                <spring:message code="user.updatePassword.password" var="password"/>
            </form:label>
            <span class="uk-form-icon ${not empty passwordError ? 'icon-error' : ''}" uk-icon="icon: info"></span>
            <form:password class="uk-input ${not empty passwordError ? 'uk-form-danger' : ''}" path="password" placeholder="${password}" />
        </div>
        <div class="uk-margin-top uk-flex">
            <div class="uk-margin-auto">
                <input class="uk-button uk-button-primary uk-border-rounded" type="submit" value="<spring:message code="user.updatePassword.send"/>" />
            </div>
        </div>
    </div>
    <form:hidden path="token" />
</form:form>

</body>
</html>
