<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

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
    <form action="<c:url value="/admin/deleted/users"/>" method="get">
        <section id="controllers">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSelection" value="2" scope="request"/>
            <c:set var="selectedView" scope="request"><spring:message code="admin.deleted.users"/></c:set>
            <jsp:include page="/WEB-INF/jsp/adminPanel/deleted/mainController.jsp"/>
        </section>
        <section id="search-results" class="uk-margin-top">
            <c:if test="${empty users.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.usersNotFound"/></h1>
            </c:if>

            <div class="uk-flex uk-flex-wrap">
                <c:forEach items="${users.results}" var="user">
                    <div class="uk-width-1-1">
                        <div class="uk-flex">
                            <c:if test="${loggedUser.admin and !user.enabled}">
                                <button class="uk-button uk-button-default uk-border-rounded uk-margin-auto-vertical uk-margin-right restore-btn"
                                        data-id="${user.id}"
                                        type="button"
                                >
                                    <spring:message code="adminPanel.restore"/>
                                </button>
                            </c:if>
                            <div class="uk-width-expand uk-margin-small-top">
                                <p class="uk-margin-remove ${user.admin ? 'uk-text-primary uk-text-middle' : ''}">
                                    <c:out value="${user.username}"/>
                                    <c:if test="${user.admin}">
                                        <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                                    </c:if>
                                </p>
                                <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                                    <spring:message code="userDisplay.meta.description" arguments="${user.name}, ${user.totalLikes}"/>
                                </p>
                            </div>
                            <div class="uk-width-auto">
                                <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                                    <c:if test="${user.daysSinceCreation > 0}">
                                        <spring:message code="postDisplay.meta.age.days" arguments="${user.daysSinceCreation}"/>
                                    </c:if>
                                    <c:if test="${user.daysSinceCreation == 0 && user.hoursSinceCreation > 0}">
                                        <spring:message code="postDisplay.meta.age.hours" arguments="${user.hoursSinceCreation}"/>
                                    </c:if>
                                    <c:if test="${user.daysSinceCreation == 0 && user.hoursSinceCreation == 0}">
                                        <spring:message code="postDisplay.meta.age.minutes" arguments="${user.minutesSinceCreation}"/>
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>

        <c:if test="${not empty users.results}">
            <c:set var="collection" value="${users}" scope="request"/>
            <c:url var="baseURL" value="/admin/deleted/users" context="/" scope="request">
                <c:param name="query" value="${query}"/>
            </c:url>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </c:if>
    </form>

    <form id="restore-form" method="post" action="<c:url value="/user/restore"/>">
        <label>
            <input hidden name="query" type="text" value="${query}"/>
        </label>
        <label>
            <input hidden name="pageSize" type="text" value="${collection.pageSize}"/>
        </label>
        <label>
            <input hidden name = "pageNumber" value = "${collection.pageNumber}"/>
        </label>
    </form>
</main>
</body>
</html>
