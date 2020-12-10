package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.Image;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.Size;

public class UpdateAvatarDto {

    @Image
    @FormDataParam("avatar")
    private FormDataBodyPart avatarBody;

    @Size(max = 10 * 1024 * 1024)
    @FormDataParam("avatar")
    private byte[] avatarBytes;

    public byte[] getAvatarBytes() {
        return avatarBytes;
    }

    public void setAvatarBytes(byte[] avatarBytes) {
        this.avatarBytes = avatarBytes;
    }

    public FormDataBodyPart getAvatarBody() {
        return avatarBody;
    }

    public void setAvatarBody(FormDataBodyPart avatarBody) {
        this.avatarBody = avatarBody;
    }
}
