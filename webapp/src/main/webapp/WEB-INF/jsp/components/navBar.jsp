<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="isAuthenticated()">
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</sec:authorize>

<%-- Added to prevent a bug in Firefox which starts loading page before finishing loading the stylesheets, cause a Flash Of Unstyled Content --%>
<%--<script>0</script>--%>

<header id="navbar" uk-sticky="sel-target: .uk-navbar-container; cls-active: uk-navbar-sticky; bottom: #transparent-sticky-navbar">
    <nav class="uk-navbar-container" uk-navbar>
        <a class="uk-logo" href="<c:url value="/" />"><img id="nav-logo" src="<c:url value="/resources/images/logo.png"/>" alt="Moovify"></a>
        <div class="uk-navbar-right">
            <ul class="uk-navbar-nav">
                <li class="uk-visible@s">
                    <a id="nav-search-toggle" class="uk-navbar-toggle" title="<spring:message code="search.title"/>">
                        <span uk-icon="icon: search; ratio: 1.7"></span>
                    </a>
                    <div class="uk-drop" data-uk-drop="mode: click; pos: left-center; offset: 0; boundary: #navbar">
                        <form action="<c:url value="/search/posts"/>" class="uk-search uk-search-navbar uk-width-1-1">
                            <label for="nav-search"></label>
                            <input autofocus id="nav-search" name="query" class="uk-search-input" type="search" placeholder="<spring:message code="navbar.searchDots"/>" >
                            <button id="submit-navbar-search-button" class="uk-button uk-button-default uk-border-rounded search-button" type="submit"><spring:message code="navbar.search"/></button>
                        </form>
                    </div>
                </li>
                <sec:authorize access="isAuthenticated()">
                    <li class="uk-navbar-item">
                        <c:if test="${!loggedUser.validated}">
                            <!-- This is a button toggling the modal -->
                            <a class="uk-padding-remove" href="#" uk-toggle="target: #confirm-email-modal">
                                <spring:message code="navbar.createPost"/>
                            </a>
                        </c:if>
                        <c:if test="${loggedUser.validated}">
                            <a class="uk-padding-remove" href="<c:url value="/post/create" />"><spring:message code="navbar.createPost"/></a>
                        </c:if>
                    </li>
                    <li>
                        <a class="nav-user uk-padding-remove uk-margin-right uk-margin-small-left" href="<c:url value="/user/profile"/>">
<%--                            <span class="iconify" data-icon="teenyicons:user-circle-outline" data-inline="false"></span>--%>
                            <img src="<c:url value="/user/avatar/${loggedUser.avatarId}" />" class="uk-comment-avatar circle-navbar" width="45" height="45" alt="User Avatar">
                            <span class="uk-text-bold uk-margin-small-left">${loggedUser.username}</span>
                            <c:if test="${loggedUser.admin}">
                                <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false" title="<spring:message code="admin.title"/>"></span>
                            </c:if>
                        </a>
                        <div class="uk-navbar-dropdown m-bg-primary-lighter-5 m-rounded-bottom-10">
                            <ul class="uk-nav uk-navbar-dropdown-nav">
                                <li>
                                    <a class="uk-text-normal uk-text-center m-text-primary" href="<c:url value="/user/profile"/>">
                                        <spring:message code="user.profile"/>
                                    </a>
                                </li>
                                <c:if test="${loggedUser.admin}">
                                <li>
                                    <a class="uk-text-normal uk-text-center m-text-primary" href="<c:url value="/admin/deleted/posts"/>">
                                        <spring:message code="adminPanel.btn"/>
                                    </a>
                                </li>
                                </c:if>
                                <li>
                                    <a class="uk-text-normal uk-text-center m-text-red" href="<c:url value="/logout"/>">
                                        <spring:message code="user.logout"/>
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </li>
                </sec:authorize>
                <sec:authorize access="!isAuthenticated()">
                    <li class="uk-navbar-item">
                        <a href="<c:url value="/login"/>"><spring:message code="user.login"/></a>
                    </li>
                    <li>
                        <a class="uk-text-center" href="<c:url value="/user/create"/>">
                            <button class="uk-button uk-button-primary uk-border-rounded uk-text-bolder uk-text-primary" type="button"><spring:message code="user.signup"/></button>
                        </a>
                    </li>
                </sec:authorize>
            </ul>
        </div>
    </nav>
</header>

<c:if test="${not empty loggedUser and !loggedUser.validated}">
<!-- Confirm email modal -->
<div id="confirm-email-modal" uk-modal>
    <div class="uk-modal-dialog uk-modal-body">
        <h2 class="uk-modal-title"><spring:message code="user.emailConfirm.title"/></h2>
        <p><spring:message code="user.emailConfirm.text"/></p>
        <p class="uk-text-right">
            <a class="uk-button uk-button-secondary uk-border-rounded uk-text-bolder" href="<c:url value="/user/resendConfirmation" /> "><spring:message code="user.profile.ResendEmail"/></a>
            <button class="uk-button uk-button-primary uk-border-rounded uk-text-bolder uk-modal-close" type="button"><spring:message code="user.emailConfirm.closeModal"/></button>
        </p>
    </div>
</div>
</c:if>