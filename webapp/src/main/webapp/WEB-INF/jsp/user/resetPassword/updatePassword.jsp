<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.updatePassword.title" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div>
    <h1 class="uk-margin-left"><spring:message code="user.updatePassword.success"/></h1>
</div>
<c:url value="/user/updatePassword" var="action"/>
<form:form modelAttribute="updatePasswordForm" action="${action}" method="post">

    <form:errors path="password" element="p" cssClass="error"/>
    <div class="uk-grid-small uk-margin-auto" uk-grid>
        <div class="uk-width-1-2@s">
            <c:set var="passwordError"><form:errors path="password"/></c:set>
            <div class="uk-inline">
                <form:label path="password">
                    <spring:message code="user.updatePassword.password" var="password"/>
                    <c:choose>
                        <c:when test="${not empty passwordError}">
                            <span class="uk-form-icon icon-error" uk-icon="icon: info"></span>
                            <form:password class="uk-input uk-form-danger" path="password" placeholder="${password}" />
                        </c:when>
                        <c:otherwise>
                            <span class="uk-form-icon" uk-icon="icon: info"></span>
                            <form:password class="uk-input" path="password" placeholder="${password}" />
                        </c:otherwise>
                    </c:choose>
                </form:label>
            </div>
        </div>
    </div>
    <form:hidden path="token" />

    <div class="uk-margin-top uk-margin-left">
        <input class="uk-button uk-button-primary uk-border-rounded" type="submit" value="<spring:message code="user.updatePassword.send"/>" />
    </div>

</form:form>

</body>
</html>
