package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.MoviePoster;
import org.springframework.web.multipart.MultipartFile;

public class UpdateMoviePosterForm {

    @MoviePoster
    private MultipartFile poster;

    public MultipartFile getPoster() {
        return poster;
    }

    public void setPoster(MultipartFile poster) {
        this.poster = poster;
    }
}
