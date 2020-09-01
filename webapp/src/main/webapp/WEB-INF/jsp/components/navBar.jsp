<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div uk-sticky="sel-target: .uk-navbar-container; cls-active: uk-navbar-sticky; bottom: #transparent-sticky-navbar">
    <nav class="uk-navbar-container" uk-navbar>
        <div class="uk-navbar-left">
            <a class="uk-logo" href="<c:url value="/" />"><img src="<c:url value="/resources/images/logo.svg" />" alt="Moovify Logo"></a>


            <ul class="uk-navbar-nav">
                <li><a href="<c:url value="/" />">Home</a></li>
                <li><a href="<c:url value="/post/create" />">Create Post</a></li>
            </ul>

        </div>
    </nav>
</div>