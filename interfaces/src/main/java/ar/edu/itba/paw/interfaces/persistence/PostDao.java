package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    enum FetchRelation {
        MOVIES, COMMENTS
    }

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    long register(String title, String body, long category, long user, Set<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id, EnumSet<FetchRelation> includedRelations);

    Collection<Post> getAllPosts(EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> findPostsByMovieId(long id, EnumSet<FetchRelation> includedRelations);

    Collection<Post> searchPosts(String title, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> searchPostsByCategory(String title, String category, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> searchPostsOlderThan(String title, LocalDateTime fromDate, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> searchPostsByCategoryAndOlderThan(String title, String category, LocalDateTime fromDate, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);
}