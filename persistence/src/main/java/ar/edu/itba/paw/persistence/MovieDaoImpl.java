package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MovieDaoImpl implements MovieDao {

    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIE_TO_MOVIE_CATEGORY = TableNames.MOVIE_TO_MOVIE_CATEGORY.getTableName();
    private static final String MOVIE_CATEGORIES = TableNames.MOVIE_CATEGORIES.getTableName();

    private static final String BASE_MOVIE_SELECT = "SELECT " +
            // Movie Table Columns - Alias: m_column_name
            MOVIES + ".movie_id m_movie_id, " +
            MOVIES + ".creation_date m_creation_date, " +
            MOVIES + ".release_date m_release_date, " +
            MOVIES + ".title m_title, " +
            MOVIES + ".original_title m_original_title, " +
            MOVIES + ".tmdb_id m_tmdb_id, " +
            MOVIES + ".imdb_id m_imdb_id, " +
            MOVIES + ".original_language m_original_language, " +
            MOVIES + ".overview m_overview, " +
            MOVIES + ".popularity m_popularity, " +
            MOVIES + ".runtime m_runtime, " +
            MOVIES + ".vote_average m_vote_average";

    private static final String POST_COUNT_SELECT = "coalesce(POST_COUNT.post_count, 0) m_post_count";

    private static final String MOVIE_CATEGORY_SELECT =
            // Movie Table Columns - Alias: mc_column_name
            MOVIE_CATEGORIES + ".category_id mc_category_id, " +
            MOVIE_CATEGORIES + ".tmdb_category_id mc_tmdb_category_id, " +
            MOVIE_CATEGORIES + ".name mc_name";

    private static final String BASE_MOVIE_FROM = "FROM " + MOVIES;

    private static final String POST_COUNT_FROM =
            "LEFT OUTER JOIN ( " +
                    "SELECT " + POST_MOVIE + ".movie_id, COUNT(" + POSTS + ".post_id) post_count " +
                    "FROM " + POST_MOVIE +
                        " INNER JOIN " + POSTS + " ON " + POSTS + ".post_id = " + POST_MOVIE + ".post_id " +
                    "WHERE " + POSTS + ".enabled = true " +
                    "GROUP BY " + POST_MOVIE + ".movie_id " +
            ") POST_COUNT ON POST_COUNT.movie_id = " + MOVIES + ".movie_id";

    private static final String CATEGORY_FROM =
            " INNER JOIN " + MOVIE_TO_MOVIE_CATEGORY + " ON " + MOVIE_TO_MOVIE_CATEGORY + ".tmdb_id = " + MOVIES + ".tmdb_id" +
            " INNER JOIN " + MOVIE_CATEGORIES + " ON " + MOVIE_TO_MOVIE_CATEGORY + ".tmdb_category_id = " + MOVIE_CATEGORIES + ".tmdb_category_id";


    private static final ResultSetExtractor<Collection<Movie>> MOVIE_ROW_MAPPER = (rs) -> {

            // Important use of LinkedHashMap to maintain Post insertion order
            final Map<Long, Movie> idToMovieMap = new HashMap<>();
            final Map<Long, MovieCategory> idToMovieCategoryMap = new HashMap<>();

            while(rs.next()) {

                final long movie_id = rs.getLong("m_movie_id");
                final long movie_category_tmdb_id = rs.getLong("mc_tmdb_category_id");

                if (!idToMovieMap.containsKey(movie_id)) {
                    idToMovieMap.put(movie_id,
                            new Movie(
                                    movie_id, rs.getObject("m_creation_date", LocalDateTime.class),
                                    rs.getString("m_title"),
                                    rs.getString("m_original_title"),
                                    rs.getLong("m_tmdb_id"),
                                    rs.getString("m_imdb_id"),
                                    rs.getString("m_original_language"),
                                    rs.getString("m_overview"),
                                    rs.getFloat("m_popularity"),
                                    rs.getFloat("m_runtime"),
                                    rs.getFloat("m_vote_average"),
                                    rs.getObject("m_release_date", LocalDate.class),
                                    rs.getLong("m_post_count"),
                                    // categories
                                    new HashSet<>())
                    );
                }

                if(!idToMovieCategoryMap.containsKey(movie_category_tmdb_id)) {
                    idToMovieCategoryMap.put(movie_category_tmdb_id, new MovieCategory(
                            rs.getLong("mc_category_id"),
                            rs.getLong("mc_tmdb_category_id"),
                            rs.getString("mc_name")));
                }

                idToMovieMap.get(movie_id).getCategories().add(idToMovieCategoryMap.get(movie_category_tmdb_id));
            }

            return idToMovieMap.values();
    };

    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQuery() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, MOVIES + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, MOVIES + ".creation_date");

        return sortCriteriaQuery;
    }

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert movieJdbcInsert;
    private final SimpleJdbcInsert movieToMovieCategoryJdbcInsert;

    @Autowired
    private MovieCategoryDao movieCategoryDao;

    @Autowired
    public MovieDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);

        movieJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(MOVIES)
                .usingGeneratedKeyColumns("movie_id");

        movieToMovieCategoryJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(MOVIE_TO_MOVIE_CATEGORY);
    }

    @Override
    public Movie register(String title, String originalTitle, long tmdbId, String imdbId, String originalLanguage,
                          String overview, float popularity, float runtime, float voteAverage, LocalDate releaseDate, Collection<Long> categories) {

        Objects.requireNonNull(title);
        Objects.requireNonNull(originalTitle);
        Objects.requireNonNull(imdbId);
        Objects.requireNonNull(originalLanguage);
        Objects.requireNonNull(overview);
        Objects.requireNonNull(releaseDate);
        Objects.requireNonNull(categories);

        LocalDateTime creationDate = LocalDateTime.now();

        Collection<MovieCategory> categoryCollection = movieCategoryDao.findCategoriesByTmdbId(categories);

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("title", title);
        map.put("original_title", originalTitle);
        map.put("tmdb_id", tmdbId);
        map.put("imdb_id", imdbId);
        map.put("original_language", originalLanguage);
        map.put("overview", overview);
        map.put("popularity", popularity);
        map.put("vote_average", voteAverage);
        map.put("runtime", runtime);
        map.put("release_date", releaseDate);

        final long id = movieJdbcInsert.executeAndReturnKey(map).longValue();

        for(Long category_id: categories){
            map = new HashMap<>();
            map.put("tmdb_category_id", category_id);
            map.put("tmdb_id", tmdbId);
            movieToMovieCategoryJdbcInsert.execute(map);
        }

        return new Movie(id, creationDate, title, originalTitle, tmdbId, imdbId,
                originalLanguage, overview, popularity, runtime, voteAverage, releaseDate, 0, categoryCollection);
    }

    private Collection<Movie> executeQuery(String select, String from, String where, String orderBy, Object[] args) {

        final String query = select + " " + from + " " + where + " " + orderBy;

        if(args != null)
            return jdbcTemplate.query(query, args, MOVIE_ROW_MAPPER);

        else
            return jdbcTemplate.query(query, MOVIE_ROW_MAPPER);
    }

    private Collection<Movie> buildAndExecuteQuery(String customWhereStatement, Object[] args){

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        return executeQuery(select, from, customWhereStatement, "", args);
    }

    private PaginatedCollection<Movie> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args) {

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        // Execute original query to count total posts in the query
        final int totalMovieCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + MOVIES + ".movie_id) " + from + " " + customWhereStatement, args, Integer.class);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String newWhere = "WHERE " + MOVIES + ".movie_id IN ( " +
                "SELECT AUX.movie_id " +
                "FROM (" +
                "SELECT ROW_NUMBER() OVER(" + orderBy + ") row_num, " + MOVIES + ".movie_id " +
                from + " " +
                customWhereStatement +
                " ) AUX " +
                "GROUP BY AUX.movie_id " +
                "ORDER BY MIN(AUX.row_num) " +
                pagination + ")";

        final Collection<Movie> results = executeQuery(select, from, newWhere, orderBy, args);

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalMovieCount);
    }

    private String buildSelectStatement() {
        return BASE_MOVIE_SELECT + ", " + POST_COUNT_SELECT + ", " + MOVIE_CATEGORY_SELECT;
    }

    private String buildFromStatement() {
        return BASE_MOVIE_FROM + " " + POST_COUNT_FROM + " " + CATEGORY_FROM;
    }

    private String buildOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria))
            throw new IllegalArgumentException("SortCriteria implementation not found for " + sortCriteria + " in MovieDaoImpl.");

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildLimitAndOffsetStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0)
            throw new IllegalArgumentException("Illegal Movie pagination arguments. Page Number: " + pageNumber + ". Page Size: " + pageSize);

        return "LIMIT " + pageSize + " OFFSET " + (pageNumber * pageSize);
    }

    @Override
    public Optional<Movie> findById(long id) {
        return buildAndExecuteQuery(" WHERE " + MOVIES + ".movie_id = ?", new Object[]{ id })
                .stream().findFirst();
    }

    @Override
    public Collection<Movie> findMoviesByPostId(long postId) {

        return buildAndExecuteQuery(" WHERE " + MOVIES + ".movie_id IN (" +
                        "SELECT " + POST_MOVIE + ".movie_id FROM " + POST_MOVIE + " WHERE " + POST_MOVIE + ".post_id = ?)",
                 new Object[]{ postId });
    }

    @Override
    public PaginatedCollection<Movie> getAllMovies(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(" ", sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public Collection<Movie> getAllMoviesNotPaginated() {
        return buildAndExecuteQuery(" ", null);
    }

    // TODO: Search by Category and Release Date Range
    @Override
    public PaginatedCollection<Movie> searchMovies(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(" WHERE " + MOVIES + ".title ILIKE '%' || ? || '%'",
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }
}
