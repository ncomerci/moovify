window.addEventListener('load', () => {

    interpretBody();

  }, false);

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