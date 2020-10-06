window.addEventListener('load', function(){

    let avatarUpdater = document.getElementById('avatar-edit');

    if(avatarUpdater)
        avatarUpdater.addEventListener('change',() => avatarUpdater.form.submit(),false);

}, false);