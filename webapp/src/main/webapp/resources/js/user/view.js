window.addEventListener('load', () => {

    document.getElementById('modal-admin-confirm').addEventListener('click', () => {
        document.forms['promote-user-form'].submit();
    },false);

    document.getElementById('delete-admin-confirm').addEventListener('click', () => {
        document.forms['delete-user-form'].submit();
    },false);

}, false);