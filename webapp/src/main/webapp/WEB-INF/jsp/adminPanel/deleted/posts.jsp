<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>

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

            <div class="uk-flex uk-flex-wrap">
                <c:forEach items="${posts.results}" var="post">
                    <div class="uk-width-1-1">
                        <div class="uk-flex">
                                <button class="uk-button uk-button-default uk-border-rounded uk-margin-auto-vertical uk-margin-right restore-btn"
                                        data-id="${post.id}"
                                        type="button"
                                >
                                    <spring:message code="adminPanel.restore"/>
                                </button>
                            <div class="uk-width-expand uk-margin-small-top">
                                <a><c:out value="${post.title}"/></a>
                                <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                                    <c:choose>
                                        <c:when test="${post.user.enabled}">
                                            <c:set var="name" value="${post.user.name}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="name"><spring:message code="user.notEnabled.name"/></c:set>
                                        </c:otherwise>
                                    </c:choose>
                                    <spring:message code="postDisplay.meta.description" arguments="${post.category.name}, ${name}"/>
                                    <c:if test="${post.user.admin && post.user.enabled}">
                                        <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                                    </c:if>
                                    <spring:message code="postDisplay.meta.votes" arguments="${post.likes}"/>

                                    <span uk-icon="icon: <c:out value="${post.likes >= 0 ? 'chevron-up':'chevron-down'}"/>; ratio: 0.8"></span>
                                </p>
                            </div>
                            <div class="uk-width-auto">
                                <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                                    <c:if test="${post.daysSinceCreation > 0}">
                                        <spring:message code="postDisplay.meta.age.days" arguments="${post.daysSinceCreation}"/>
                                    </c:if>
                                    <c:if test="${post.daysSinceCreation == 0 && post.hoursSinceCreation > 0}">
                                        <spring:message code="postDisplay.meta.age.hours" arguments="${post.hoursSinceCreation}"/>
                                    </c:if>
                                    <c:if test="${post.daysSinceCreation == 0 && post.hoursSinceCreation == 0}">
                                        <spring:message code="postDisplay.meta.age.minutes" arguments="${post.minutesSinceCreation}"/>
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
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
