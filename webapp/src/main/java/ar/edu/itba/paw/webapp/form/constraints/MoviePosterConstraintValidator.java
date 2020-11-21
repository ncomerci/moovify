package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.annotations.MoviePoster;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MoviePosterConstraintValidator implements ConstraintValidator<MoviePoster, MultipartFile> {

    private static final String ACCEPTED_MIME_TYPES = "image/";

    @Override
    public void initialize(MoviePoster constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        return file.isEmpty() || (file.getContentType().contains(ACCEPTED_MIME_TYPES));
    }
}
