window.addEventListener('load', function(){

    let form = document.getElementById('searchMoviesForm');
    let movieCategory = document.getElementById('movieCategory');
    let decade = document.getElementById('decade');
    let sortCriteria = document.getElementById('sortCriteria');

    movieCategory.addEventListener('change',() => form.submit(),false);
    decade.addEventListener('change',() => form.submit(),false);
    sortCriteria.addEventListener('change',() => form.submit(),false);

}, false);