<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Moovify | Page not found</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container uk-container-large">
    <div class="uk-margin-auto">
        <h1 class="uk-margin-auto uk-text-center uk-text-bold uk-margin-medium-top">ERROR 404</h1>
        <h1 class="uk-margin-auto uk-text-center uk-text-large uk-margin-remove-top mid-bold">Page not found</h1>
    </div>
    <br>
    <div class="uk-margin-auto">
        <button class="uk-button uk-button-primary uk-border-rounded uk-margin-auto uk-align-center"
                type="button"
                onclick="goBack()"
        >
            Go back
        </button>
    </div>

</main>

</body>
</html>

<script>
    function goBack() {
        window.history.back();
    }
</script>
