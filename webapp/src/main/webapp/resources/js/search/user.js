window.addEventListener('load', function(){

    let form = document.getElementById('searchUsersForm');
    let roles = document.getElementById('role');
    let sortCriteria = document.getElementById('sortCriteria');

    roles.addEventListener('change',() => form.submit(),false);
    sortCriteria.addEventListener('change',() => form.submit(),false);

}, false);