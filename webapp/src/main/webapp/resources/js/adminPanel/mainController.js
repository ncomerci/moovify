window.addEventListener('load', () => {

    document.querySelectorAll(".restore-btn").forEach(
        btn => btn.addEventListener('click', () => restore(btn.dataset.id), false));

}, false);

function restore(id) {
    const form = document.forms['restore-form'];
    form.action += `/${id}`;
    form.submit();
}