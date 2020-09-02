package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Post {
    
    private final long id;
    private final LocalDateTime creationDate;
    private final String title;
    private final String body;
    private final int wordCount;
    private final String email;

    public Post(long id, LocalDateTime creationDate, String title, String body, int wordCount, String email) {
        this.id = id;
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.email = email;
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
}