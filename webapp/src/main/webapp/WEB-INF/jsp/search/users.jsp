<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.users.pageTitle"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/users/" var="action"/>
    <form:form modelAttribute="searchUsersForm" method="get" action="${action}">
        <section id="search-controllers">
            <c:set var="query" value="${query}" scope="request" />
            <c:set var="currentSearch" value="2" scope="request" />
            <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>
        </section>

        <section id="search-results" class="uk-flex uk-flex-wrap">
            <c:if test="${empty users.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.user.userNotFound"/> </h1>
            </c:if>
            <c:forEach items="${users.results}" var="user">
                <div class="uk-width-1-1">
                    <div class="uk-flex">
                        <div class="uk-width-expand uk-margin-small-top">
                            <a href="<c:url value="/user/${user.id}"/>">
                                <c:out value="${user.username}"/>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </section>

        <c:if test="${not empty users.results}">
            <c:set var="collection" value="${users}" scope="request"/>
            <c:url var="baseURL" value="/search/users/" scope="request">
                <c:param name="query" value="${searchUsersForm.query}"/>
            </c:url>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </c:if>
    </form:form>
</main>
</body>
</html>