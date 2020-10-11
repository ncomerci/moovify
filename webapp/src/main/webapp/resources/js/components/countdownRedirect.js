var countdownInteval;

window.addEventListener('load', () => {
    const countdownElem = document.getElementById('countdown');

    console.log(countdownElem);

    countdownInteval = setInterval(redirectCountdownInterval, 1000, countdownElem)
}, false);

function redirectCountdownInterval(countdownElem) {
    let currentSec = parseInt(countdownElem.innerText);

    if(currentSec === 0) {
        clearInterval(countdownInteval);
        window.location.replace(countdownElem.dataset.redirectUrl)
    }
    else
        countdownElem.innerText = currentSec - 1;
}