window.onload = function(){

    var easymde = new EasyMDE({
        element: document.getElementById("createPostData"),
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
        placeholder: "This is the Placeholder. SOlo aparece si no hay texto.",

        // Upload Image Support Configurations

        inputStyle: "textarea", // Could be contenteditable
        theme: "easymde", // Default

        toolbar: ["bold", "italic", "heading", "heading-smaller", "heading-bigger", "|",
            "quote", "unordered-list", "ordered-list", "|",
            "horizontal-rule", "strikethrough",
            "link", "image", "|",
            "preview", "side-by-side", "fullscreen", "|",
            "clean-block", "guide",
        ],

        renderingConfig: {
            sanitizerFunction: (dirtyHTML) => DOMPurify.sanitize(dirtyHTML),
        }

    });
}