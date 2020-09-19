window.addEventListener('load', function(){

    let form = document.getElementById('search-form');
    let postCategory = document.getElementById('post-category');
    let postAge = document.getElementById('post-age');
    let sortCriteria = document.getElementById('sort-criteria');

    postCategory.addEventListener('change',() => form.submit(),false);
    postAge.addEventListener('change',() => form.submit(),false);
    sortCriteria.addEventListener('change',() => form.submit(),false);

}, false);