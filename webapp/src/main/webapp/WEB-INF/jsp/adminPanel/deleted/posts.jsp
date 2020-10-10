<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="adminPanel.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
    <script src="<c:url value="/resources/js/adminPanel/mainController.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>
<main class="uk-container-small uk-margin-auto uk-padding-small">

    <form action="<c:url value="/admin/deleted/posts"/>" method="get">
        <section id="controllers">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSelection" value="0" scope="request"/>
            <c:set var="selectedView" scope="request"><spring:message code="admin.deleted.posts"/></c:set>
            <jsp:include page="/WEB-INF/jsp/adminPanel/deleted/mainController.jsp"/>
        </section>
        <section id="search-results" class="uk-margin-top">
            <c:if test="${empty posts.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.posts.postsNotFound"/></h1>
            </c:if>
            <c:set var="posts" value="${posts}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
        </section>
        <c:if test="${not empty posts.results}">
            <c:set var="collection" value="${posts}" scope="request"/>
            <c:url var="baseURL" value="/admin/deleted/posts" scope="request">
                <c:param name="query" value="${query}"/>
            </c:url>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </c:if>
    </form>

    <form id="restore-form" method="post" action="<c:url value="/post/restore"/>">
    </form>
</main>
</body>
</html>
