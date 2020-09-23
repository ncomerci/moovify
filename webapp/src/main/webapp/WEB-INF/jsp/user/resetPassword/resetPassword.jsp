<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.resetPassword.title" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div>
    <h1 class="uk-margin-left"><spring:message code="user.resetPassword.header" /> </h1>
    <p class="uk-margin-left"><spring:message code="user.resetPassword.validatedEmail.request"/></p>
</div>
<c:url value="/user/resetPassword" var="action"/>
<form:form modelAttribute="resetPasswordForm" action="${action}" method="post" >
    <div class="uk-grid-small uk-margin-auto" uk-grid>
        <div class="uk-width-1-2@s">
            <c:set var="emailError"><form:errors path="email"/></c:set>
            <form:errors path="email" element="p" cssClass="error" cssStyle="color:red;"/>
            <form:label path="email">
                <spring:message code="user.resetPassword.email" var="email"/>
                <c:choose>
                    <c:when test="${not empty emailError}">
                        <span class="uk-form-icon icon-error"  uk-icon="icon: mail"></span>
                        <form:input class="uk-input uk-form-danger" path="email" placeholder="${email}" />
                    </c:when>
                    <c:otherwise>
                        <span class="uk-form-icon icon-error"  uk-icon="icon: mail"></span>
                        <form:input class="uk-input" path="email" placeholder="${email}" />
                    </c:otherwise>
                </c:choose>
            </form:label>
        </div>
    </div>
    <div class="uk-margin-top uk-margin-left">
        <input class="uk-button uk-button-primary uk-border-rounded" type="submit" value="<spring:message code="user.resetPassword.send"/>" />
    </div>
</form:form>

</body>
</html>
