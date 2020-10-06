<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="adminPanel.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>
<main class="uk-container-small uk-margin-auto uk-padding-small">
    <section id="controllers">
        <form action="<c:url value="/admin/deleted/posts"/>" method="get">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSelection" value="0" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/adminPanel/deleted/mainController.jsp"/>
        </form>
    </section>
</main>
</body>
</html>
