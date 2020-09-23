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

    Collection<Post> findPostsByMovieId(long movie_id, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByUserId(long user_id, EnumSet<FetchRelation> includedRelations);

    Collection<Post> searchPosts(String query, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> searchPostsByCategory(String query, String category, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);

    Collection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, EnumSet<FetchRelation> includedRelations, SortCriteria sortCriteria);
}