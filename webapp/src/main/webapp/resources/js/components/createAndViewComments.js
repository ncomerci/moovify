const localStorageIds = [];

window.addEventListener('load', () => {

    const commentLikeForm = document.forms['comment-like-form'];
    const postLikeForm = document.forms['post-like-form'];

    interpretBody();

    document.querySelectorAll(".like-comment-button")
        .forEach(button => {
                button.addEventListener('click', () => likeComment(button.dataset.id, commentLikeForm, button.dataset.value === "true"), false)
        });

    document.querySelectorAll(".like-post-button")
        .forEach(button => {
            button.addEventListener('click', () => likePost(postLikeForm, button.dataset.value === "true"), false)
    });

    document.body.addEventListener('click', e => {
        const replyForm = document.forms['reply-form'];

        if(e.target !== replyForm && !replyForm.contains(e.target)) {
            const id = replyForm.elements['send-bt'].dataset.id;
            const replyToSave = replyForm.elements['textarea'].value;
            if(id !== undefined && replyToSave !== "") {
                localStorage.setItem(`comment-${id}`, JSON.stringify(replyToSave));
                localStorageIds.push(id);
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

    document.getElementById('pagination-page-size').addEventListener('change',
        () => document.getElementById('pagination-form').submit())

    document.querySelectorAll(".delete-comment-button")
        .forEach(button => button.addEventListener('click', () => deleteComment(button.dataset.id), false));
}, false);

function likeComment(commentId, commentLikeForm, boolean){
    document.getElementById('comment-id').value = commentId;
    document.getElementById('like-value').checked = boolean;
    commentLikeForm.submit();
}
function likePost(postLikeForm, boolean){
    document.getElementById('post-like-value').checked = boolean;
    postLikeForm.submit();
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
   document.getElementById('modal-confirm')
       .addEventListener('click', () => submitDeleteForm(`/comment/delete/${commentId}`), false);
}

function submitDeleteForm(action) {
    const deleteForm = document.forms['delete-form'];
    deleteForm.action = action;
    deleteForm.submit();
}

function interpretBody() {
    marked.setOptions({
        gfm: true,
        breaks: true,
    //  silent: true,
    });

    //let unparsedBodyElem = document.getElementById("unparsedBody");
    let parsedBodyElem = document.getElementById("parsedBody");
    let body = document.querySelector("body").dataset.postBody;

    parsedBodyElem.innerHTML = marked(body);
    //unparsedBodyElem.style.display = 'none';
}