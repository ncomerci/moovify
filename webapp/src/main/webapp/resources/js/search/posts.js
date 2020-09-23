window.addEventListener('load', function(){

    let form = document.getElementById('searchPostsForm');
    let postCategory = document.getElementById('postCategory');
    let postAge = document.getElementById('postAge');
    let sortCriteria = document.getElementById('sortCriteria');

    postCategory.addEventListener('change',() => form.submit(),false);
    postAge.addEventListener('change',() => form.submit(),false);
    sortCriteria.addEventListener('change',() => form.submit(),false);

}, false);