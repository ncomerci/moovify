<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<form method="get" class="uk-form-stacked" action="<c:url value="/search"/>">
    <fieldset>
        <div>
            <div class="uk-margin">
                <legend class="uk-text-primary uk-text-large uk-text-lead"><spring:message code="search.posts.form.changeSearch"/></legend>
                <br>
                <div class="uk-inline">
                    <span class="uk-form-icon uk-form-icon-flip" uk-icon="icon: search" ></span>
                    <label><input class="uk-input" type="text" name="query" value="<c:out value="${query}"/>" placeholder="<spring:message code="search.posts.form.newSearch"/>"></label>
                </div>
            </div>
        </div>

        <div>
            <legend class="uk-text-primary uk-text-large uk-text-lead"><spring:message code="search.posts.form.searchingFor"/></legend>
            <p class="uk-text-left">
                <label class="uk-text-primary"><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_post_title">&nbsp;<spring:message code="search.posts.form.postTitle"/></label><br>
                <label class="uk-text-primary"><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_movie_title">&nbsp;<spring:message code="search.posts.form.movieTitle"/></label><br>
                <label class="uk-text-primary"><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_tags">&nbsp;<spring:message code="search.posts.form.tags"/></label><br>
            </p>
        </div>

        <div>
            <legend class="uk-text-primary uk-text-large uk-text-lead"><spring:message code="search.posts.form.sortBy"/></legend>
            <p class="uk-text-center">
                <label class="uk-text-primary"><input class="uk-radio" type="radio" name="sort_criteria" value="newest"> <spring:message code="search.posts.form.newest"/></label>
                <label class="uk-text-primary"><input class="uk-radio" type="radio" name="sort_criteria" value="oldest"> <spring:message code="search.posts.form.oldest"/></label>
            </p>
        </div>
        <p class="uk-text-center">
            <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="submit"><spring:message code="search.posts.form.send"/></button>
        </p>
    </fieldset>
</form>