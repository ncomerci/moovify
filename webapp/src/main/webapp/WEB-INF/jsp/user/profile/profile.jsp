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
        <div class="uk-width-1-4@m uk-flex-first uk-text-center">
            <c:url value="/user/profile/avatar" var="action"/>
            <%--@elvariable id="avatarEditForm" type="ar.edu.itba.paw.webapp.dto.input.UpdateAvatarDto"--%>
            <form:form modelAttribute="avatarEditForm" action="${action}" method="post" enctype="multipart/form-data">
                <div class="uk-inline-clip uk-transition-toggle" tabindex="0">
                    <img class="circle uk-background-cover" alt="user-avatar" data-src="<c:url value="/user/avatar/${loggedUser.avatarId}"/>" uk-img>
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
                <a class="edit-icon-button uk-margin-small-left" uk-icon="icon: pencil; ratio: 1" title="<spring:message code="edit.title"/>" data-inline="false" uk-toggle="target: #edit-username-modal"></a>
                <spring:message code="user.view.followers" arguments="${followers}, ${loggedUser.totalLikes}"/>
            </h3>
            <p class="uk-text-meta uk-margin-remove-top">
                <fmt:parseDate value="${loggedUser.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" var="parsedDateTime"/>
                <spring:message code="user.profile.inMoovifySince" arguments="${parsedDateTime}"/>
            </p>

            <ul class="uk-list uk-list-bullet">
                <li class="userTitle">
                    <spring:message code="user.profile.Name" arguments="${loggedUser.name}"/>
                    <a class="edit-icon-button uk-margin-small-left" uk-icon="icon: pencil; ratio: 1" title="<spring:message code="edit.title"/>"  data-inline="false" uk-toggle="target: #edit-name-modal"></a>
                </li>
                <li class="userTitle"><spring:message code="user.profile.Email" arguments="${loggedUser.email}"/></li>
                <c:if test="${loggedUser.admin}" >
                    <li class="userTitle">
                        <spring:message code="user.profile.Administrator"/>
                        <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false" title="<spring:message code="admin.title"/>"></span>
                    </li>
                </c:if>
                <c:if test="${fn:length(loggedUser.description) == 0}">
                    <li class="userTitle">
                        <a uk-toggle="target: #edit-description-modal"><spring:message code="user.profile.notDescription"/></a>
                    </li>
                </c:if>
                <c:if test="${fn:length(loggedUser.description) != 0}">
                    <li class="userTitle m-long-text">
                        <spring:message code="user.profile.Description" arguments="${loggedUser.description}"/>
                        <a class="edit-icon-button uk-margin-small-left" uk-icon="icon: pencil; ratio: 1" title="<spring:message code="edit.title"/>"  data-inline="false" uk-toggle="target: #edit-description-modal"></a></li>
                </c:if>
            </ul>
            <c:if test="${loggedUser.validated}">
                <p class="uk-margin-left">
                    <a href="<c:url value="/user/changePassword"/>">
                        <button id="submit-form-button" class="uk-button uk-button-default uk-border-rounded" type="button">
                            <spring:message code="user.profile.edit.changePassword"/>
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
                    <a href="${currentState != 0 ? postsURL : ''}">
                        <spring:message code="user.profile.yourPosts"/>
                    </a>
                </li>
                <li class="${currentState == 1 ? 'uk-active' : ''}">
                    <c:url value="${'/user/profile/comments'}" var="commentsURL"/>
                    <a href="${currentState != 1 ? commentsURL : ''}">
                        <spring:message code="user.profile.yourComments"/>
                    </a>
                </li>
                <li class="${currentState == 2 ? 'uk-active' : ''}">
                    <c:url value="${'/user/profile/favourite/posts'}" var="favouritePostsURL"/>
                    <a href="${currentState != 2 ? favouritePostsURL : ''}">
                        <spring:message code="user.profile.bookmarkedPosts"/>
                    </a>
                </li>
                <li class="${currentState == 3 ? 'uk-active' : ''}">
                    <c:url value="${'/user/profile/followed/users'}" var="followedUsersURL"/>
                    <a href="${currentState != 3 ? followedUsersURL : ''}">
                        <spring:message code="user.profile.yourFollowedUsers"/>
                    </a>
                </li>
            </ul>
        </div>
    </c:if>
