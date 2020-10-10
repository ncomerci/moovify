<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:useBean id="users" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection"/>

<div class="uk-flex uk-flex-wrap">
    <c:forEach items="${users.results}" var="user">
        <div class="uk-width-1-1">
            <div class="uk-flex">
                <div class="uk-width-expand uk-margin-small-top">
                    <a href="<c:url value="/user/${user.id}"/>" class="${user.admin ? 'uk-text-primary uk-text-middle' : ''}">
                        <c:out value="${user.username}"/>
                        <c:if test="${user.admin}">
                            <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                        </c:if>
                    </a>
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