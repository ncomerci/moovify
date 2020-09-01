window.addEventListener('load', () => {

    // Current Location Highlighting
    let path = window.location.pathname;
    let navItem = document.querySelector(`nav a[href = '${path}']`).parentElement;
    navItem.classList.add("uk-active");
}, false);