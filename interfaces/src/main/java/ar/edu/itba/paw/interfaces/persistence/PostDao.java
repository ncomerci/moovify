package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    enum FetchRelation {
        MOVIES, COMMENTS
    }

    long register(String title, String email, String body, long category, Set<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id, EnumSet<FetchRelation> includedRelations);

    Collection<Post> getAllPostsOrderByNewest(EnumSet<FetchRelation> includedRelations);

    Collection<Post> getAllPostsOrderByOldest(EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTagsOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTagsOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleAndMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleAndMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleAndTagsOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleAndTagsOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTagsAndMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTagsAndMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleAndTagsAndMoviesOrderByNewest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByTitleAndTagsAndMoviesOrderByOldest(String title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByMovieId(long id, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByMovieTitle(String movie_title, EnumSet<FetchRelation> includedRelations);

    Collection<Post> findPostsByPostAndMovieTitle(String title, EnumSet<FetchRelation> includedRelations);
}
