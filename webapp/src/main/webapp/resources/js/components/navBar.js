window.addEventListener('load', () => {

    // Current Location Highlighting
    let path = window.location.pathname;
    let navItem = document.querySelector(`nav a[href = '${path}']`);

    if(navItem)
        navItem.parentElement.classList.add("uk-active");
}, false);