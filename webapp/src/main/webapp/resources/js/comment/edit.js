window.addEventListener('load', function(){

    const editButtonElem = document.getElementById('cmt-edit-btn');
    const bodyElem = document.getElementById('comment-body');
    const editForm = document.getElementById('comment-edit-form');

    editButtonElem.addEventListener('click', () => {
        editButtonElem.style.display = "none";
        bodyElem.style.display = "none";
        editForm.style.display = "block";
    });

    const replyBody = document.getElementById('commentEditBody');
    const replyCounter = document.getElementById('edit-counter');
    const replyBtn = document.getElementById('submit-edt-btn');

    replyBody.addEventListener('input', event => bodyLengthChecker(event, replyBody.dataset.maxlength, replyCounter, replyBtn), false)


});

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