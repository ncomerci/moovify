const localStorageIds = [];

window.addEventListener('load', () => {

    const commentLikeForm = document.forms['comment-like-form'];

    document.querySelectorAll(".like-comment-button")
        .forEach(button => {
                button.addEventListener('click', () => likeComment(commentLikeForm, button.dataset.id, button.dataset.value), false)
        });


    document.body.addEventListener('click', e => {
        const replyForm = document.forms['reply-form'];

        if(e.target !== replyForm && !replyForm.contains(e.target)) {
            const id = replyForm.elements['send-bt'].dataset.id;
            const replyToSave = replyForm.elements['textarea'].value;
            if(id !== undefined) {
                if(replyToSave !== "") {
                    localStorage.setItem(`comment-${id}`, JSON.stringify(replyToSave));
                    localStorageIds.push(id);
                }
                else if(localStorage.getItem(`comment-${id}`)) {
                    localStorage.removeItem(`comment-${id}`);
                }
            }

            replyForm.classList.add('uk-hidden');
        }

    }, true);

    document.getElementById('send-bt').addEventListener('click', submitCommentReply);

    document.querySelectorAll(".replies-show")
        .forEach(button => {
            if (parseInt(button.dataset.amount) === 0) {
                button.classList.add('uk-hidden');
            }
            else {
                button.addEventListener('click', () => showReplies(button.dataset.id), false)
            }
        });

    document.querySelectorAll(".reply-button")
        .forEach(button => button.addEventListener('click', () => openCommentForm(button.dataset.id), false));

    const paginationPageSizeElem = document.getElementById('pagination-page-size');

    if(paginationPageSizeElem)
        paginationPageSizeElem.addEventListener('change',
            () => paginationPageSizeElem.form.submit())

    document.querySelectorAll(".delete-comment-button")
        .forEach(button => button.addEventListener('click', () => deleteComment(button.dataset.id), false));

    const commentBody = document.getElementById('commentBody');
    const bodyCounter = document.getElementById('body-counter');
    const submitBtn = document.getElementById('submitBtn');

    commentBody.addEventListener('input', event => bodyLengthChecker(event, commentBody.dataset.maxlength, bodyCounter, submitBtn));

    const replyBody = document.getElementById('textarea');
    const replyCounter = document.getElementById('reply-counter');
    const replyBtn = document.getElementById('send-bt');

    replyBody.addEventListener('input', event => bodyLengthChecker(event, replyBody.dataset.maxlength, replyCounter, replyBtn))

}, false);

function likeComment(commentLikeForm, commentId, value){
    console.log(value);
    console.log(value === 1);
    document.getElementById('comment-like-id').value = commentId;
    document.getElementById('comment-like-value').value = value;
    commentLikeForm.submit();
}


function showReplies(commentId) {

    document.getElementById(commentId + '-replies-show').classList.add('uk-hidden');
    document.getElementById(commentId + '-children').classList.remove('uk-hidden');
}

function openCommentForm(parentId) {
    const replyForm = document.forms['reply-form'];
    const parentComment = document.getElementById(parentId);

    replyForm.elements['send-bt'].dataset.id = parentId;
    const commentBody = JSON.parse(localStorage.getItem(`comment-${parentId}`));
    replyForm.elements['textarea'].value = commentBody === null ? '' : commentBody;

    parentComment.appendChild(replyForm);
    replyForm.classList.remove('uk-hidden');
    replyForm.elements['textarea'].focus();
}

function submitCommentReply() {
    const springForm = document.forms['spring-form'];
    const replyForm = document.forms['reply-form'];

    springForm.elements['parentId'].value = replyForm.elements['send-bt'].dataset.id;
    springForm.elements['commentBody'].value = replyForm.elements['textarea'].value;

    springForm.submit();
    localStorageIds.forEach(id => localStorage.removeItem(`comment-${id}`));
}

function deleteComment(commentId) {
   const name = document.getElementById(commentId).getElementsByClassName('comment-user-name')[0].textContent
       .replace(/\s+/g, ' ').replace(/ $/, '');
   const body = document.getElementById(commentId).getElementsByClassName('uk-comment-body')[0]
       .getElementsByTagName('span')[0].textContent;

   document.getElementById('modal-body').textContent = `${name}: ${body}`;

   //esto es para limpiar los eventListeners del boton de confirmar del modal
   const confirmBtn = document.getElementById('modal-comment-confirm');
   const clonedBtn = confirmBtn.cloneNode(true);
   confirmBtn.parentNode.replaceChild(clonedBtn, confirmBtn);

    clonedBtn.addEventListener('click', () => submitCommentDeleteForm(commentId), false);
}

function submitCommentDeleteForm(commentId) {
    const deleteForm = document.forms['delete-comment-form'];
    deleteForm.action += `comment/delete/${commentId}`;
    deleteForm.submit();
}

function bodyLengthChecker(event, bodyLength, bodyCounter, submitBtn) {
    const currentLength = event.currentTarget.value.length;
    bodyCounter.innerText = `${currentLength}/${bodyLength}`;

    if(currentLength > bodyLength) {
        if (!submitBtn.disabled) {
            bodyCounter.classList.remove('uk-text-muted');
            bodyCounter.classList.add('uk-text-danger');
            submitBtn.disabled = true;
        }
    }
    else if(submitBtn.disabled) {
        bodyCounter.classList.remove('uk-text-danger');
        bodyCounter.classList.add('uk-text-muted');
        submitBtn.disabled = false;
    }
}