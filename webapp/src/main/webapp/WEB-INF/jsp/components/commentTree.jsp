<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>
<ul class="ul">
<c:forEach items="${comments}" var="comment" >

        <li class="li">
            <div><a href = "mailto: <c:out value="${comment.userEmail}" />"><c:out value="${comment.userEmail}" /> </a>
                <span class="uk-align-right">
                    <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                </span>
                <br>
                <c:out value="${comment.body}" />
                <hr>
            </div>
            <%--  Recursive Call  --%>
            <c:set var="comments" value="${comment.children}" scope="request"/>
            <jsp:include page="commentTree.jsp" />
        </li>

</c:forEach>
</ul>