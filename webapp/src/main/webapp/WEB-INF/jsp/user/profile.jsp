<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.profile.Profile" arguments="${user.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div class="uk-container uk-margin-top">
    <div class="uk-card uk-card-default uk-grid-collapse uk-child-width-1-2@s uk-margin" uk-grid>
        <div class="uk-card-media-left uk-cover-container">
            <img src="<c:url value="/resources/images/avatar.jpg"/>" alt="" uk-cover>
            <canvas width="400" height="250"></canvas>
        </div>
        <div>
            <h3 class="uk-card-title uk-margin-remove-bottom"><c:out value="${user.username}" /></h3>

            <p class="uk-text-meta uk-margin-remove-top"><spring:message code="user.profile.inMoovifySince"/><fmt:parseDate value="${user.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" /></p>
            <ul class="uk-list uk-list-bullet">
                <li>Name: <c:out value="${user.name}"/></li>
                <li>Email: <c:out value="${user.email}"/></li>
                <sec:authorize access="hasRole('ADMIN')" >
                    <li>Sos un admin!!!</li>
                </sec:authorize>
                <li>Description:</li>
            </ul>
            <p class="uk-margin-left">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aspernatur, aut autem debitis deleniti eius fuga fugiat harum magnam maxime natus necessitatibus nisi porro provident quae quam quisquam sit sunt suscipit!</p>
            <p class="uk-text-center"><button id="edit-button" class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom" type="button">Edit profile</button></p>
        </div>
    </div>


<div class="uk-column-1-2 uk-padding">

    <section id="posts">
        <h1 class="uk-heading-small"><spring:message code="user.profile.yourPosts"/></h1>
        <dl class="uk-description-list ">
            <dt>
                <a>post uno</a>
            </dt>
            <dt>
                <a>post dos</a>
            </dt>
            <dt>
                <a>post tres</a>
            </dt>
            <dt>
                <a>post cuatro 5</a>
            </dt>
        </dl>
    </section>
    <section id="hottest-posts">
        <h1 class="uk-heading-small"><spring:message code="user.profile.yourComments"/></h1>
        <dl class="uk-description-list ">
            <dt>
                <a>comment uno</a>
            </dt>
            <dt>
                <a>comment dos</a>
            </dt>
            <dt>
                <a>post tres</a>
            </dt>
            <dt>
                <a>post cuatro</a>
            </dt>
        </dl>
    </section>
</div>
</div>
</body>
</html>
