<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Title</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-large uk-margin-auto uk-padding-large">
    <div uk-grid>
        <div class="uk-width-1-5">
        </div>
        <div class="uk-width-expand">
            <h1>
                Search results for: <c:out value="${query}"/>
            </h1>
        </div>
    </div>
    <hr>
    <div uk-grid>
        <div class="uk-width-1-5">
            <div>
                <h2>Filter by</h2>
                <div class="uk-inline">
                    <button class="uk-button uk-button-default" type="button">Newest<span class="uk-padding-small uk-padding-remove-vertical" uk-icon="icon:  triangle-down"></span></button>
                    <div uk-dropdown="pos: bottom-justify; mode: click">
                        <ul class="uk-nav uk-dropdown-nav">
                            <li class="uk-active"><a href="#">Newest</a></li>
                            <li><a href="#">Hottest</a></li>
                            <li><a href="#">More comments</a></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div>
                <h2>Result's type</h2>
                <ul class="uk-subnav uk-subnav-pill" uk-switcher>
                    <li><a href="#">Movies</a></li>
                    <li class="uk-active"><a href="#">Posts</a></li>
                </ul>
            </div>
            <div>
                <h2>Categories</h2>
                <ul class="uk-list uk-list-hyphen">
                    <li><a class="uk-text-light" href="<c:url value="#"/>">News</a></li>
                    <li><a class="uk-text-light" href="<c:url value="#"/>">Debate</a></li>
                    <li><a class="uk-text-light" href="<c:url value="#"/>">Watch-list</a></li>
                    <li><a class="uk-text-light" href="<c:url value="#"/>">Question</a></li>
                </ul>
            </div>
        </div>
        <div class="uk-width-expand">

            <dl class="uk-description-list ">
                <c:forEach items="${posts}" var="post">
                    <dt>
                        <a class="uk-text-normal"
                           href="<c:url value="/post/${post.id}"/>">
                            <c:out value="${post.title}"/>
                        </a>
                    </dt>
                    <dd>
                    <span class="uk-text-light uk-text-muted uk-text-small">
                        <c:out value="${post.email}"/>
                    </span>
                    </dd>
                </c:forEach>

            </dl>
        </div>
    </div>
</main>
</body>
</html>
<%--    <div class="uk-container uk-container-small">--%>
<%--        <div>--%>
<%--            <ol>--%>
<%--                <c:forEach items="${posts}" var="item">--%>
<%--                    <li>--%>
<%--                        <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${item.title}"/></h2>--%>
<%--                        <p class="uk-article-meta"> Written on <c:out value="${item.creationDate}"/>.</p>--%>
<%--                        <p class="uk-article-meta"> <span data-uk-icon="icon: future"></span> Takes <c:out value="${item.readingTimeMinutes}"/> min reading.</p>--%>
<%--                    </li>--%>
<%--                </c:forEach>--%>
<%--            </ol>--%>
<%--        </div>--%>
<%--        <hr>--%>
<%--    </div>--%>