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
        <img alt="" src="<c:url value="/resources/images/background.jpg"/>"  uk-cover>
        <%--<div class="uk-height-large uk-background-cover uk-light uk-flex" uk-parallax="bgy: -200" style="background-image: url(<c:url value="/resources/images/background.jpg"/>);">--%>
    </div>

    <div class="uk-position-cover uk-overlay uk-overlay-default uk-flex uk-flex-center uk-flex-middle" uk-grid>
        <div class="uk-width-1-3@m uk-flex-first uk-text-center">
            <c:url value="/user/profile/avatar" var="action"/>
            <form:form modelAttribute="avatarEditForm" action="${action}" method="post" enctype="multipart/form-data">
                <div class="uk-inline-clip uk-transition-toggle" tabindex="0">
                    <img class="uk-border-circle" alt="" height="200" width="200" data-src="<c:url value="/user/avatar/${loggedUser.avatarId}"/>" uk-img>
                        <%--@elvariable id="avatarEditForm" type=""--%>
                    <div class="uk-position-center uk-text-center">

                        <form:label path="avatar">
                            <div uk-form-custom>
                                <form:input id="avatar-edit" path="avatar" type="file"/>
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
                <div> <form:errors path="avatar" element="p" cssClass="error" cssStyle="color:red;" /></div>
            </form:form>
        </div>
        <div class="uk-width-2-3@m">
            <h3 class="uk-card-title uk-margin-remove-bottom userTitle"><c:out value="${loggedUser.username}" /></h3>
            <p class="uk-text-meta uk-margin-remove-top"><spring:message code="user.profile.inMoovifySince"/>
                <fmt:parseDate value="${loggedUser.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" /></p>
            <ul class="uk-list uk-list-bullet">
                <li class="userTitle"><spring:message code="user.profile.Name" arguments="${loggedUser.name}"/></li>
                <li class="userTitle"><spring:message code="user.profile.Email" arguments="${loggedUser.email}"/></li>
                <sec:authorize access="hasRole('ADMIN')" >
                    <li class="userTitle"><spring:message code="user.profile.Administrator"/></li>
                </sec:authorize>
                <c:if test="${fn:length(loggedUser.description) == 0}">
                    <li class="userTitle"><spring:message code="user.profile.notDescription"/> </li>
                </c:if>
                <c:if test="${fn:length(loggedUser.description) != 0}">
                    <li class="userTitle"><spring:message code="user.profile.Description" arguments="${loggedUser.description}"/></li>
                </c:if>
            </ul>
            <sec:authorize access="hasRole('USER')">
                <p class="uk-margin-large-left">
                    <a href="<c:url value="/user/profile/edit"/>"> <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="button">
                        <spring:message code="user.profile.EditProfile"/></button></a>
                </p>
            </sec:authorize>
        </div>
    </div>
</div>
<div class="uk-container">
    <div class="uk-text-center uk-margin-auto">
        <sec:authorize access="hasRole('NOT_VALIDATED')" >
            <h2><spring:message code="user.profile.ConfirmationEmail"/>  <a href="<c:url value="/user/resendConfirmation" /> "><spring:message code="user.profile.ResendEmail"/></a></h2>
        </sec:authorize>
    </div>
    <sec:authorize access="hasRole('USER')">
        <div class="uk-margin-medium-top">
            <ul class="uk-child-width-expand uk-tab">
                <li class="${currentState == 0 ? 'uk-active' : ''}">
                    <c:choose>
                        <c:when test="${currentState == 0}">
                            <a href="#"><spring:message code="user.profile.yourPosts"/></a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value="${'/user/profile/posts'}"/>"><spring:message code="user.profile.yourPosts"/></a>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li class="${currentState == 1 ? 'uk-active' : ''}">
                    <c:choose>
                        <c:when test="${currentState == 1}">
                            <a href="#"><spring:message code="user.profile.yourComments"/></a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value="${'/user/profile/comments'}"/>"><spring:message code="user.profile.yourComments"/></a>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </div>
    </sec:authorize>
</div>


