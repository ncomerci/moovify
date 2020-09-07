package ar.edu.itba.paw.models;

import java.time.LocalDateTime;
import java.util.Collection;

public class Post {
    
    private final long id;
    private final LocalDateTime creationDate;
    private final String title;
    private final String body;
    private final int wordCount;
    private final String email;
    private final Collection<String> tags;
    private final Collection<Movie> movies;

    /*
    * Average of words per minute read.
    * Extracted from
    * https://www.researchgate.net/publication/332380784_How_many_words_do_we_read_per_minute_A_review_and_meta-analysis_of_reading_rate
    */
    private static final int EN_WORDS_PER_MINUTE = 238;

    public Post(long id, LocalDateTime creationDate, String title, String body, int wordCount, String email, Collection<String> tags, Collection<Movie> movies, Collection<Comment> comments) {
        this.id = id;
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.email = email;
        this.tags = tags;
        this.movies = movies;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getEmail() {
        return email;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public Collection<Movie> getMovies() {
        return movies;
    }

    public int getReadingTimeMinutes() {
        return getWordCount() / EN_WORDS_PER_MINUTE;
    }
}