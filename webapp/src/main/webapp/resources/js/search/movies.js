window.addEventListener('load', function(){

    let form = document.getElementById('searchMoviesForm');

    document.getElementById('pageSize').addEventListener('change',() => form.submit(),false);

}, false);