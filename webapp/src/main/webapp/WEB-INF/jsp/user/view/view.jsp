<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ page contentType="text/html;charset=UTF-8" %>


<jsp:useBean id="currentState" scope="request" type="java.lang.String"/>
<jsp:useBean id="user" scope="request" type="ar.edu.itba.paw.models.User"/>

<sec:authorize access="hasRole('ADMIN')">
    <script src="<c:url value="/resources/js/user/view.js"/>"></script>
</sec:authorize>

<div class="uk-inline ">
    <div class="uk-cover-container">
        <canvas height="350"></canvas>
        <img alt="" src="<c:url value="/resources/images/background.jpg"/>"  uk-cover>
    </div>
    <div class="uk-position-cover uk-overlay uk-overlay-default uk-flex uk-flex-center uk-flex-middle" uk-grid>
        <div class="uk-width-1-3@m uk-flex-first uk-text-center">
            <img class="uk-border-circle uk-margin-left" alt="" height="250" width="250" data-src="<c:url value="/user/avatar/${user.avatarId}"/>" uk-img>
        </div>
        <div class="uk-width-2-3@m uk-padding-remove">
            <h3 class="uk-card-title uk-margin-remove-bottom userTitle">
                <c:out value="${user.username} "/>
                <c:if test="${user.admin}">
                    <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                </c:if>
            </h3>
            <p class="uk-text-meta uk-margin-remove-top "><spring:message code="user.profile.inMoovifySince"/><fmt:parseDate value="${user.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" /></p>
            <ul class="uk-list uk-list-bullet">
                <li class="userTitle"><spring:message code="user.profile.Name" arguments="${user.name}"/></li>
                <li class="userTitle"><spring:message code="user.profile.Email" arguments="${user.email}"/></li>
                <c:if test="${user.admin}">
                    <li class="userTitle"><spring:message code="user.profile.Administrator"/></li>
                </c:if>
                <c:if test="${fn:length(user.description) == 0}">
                    <li class="userTitle"><spring:message code="user.view.notDescription"/> </li>
                </c:if>
                <c:if test="${fn:length(user.description) != 0}">
                    <li class="userTitle"><spring:message code="user.profile.Description" arguments="${user.description}"/></li>
                </c:if>
            </ul>
            <sec:authorize access="hasRole('ADMIN')">
                <c:if test="${!user.admin}">
                    <span>
                        <button class="uk-button uk-button-default uk-border-rounded admin-button" type="button" uk-toggle="target: #modal-admin-promote">
                            <spring:message code="user.profile.adminBtn"/>
                        </button>
                    </span>
                </c:if>
                <span>
                    <button class="uk-button uk-button-default uk-border-rounded logout-button" type="button" uk-toggle="target: #modal-admin-delete">
                        <spring:message code="user.profile.deleteBtn"/>
                    </button>
                </span>
            </sec:authorize>
            <%--<p class="uk-margin userTitle">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aspernatur, aut autem debitis deleniti eius fuga fugiat harum magnam maxime natus necessitatibus nisi porro provident quae quam quisquam sit sunt suscipit!</p>--%>
        </div>

    </div>
</div>
<div class="uk-container">
    <div class="uk-margin-medium-top">
        <ul class="uk-child-width-expand uk-tab">
            <li class="${currentState == 0 ? 'uk-active' : ''}">
                <c:choose>
                    <c:when test="${currentState == 0}">
                        <a href="#"><spring:message code="user.view.Posts" arguments="${user.username}"/></a>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="${'/user/'}${userId}${'/posts'}"/>"><spring:message code="user.view.Posts" arguments="${user.username}"/></a>
                    </c:otherwise>
                </c:choose>
            </li>
            <li class="${currentState == 1 ? 'uk-active' : ''}">
                <c:choose>
                    <c:when test="${currentState == 1}">
                        <a href="#"><spring:message code="user.view.Comments" arguments="${user.username}"/></a>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="${'/user/'}${userId}${'/comments'}"/>"><spring:message code="user.view.Comments" arguments="${user.username}"/></a>
                    </c:otherwise>
                </c:choose>
            </li>
        </ul>
    </div>
</div>

<sec:authorize access="hasRole('ADMIN')">
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

</sec:authorize>