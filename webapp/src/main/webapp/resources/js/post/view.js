window.addEventListener('load', () => {

    const postLikeForm = document.forms['post-like-form'];

    interpretBody();

    document.querySelectorAll(".like-post-button")
        .forEach(button => {
            button.addEventListener('click', () => likePost(postLikeForm, button.dataset.flag === "true", button.dataset.value), false)
        });

  }, false);
function likePost(postLikeForm, boolean, value){
    document.getElementById('post-like-flag').checked = boolean;
    document.getElementById('post-like-value').value = value;
    postLikeForm.submit();
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
