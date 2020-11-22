<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<jsp:useBean id="users" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection"/>

<c:forEach items="${users.results}" var="user">
    <div class="uk-grid-small" uk-grid>
        <div class="uk-width-1-6 uk-margin-remove">
            <img class="image-user-display" src="<c:url value="/user/avatar/${user.avatarId}"/>" alt="">
        </div>
        <div class="uk-width-3-5 margin-user-display uk-padding-remove-left">
            <div class="uk-flex">
                <div class="uk-width-expand">
                    <a class="text-lead" href="<c:url value="/user/${user.id}"/>" class="${user.admin ? 'uk-text-primary uk-text-middle' : ''}">
                        <sec:authorize access="isAuthenticated()">
                            <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
                            <c:set var="followed" value="${customTag:hasUserFollowed(loggedUser,user)}"/>
                            <c:if test="${followed}">
                                <span class="iconify small-iconify" data-icon="ri:user-follow-line" data-inline="false" title="<spring:message code="user.followed"/>"></span>
                                <c:out value="-"/>
                            </c:if>
                        </sec:authorize>
                        <c:out value="${user.username}"/>
                        <c:if test="${user.admin}">
                            <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false" title="<spring:message code="admin.title"/>"></span>
                        </c:if>
                    </a>
                    <p class="uk-text-meta uk-margin-remove-vertical uk-text-truncate">
                        <c:if test="${!customTag:hasDescription(user)}">
                            <c:set var="description" value="${user.description}"/>
                        </c:if>
                        <c:if test="${customTag:hasDescription(user)}">
                            <spring:message var="description" code="userDisplay.meta.empty.description"/>
                        </c:if>
                        <spring:message code="userDisplay.meta.description" arguments="${user.name}, ${description}"/>
                    </p>
                </div>
            </div>
        </div>
        <div class="uk-width-1-5">
            <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                <spring:message code="userDisplay.meta.votes" arguments="${user.totalLikes}"/>
            </p>
        </div>
    </div>
</c:forEach>