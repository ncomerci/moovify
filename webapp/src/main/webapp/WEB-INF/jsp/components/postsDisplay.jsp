<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="posts" scope="request" type="java.util.Collection"/>

<div class="uk-flex uk-flex-wrap">
    <c:forEach items="${posts}" var="post">
        <div class="uk-width-1-1">
            <div class="uk-flex">
                <div class="uk-width-expand uk-margin-small-top">
                    <a href="<c:url value="/post/${post.id}"/>">
                        <c:out value="${post.title}"/>
                    </a>
                    <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                        <spring:message code="postDisplay.meta.description" arguments="${post.category.name},${post.user.name}"/>
                    </p>
                </div>
                <div class="uk-width-auto">
                    <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                        <c:if test="${post.timeSinceCreation.toDays() > 0}">
                            <spring:message code="postDisplay.meta.age.days" arguments="${post.timeSinceCreation.toDays()}"/>
                        </c:if>
                        <c:if test="${post.timeSinceCreation.toDays() == 0 && post.timeSinceCreation.toHours() > 0}">
                            <spring:message code="postDisplay.meta.age.hours" arguments="${post.timeSinceCreation.toHours()}"/>
                        </c:if>
                        <c:if test="${post.timeSinceCreation.toDays() == 0 && post.timeSinceCreation.toHours() == 0}">
                            <spring:message code="postDisplay.meta.age.minutes" arguments="${post.timeSinceCreation.toMinutes()}"/>
                        </c:if>
                    </p>
                </div>
            </div>
        </div>
    </c:forEach>
</div>