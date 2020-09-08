<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>

<c:forEach items="${comments}" var="comment" >
    <p>${comment.id}</p>
    <p>${comment.body}</p>

    <div class="uk-margin-small-left">
        <%--  Recursive Call  --%>
        <c:set var="comments" value="${comment.children}" scope="request"/>
        <jsp:include page="commentTree.jsp" />
    </div>
</c:forEach>