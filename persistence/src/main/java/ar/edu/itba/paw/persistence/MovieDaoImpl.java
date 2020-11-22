package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MovieDaoImpl implements MovieDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDaoImpl.class);

    private static final String MOVIES = Movie.TABLE_NAME;
    private static final String POST_MOVIE = Post.POST_MOVIE_TABLE_NAME;
    private static final String POSTS = Post.TABLE_NAME;
    private static final String MOVIE_TO_MOVIE_CATEGORY = Movie.MOVIE_TO_MOVIE_CATEGORY_TABLE_NAME;
    private static final String MOVIE_CATEGORIES = MovieCategory.TABLE_NAME;

    private static final String NATIVE_BASE_MOVIE_FROM = "FROM " + MOVIES;

    private static final String NATIVE_POST_COUNT_FROM =
            "LEFT OUTER JOIN ( " +
                    "SELECT " + POST_MOVIE + ".movie_id, COUNT(" + POSTS + ".post_id) post_count " +
                    "FROM " + POST_MOVIE +
                        " INNER JOIN " + POSTS + " ON " + POSTS + ".post_id = " + POST_MOVIE + ".post_id " +
                    "WHERE " + POSTS + ".enabled = true " +
                    "GROUP BY " + POST_MOVIE + ".movie_id " +
                    ") POST_COUNT ON POST_COUNT.movie_id = " + MOVIES + ".movie_id";

    private static final String NATIVE_SEARCH_BY_MOVIE_TITLE = "LOWER(" + MOVIES + ".title) LIKE '%' || LOWER(?) || '%'";

    private static final String NATIVE_SEARCH_BY_CATEGORY = MOVIES + ".tmdb_id IN (" +
            " SELECT " + MOVIE_TO_MOVIE_CATEGORY + ".tmdb_id" +
            " FROM " + MOVIE_TO_MOVIE_CATEGORY +
            " WHERE tmdb_category_id IN ( " +
                " SELECT tmdb_category_id " +
                " FROM " + MOVIE_CATEGORIES +
                " WHERE LOWER(name) LIKE LOWER(?)" +
            "))";

    private static final String NATIVE_SEARCH_BY_RELEASE_DATE = MOVIES + ".release_date BETWEEN ? AND ?";


    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria,String> sortCriteriaHQLMap = initializeSortCriteriaHQLMap();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQueryMap() {

        final EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, MOVIES + ".release_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, MOVIES + ".release_date");
        sortCriteriaQuery.put(SortCriteria.POST_COUNT, "coalesce(POST_COUNT.post_count, 0) desc");
        sortCriteriaQuery.put(SortCriteria.TITLE, MOVIES + ".title");

        return sortCriteriaQuery;
    }

    private static EnumMap<SortCriteria, String> initializeSortCriteriaHQLMap() {

        final EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "m.releaseDate desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "m.releaseDate");
        sortCriteriaQuery.put(SortCriteria.POST_COUNT, "postCount desc NULLS LAST");
        sortCriteriaQuery.put(SortCriteria.TITLE, "m.title");

        return sortCriteriaQuery;
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage, String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Set<MovieCategory> categories) {

        final Movie movie = new Movie(LocalDateTime.now(), title, originalTitle, tmdbId, imdbId, originalLanguage, overview, popularity, runtime, voteAverage, releaseDate, null, Collections.emptySet(), categories);

        em.persist(movie);

        movie.setPostCount(0);

        LOGGER.info("Created Movie: {}", movie.getId());

        return movie;
    }

    @Override
    public Optional<Movie> findMovieById(long movie_id) {

        LOGGER.info("Find Movie By Id: {}", movie_id);
        return Optional.ofNullable(em.find(Movie.class, movie_id));
    }

    @Override
    public Collection<Movie> findMoviesById(Collection<Long> moviesId) {

        LOGGER.info("Find Movies By Id: {}", moviesId);

        if(moviesId.isEmpty())
            return Collections.emptyList();

        return em
                .createQuery("SELECT m FROM Movie m WHERE m.id in :moviesId", Movie.class)
                .setParameter("moviesId", moviesId)
                .getResultList();
    }

    @Override
    public PaginatedCollection<Movie> getAllMovies(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get All Movies Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);

        return queryMovies("", sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public Collection<Movie> getAllMoviesNotPaginated() {

        LOGGER.info("Get All Movies Not Paginated");

        return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    @Override
    public PaginatedCollection<Movie> searchMovies(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Movies By Movie Title {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);

        return queryMovies(
                "WHERE " + NATIVE_SEARCH_BY_MOVIE_TITLE,
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }

    @Override
    public PaginatedCollection<Movie> searchMoviesByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Movies By Movie Title {} And Category {} Order By {}. Page number {}, Page Size {}", query, category, sortCriteria, pageNumber, pageSize);

        return queryMovies(
                " WHERE " + NATIVE_SEARCH_BY_MOVIE_TITLE + " AND " + NATIVE_SEARCH_BY_CATEGORY,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, category });
    }

    @Override
    public PaginatedCollection<Movie> searchMoviesByReleaseDate(String query, LocalDate since, LocalDate upTo, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Movies By Movie Title {} And Release Date {} - {} Order By {}. Page number {}, Page Size {}", query, since, upTo, sortCriteria, pageNumber, pageSize);

        return queryMovies(
                "WHERE " + NATIVE_SEARCH_BY_MOVIE_TITLE + " AND " + NATIVE_SEARCH_BY_RELEASE_DATE,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, since, upTo });
    }

    @Override
    public PaginatedCollection<Movie> searchMoviesByCategoryAndReleaseDate(String query, String category, LocalDate since, LocalDate upTo, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Movies By Movie Title {}, Category {} And Release Date {} - {} Order By {}. Page number {}, Page Size {}", query, category, since, upTo, sortCriteria, pageNumber, pageSize);

        return queryMovies(
                "WHERE " + NATIVE_SEARCH_BY_MOVIE_TITLE + " AND " + NATIVE_SEARCH_BY_CATEGORY + " AND " + NATIVE_SEARCH_BY_RELEASE_DATE,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, category, since, upTo });
    }

    private String buildNativeFromStatement() {
        return NATIVE_BASE_MOVIE_FROM + " " + NATIVE_POST_COUNT_FROM;
    }

    private String buildNativeOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria Native implementation not found for {} in MovieDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildHQLOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaHQLMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria HQL implementation not found for {} in MovieDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaHQLMap.get(sortCriteria);
    }

    private String buildNativePaginationStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0) {
            LOGGER.error("Invalid pagination argument found in MovieDaoImpl. pageSize: {}, pageNumber: {}", pageSize, pageNumber);
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

    private PaginatedCollection<Movie> queryMovies(String nativeWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] params) {

        final String nativeSelect = "SELECT " + MOVIES + ".movie_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + MOVIES + ".movie_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhereStatement);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhereStatement, nativeOrderBy, nativePagination);

        final String fetchQuery = String.format(
                "SELECT m, count(posts.id) AS postCount " +
                        "FROM Movie m LEFT OUTER JOIN m.posts posts " +
                        "WHERE m.id IN :movieIds " +
                        "GROUP BY m " +
                        "%s", HQLOrderBy);

        LOGGER.debug("QueryMovies nativeCountQuery: {}", nativeCountQuery);
        LOGGER.debug("QueryMovies nativeQuery: {}", nativeQuery);
        LOGGER.debug("QueryMovies fetchQuery: {}", fetchQuery);

        // Calculate Total Movie Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalMoviesNativeQuery = em.createNativeQuery(nativeCountQuery);

        addParamsToNativeQuery(totalMoviesNativeQuery, params);

        final long totalMovies = ((Number) totalMoviesNativeQuery.getSingleResult()).longValue();

        if(totalMovies == 0) {
            LOGGER.debug("QueryMovies Total Count == 0");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalMovies);
        }

        // Calculate Which Movies To Load And Load Their Ids
        final Query movieIdsNativeQuery = em.createNativeQuery(nativeQuery);

        addParamsToNativeQuery(movieIdsNativeQuery, params);

        @SuppressWarnings("unchecked")
        final Collection<Long> movieIds =
                ((List<Number>)movieIdsNativeQuery.getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        if(movieIds.isEmpty()) {
            LOGGER.debug("QueryMovies Empty Page");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalMovies);
        }

        // Get Movies Based on Ids
        final Collection<Tuple> fetchQueryResult = em.createQuery(fetchQuery, Tuple.class)
                .setParameter("movieIds", movieIds)
                .getResultList();

        // Map Tuples To Movies
        final Collection<Movie> movies = fetchQueryResult.stream().map(tuple -> {

            tuple.get(0, Movie.class).setPostCount(tuple.get(1, Long.class).intValue());
            return tuple.get(0, Movie.class);

        }).collect(Collectors.toList());

        return new PaginatedCollection<>(movies, pageNumber, pageSize, totalMovies);
    }
}