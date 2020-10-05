window.addEventListener('load', function(){

    let pageSizeSelector = document.getElementById('pagination-page-size');

    pageSizeSelector.addEventListener('change',() => pageSizeSelector.form.submit(),false);

}, false);