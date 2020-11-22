<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title><spring:message code="post.edit.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
    <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp"/>
    <script src="<c:url value="/resources/js/post/edit.js"/>"></script>
</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>

<div class="uk-margin-auto uk-margin-top">
    <h1 class="uk-article-title uk-margin-auto uk-text-center uk-text-primary">
        <spring:message code="post.edit.h1"/></h1>
    <hr class="uk-divider-icon">
</div>
<div>
    <c:url value="/post/edit/${post.id}" var="action"/>
    <form:form modelAttribute="postEditForm"
               class="uk-form-stacked uk-margin-auto uk-padding-large uk-padding-remove-vertical" method="post"
               action='${action}' id="edit-post-form">

        <div class="uk-margin-auto">
            <form:errors path="body" element="p" cssClass="error"/>
            <form:label path="body" for="edit-post-data"><spring:message code="post.edit.body"/>
                <form:textarea path="body" id="edit-post-data"/>
            </form:label>
        </div>

        <div class="uk-text-center uk-margin-medium-top">
            <input class="uk-button uk-button-primary uk-border-rounded" type="submit" value="<spring:message code="post.edit"/>" />
        </div>
    </form:form>
</div>
</body>
</html>
