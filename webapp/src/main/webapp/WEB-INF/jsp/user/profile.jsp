<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sprin" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.profile.Profile" arguments="${user.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div class="uk-inline">
    <div class="uk-cover-container">
        <canvas height="430"></canvas>
        <img src="<c:url value="/resources/images/background.jpg"/>"  alt="" uk-cover>
    </div>
    <div class="uk-position-medium uk-position-cover uk-overlay uk-overlay-default uk-flex uk-flex-center uk-flex-middle" uk-grid>
        <div class="uk-width-2-3@m">
            <h3 class="uk-card-title uk-margin-remove-bottom userTitle"><c:out value="${user.username}" /></h3>
            <p class="uk-text-meta uk-margin-remove-top"><spring:message code="user.profile.inMoovifySince"/>
                <fmt:parseDate value="${user.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" /></p>
            <ul class="uk-list uk-list-bullet">
                <li class="userTitle"><spring:message code="user.profile.Name" arguments="${user.name}"/></li>
                <li class="userTitle"><spring:message code="user.profile.Email" arguments="${user.email}"/></li>
                <sec:authorize access="hasRole('ADMIN')" >
                    <li class="userTitle"><spring:message code="user.profile.Administrator"/></li>
                </sec:authorize>
                <li class="userTitle"><spring:message code="user.profile.Description"/></li>
            </ul>
             <p class="uk-margin userTitle">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aspernatur, aut autem debitis deleniti eius fuga fugiat harum magnam maxime natus necessitatibus nisi porro provident quae quam quisquam sit sunt suscipit!</p>
            <p class="uk-text-center"><button id="edit-button" class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom" type="button"><spring:message code="user.profile.EditProfile"/></button></p>
        </div>

        <div class="uk-width-1-3@m uk-flex-first uk-text-center">
            <div class="uk-inline-clip uk-transition-toggle" tabindex="0">
                <img class="uk-border-circle uk-margin-left" alt="" height="250" width="250" data-src="<c:url value="/resources/images/avatar.jpg"/>" uk-img>
                <div class="uk-position-center uk-text-center">
                    <span class="uk-transition-fade" uk-icon="icon: plus; ratio: 2"></span>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- TODO: Tobi hdp como vas a clavar esto aca--%>
<div>
    <sec:authorize access="hasRole('NOT_VALIDATED')" >
        <p>Por favor no olvides de validar tu cuenta mediante el mail que te mandamos!</p>
        <a href="<c:url value="/user/resendConfirmation" /> ">Reenviar mail</a>
    </sec:authorize>
</div>

<div class="uk-container uk-margin-top">
    <div class="uk-flex-middle" uk-grid>
        <section id="posts" class="uk-width-1-2@m uk-flex-first">
            <h1><spring:message code="user.profile.yourPosts"/></h1>
            <dl class="uk-description-list "><%--TODO cuando no hay posts se ve feo--%>
                <c:forEach items="${posts}" var="post">
                    <dt>
                        <a href="<c:url value="/post/${post.id}"/>">
                            <c:out value="${post.title}"/>
                        </a>
                    </dt>
                </c:forEach>
            </dl>
        </section>
        <section id="comments" class="uk-width-1-2@m uk-flex-first">
            <h1><spring:message code="user.profile.yourComments"/></h1>
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
