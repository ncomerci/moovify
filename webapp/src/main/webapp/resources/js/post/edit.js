window.addEventListener('load', function(){

    let formElem = document.getElementById('edit-post-form');
    let easyMDE = configureEasyMDE(formElem);

});

function configureEasyMDE(formElem){

    let textarea = document.getElementById("edit-post-data");

    return new EasyMDE({
        element: textarea,
        spellChecker: false,
        forceSync: true,
        initialValue: textarea.innerText,
        minHeight: "300px", // This is the default minHeight
        parsingConfig: {
            allowAtxHeaderWithoutSpace: true,
            strikethrough: true,
            underscoresBreakWords: true
        },

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
                action: () => formElem.submit(),
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