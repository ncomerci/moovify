window.addEventListener('load', function(){

    let form = document.getElementById('searchPostsForm');
    let postCategory = document.getElementById('postCategory');
    let postAge = document.getElementById('postAge');
    let sortCriteria = document.getElementById('sortCriteria');
    let pageSize = document.getElementById('pageSize');

    postCategory.addEventListener('change',() => form.submit(),false);
    postAge.addEventListener('change',() => form.submit(),false);
    sortCriteria.addEventListener('change',() => form.submit(),false);
    pageSize.addEventListener('change',() => form.submit(),false);

}, false);