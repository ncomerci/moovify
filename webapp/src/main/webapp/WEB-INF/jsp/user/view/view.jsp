<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>


<jsp:useBean id="currentState" scope="request" type="java.lang.String"/>
<jsp:useBean id="user" scope="request" type="ar.edu.itba.paw.models.User"/>

<sec:authorize access="isAuthenticated()">
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</sec:authorize>

<c:if test="${not empty loggedUser and loggedUser.admin}">
    <script src="<c:url value="/resources/js/user/view.js"/>"></script>
</c:if>

<div class="uk-inline ">
    <div class="uk-cover-container">
        <canvas height="350"></canvas>
        <img alt="" src="<c:url value="/resources/images/background.jpg"/>" uk-cover>
    </div>
    <div class="uk-position-cover uk-overlay uk-overlay-default uk-flex uk-flex-center uk-flex-middle" uk-grid>
        <div class="uk-width-1-4@m uk-flex-first uk-text-center">
            <img class="circle uk-background-cover" alt="user-avatar" data-src="<c:url value="/user/avatar/${user.avatarId}"/>" uk-img>
            <c:if test="${not empty loggedUser and loggedUser.validated and loggedUser.enabled and loggedUser.id ne user.id}">
                <c:set var="followed" value="${customTag:hasUserFollowed(loggedUser,user)}"/>
                <c:if test="${!followed}">
                    <div class="uk-width-auto uk-text-center uk-padding-remove uk-margin-remove">
                        <form action="<c:url value="/user/follow/${user.id}"/>" class="uk-search uk-search-navbar uk-width-1-1" method="post">
                            <button class="uk-button uk-button-default uk-border-rounded" type="submit">
                                <spring:message code="user.follow"/>
                            </button>
                        </form>
                    </div>
                </c:if>
                <c:if test="${followed}">
                    <div class="uk-width-auto uk-text-center uk-padding-remove uk-margin-remove">
                        <form action="<c:url value="/user/unfollow/${user.id}"/>" class="uk-search uk-search-navbar uk-width-1-1" method="post">
                            <button class="uk-button uk-button-default uk-border-rounded" type="submit">
                                <spring:message code="user.unfollow"/>
                            </button>
                        </form>
                    </div>
                </c:if>
            </c:if>
        </div>
        <div class="uk-width-2-3@m uk-padding-remove">
            <h3 class="uk-card-title uk-margin-remove-bottom userTitle">
                <c:out value="${user.username} "/>
                <c:if test="${user.admin}">
                    <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false" title="<spring:message code="admin.title"/>"></span>
                </c:if>
                <spring:message code="user.view.followers" arguments="${followers}, ${user.totalLikes}"/>
            </h3>
            <p class="uk-text-meta uk-margin-remove-top">
                <fmt:parseDate value="${user.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both"/>
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" var="parsedDateTime"/>
                <spring:message code="user.profile.inMoovifySince" arguments="${parsedDateTime}"/>
            </p>
            <ul class="uk-list uk-list-bullet">
                <li class="userTitle">
                    <spring:message code="user.profile.Name" arguments="${user.name}"/>
                </li>
                <li class="userTitle">
                    <spring:message code="user.profile.Email" arguments="${user.email}"/>
                </li>
                <c:if test="${user.admin}">
                    <li class="userTitle"><spring:message code="user.profile.Administrator"/></li>
                </c:if>
                <c:if test="${fn:length(user.description) == 0}">
                    <li class="userTitle"><spring:message code="user.view.notDescription"/> </li>
                </c:if>
                <c:if test="${fn:length(user.description) != 0}">
                    <li class="userTitle m-long-text"><spring:message code="user.profile.Description" arguments="${user.description}"/></li>
                </c:if>
            </ul>
            <c:if test="${not empty loggedUser and loggedUser.admin}">
                <c:if test="${!user.admin and user.validated}">
                    <span>
                        <button class="uk-button uk-button-primary uk-border-rounded" type="button" uk-toggle="target: #modal-admin-promote">
                            <spring:message code="user.profile.adminBtn"/>
                        </button>
                    </span>
                </c:if>
                <span>
                    <button class="uk-button uk-button-primary uk-border-rounded red-button" type="button" uk-toggle="target: #modal-admin-delete">
                        <spring:message code="user.profile.deleteBtn"/>
                    </button>
                </span>
            </c:if>
        </div>
    </div>
</div>

<div class="uk-container">
    <div class="uk-margin-medium-top">
        <ul class="uk-child-width-expand uk-tab">
            <li class="${currentState == 0 ? 'uk-active' : ''}">
                <c:url value="${'/user/'}${user.id}${'/posts'}" var="postsURL"/>
                <a href="${currentState != 0 ? postsURL : ''}">
                    <spring:message code="user.view.Posts" arguments="${user.username}"/>
                </a>
            </li>
            <li class="${currentState == 1 ? 'uk-active' : ''}">
                <c:url value="${'/user/'}${user.id}${'/comments'}" var="commentsURL"/>
                <a href="${currentState != 1 ? commentsURL : ''}">
                    <spring:message code="user.view.Comments" arguments="${user.username}"/>
                </a>
            </li>
            <li class="${currentState == 2 ? 'uk-active' : ''}">
                <c:url value="${'/user/'}${user.id}${'/followed/users'}" var="followedUsersURL"/>
                <a href="${currentState != 2 ? followedUsersURL : ''}">
                    <spring:message code="user.view.usersFollowed" arguments="${user.username}"/>
                </a>
            </li>
        </ul>
    </div>
</div>

<%--Delete and promte modals--%>
<c:if test="${not empty loggedUser and loggedUser.admin}">
    <div id="modal-admin-promote" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title"><spring:message code="user.profile.modalTitle" arguments="${user.username}"/></h2>
            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close uk-border-rounded" type="button"><spring:message code="user.profile.modalCancel"/></button>
                <button id="modal-admin-confirm" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="user.profile.adminBtn"/></button>
            </p>
        </div>
    </div>

    <div id="modal-admin-delete" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title"><spring:message code="user.profile.deleteTitle" arguments="${user.username}"/></h2>
            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close uk-border-rounded" type="button"><spring:message code="user.profile.modalCancel"/></button>
                <button id="delete-admin-confirm" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="user.profile.deleteBtn"/></button>
            </p>
        </div>
    </div>

    <form id="promote-user-form" method="post" action="<c:url value="/user/promote/${user.id}"/>"></form>
    <form id="delete-user-form" method="post" action="<c:url value="/user/delete/${user.id}"/>"></form>
</c:if>