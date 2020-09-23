<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>


<jsp:useBean id="currentState" scope="request" type="java.lang.String"/>


<div class="uk-inline">
    <div class="uk-cover-container">
        <canvas height="350"></canvas>
        <img alt="" src="<c:url value="/resources/images/background.jpg"/>"  uk-cover>
        <%--<div class="uk-height-large uk-background-cover uk-light uk-flex" uk-parallax="bgy: -200" style="background-image: url(<c:url value="/resources/images/background.jpg"/>);">--%>
    </div>

    <div class="uk-position-cover uk-overlay uk-overlay-default uk-flex uk-flex-center uk-flex-middle" uk-grid>
        <div class="uk-width-1-3@m uk-flex-first uk-text-center">
            <div class="uk-inline-clip uk-transition-toggle" tabindex="0">
                <img class="uk-border-circle" alt="" height="250" width="250" data-src="<c:url value="/resources/images/avatar.jpg"/>" uk-img>
                <div class="uk-position-center uk-text-center">
                    <span class="uk-transition-fade" uk-icon="icon: plus; ratio: 2"></span>
                </div>
            </div>
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
                <%--                <li class="userTitle"><spring:message code="user.profile.Description"/></li>--%>
            </ul>
            <%--             <p class="uk-margin userTitle">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aspernatur, aut autem debitis deleniti eius fuga fugiat harum magnam maxime natus necessitatibus nisi porro provident quae quam quisquam sit sunt suscipit!</p>--%>
            <%--            <p class="uk-text-center"><button id="edit-button" class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom" type="button"><spring:message code="user.profile.EditProfile"/></button></p>--%>
        </div>
    </div>
</div>

<div class="uk-text-center uk-margin-auto">
    <sec:authorize access="hasRole('NOT_VALIDATED')" >
        <h2><spring:message code="user.profile.ConfirmationEmail"/>  <a href="<c:url value="/user/resendConfirmation" /> "><spring:message code="user.profile.ResendEmail"/></a></h2>
    </sec:authorize>
</div>
<div class="uk-container">
<sec:authorize access="hasAnyRole('ADMIN','USER')">
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


