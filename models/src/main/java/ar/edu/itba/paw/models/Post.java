package ar.edu.itba.paw.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class Post {
    
    private final long id;
    private final LocalDateTime creationDate;
    private final String title;
    private final String body;
    private final int wordCount;
    private final User user;
    private final PostCategory category;
    private final Collection<String> tags;
    private final boolean enabled;

    private static final int EN_WORDS_PER_MINUTE = 150;

    public Post(long id, LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Collection<String> tags, boolean enabled) {
        this.id = id;
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.user = user;
        this.category = category;
        this.tags = tags;
        this.enabled = enabled;
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

    public User getUser() {
        return user;
    }

    public PostCategory getCategory() {
        return category;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public int getReadingTimeMinutes() {
        return getWordCount() / EN_WORDS_PER_MINUTE;
    }

    public Duration getTimeSinceCreation() {
        return Duration.between(creationDate, LocalDateTime.now());
    }

    public long getDaysSinceCreation() {
        return getTimeSinceCreation().toDays();
    }

    public long getHoursSinceCreation() {
        return getTimeSinceCreation().toHours();
    }

    public long getMinutesSinceCreation() {
        return getTimeSinceCreation().toMinutes();
    }

    public boolean isEnabled() { return enabled; }
}