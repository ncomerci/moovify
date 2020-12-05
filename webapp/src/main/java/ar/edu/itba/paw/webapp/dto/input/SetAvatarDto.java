package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.Avatar;
import org.springframework.web.multipart.MultipartFile;

public class SetAvatarDto {

    @Avatar
    private MultipartFile avatar;

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }
}
