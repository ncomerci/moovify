<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.pageTitle" arguments="${query}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <form method="get" action="<c:url value="/search/movies/"/>">

        <c:set var="query" value="${query}" scope="request" />
        <c:set var="currentSearch" value="1" scope="request" />
        <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>
    </form>
</main>
</body>
</html>
