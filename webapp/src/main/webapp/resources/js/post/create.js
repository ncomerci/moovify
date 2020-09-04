window.addEventListener('load', function(){

    let formElem = document.getElementById('new-post-form');
    let addMovieInputElem = document.getElementById('add-movie-input');
    let addMovieButtonElem = document.getElementById('add-movie-button');
    let moviesSelectedElem = document.getElementById('movies-selected');
    let submitFormButton = document.getElementById('submit-form-button');
    let datalistElem = document.getElementById('movie-list');
    let openModalButtonElem = document.getElementById('open-modal-button');
    let moviesModalElem = document.getElementById('movies-modal');

    configureEasyMDE(formElem, moviesModalElem);

    // Validate form before opening modal
    // reportValidity is not compatible with IE, Chrome < 40, Firefox < 49, Edge < 17;
    openModalButtonElem.addEventListener('click', () => openModal(formElem, moviesModalElem), false);

    addMovieButtonElem.addEventListener('click',
        () => addMovie(formElem, addMovieInputElem, datalistElem, moviesSelectedElem),
        false);

    submitFormButton.addEventListener('click', () => formElem.submit(), false);

    moviesModalElem.addEventListener('beforehide', () => cancelModal(formElem, datalistElem, moviesSelectedElem), false);

}, false);

function configureEasyMDE(formElem, moviesModalElem){
    new EasyMDE({
        element: document.getElementById("create-post-data"),
        spellChecker: false,
        autosave: {
            enabled: true,
            uniqueId: "source",
            delay: 1000,
            text: "Saved: ",
        },
        forceSync: true,
        initialValue: "El valor inicial del editor. Cuando se carga la pagina, si no hay nada guardado, se llena con esto",
        minHeight: "300px", // This is the default minHeight
        parsingConfig: {
            allowAtxHeaderWithoutSpace: true,
            strikethrough: true,
            underscoresBreakWords: true
        },
        placeholder: "This is the Placeholder. Solo aparece si no hay texto.",

        // Upload Image Support Configurations

        inputStyle: "textarea", // Could be contenteditable
        theme: "easymde", // Default

        toolbar: ["bold", "italic", "heading", "|",
            "quote", "unordered-list", "ordered-list", "|",
            "horizontal-rule", "strikethrough",
            "link", "image", "|",
            "preview", "side-by-side", "fullscreen", "|",
            "clean-block", "guide", "|",
            {
                name: "upload",
                action: () => openModal(formElem, moviesModalElem),
                className: "fa fa-upload",
                title: "Upload",
            },
        ],

        renderingConfig: {
            sanitizerFunction: (dirtyHTML) => DOMPurify.sanitize(dirtyHTML),
        },

        onToggleFullScreen: easyMdeFullscreenHandle,

    });
}

// Recives a boolean indicating if editor is entering fullscreen mode (true), or leaving (false).
function easyMdeFullscreenHandle(fullscreen){
    document.getElementById('navbar').style.visibility = (fullscreen) ? 'hidden' : 'visible';
}

function openModal(formElem, moviesModalElem){
    if(formElem.reportValidity())
        UIkit.modal(moviesModalElem).show();
}

function addMovie(formElem, inputElem, datalistElem, moviesSelectedElem){
    let movieName = inputElem.value;
    if(!movieName)
        return;

    let movieOption = datalistElem.querySelector(`option[value = '${movieName}']`);

    if(!movieOption)
        return;

    let movieId = movieOption.dataset.id;

    let newInput = document.createElement("input");
    newInput.setAttribute('name', `movies[]`);
    newInput.setAttribute('type', 'number');
    newInput.setAttribute('value', movieId);
    newInput.setAttribute('data-movie-name', movieName);
    newInput.style.display = 'none';

    // Add new movie to form
    formElem.appendChild(newInput);

    // Remove movie from selectable options
    datalistElem.removeChild(movieOption);

    // Clear movie input
    inputElem.value = "";

    // Add movie to movies selected list
    /* <span class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom">
         ${movieName}
         <button class="uk-margin-small-left uk-light" type="button" uk-close></button>
       </span> */

    let closeElem = document.createElement("button");
    closeElem.setAttribute('class', 'uk-margin-small-left uk-light');
    closeElem.setAttribute('type', 'button');
    closeElem.setAttribute('uk-close', '');

    let movieBadgeElem = document.createElement("span");

    movieBadgeElem.setAttribute('class', 'uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom');

    movieBadgeElem.appendChild(document.createTextNode(movieName));
    movieBadgeElem.appendChild(closeElem);

    moviesSelectedElem.appendChild(movieBadgeElem);

    closeElem.addEventListener('click',
        () => unselectMovie(formElem, datalistElem, newInput, moviesSelectedElem, movieBadgeElem), false);
}

function unselectMovie(formElem, datalistElem, inputElem, moviesSelectedElem, movieBadgeElem){
    let opt = document.createElement("option");

    opt.setAttribute('value', inputElem.dataset.movieName);
    opt.setAttribute('data-id', inputElem.value);

    datalistElem.appendChild(opt);

    formElem.removeChild(inputElem);

    if(moviesSelectedElem && movieBadgeElem)
        moviesSelectedElem.removeChild(movieBadgeElem);
}

function cancelModal(formElem, datalistElem, moviesSelectedElem) {

    formElem.querySelectorAll("input[name ^= 'movies']")
        .forEach(movieInputElem => unselectMovie(formElem, datalistElem, movieInputElem));

    moviesSelectedElem.innerHTML = "";
}
