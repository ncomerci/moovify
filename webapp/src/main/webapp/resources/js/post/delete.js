window.addEventListener('load', () => {

    const deletePostBtn = document.getElementById('post-delete-btn');

    if(deletePostBtn)
        deletePostBtn.addEventListener('click', () => deletePost(deletePostBtn.dataset.id, deletePostBtn.dataset.msg), false);

})

function deletePost(postId, modalTitle) {
    document.getElementById('delete-modal').getElementsByClassName('uk-modal-title')[0].textContent = modalTitle;
    document.getElementById('')
    document.getElementById('modal-confirm')
        .addEventListener('click', () => submitDeleteForm(postId), false);
}

function submitDeleteForm(postId) {
    const deleteForm = document.forms['delete-form'];
    deleteForm.action += `post/delete/${postId}`;
    deleteForm.submit();
}