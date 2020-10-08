<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="adminPanel.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <link rel="stylesheet" href="<c:url value="/resources/css/extraStyle.css"/>"/>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
    <script src="<c:url value="/resources/js/adminPanel/mainController.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>
<main class="uk-container-small uk-margin-auto uk-padding-small">
        <form action="<c:url value="/admin/deleted/users"/>" method="get">
            <section id="controllers">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSelection" value="2" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/adminPanel/deleted/mainController.jsp"/>
            </section>

            <section id="search-results" class="uk-flex uk-flex-wrap">
                <c:if test="${empty users.results}">
                    <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.notFound" arguments="users"/> </h1>
                </c:if>
                <c:forEach items="${users.results}" var="user">
                    <div class="uk-width-1-1 uk-margin-small-bottom">
                        <div class="uk-flex">
                            <sec:authorize access="hasRole('ADMIN')">
                                <c:if test="${!user.enabled}">
                                    <button class="uk-button uk-button-default uk-border-rounded uk-margin-auto-vertical uk-margin-right restore-btn"
                                            data-id="${user.id}"
                                            type="button"
                                    >
                                        <spring:message code="adminPanel.restore"/>
                                    </button>
                                </c:if>
                            </sec:authorize>
                            <div class="uk-width-expand uk-margin-auto-vertical">
                                <a href="<c:url value="/user/${user.id}"/>" <c:out value="${user.admin ? 'class=uk-text-primary uk-text-middle': ''}"/>>
                                    <c:out value="${user.username}"/>
                                    <c:if test="${user.admin}">
                                        <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                                    </c:if>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </section>

            <c:if test="${not empty users.results}">
                <c:set var="collection" value="${users}" scope="request"/>
                <c:url var="baseURL" value="/admin/deleted/users" scope="request">
                    <c:param name="query" value="${query}"/>
                </c:url>
                <c:set var="numberOfInputs" value="${2}" scope="request"/>
                <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
            </c:if>
        </form>

    <form id="restore-form" method="post" action="<c:url value="/user/restore"/>">
    </form>
</main>
</body>
</html>
