window.addEventListener('load', () => {

    const deletePostBtn = document.getElementById('post-delete-btn');

    if(deletePostBtn)
        deletePostBtn.addEventListener('click', () => deletePost(deletePostBtn.dataset.id), false);

})

function deletePost(postId) {
    //esto es para limpiar los eventListeners del boton de confirmar del modal
    const confirmBtn = document.getElementById('modal-post-confirm');
    const clonedBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(clonedBtn, confirmBtn);

    clonedBtn.addEventListener('click', () => submitPostDeleteForm(postId), false);
}

function submitPostDeleteForm(postId) {
    const deleteForm = document.forms['delete-post-form'];
    deleteForm.action += `post/delete/${postId}`;
    deleteForm.submit();
}