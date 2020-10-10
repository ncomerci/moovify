window.addEventListener('load', () => {
   const inputFile = document.getElementById('avatar');
   const avatarUploadedStatusElem = document.getElementById('avatar-uploaded');
   const avatarNotUploadedStatusElem = document.getElementById('avatar-not-uploaded');
   const fileNameElem = document.getElementById('file-name');


   inputFile.addEventListener('change', (event) => handleImageUpload(event, avatarUploadedStatusElem, avatarNotUploadedStatusElem, fileNameElem), false);
}, false);

function handleImageUpload(event, avatarUploadedStatusElem, avatarNotUploadedStatusElem, fileNameElem) {
    const inputFile = event.target;
    const files = inputFile.files;

    if(files.length === 0) {
        avatarUploadedStatusElem.style.display = "none";
        avatarNotUploadedStatusElem.style.display = "block";
        return;
    }

    console.log(files[0]);
    console.log(files[0].name);

    fileNameElem.innerText = files[0].name;
    console.log(fileNameElem);

    avatarUploadedStatusElem.style.display = "block";
    avatarNotUploadedStatusElem.style.display = "none";
}

