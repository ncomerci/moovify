<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="posts" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection"/>

<div class="uk-flex uk-flex-wrap">
    <c:forEach items="${posts.results}" var="post">
        <div class="uk-width-1-1">
            <div class="uk-flex">
                <div class="uk-width-expand uk-margin-small-top">
                    <a href="<c:url value="/post/${post.id}"/>">
                        <c:out value="${post.title}"/>
                    </a>
                    <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                        <c:choose>
                            <c:when test="${post.user.enabled}">
                                <c:set var="name" value="${post.user.name}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="name"><spring:message code="user.notEnabled.name"/></c:set>
                            </c:otherwise>
                        </c:choose>
                        <spring:message code="postDisplay.meta.description" arguments="${post.category.name},${name}"/>
                    </p>
                </div>
                <div class="uk-width-auto">
                    <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                        <c:if test="${post.daysSinceCreation > 0}">
                            <spring:message code="postDisplay.meta.age.days" arguments="${post.daysSinceCreation}"/>
                        </c:if>
                        <c:if test="${post.daysSinceCreation == 0 && post.hoursSinceCreation > 0}">
                            <spring:message code="postDisplay.meta.age.hours" arguments="${post.hoursSinceCreation}"/>
                        </c:if>
                        <c:if test="${post.daysSinceCreation == 0 && post.hoursSinceCreation == 0}">
                            <spring:message code="postDisplay.meta.age.minutes" arguments="${post.minutesSinceCreation}"/>
                        </c:if>
                    </p>
                </div>
            </div>
        </div>
    </c:forEach>
</div>