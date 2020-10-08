<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="email.emailConfirmation"/>></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

    <sec:authorize access="isAuthenticated()">
        <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    </sec:authorize>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<c:if test="${success}" >
    <div class="uk-text-center uk-margin-auto">
        <h2 class="userTitle"> <spring:message code="email.registrationConfirm" arguments="${loggedUser.name}"/></h2>
    </div>
</c:if>

<c:if test="${!success}" >
    <h2 class="userTitle"><spring:message code="email.errorResendEmail"/>  <a href="<c:url value="/user/resendConfirmation" /> "><spring:message code="user.profile.ResendEmail"/></a></h2>
</c:if>
<div>

</div>
</body>
</html>
