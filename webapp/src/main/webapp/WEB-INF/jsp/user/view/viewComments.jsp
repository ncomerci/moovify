<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.view.Profile" arguments="${user.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div class="uk-container">
    <c:set var="currentState" value="1" scope="request" />
    <jsp:include page="view.jsp"/>

    <c:if test="${empty comments}">
        <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="user.view.CommentsNotFound"/> </h2>
    </c:if>
    <dl class="uk-description-list">
        <c:forEach items="${comments}" var="comment">
            <dt>
                <a href="<c:url value="/post/${comment.postId}#${comment.id}"/>">
                    <c:out value="${comment.body}"/>
                </a>
            </dt>
        </c:forEach>
    </dl>
</div>
</body>
</html>