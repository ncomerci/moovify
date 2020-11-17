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

<h1><spring:message code="post.edit.h1"/></h1>

<c:url value="/post/edit/${post.id}" var="action"/>
<form:form modelAttribute="postEditForm"
           class="uk-form-stacked uk-margin-auto uk-padding-large uk-padding-remove-vertical" method="post"
           action='${action}' id="edit-post-form">

    <div class="uk-margin-auto">
        <form:errors path="body" element="p" cssClass="error"/>
        <form:label path="body" for="edit-post-data">
            <form:textarea path="body" id="edit-post-data"/>
        </form:label>
    </div>

    <spring:message var="send" code="post.edit.send"/>
    <input type="submit" value="${send}">

</form:form>

</body>
</html>
