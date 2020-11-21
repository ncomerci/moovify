window.addEventListener('load', function(){

    let formElem = document.getElementById('new-post-form');
    let addMovieInputElem = document.getElementById('add-movie-input');
    let addMovieButtonElem = document.getElementById('add-movie-button');
    let moviesSelectedElem = document.getElementById('movies-selected');
    let addTagInputElem = document.getElementById('add-tag-input');
    let addTagButtonElem = document.getElementById('add-tag-button');
    let addTagCounter = document.getElementById('tag-counter');
    let tagsSelectedElem = document.getElementById('tags-selected');
    let submitFormButton = document.getElementById('submit-form-button');
    let datalistElem = document.getElementById('movie-list');
    let openModalButtonElem = document.getElementById('open-modal-button');
    let moviesModalElem = document.getElementById('movies-modal');
    let movieErrorElem = document.getElementById('movie-error');

    let easyMDE = configureEasyMDE(formElem, moviesModalElem);

    // Validate form before opening modal
    // reportValidity is not compatible with IE, Chrome < 40, Firefox < 49, Edge < 17;
    openModalButtonElem.addEventListener('click', () => openModal(formElem, moviesModalElem), false);

    addMovieButtonElem.addEventListener('click',
        () => addMovie(formElem, addMovieInputElem, datalistElem, moviesSelectedElem),
        false);

    addMovieInputElem.addEventListener('change', () => addMovie(formElem, addMovieInputElem, datalistElem, moviesSelectedElem));

    addTagInputElem.addEventListener('input', event => bodyLengthChecker(event, addTagInputElem.dataset.maxlength, addTagCounter, addTagButtonElem));

    addTagInputElem.addEventListener('change', () => addTag(formElem, addTagInputElem, tagsSelectedElem));

    addTagButtonElem.addEventListener('click',
        () => addTag(formElem, addTagInputElem, tagsSelectedElem),
        false);

    submitFormButton.addEventListener('click', () => {
        if(validateModal(formElem, movieErrorElem)) {
            easyMDE.clearAutosavedValue();
            formElem.submit();
        }
    }, false);

    // moviesModalElem.addEventListener('beforehide', () => cancelModal(formElem, datalistElem, moviesSelectedElem), false);

}, false);


function configureEasyMDE(formElem, moviesModalElem){
    return new EasyMDE({
        element: document.getElementById("create-post-data"),
        spellChecker: false,
        autosave: {
            enabled: true,
            uniqueId: "source",
            delay: 1000,
            text: "Saved: ",
        },
        forceSync: true,
        // initialValue: "El valor inicial del editor. Cuando se carga la pagina, si no hay nada guardado, se llena con esto",
        minHeight: "300px", // This is the default minHeight
        parsingConfig: {
            allowAtxHeaderWithoutSpace: true,
            strikethrough: true,
            underscoresBreakWords: true
        },
        placeholder: "Escriba su Post aquí...",

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

    let movieOption = datalistElem.querySelector(`option[value = "${movieName}"]`);

    if(!movieOption)
        return;

    let movieId = movieOption.dataset.id;

    let newInput = document.createElement("input");
    newInput.setAttribute('id', 'movies');
    newInput.setAttribute('name', 'movies');
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
    let movieBadge = createBadge(moviesSelectedElem, movieName);

    movieBadge.closeElem.addEventListener('click',
        () => unselectMovie(formElem, datalistElem, inputElem, newInput, moviesSelectedElem, movieBadge.badgeElem), false);

    updateMoviesEnabledStatus(formElem, inputElem)
}

function unselectMovie(formElem, datalistElem, inputElem, inputToRemove, moviesSelectedElem, movieBadgeElem){
    let opt = document.createElement("option");

    opt.setAttribute('value', inputToRemove.dataset.movieName);
    opt.setAttribute('data-id', inputToRemove.value);

    datalistElem.appendChild(opt);

    formElem.removeChild(inputToRemove);

    if(moviesSelectedElem && movieBadgeElem)
        moviesSelectedElem.removeChild(movieBadgeElem);

    updateMoviesEnabledStatus(formElem, inputElem)
}

function addTag(formElem, inputElem, tagsSelectedElem){
    let tagName = inputElem.value;
    if(!tagName)
        return;

    let newInput = document.createElement("input");
    newInput.setAttribute('id', 'tags');
    newInput.setAttribute('name', 'tags');
    newInput.setAttribute('type', 'text');
    newInput.setAttribute('value', tagName);
    newInput.style.display = 'none';


    formElem.appendChild(newInput);

    inputElem.value = "";

    let tagBadge = createBadge(tagsSelectedElem, tagName);

    tagBadge.closeElem.addEventListener('click',
        () => unselectTag(formElem, inputElem, newInput, tagsSelectedElem, tagBadge.badgeElem), false);

    updateTagsEnabledStatus(formElem, inputElem);
}

/* <span class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom">
         ${text}
         <button class="uk-margin-small-left uk-light" type="button" uk-close></button>
       </span> */
function createBadge(parentElem, text) {
    let closeElem = document.createElement("button");

    closeElem.setAttribute('class', 'uk-margin-small-left uk-light');
    closeElem.setAttribute('type', 'button');
    closeElem.setAttribute('uk-close', '');

    let badgeElem = document.createElement("span");

    badgeElem.setAttribute('class', 'uk-badge disabled uk-padding-small uk-margin-small-right uk-margin-small-bottom');

    badgeElem.appendChild(document.createTextNode(text));
    badgeElem.appendChild(closeElem);

    parentElem.appendChild(badgeElem);

    return {badgeElem, closeElem};
}

function unselectTag(formElem, inputElem, inputToRemove, tagsSelectedElem, tagBadgeElem){

    formElem.removeChild(inputToRemove);

    if(tagsSelectedElem && tagBadgeElem)
        tagsSelectedElem.removeChild(tagBadgeElem);

    updateTagsEnabledStatus(formElem, inputElem);
}

// function cancelModal(formElem, datalistElem, moviesSelectedElem) {
//
//     formElem.querySelectorAll("input[name ^= 'movies']")
//         .forEach(movieInputElem => unselectMovie(formElem, datalistElem, movieInputElem));
//
//     moviesSelectedElem.innerHTML = "";
// }

function updateTagsEnabledStatus(formElem, inputElem) {
    let tagsCount = formElem.querySelectorAll("input[name ^= 'tags']").length;

    inputElem.disabled = tagsCount >= 5;
}

function updateMoviesEnabledStatus(formElem, inputElem) {
    let moviesCount = formElem.querySelectorAll("input[name ^= 'movies']").length;

    inputElem.disabled = moviesCount >= 19;
}

function validateModal(formElem, movieErrorElem) {
    let moviesCount = formElem.querySelectorAll("input[name ^= 'movies']").length;

    if(moviesCount <= 0) {
        movieErrorElem.style.display = "block";
        return false;
    }

    return true;
}
function bodyLengthChecker(event, bodyLength, bodyCounter, submitBtn) {
    const currentLength = event.currentTarget.value.length;
    bodyCounter.innerText = `${currentLength}/${bodyLength}`;

    if(currentLength > bodyLength) {
        bodyCounter.classList.remove('uk-text-muted');
        bodyCounter.classList.add('uk-text-danger');
        submitBtn.disabled = true;
    }
    else if(submitBtn.disabled) {
        bodyCounter.classList.remove('uk-text-danger');
        bodyCounter.classList.add('uk-text-muted');
        submitBtn.disabled = false;
    }
}