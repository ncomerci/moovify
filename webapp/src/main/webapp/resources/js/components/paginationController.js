window.addEventListener('load', function(){

    let pageSizeSelector = document.getElementById('pagination-page-size');

    if(pageSizeSelector)
        pageSizeSelector.addEventListener('change',() => pageSizeSelector.form.submit(),false);

}, false);