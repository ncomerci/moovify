package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Post {
    
    private final long id;
    private final LocalDateTime timestamp;
    private final String title;
    private final String body;
    private final int wordCount;
    private final String email;

    public Post(long id, LocalDateTime timestamp, String title, String body, int wordCount, String email) {
        this.id = id;
        this.timestamp = timestamp;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
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