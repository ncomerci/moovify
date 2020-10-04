<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="Comment ID = ${comment.id}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<%--  TODO: Aca habria que mostrar el comment root  --%>

<c:set var="comments" value="${children.results}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/components/commentTree.jsp"/>

</body>
</html>