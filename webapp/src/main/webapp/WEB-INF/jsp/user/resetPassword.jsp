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
    <h1><spring:message code="user.resetPassword.header" /> </h1>
    <p><spring:message code="user.resetPassword.validatedEmail.request"/></p>

    <c:url value="/user/resetPassword" var="action"/>
    <form:form modelAttribute="resetPasswordForm" action="${action}" method="post">

        <c:set var="emailError"><form:errors path="email"/></c:set>
        <form:errors path="email" element="p" cssClass="error" cssStyle="color:red;"/>
        <form:label path="email">
            <spring:message code="user.resetPassword.email" var="email"/>
            <c:choose>
                <c:when test="${not empty emailError}">
                    <span class="uk-form-icon icon-error" uk-icon="icon: info"></span>
                    <form:input class="uk-input uk-form-danger" path="email" placeholder="${email}" />
                </c:when>
                <c:otherwise>
                    <span class="uk-form-icon" uk-icon="icon: info"></span>
                    <form:input class="uk-input" path="email" placeholder="${email}" />
                </c:otherwise>
            </c:choose>
        </form:label>

        <input type="submit" value="Send" />

    </form:form>

</body>
</html>
