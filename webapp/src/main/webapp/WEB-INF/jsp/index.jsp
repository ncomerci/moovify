<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="index.pagename"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js" ></script>
    <script src="<c:url value="/resources/js/post/read.js" />"></script>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <main class="uk-container uk-container-large">

        <div class="uk-column-1-2 uk-padding-large">

            <section id="newest-posts">
                <h1 class="uk-heading-small"><spring:message code="index.newestPost"/></h1>
                <dl class="uk-description-list ">
                    <c:forEach items="${newestPosts}" var="post">
                        <dt>
                            <a href="<c:url value="/post/${post.id}"/>">
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
            </section>
            <section id="hottest-posts">
                <h1 class="uk-heading-small"><spring:message code="index.oldestPost"/></h1>
                <dl class="uk-description-list ">
                    <c:forEach items="${oldestPosts}" var="post">
                        <dt>
                            <a href="<c:url value="/post/${post.id}"/>">
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
            </section>
        </div>

        <div uk-slider>

            <div class="uk-position-relative">

                <div class="uk-slider-container">
                    <ul class="uk-padding-large uk-slider-items uk-child-width-1-2 uk-child-width-1-3@s uk-child-width-1-5@m uk-grid">
                        <li>
                            <div>
                                <div class="uk-card uk-card-default" >
                                    <div class="uk-card-media-top">
                                        <img src="https://m.media-amazon.com/images/M/MV5BYmU1NDRjNDgtMzhiMi00NjZmLTg5NGItZDNiZjU5NTU4OTE0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY820_.jpg" alt="">
                                    </div>
                                    <div class="uk-card-body uk-padding-small">
                                        <p class="uk-text-justify">Empire Strikes Back (1980)</p>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li>
                            <div>
                                <div class="uk-card uk-card-default" >
                                    <div class="uk-card-media-top">
                                        <img src="https://m.media-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY824_.jpg" alt="">
                                    </div>
                                    <div class="uk-card-body uk-padding-small">
                                        <p class="uk-text-justify">Star Wars (1977)</p>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li>
                            <div>
                                <div class="uk-card uk-card-default" >
                                    <div class="uk-card-media-top">
                                        <img src="https://m.media-amazon.com/images/M/MV5BYmU1NDRjNDgtMzhiMi00NjZmLTg5NGItZDNiZjU5NTU4OTE0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY820_.jpg" alt="">
                                    </div>
                                    <div class="uk-card-body uk-padding-small">
                                        <p class="uk-text-justify">Empire Strikes Back (1980)</p>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li>
                            <div>
                                <div class="uk-card uk-card-default" >
                                    <div class="uk-card-media-top">
                                        <img src="https://m.media-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY824_.jpg" alt="">
                                    </div>
                                    <div class="uk-card-body uk-padding-small">
                                        <p class="uk-text-justify">Star Wars (1977)</p>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li>
                            <div>
                                <div class="uk-card uk-card-default" >
                                    <div class="uk-card-media-top">
                                        <img src="https://m.media-amazon.com/images/M/MV5BYmU1NDRjNDgtMzhiMi00NjZmLTg5NGItZDNiZjU5NTU4OTE0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY820_.jpg" alt="">
                                    </div>
                                    <div class="uk-card-body uk-padding-small">
                                        <p class="uk-text-justify">Empire Strikes Back (1980)</p>
                                    </div>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>


                <a class="uk-position-center-left-out uk-position-small" href="#" uk-slidenav-previous uk-slider-item="previous"></a>
                <a class="uk-position-center-right-out uk-position-small" href="#" uk-slidenav-next uk-slider-item="next"></a>
            </div>
        </div>
    </main>
</body>

</html>