<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<header id="navbar" uk-sticky="sel-target: .uk-navbar-container; cls-active: uk-navbar-sticky; bottom: #transparent-sticky-navbar">
    <nav class="uk-navbar-container" uk-navbar>
        <a class="uk-logo" href="<c:url value="/" />"><img id="nav-logo" src="<c:url value="/resources/images/logo.png"/>" alt="Moovify"></a>
        <div class="uk-navbar-right">
            <ul class="uk-navbar-nav">
                <li class="uk-visible@s">
                    <a id="nav-search-toggle" class="uk-navbar-toggle" data-uk-search-icon ></a>
                    <div class="uk-drop" data-uk-drop="mode: click; pos: right-center; offset: 0; boundary: #navbar">
                        <form action="<c:url value="/search/posts/"/>" class="uk-search uk-search-navbar uk-width-1-1">
                            <label for="nav-search"></label>
                            <input id="nav-search" name="query" class="uk-search-input" type="search" placeholder="<spring:message code="navbar.searchDots"/>" >
                            <button id="submit-navbar-search-button" class="uk-button uk-button-default uk-border-rounded search-button" type="submit"><spring:message code="navbar.search"/></button>
                        </form>
                    </div>
                </li>
                <li>
                    <a class="uk-padding-remove" href="">
                    <span class="iconify" data-icon="teenyicons:user-circle-outline" data-inline="false"></span>
                    </a>
                    <div class="uk-navbar-dropdown">
                        <ul class="uk-nav uk-navbar-dropdown-nav">
                            <li>
                                <sec:authorize access="!isAuthenticated()">
                                    <a href="<c:url value="/login"/>">Login</a>
                                </sec:authorize>
                                <sec:authorize access="isAuthenticated()">
                                    <a href="<c:url value="/user/profile"/>">My profile</a>
                                </sec:authorize>
                            </li>
                            <li>
                                <sec:authorize access="!isAuthenticated()">
                                    <a href="<c:url value="/user/create"/>">Sign up</a>
                                </sec:authorize>
                                <sec:authorize access="isAuthenticated()">
                                    <a href="<c:url value="/logout"/>">Logout</a>
                                </sec:authorize>
                            </li>
                        </ul>
                    </div>
                </li>
                <li class="uk-navbar-item"><a class="uk-padding-remove" href="<c:url value="/post/create" />"><spring:message code="navbar.createPost"/></a></li>
            </ul>

        </div>
    </nav>
</header>