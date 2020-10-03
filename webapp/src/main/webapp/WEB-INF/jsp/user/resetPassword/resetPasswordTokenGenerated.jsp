<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.resetPasswordTokenGenerated.title" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <div class="uk-container uk-text-center uk-margin">
        <h2 class="userTitle" style="white-space: pre-line"> <spring:message code="user.resetPasswordTokenGenerated.body" arguments="${loggedUser.email}" /></h2>
    </div>

</body>
</html>