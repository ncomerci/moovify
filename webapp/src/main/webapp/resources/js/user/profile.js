window.addEventListener('load', function(){

    let avatarUpdater = document.getElementById('avatar-edit');

    if(avatarUpdater)
        avatarUpdater.addEventListener('change',() => avatarUpdater.form.submit(),false);

    prepareEditUserModals();

}, false);

function prepareEditUserModals() {
    let formElem = document.getElementById('edit-user-form');

    // Name
    let nameFormInput = document.getElementById('user-edit-name-input');
    let nameFormError = document.getElementById('user-edit-name-error');
    let nameModal = document.getElementById('edit-name-modal');
    let nameModalInput = document.getElementById('edit-name-modal-input');
    let nameModalSubmit = document.getElementById('edit-name-modal-submit');
    let nameModalError = document.getElementById('edit-user-name-error');

    prepareEditUserField(formElem, nameModalSubmit, nameModal, nameFormError, nameFormInput,
        nameModalError, nameModalInput);

    // Username
    let usernameFormInput = document.getElementById('user-edit-username-input');
    let usernameFormError = document.getElementById('user-edit-username-error');
    let usernameModal = document.getElementById('edit-username-modal');
    let usernameModalInput = document.getElementById('edit-username-modal-input');
    let usernameModalSubmit = document.getElementById('edit-username-modal-submit');
    let usernameModalError = document.getElementById('edit-user-username-error');

    prepareEditUserField(formElem, usernameModalSubmit, usernameModal, usernameFormError,
        usernameFormInput, usernameModalError, usernameModalInput);

    // Description
    let descriptionFormInput = document.getElementById('user-edit-description-input');
    let descriptionFormError = document.getElementById('user-edit-description-error');
    let descriptionModal = document.getElementById('edit-description-modal');
    let descriptionModalInput = document.getElementById('edit-description-modal-input');
    let descriptionModalSubmit = document.getElementById('edit-description-modal-submit');
    let descriptionModalError = document.getElementById('edit-user-description-error');

    prepareEditUserField(formElem, descriptionModalSubmit, descriptionModal, descriptionFormError,
        descriptionFormInput, descriptionModalError, descriptionModalInput);
}

function prepareEditUserField(formElem, modalSubmit, modalElem, formError, formInput, modalError, modalInput) {

    if(formError != null) {
        modalInput.value = formInput.value;
        modalError.innerHTML = formError.innerHTML;
        UIkit.modal(modalElem).show();
    }
    else {
        formInput.value = modalInput.value;
    }

    modalSubmit.addEventListener('click', () => {
        formInput.value = modalInput.value;

        formElem.submit();
    }, false);
}