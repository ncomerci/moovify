package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PostDaoImpl implements PostDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);

    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String TAGS = TableNames.TAGS.getTableName();
    private static final String POST_CATEGORY = TableNames.POST_CATEGORY.getTableName();

    @PersistenceContext
    private EntityManager em;

    private static final String NATIVE_BASE_POST_FROM = "FROM " + POSTS;

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
    public Post register(String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, Collection<Movie> movies, boolean enabled) {

        final Post post = new Post(LocalDateTime.now(), title, body, wordCount, category, user, tags, enabled, Collections.emptyList(), movies, Collections.emptyList());

        em.persist(post);

        post.setTotalLikes(0L);

        return post;
    }

    @Override
    public Optional<Post> findPostById(long id) {

        LOGGER.info("Find Post By Id {}", id);
        return findPostByIdAndEnabled(id, true);
    }

    @Override
    public Optional<Post> findDeletedPostById(long id) {

        LOGGER.info("Find Deleted Post By Id {}", id);
        return findPostByIdAndEnabled(id, false);
    }

    private Optional<Post> findPostByIdAndEnabled(long id, boolean enabled) {

        TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p WHERE p.id = :postId AND enabled = :enabled", Post.class)
                .setParameter("postId", id)
                .setParameter("enabled", enabled);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public PaginatedCollection<Post> getAllPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        return queryPosts("", sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Posts By Movie {} Order By {}. Page number {}, Page Size {}", movie.getId(), sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " +
                        POSTS + ".post_id IN ( " +
                            "SELECT " + POST_MOVIE + ".post_id " +
                            "FROM " + POST_MOVIE +
                            " WHERE " + POST_MOVIE + ".movie_id = ?)" +
                        " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ movie.getId() });
    }

    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Posts By User {} Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + POSTS + ".user_id = ? AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Post> getDeletedPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get Deleted Posts Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + POSTS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Post> searchPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags and Movie {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query });
    }

    @Override
    public PaginatedCollection<Post> searchDeletedPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Deleted Posts By Post Title, Tags and Movie {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                 "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + POSTS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query });
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {} And Category {} Order By {}. Page number {}, Page Size {}", query, category, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_SEARCH_BY_POST_CATEGORY +
                                    " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, category });
    }

    @Override
    public PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {} And Min Age {} Order By {}. Page number {}, Page Size {}", query, fromDate, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_SEARCH_POSTS_OLDER_THAN +
                                    " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, Timestamp.valueOf(fromDate) });
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {}, Category {} And Min Age {} Order By {}. Page number {}, Page Size {}", query, category, fromDate, sortCriteria, pageNumber, pageSize);

        return queryPosts(
                "WHERE " + NATIVE_SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + NATIVE_SEARCH_BY_POST_CATEGORY +
                                    " AND " + NATIVE_SEARCH_POSTS_OLDER_THAN +
                                    " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, category, Timestamp.valueOf(fromDate) });
    }

    private String buildNativeFromStatement() {
        return NATIVE_BASE_POST_FROM + " " + NATIVE_TOTAL_LIKES_FROM;
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

    private PaginatedCollection<Post> queryPosts(String nativeWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] params) {

        final String nativeSelect = "SELECT " + POSTS + ".post_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + POSTS + ".post_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhereStatement);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhereStatement, nativeOrderBy, nativePagination);

        final String fetchQuery = String.format(
                "SELECT p, sum(coalesce(likes.value, 0)) AS totalLikes " +
                        "FROM Post p LEFT OUTER JOIN p.likes likes " +
                        "WHERE p.id IN :postIds " +
                        "GROUP BY p " +
                        "%s", HQLOrderBy);

        // Calculate Total Posts Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalPostsNativeQuery = em.createNativeQuery(nativeCountQuery);

        addParamsToNativeQuery(totalPostsNativeQuery, params);

        final long totalPosts = ((Number) totalPostsNativeQuery.getSingleResult()).longValue();

        if(totalPosts == 0)
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalPosts);

        // Calculate Which Posts To Load And Load Their Ids
        final Query postIdsNativeQuery = em.createNativeQuery(nativeQuery);

        addParamsToNativeQuery(postIdsNativeQuery, params);

        @SuppressWarnings("unchecked")
        final Collection<Long> postIds =
                ((List<Number>)postIdsNativeQuery.getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        // Get Posts Based on Ids
        final Collection<Tuple> fetchQueryResult = em.createQuery(fetchQuery, Tuple.class)
                .setParameter("postIds", postIds)
                .getResultList();

        // Map Tuples To Posts
        final Collection<Post> posts = fetchQueryResult.stream().map(tuple -> {

            tuple.get(0, Post.class).setTotalLikes(tuple.get(1, Long.class));
            return tuple.get(0, Post.class);

        }).collect(Collectors.toList());

        return new PaginatedCollection<>(posts, pageNumber, pageSize, totalPosts);
    }
}