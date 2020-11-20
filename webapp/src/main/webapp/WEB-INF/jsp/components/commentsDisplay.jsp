<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:useBean id="comments" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection<ar.edu.itba.paw.models.Comment>"/>

<sec:authorize access="isAuthenticated()">
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</sec:authorize>

<div class="uk-flex uk-flex-wrap">
    <c:forEach items="${comments.results}" var="comment">
        <div class="uk-width-1-1">
            <div class="uk-flex">
                <c:if test="${not empty loggedUser and loggedUser.admin and !comment.enabled}">
                    <button class="uk-button uk-button-default uk-border-rounded uk-margin-auto-vertical uk-margin-right restore-btn"
                            data-id="${comment.id}"
                            type="button">
                        <spring:message code="adminPanel.restore"/>
                    </button>
                </c:if>
                <div class="uk-width-expand uk-margin-small-top">
                    <a class="uk-text-italic" href="<c:url value="/comment/${comment.id}"/>">
                        <c:set var="maxLength" value="${80}"/>
                        <c:set var="length" value="${fn:length(comment.body)}"/>
                        <c:out value="\"${fn:substring(comment.body, 0, maxLength)}${length > maxLength ? '[...]':''}\""/>
                    </a>
                    <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                        <c:choose>
                            <c:when test="${comment.user.enabled}">
                                <c:set var="name" value="${comment.user.username}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="name"><spring:message code="user.notEnabled.name"/></c:set>
                            </c:otherwise>
                        </c:choose>
                        <spring:message code="commentDisplay.meta.description" arguments="${comment.post.title}, ${comment.totalLikes}"/>
                        <span uk-icon="icon: ${comment.totalLikes >= 0 ? 'chevron-up' : 'chevron-down'}; ratio: 0.8"></span>
                    </p>
                </div>
                <div class="uk-width-auto">
                    <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                        <c:if test="${comment.daysSinceCreation > 0}">
                            <spring:message code="postDisplay.meta.age.days" arguments="${comment.daysSinceCreation}"/>
                        </c:if>
                        <c:if test="${comment.daysSinceCreation == 0 && comment.hoursSinceCreation > 0}">
                            <spring:message code="postDisplay.meta.age.hours" arguments="${comment.hoursSinceCreation}"/>
                        </c:if>
                        <c:if test="${comment.daysSinceCreation == 0 && comment.hoursSinceCreation == 0}">
                            <spring:message code="postDisplay.meta.age.minutes" arguments="${comment.minutesSinceCreation}"/>
                        </c:if>
                    </p>
                </div>
            </div>
        </div>
    </c:forEach>
</div>