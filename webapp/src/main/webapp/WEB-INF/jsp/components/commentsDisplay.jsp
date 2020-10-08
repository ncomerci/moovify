<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<jsp:useBean id="comments" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection<ar.edu.itba.paw.models.Comment>"/>

<div class="uk-flex uk-flex-wrap">
    <c:forEach items="${comments.results}" var="comment">
        <div class="uk-width-1-1">
            <div class="uk-flex">
                <sec:authorize access="hasRole('ADMIN')">
                    <c:if test="${!comment.enabled}">
                        <button class="uk-button uk-button-default uk-border-rounded uk-margin-auto-vertical uk-margin-right restore-btn"
                                data-id="${comment.id}"
                                type="button"
                        >
                            <spring:message code="adminPanel.restore"/>
                        </button>
                    </c:if>
                </sec:authorize>
                <div class="uk-width-expand uk-margin-small-top">
                    <a href="<c:url value="/comment/${comment.id}"/>">
                        <c:set var="maxLength" value="${80}"/>
                        <c:set var="length" value="${fn:length(comment.body)}"/>
                        <c:out value="${fn:substring(comment.body, 0, maxLength)}${length > maxLength ? '...':''}"/>
                    </a>
                   <%--TODO una vez que se incluya al post dentro del comment, refactorear la vista para reflejarlo--%>
                    <%--<p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                        <spring:message code="commentDisplay.meta.description" arguments="${comment.category.name},${post.user.name}"/>
                    </p>--%>
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