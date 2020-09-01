<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div uk-sticky="sel-target: .uk-navbar-container; cls-active: uk-navbar-sticky; bottom: #transparent-sticky-navbar">
    <nav class="uk-navbar-container" uk-navbar>
        <a class="uk-logo" href="<c:url value="/" />"><img id ="logo" src="<c:url value="/resources/images/logo.png" />" alt="Moovify Logo"></a>
        <div class="uk-navbar-right">
            <ul class="uk-navbar-nav">
                <li class="nav-item"><a href="<c:url value="/" />">Home</a></li>
                <li class="nav-item"><a href="<c:url value="/post/create" />">Create Post</a></li>
            </ul>

        </div>
    </nav>
</div>

<style>
    nav {
        background-color: #355070 !important;
    }
    #logo {
        width: auto;
        height: 60px;
        margin: 10px;
    }

</style>