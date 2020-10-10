<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="currentState" scope="request" type="java.lang.String"/>
<jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>

<div class="uk-inline">
    <div class="uk-cover-container">
        <canvas height="350"></canvas>
        <img alt="" src="<c:url value="/resources/images/background.jpg"/>" uk-cover>
    </div>

    <div class="uk-position-cover uk-overlay uk-overlay-default uk-flex uk-flex-center uk-flex-middle">
        <div class="uk-width-1-3@m uk-flex-first uk-text-center">
            <c:url value="/user/profile/avatar" var="action"/>
            <%--@elvariable id="avatarEditForm" type="ar.edu.itba.paw.webapp.form.editProfile.AvatarEditForm"--%>
            <form:form modelAttribute="avatarEditForm" action="${action}" method="post" enctype="multipart/form-data">
                <div class="uk-inline-clip uk-transition-toggle" tabindex="0">
                    <img class="uk-border-circle" alt="user-avatar" height="200" width="200" data-src="<c:url value="/user/avatar/${loggedUser.avatarId}"/>" uk-img>
                    <div class="uk-position-center uk-text-right">
                        <form:label path="avatar">
                            <div uk-form-custom>
                                <form:input id="avatar-edit" path="avatar" type="file" accept="image/*" />
                                <button class="uk-transition-fade uk-button-primary uk-border-rounded">
                                    <span type="button" uk-icon="icon: upload; ratio: 2.5"></span>
                                    <div>
                                        <span><spring:message code="user.profile.uploadAvatar"/></span>
                                    </div>
                                </button>
                            </div>
                        </form:label>
                    </div>
                </div>
                <div> <form:errors path="avatar" element="p" cssClass="error"/></div>
            </form:form>
        </div>
        <div class="uk-width-2-3@m uk-padding-remove">
            <h3 class="uk-card-title uk-margin-remove-bottom userTitle">
                <c:out value="${loggedUser.username}"/>
                <c:if test="${loggedUser.admin}">
                    <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                </c:if>
            </h3>
            <p class="uk-text-meta uk-margin-remove-top">
                <fmt:parseDate value="${loggedUser.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" var="parsedDateTime"/>
                <spring:message code="user.profile.inMoovifySince" arguments="${parsedDateTime}"/>
            </p>

            <ul class="uk-list uk-list-bullet">
                <li class="userTitle"><spring:message code="user.profile.Name" arguments="${loggedUser.name}"/></li>
                <li class="userTitle"><spring:message code="user.profile.Email" arguments="${loggedUser.email}"/></li>
                <c:if test="${loggedUser.admin}" >
                    <li class="userTitle"><spring:message code="user.profile.Administrator"/></li>
                </c:if>
                <c:if test="${fn:length(loggedUser.description) == 0}">
                    <li class="userTitle"><spring:message code="user.profile.notDescription"/> </li>
                </c:if>
                <c:if test="${fn:length(loggedUser.description) != 0}">
                    <li class="userTitle"><spring:message code="user.profile.Description" arguments="${loggedUser.description}"/></li>
                </c:if>
            </ul>
            <c:if test="${loggedUser.validated}">
                <p class="uk-margin-large-left">
                    <a href="<c:url value="/user/profile/edit"/>">
                        <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="button">
                            <spring:message code="user.profile.EditProfile"/>
                        </button>
                    </a>
                </p>
            </c:if>
        </div>
    </div>
</div>
<div class="uk-container">
    <c:if test="${!loggedUser.validated}" >
        <div class="uk-text-center uk-margin-auto uk-padding-large">
            <h1 class="uk-h1"><spring:message code="user.profile.ConfirmationEmail"/></h1>
            <p class="uk-text-bold">
                <spring:message code="user.profile.ConfirmationEmail.body"/>
                <a href="<c:url value="/user/resendConfirmation" />">
                    <spring:message code="user.profile.ResendEmail"/>
                </a>
            </p>
        </div>
    </c:if>

    <c:if test="${loggedUser.validated}">
        <div class="uk-margin-medium-top">
            <ul class="uk-child-width-expand uk-tab">
                <li class="${currentState == 0 ? 'uk-active' : ''}">
                    <c:url value="${'/user/profile/posts'}" var="postsURL"/>
                    <a href="${currentState != 0 ? postsURL : '#'}">
                        <spring:message code="user.profile.yourPosts"/>
                    </a>
                </li>
                <li class="${currentState == 1 ? 'uk-active' : ''}">
                    <c:url value="${'/user/profile/comments'}" var="commentsURL"/>
                    <a href="${currentState != 1 ? commentsURL : '#'}">
                        <spring:message code="user.profile.yourComments"/>
                    </a>
                </li>
            </ul>
        </div>
    </c:if>
</div>


