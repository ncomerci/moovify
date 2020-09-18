package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import java.util.Set;

public class PostCreateForm {

    @Size( min = 6, max = 200)
    private String title;

    @Email
    private String email;

    @Size( min = 1, max = 100000)
    private String body;

    @MaxTagsSizeConstraint
    private Set<String> tags;

    @MoviesSizeConstraint
    private Set<Long> movies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<Long> getMovies() {
        return movies;
    }

    public void setMovies(Set<Long> movies) {
        this.movies = movies;
    }

}
