window.addEventListener('load', () => {

    document.getElementById('modal-admin-confirm').addEventListener('click', () => {
        document.forms['promote-user-form'].submit();
    },false);

}, false);