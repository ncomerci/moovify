package ar.edu.itba.paw.webapp.form.editProfile;

import ar.edu.itba.paw.webapp.form.Annotations.Avatar;
import org.springframework.web.multipart.MultipartFile;

public class AvatarEditForm {

    @Avatar
    private MultipartFile avatar;

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }
}
