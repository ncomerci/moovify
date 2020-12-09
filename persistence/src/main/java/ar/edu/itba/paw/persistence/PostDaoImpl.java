package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PostDaoImpl implements PostDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);

    private static final String POSTS = Post.TABLE_NAME;
    private static final String MOVIES = Movie.TABLE_NAME;
    private static final String POST_MOVIE = Post.POST_MOVIE_TABLE_NAME;
    private static final String POSTS_LIKES = PostVote.TABLE_NAME;
    private static final String TAGS = Post.TAGS_TABLE_NAME;
    private static final String POST_CATEGORY = PostCategory.TABLE_NAME;
    private static final String USER_FAV_POST = User.USER_FAV_POST;
    private static final String USERS_FOLLOWS = User.USERS_FOLLOWS;

    @PersistenceContext
    private EntityManager em;

    private static final String NATIVE_BASE_POST_FROM = "FROM " + POSTS;

    private static final String NATIVE_CATEGORY_FROM =
            "INNER JOIN " + POST_CATEGORY + " ON " + POSTS + ".category_id = " + POST_CATEGORY + ".category_id";

    private static final String NATIVE_TOTAL_LIKES_FROM =
            "INNER JOIN " +
                    "(SELECT " + POSTS + ".post_id, COALESCE(SUM( " + POSTS_LIKES + ".value ), 0) likes " +
                    "FROM " + POSTS + " LEFT OUTER JOIN " + POSTS_LIKES + " on " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id" +
                    " GROUP BY " + POSTS + ".post_id ) " + POSTS_LIKES + " ON " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id";

    // Search Query Statements
    private static final String NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS = "( " +
                    "LOWER(" + POSTS + ".title) LIKE '%' || LOWER(?) || '%'" +

                    " OR " + POSTS + ".post_id IN ( " +
                        "SELECT " + TAGS + ".post_id FROM " + TAGS +
                        " WHERE LOWER(" +  TAGS + ".tag) LIKE '%' || LOWER(?) || '%' )" +

                    " OR " + POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        " FROM " + POST_MOVIE +
                            " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                        " WHERE LOWER(" + MOVIES + ".title) LIKE '%' || LOWER(?) || '%')" +
                    " )";

    private static final String NATIVE_SEARCH_POSTS_OLDER_THAN = POSTS + ".creation_date >= ?";

    private static final String NATIVE_SEARCH_BY_POST_CATEGORY = "LOWER(" + POST_CATEGORY + ".name) LIKE LOWER(?)";

    private static final String NATIVE_ENABLED_FILTER = POSTS + ".enabled = true";

    private static final String NATIVE_DISABLED_FILTER = POSTS + ".enabled = false";


    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria,String> sortCriteriaHQLMap = initializeSortCriteriaHQLMap();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQueryMap() {

        final EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, POSTS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, POSTS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, POSTS_LIKES + ".likes desc");

        return sortCriteriaQuery;
    }

    private static EnumMap<SortCriteria, String> initializeSortCriteriaHQLMap() {

        final EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "p.creationDate desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "p.creationDate");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, "totalLikes desc");

        return sortCriteriaQuery;
    }

    @Override
    public Post register(String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, Set<Movie> movies, boolean enabled) {

        final Post post = new Post(LocalDateTime.now(), title, body, wordCount, category, user, tags, false, null, enabled, Collections.emptySet(), movies, Collections.emptySet());

        em.persist(post);

        post.setTotalVotes(0L);

        LOGGER.info("Created Post: {}", post.getId());

        return post;
    }

    @Override
    public Optional<Post> findPostById(long id) {

        LOGGER.info("Find Post By Id {}", id);
        return Optional.ofNullable(em.find(Post.class, id));
    }

    @Override
    public PaginatedCollection<Post> getAllPosts(Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get All Posts Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);

        return queryPosts("", enabled, sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Posts By Movie {} Order By {}. Page number {}, Page Size {}", movie.getId(), sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " +
                        POSTS + ".post_id IN ( " +
                            "SELECT " + POST_MOVIE + ".post_id " +
                            "FROM " + POST_MOVIE +
                            " WHERE " + POST_MOVIE + ".movie_id = ?)",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ movie.getId() });
    }

    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Posts By User {} Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + POSTS + ".user_id = ?",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Post> getFollowedUsersPosts(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get User {} Followed Users Posts Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " +
                        POSTS + ".user_id IN ( " +
                        "SELECT " + USERS_FOLLOWS + ".user_follow_id " +
                        "FROM " + USERS_FOLLOWS +
                        " WHERE " + USERS_FOLLOWS + ".user_id = ?)",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Post> getUserFavouritePosts(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get User {} Favourite Posts Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " +
                        POSTS + ".post_id IN ( " +
                        "SELECT " + USER_FAV_POST + ".post_id " +
                        "FROM " + USER_FAV_POST +
                        " WHERE " + USER_FAV_POST + ".user_id = ?)",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Post> searchPosts(String query, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags and Movie {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query });
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategory(String query, String category, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {} And Category {} Order By {}. Page number {}, Page Size {}", query, category, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_SEARCH_BY_POST_CATEGORY,
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, category });
    }

    @Override
    public PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {} And Min Age {} Order By {}. Page number {}, Page Size {}", query, fromDate, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_SEARCH_POSTS_OLDER_THAN,
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, Timestamp.valueOf(fromDate) });
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {}, Category {} And Min Age {} Order By {}. Page number {}, Page Size {}", query, category, fromDate, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_SEARCH_BY_POST_CATEGORY +
                                    " AND " + NATIVE_SEARCH_POSTS_OLDER_THAN,
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, category, Timestamp.valueOf(fromDate) });
    }

    private String buildNativeFromStatement() {
        return NATIVE_BASE_POST_FROM + " " + NATIVE_CATEGORY_FROM + " " + NATIVE_TOTAL_LIKES_FROM;
    }

    private String buildNativeOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria Native implementation not found for {} in PostDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildHQLOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaHQLMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria HQL implementation not found for {} in PostDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaHQLMap.get(sortCriteria);
    }

    private String buildNativePaginationStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0) {
            LOGGER.error("Invalid pagination argument found in PostDaoImpl. pageSize: {}, pageNumber: {}", pageSize, pageNumber);
            throw new InvalidPaginationArgumentException();
        }

        return String.format("LIMIT %d OFFSET %d", pageSize, pageNumber * pageSize);
    }

    private void addParamsToNativeQuery(Query query, Object[] params) {
        if(params == null)
            return;

        int i = 1;

        for(Object param : params) {
            query.setParameter(i, param);
            i++;
        }
    }

    private String addEnabledFilter(String nativeWhereStatement, Boolean enabled) {
        if(enabled == null)
            return nativeWhereStatement;

        final StringBuilder sb;

        if(nativeWhereStatement != null)
            sb = new StringBuilder(nativeWhereStatement.trim());
        else
            sb = new StringBuilder();

        if(sb.length() == 0)
            sb.append("WHERE ");
        else
            sb.append(" AND ");

        if(enabled)
            sb.append(NATIVE_ENABLED_FILTER);
        else
            sb.append(NATIVE_DISABLED_FILTER);

        return sb.toString();
    }

    private PaginatedCollection<Post> queryPosts(String nativeWhereStatement, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] params) {

        final String nativeSelect = "SELECT " + POSTS + ".post_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + POSTS + ".post_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String nativeWhere = addEnabledFilter(nativeWhereStatement, enabled);

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhere);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhere, nativeOrderBy, nativePagination);

        final String fetchQuery = String.format(
                "SELECT p, sum(coalesce(likes.value, 0)) AS totalLikes " +
                        "FROM Post p LEFT OUTER JOIN p.votes likes " +
                        "WHERE p.id IN :postIds " +
                        "GROUP BY p " +
                        "%s", HQLOrderBy);

        LOGGER.debug("QueryPosts nativeCountQuery: {}", nativeCountQuery);
        LOGGER.debug("QueryPosts nativeQuery: {}", nativeQuery);
        LOGGER.debug("QueryPosts fetchQuery: {}", fetchQuery);

        // Calculate Total Posts Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalPostsNativeQuery = em.createNativeQuery(nativeCountQuery);

        addParamsToNativeQuery(totalPostsNativeQuery, params);

        final long totalPosts = ((Number) totalPostsNativeQuery.getSingleResult()).longValue();

        if(totalPosts == 0) {
            LOGGER.debug("QueryPosts Total Count == 0");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalPosts);
        }

        // Calculate Which Posts To Load And Load Their Ids
        final Query postIdsNativeQuery = em.createNativeQuery(nativeQuery);

        addParamsToNativeQuery(postIdsNativeQuery, params);

        @SuppressWarnings("unchecked")
        final Collection<Long> postIds =
                ((List<Number>)postIdsNativeQuery.getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        if(postIds.isEmpty()) {
            LOGGER.debug("QueryPosts Empty Page");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalPosts);
        }

        // Get Posts Based on Ids
        final Collection<Tuple> fetchQueryResult = em.createQuery(fetchQuery, Tuple.class)
                .setParameter("postIds", postIds)
                .getResultList();

        // Map Tuples To Posts
        final Collection<Post> posts = fetchQueryResult.stream().map(tuple -> {

            tuple.get(0, Post.class).setTotalVotes(tuple.get(1, Long.class));
            return tuple.get(0, Post.class);

        }).collect(Collectors.toList());

        return new PaginatedCollection<>(posts, pageNumber, pageSize, totalPosts);
    }

    @Override
    public PaginatedCollection<PostVote> getPostVotes(Post post, int pageNumber, int pageSize) {

        final String nativeSelect = "SELECT " + POSTS_LIKES + ".post_likes_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + POSTS_LIKES + ".post_likes_id)";

        final String nativeFrom = "FROM " + POSTS_LIKES;

        final String nativeWhere = "WHERE " + POSTS_LIKES + ".post_id = :post_id";

        final String nativeOrderBy = "ORDER BY " + POSTS_LIKES + ".post_likes_id";

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhere);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhere, nativeOrderBy, nativePagination);

        final String fetchQuery = "SELECT pv FROM PostVote pv WHERE pv.id IN :postVoteIds ORDER BY pv.id";

        LOGGER.debug("QueryPostVotes nativeCountQuery: {}", nativeCountQuery);
        LOGGER.debug("QueryPostsVotes nativeQuery: {}", nativeQuery);
        LOGGER.debug("QueryPostsVotes fetchQuery: {}", fetchQuery);

        final long totalPostVotes =
                ((Number) em.createNativeQuery(nativeCountQuery)
                        .setParameter("post_id", post.getId())
                        .getSingleResult())
                        .longValue();

        if(totalPostVotes == 0) {
            LOGGER.debug("QueryPostVotes Total Count == 0");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalPostVotes);
        }

        @SuppressWarnings("unchecked")
        final Collection<Long> postVoteIds =
                ((List<Number>) em.createNativeQuery(nativeQuery).setParameter("post_id", post.getId()).getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        if(postVoteIds.isEmpty()) {
            LOGGER.debug("QueryPostVotes Empty Page");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalPostVotes);
        }

        final Collection<PostVote> results = em.createQuery(fetchQuery, PostVote.class)
                .setParameter("postVoteIds", postVoteIds).getResultList();

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalPostVotes);
    }
}