<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<form method="get" action="<c:url value="/search"/>">
    <fieldset>
        <div>
            <div class="uk-margin">
                <legend class="uk-text-primary uk-text-large">Want to change your search?</legend>
                <label><input class="uk-input" type="text" name="query" placeholder="New Search"></label>
            </div>
        </div>

        <div>
            <legend class="uk-text-primary uk-text-large">Searching for</legend>
            <label><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_post_title">Post Title</label><br>
            <label><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_movie_title">Movie Title</label><br>
            <label><input class="uk-checkbox" type="checkbox" name="filter_criteria[]" value="by_tags">Tags</label><br>
        </div>

        <div>
            <legend class="uk-text-primary uk-text-large">Sort by</legend>
            <label><input class="uk-radio" type="radio" name="sort_criteria" value="newest" checked> Newest</label>
            <label><input class="uk-radio" type="radio" name="sort_criteria" value="oldest"> Oldest</label>
            <br>
        </div>
        <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="submit">Enviar</button>
    </fieldset>
</form>