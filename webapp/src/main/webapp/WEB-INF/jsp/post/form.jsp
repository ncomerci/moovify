<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
    <style>
        .error { color:red;}
    </style>
</head>
<body>
<c:url value="/post/create" var="action"/>
<form:form modelAttribute="postCreateForm" class="uk-form-stacked uk-margin-auto uk-padding-large uk-padding-remove-vertical" method="post" action='${action}' id="new-post-form">
    <spring:message code="post.create.titlePlaceholder" var="titlePlaceholder"/>
    <div class="uk-margin-left uk-margin-bottom" >
        <form:label path="title" class="uk-form-label uk-text-secondary uk-margin-auto" for="title-text" ><spring:message code="post.create.newPostTitle"/>
            <%--<div class="uk-form-controls">--%>
            <form:input path="title" class="uk-input uk-form-small" id="title-text" type="text" placeholder="${titlePlaceholder}"/>
            <%--</div>--%>
        </form:label>
        <form:errors path="title" element="p" cssClass="error" />
    </div>

    <div class="uk-margin-left uk-margin-bottom">
        <spring:message code="post.create.emailPlaceholder" var="emailPlaceholder"/>
        <form:label path="email" class="uk-form-label uk-text-secondary uk-margin-auto" for="email-text"><spring:message code="post.create.newPostEmail"/>
            <form:input path="email"  class="uk-input uk-form-small" id="email-text" type="email" placeholder="${emailPlaceholder}"/>
        </form:label>
        <form:errors path="email" element="p" cssClass="error" />

    </div>

    <div class="uk-margin-auto">
        <form:errors path="body" element="p" cssClass="error" />
        <form:label path="body" for="create-post-data">
            <form:textarea path="body" id="create-post-data"/>
        </form:label>
    </div>
</form:form>
</body>
</html>
