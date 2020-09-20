<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>
<ul class="uk-comment-list">
<c:forEach items="${comments}" var="comment" >

    <li>
        <article class="uk-comment uk-visible-toggle" tabindex="-1">
            <header class="uk-comment-header uk-position-relative">
                <div class="uk-grid-medium uk-flex-middle" uk-grid>
                    <div class="uk-width-auto">
                        <img class="uk-comment-avatar" src="<c:url value="/resources/images/avatar.jpg"/>" width="80" height="80" alt="">
                    </div>
                    <div class="uk-width-expand">
                        <h4 class="uk-comment-title uk-margin-remove">
                            <a href = "mailto: <c:out value="${comment.userEmail}" />"><c:out value="${comment.userEmail}" /> </a>
                        </h4>
                        <p class="uk-comment-meta uk-margin-remove-top">
                            <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                            <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                        </p>
                    </div>
                </div>
                <div class="uk-position-top-right uk-position-small uk-hidden-hover"><a class="uk-link-muted" href="#">Reply</a></div>
            </header>
            <div class="uk-comment-body">
                <p><c:out value="${comment.body}" /></p>
            </div>
        </article>
        <hr>
        <ul class="li">
            <%--  Recursive Call  --%>
            <c:set var="comments" value="${comment.children}" scope="request"/>
            <jsp:include page="commentTree.jsp" />
        </ul>
    </li>

</c:forEach>
</ul>

<%--
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
                &lt;%&ndash;  Recursive Call  &ndash;%&gt;
            <c:set var="comments" value="${comment.children}" scope="request"/>
            <jsp:include page="commentTree.jsp" />
        </li>

    </c:forEach>
</ul>
--%>