</div>

<c:url value="/user/edit" var="action"/>
<%--@elvariable id="userEditForm" type="ar.edu.itba.paw.webapp.form.UserEditForm"--%>
<form:form modelAttribute="userEditForm" class="uk-hidden" method="post" action='${action}' id="edit-user-form">

    <form:label path="username">
        <form:input path="username" id="user-edit-username-input"/>
    </form:label>
    <form:errors path="username" element="p" cssClass="error" id="user-edit-username-error"/>

    <form:label path="name">
        <form:input path="name" id="user-edit-name-input"/>
    </form:label>
    <form:errors path="name" element="p" cssClass="error" id="user-edit-name-error"/>

    <form:label path="description">
        <form:textarea path="description" id="user-edit-description-input"/>
    </form:label>
    <form:errors path="description" element="p" cssClass="error" id="user-edit-description-error"/>
</form:form>

<!-- edit name modal -->
<div id="edit-name-modal" uk-modal>
    <div class="uk-modal-dialog uk-modal-body">
        <h2 class="uk-modal-title"><spring:message code="user.profile.edit.name"/></h2>
        <span class="uk-text-normal uk-text-italic uk-text-bold"><spring:message code="user.profile.edit.name.meta"/> </span>
        <p id="edit-user-name-error" class="error"></p>
        <div class="uk-flex">
            <input id="edit-name-modal-input"
                   class="uk-input uk-margin-right uk-input uk-border-rounded uk-width-expand"
                   value="<c:out value="${loggedUser.name}"/>"/>
            <button id="edit-name-modal-submit" class="uk-button uk-button-primary uk-border-rounded uk-width-auto"
                    type="button"><spring:message code="user.profile.edit.submit.button"/></button>
        </div>
    </div>
</div>

<!-- edit username modal -->
<div id="edit-username-modal" uk-modal>
    <div class="uk-modal-dialog uk-modal-body">
        <h2 class="uk-modal-title"><spring:message code="user.profile.edit.username"/></h2>
        <span class="uk-text-normal uk-text-italic uk-text-bold"><spring:message code="user.profile.edit.username.meta"/> </span>
        <p id="edit-user-username-error" class="error"></p>
        <div class="uk-flex">
            <input id="edit-username-modal-input"
                   class="uk-input uk-margin-right uk-input uk-border-rounded uk-width-expand"
                   value="<c:out value="${loggedUser.username}"/>"/>
            <button id="edit-username-modal-submit" class="uk-button uk-button-primary uk-border-rounded uk-width-auto"
                    type="button"><spring:message code="user.profile.edit.submit.button"/></button>
        </div>
    </div>
</div>

<!-- edit description modal -->
<div id="edit-description-modal" uk-modal>
    <div class="uk-modal-dialog uk-modal-body">
        <h2 class="uk-modal-title"><spring:message code="user.profile.edit.description"/></h2>
        <span class="uk-text-normal uk-text-italic uk-text-bold"><spring:message code="user.profile.edit.description.meta"/> </span>
        <p id="edit-user-description-error" class="error"></p>
        <div class="uk-flex">
            <textarea id="edit-description-modal-input"
                      class="uk-input uk-margin-right uk-input uk-border-rounded uk-width-expand"><c:out value="${loggedUser.description}"/></textarea>
            <button id="edit-description-modal-submit" class="uk-button uk-button-primary uk-border-rounded uk-width-auto"
                    type="button"><spring:message code="user.profile.edit.submit.button"/></button>
        </div>
    </div>
</div>