window.addEventListener('load', function(){

    let editButtonElem = document.getElementById('cmt-edit-btn');
    let bodyElem = document.getElementById('comment-body');
    let editForm = document.getElementById('comment-edit-form');

    editButtonElem.addEventListener('click', () => {
        editButtonElem.style.display = "none";
        bodyElem.style.display = "none";
        editForm.style.display = "block";
    });
});