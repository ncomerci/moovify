<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    <title><spring:message code="index.feed.title" arguments="${loggedUser.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body class="min-height-1000">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div>
    <c:set var="currentState" value="2" scope="request" />
    <jsp:include page="index.jsp"/>
    <main class="uk-container uk-container-large main-page">
        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-bottom uk-margin-top" uk-grid>
            <div class="uk-width-3-5 uk-flex-first">
                <div class="uk-child-width-expand uk-margin-medium-top">
                    <ul class="uk-child-width-expand uk-tab">
                        <li class="${currentState == 0 ? 'uk-active' : ''}">
                            <c:url value="${'/hottest'}" var="hottestURL"/>
                            <a href="${currentState != 0 ? hottestURL : ''}">
                                <spring:message code="index.hottestPosts"/>
                            </a>
                        </li>
                        <li class="${currentState == 1 ? 'uk-active' : ''}">
                            <c:url value="${'/newest'}" var="newestURL"/>
                            <a href="${currentState != 1 ? newestURL : ''}">
                                <spring:message code="index.newestPosts"/>
                            </a>
                        </li>
                        <li class="${currentState == 2 ? 'uk-active' : ''}">
                            <c:url value="${'/feed'}" var="feedURL"/>
                            <a href="${currentState != 2 ? feedURL : ''}">
                                <spring:message code="index.myFeed"/>
                            </a>
                        </li>
                    </ul>
                </div>
                <div class="uk-container">
                    <c:if test="${empty followedUsersPosts.results}">
                        <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="index.myFeedEmpty"/></h2>
                    </c:if>

                    <c:set var="posts" value="${followedUsersPosts}" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>

                    <c:if test="${not empty followedUsersPosts.results}">
                        <c:set var="collection" value="${posts}" scope="request"/>
                        <c:url var="baseURL" value="/feed" context="/" scope="request"/>
                        <c:set var="numberOfInputs" value="${2}" scope="request"/>
                        <form action="<c:url value="${baseURL}"/>" method="get">
                            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
                        </form>
                    </c:if>
                </div>
            </div>
            <div class="uk-width-1-3 uk-margin-large-left uk-padding uk-padding-remove-top">
                <h3 class="uk-margin-medium-top"><spring:message code="index.hottestUsers"/></h3>
                <c:forEach items="${hottestUsers.results}" var="user">
                    <c:if test="${user.enabled}">
                        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
                            <div class="uk-width-1-3">
                                <a href="<c:url value="/user/${user.id}"/>">
                                    <img class="circle-comment uk-comment-avatar" src="<c:url value="/user/avatar/${user.avatarId}"/>" alt="">
                                </a>
                            </div>
                            <div class="uk-width-2-3 uk-margin-top">
                                <div class="uk-flex">
                                    <div class="uk-width-expand">
                                        <a href="<c:url value="/user/${user.id}"/>" class="${user.admin ? 'uk-text-primary uk-text-middle' : ''}">
                                            <c:out value="${user.username}"/>
                                            <c:if test="${user.admin}">
                                                <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false" title="<spring:message code="admin.title"/>"></span>
                                            </c:if>
                                            <sec:authorize access="isAuthenticated()">
                                                <c:set var="followed" value="${customTag:hasUserFollowed(loggedUser,user)}"/>
                                                <c:if test="${followed}">
                                                    <c:out value="-"/>
                                                    <span class="iconify small-iconify" data-icon="ri:user-follow-line" data-inline="false" title="<spring:message code="user.followed"/>"></span>
                                                </c:if>
                                            </sec:authorize>
                                        </a>
                                        <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                                            <spring:message code="userDisplay.meta.name.votes" arguments="${user.name}, ${user.totalLikes}"/>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </main>
</body>
</html>
