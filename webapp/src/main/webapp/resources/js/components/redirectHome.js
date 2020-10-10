var countdownInteval;

window.addEventListener('load', () => {
    const countdownElem = document.getElementById('countdown');

    countdownInteval = setInterval(redirectCountdownInterval, 1000, countdownElem)
});

function redirectCountdownInterval(countdownElem) {
    let currentSec = parseInt(countdownElem.value);

    if(currentSec === 0) {
        clearInterval(countdownInteval);
        window.location.replace(countdownElem.dataset.redirectUrl)
    }
    else
        countdownElem.value = currentSec - 1;
}