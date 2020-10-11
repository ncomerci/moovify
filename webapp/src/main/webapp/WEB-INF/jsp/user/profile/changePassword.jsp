<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <sec:authorize access="isAuthenticated()">
        <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    </sec:authorize>
    <title><spring:message code="user.profile.edit.changePassword" arguments="${loggedUser.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body class="min-height-1000">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-container uk-margin-medium-top">
    <c:url value="/user/changePassword" var="action"/>
    <legend class="uk-legend uk-text-uppercase uk-text-center uk-text-bold uk-text-large uk-text-primary"><spring:message code="user.profile.edit.changePassword"/></legend>
    <p class="uk-text-center uk-text-normal uk-text-italic uk-text-bold"><spring:message code="user.profile.changePasswordDesc"/></p>
    <div class="uk-container uk-text-center">
        <%--@elvariable id="userEditForm" type=""--%>
        <form:form modelAttribute="changePasswordForm" class="uk-form-horizontal uk-margin-large" action="${action}" method="post">

        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
            <div class="uk-width-1-3">
                <h3 class="uk-margin-bottom uk-text-left"><spring:message code="user.profile.edit.changePassword" /></h3>
            </div>
            <div class="uk-width-1-2">
                <c:set var="passError"><form:errors path="password"/></c:set>
                <div class="uk-inline">
                    <form:label path="password">
                        <spring:message code="user.profile.edit.newPassword" var="newPassword"/>
                        <span class="uk-form-icon <c:out value="${not empty passError ? 'icon-error':''}"/>" uk-icon="icon: lock"></span>
                        <c:choose>
                            <c:when test="${not empty passError}">
                                <form:password class="uk-input uk-form-danger"  path="password"  placeholder="${newPassword}" />
                            </c:when>
                            <c:otherwise>
                                <form:password class="uk-input"  path="password"  placeholder="${newPassword}" />
                            </c:otherwise>
                        </c:choose>
                    </form:label>
                </div>
                <form:errors path="password" element="p" cssClass="error"/>
                <form:errors element="p" cssClass="error"/>
            </div>
        </div>

        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
            <div class="uk-width-1-3">
                <h3 class="uk-margin-bottom uk-text-left"><spring:message code="user.profile.edit.newRepeatPassword" /></h3>
            </div>
            <div class="uk-width-1-2">
                <c:set var="repPassError"><form:errors path="repeatPassword"/></c:set>
                <div class="uk-inline">
                    <form:label path="repeatPassword">
                        <spring:message code="user.profile.edit.repeatPassword" var="repeatPassword"/>
                        <span class="uk-form-icon <c:out value="${not empty repPassError ? 'icon-error':''}"/>" uk-icon="icon: lock"></span>
                        <c:choose>
                            <c:when test="${not empty repPassError}">
                                <form:password class="uk-input uk-form-danger"  path="repeatPassword" placeholder="${repeatPassword}" />
                            </c:when>
                            <c:otherwise>
                                <form:password class="uk-input"  path="repeatPassword" placeholder="${repeatPassword}" />
                            </c:otherwise>
                        </c:choose>
                    </form:label>
                </div>
                <form:errors path="repeatPassword" element="p" cssClass="error" />
            </div>
            <div class="uk-text-center uk-margin-medium-top">
                <input class="uk-button uk-button-primary uk-border-rounded extended-button" type="submit" value="<spring:message code="user.profile.edit.changePassword"/>" />
            </div>
        </div>

        </form:form>


</body>
</html>