<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<form method="get" class="uk-form-stacked" action="<c:url value="/search"/>">
    <fieldset>
        <div>
            <div class="uk-margin">
                <legend class="uk-text-primary uk-text-large uk-text-lead">Want to change your search?</legend>
                <br>
                <div class="uk-inline">
                    <span class="uk-form-icon uk-form-icon-flip" uk-icon="icon: search" ></span>
                    <label><input class="uk-input" type="text" name="query" value="<c:out value="${query}"/>" placeholder="New Search"></label>
                </div>
            </div>
        </div>

        <div>
            <legend class="uk-text-primary uk-text-large uk-text-lead">Searching for:</legend>
            <p class="uk-text-left">
                <label class="uk-text-primary"><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_post_title">&nbsp;Post Title</label><br>
                <label class="uk-text-primary"><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_movie_title">&nbsp;Movie Title</label><br>
                <label class="uk-text-primary"><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_tags">&nbsp;Tags</label><br>
            </p>
        </div>

        <div>
            <legend class="uk-text-primary uk-text-large uk-text-lead">Sort by:</legend>
            <p class="uk-text-center">
                <label class="uk-text-primary"><input class="uk-radio" type="radio" name="sort_criteria" value="newest"> Newest</label>
                <label class="uk-text-primary"><input class="uk-radio" type="radio" name="sort_criteria" value="oldest"> Oldest</label>
            </p>
        </div>
        <p class="uk-text-center">
            <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="submit">Enviar</button>
        </p>
    </fieldset>
</form>