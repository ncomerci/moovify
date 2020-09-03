<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Moovify</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js" ></script>
    <script src="<c:url value="/resources/js/post/read.js" />"></script>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <div class="uk-container uk-container-small">
        <h1>All posts order chronologically</h1>
        <hr>
        <ul style="list-style-type:circle">
            <c:forEach items="${posts}" var="post">
                <li>
                    <a href="<c:url value="/post/${post.id}"/>">
                        <c:out value="${post.title}"/>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>

</body>

</html>