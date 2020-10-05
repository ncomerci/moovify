package ar.edu.itba.paw.webapp.form.Constraints;

import ar.edu.itba.paw.webapp.form.Annotations.Avatar;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AvatarConstraintValidator implements ConstraintValidator<Avatar, MultipartFile> {

    private static final long MAX_SIZE = 1024*1024;
    private static final String ACCEPTED_MIME_TYPES = "image/";

    public void initialize(Avatar constraint) {
    }

    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        return file.isEmpty() || (file.getSize() < MAX_SIZE && file.getContentType().contains(ACCEPTED_MIME_TYPES));
    }

}
