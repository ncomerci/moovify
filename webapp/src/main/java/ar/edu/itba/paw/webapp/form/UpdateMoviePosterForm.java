package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.annotations.MoviePoster;
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
