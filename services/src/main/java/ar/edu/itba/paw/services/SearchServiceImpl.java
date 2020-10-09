package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.persistence.PostDao.SortCriteria;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@DependsOn("dataSourceInitializer")
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private PostDao postDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;

    private enum PostSearchOptions {
        BY_CATEGORY, OLDER_THAN
    }

    private enum MovieSearchOptions {
        BY_CATEGORY, BY_RELEASE_DATE
    }

    private enum UserSearchOptions {
        BY_ROLE
    }

    private static final PostDao.SortCriteria DEFAULT_POST_SORT_CRITERIA = PostDao.SortCriteria.NEWEST;
    private static final MovieDao.SortCriteria DEFAULT_MOVIE_SORT_CRITERIA = MovieDao.SortCriteria.TITLE;
    private static final UserDao.SortCriteria DEFAULT_USER_SORT_CRITERIA = UserDao.SortCriteria.NAME;

    private final List<String> postCategoriesOptions;
    private final static Map<String, PostDao.SortCriteria> postSortCriteriaMap = getPostSortCriteriaMap();
    private final static Map<String, LocalDateTime> postPeriodOptionsMap = getPostPeriodOptionsMap();

    private final List<String> movieCategoriesOptions;
    private final static Map<String, MovieDao.SortCriteria> movieSortCriteriaMap = getMovieSortCriteriaMap();
    private final static Map<String, LocalDate> movieDecadeMap = getMovieDecadeMap();

    private final static Map<String, UserDao.SortCriteria> userSortCriteriaMap = getUserSortCriteriaMap();
    private final static Map<String, String> userRoleOptionsMap = getUserRoleOptionsMap();

    private static Map<String, PostDao.SortCriteria> getPostSortCriteriaMap() {
        Map<String, PostDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("newest", PostDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", PostDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("hottest", PostDao.SortCriteria.HOTTEST);

        return sortCriteriaMap;
    }

    private static Map<String, LocalDateTime> getPostPeriodOptionsMap() {
        Map<String, LocalDateTime> periodOptions = new LinkedHashMap<>();

        periodOptions.put("pastYear", LocalDateTime.now().minusYears(1));
        periodOptions.put("pastMonth", LocalDateTime.now().minusMonths(1));
        periodOptions.put("pastWeek", LocalDateTime.now().minusWeeks(1));
        periodOptions.put("pastDay", LocalDateTime.now().minusDays(1));

        return periodOptions;
    }

    private static Map<String, MovieDao.SortCriteria> getMovieSortCriteriaMap() {
        Map<String, MovieDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("title", MovieDao.SortCriteria.TITLE);
        sortCriteriaMap.put("newest", MovieDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", MovieDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("mostPosts", MovieDao.SortCriteria.POST_COUNT);

        return sortCriteriaMap;
    }

    private static Map<String, LocalDate> getMovieDecadeMap() {
        Map<String, LocalDate> decadeMap = new LinkedHashMap<>();

        decadeMap.put("1920s", LocalDate.ofYearDay(1920, 1));
        decadeMap.put("1930s", LocalDate.ofYearDay(1930, 1));
        decadeMap.put("1940s", LocalDate.ofYearDay(1940, 1));
        decadeMap.put("1950s", LocalDate.ofYearDay(1950, 1));
        decadeMap.put("1960s", LocalDate.ofYearDay(1960, 1));
        decadeMap.put("1970s", LocalDate.ofYearDay(1970, 1));
        decadeMap.put("1980s", LocalDate.ofYearDay(1980, 1));
        decadeMap.put("1990s", LocalDate.ofYearDay(1990, 1));
        decadeMap.put("2000s", LocalDate.ofYearDay(2000, 1));
        decadeMap.put("2010s", LocalDate.ofYearDay(2010, 1));

        return decadeMap;
    }

    private static Map<String, UserDao.SortCriteria> getUserSortCriteriaMap() {
        Map<String, UserDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("name", UserDao.SortCriteria.NAME);
        sortCriteriaMap.put("newest", UserDao.SortCriteria.NEWEST);
//        sortCriteriaMap.put("oldest", UserDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("likes", UserDao.SortCriteria.LIKES);

        return sortCriteriaMap;
    }

    private static Map<String, String> getUserRoleOptionsMap() {
        Map<String, String> roleOptions = new LinkedHashMap<>();

        roleOptions.put("user", Role.USER_ROLE);
        roleOptions.put("admin", Role.ADMIN_ROLE);

        return roleOptions;
    }

    @Autowired
    public SearchServiceImpl(PostCategoryDao postCategoryDao, MovieCategoryDao movieCategoryDao) {
        postCategoriesOptions = postCategoryDao.getAllPostCategories().stream().map(PostCategory::getName).collect(Collectors.toList());
        movieCategoriesOptions = movieCategoryDao.getAllCategories().stream().map(MovieCategory::getName).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<Post>> searchPosts(String query, String category, String period, String sortCriteria, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        final EnumSet<PostSearchOptions> options = EnumSet.noneOf(PostSearchOptions.class);
        final PostDao.SortCriteria sc;

        if(category != null && postCategoriesOptions.contains(category))
            options.add(PostSearchOptions.BY_CATEGORY);


        if(period != null && postPeriodOptionsMap.containsKey(period))
            options.add(PostSearchOptions.OLDER_THAN);


        if(sortCriteria != null && postSortCriteriaMap.containsKey(sortCriteria))
            sc = postSortCriteriaMap.get(sortCriteria);

        else
            sc = DEFAULT_POST_SORT_CRITERIA;

        LOGGER.debug("Search Posts using Filter Options {} and Sort Criteria {}", options, sc);

        if(options.isEmpty())
            return Optional.of(postDao.searchPosts(query, sc, pageNumber, pageSize));

        else if(options.size() == 1) {

            if(options.contains(PostSearchOptions.OLDER_THAN))
                return Optional.of(postDao.searchPostsOlderThan(query, postPeriodOptionsMap.get(period), sc, pageNumber, pageSize));

            else if(options.contains(PostSearchOptions.BY_CATEGORY))
                return Optional.of(postDao.searchPostsByCategory(query, category, sc, pageNumber, pageSize));
        }

        else if(options.contains(PostSearchOptions.OLDER_THAN) && options.contains(PostSearchOptions.BY_CATEGORY) && options.size() == 2)
            return Optional.of(postDao.searchPostsByCategoryAndOlderThan(query, category, postPeriodOptionsMap.get(period), sc, pageNumber, pageSize));

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<Movie>> searchMovies(String query, String category, String decade, String sortCriteria, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        final EnumSet<MovieSearchOptions> options = EnumSet.noneOf(MovieSearchOptions.class);
        final MovieDao.SortCriteria sc;
        LocalDate since = LocalDate.ofYearDay(1900,1);
        LocalDate upTo = LocalDate.ofYearDay(2100, 1);

        if(category != null && movieCategoriesOptions.contains(category))
            options.add(MovieSearchOptions.BY_CATEGORY);

        if(decade != null && movieDecadeMap.containsKey(decade)){
            options.add(MovieSearchOptions.BY_RELEASE_DATE);
            since = movieDecadeMap.get(decade);
            upTo = since.plusYears(10);
        }

        if (sortCriteria != null && movieSortCriteriaMap.containsKey(sortCriteria))
            sc = movieSortCriteriaMap.get(sortCriteria);
        else
            sc = DEFAULT_MOVIE_SORT_CRITERIA;

        LOGGER.debug("Search Movies using Filter Options {} and Sort Criteria {}", options, sc);

        if(options.isEmpty())
            return Optional.of(movieDao.searchMovies(query, sc, pageNumber, pageSize));

        else if (options.size() == 1) {

            if (options.contains(MovieSearchOptions.BY_CATEGORY))
                return Optional.of(movieDao.searchMoviesByCategory(query, category, sc, pageNumber, pageSize));

            else if (options.contains(MovieSearchOptions.BY_RELEASE_DATE))
                return Optional.of(movieDao.searchMoviesByReleaseDate(query, since, upTo, sc, pageNumber, pageSize));
        }

        else if (options.size() == 2 && options.contains(MovieSearchOptions.BY_CATEGORY) && options.contains(MovieSearchOptions.BY_RELEASE_DATE))
            return Optional.of(movieDao.searchMoviesByCategoryAndReleaseDate(query, category, since, upTo, sc, pageNumber, pageSize));

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<User>> searchUsers(String query, String role, String sortCriteria, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        final EnumSet<UserSearchOptions> options = EnumSet.noneOf(UserSearchOptions.class);
        final UserDao.SortCriteria sc;

        if(role != null && userRoleOptionsMap.containsKey(role))
            options.add(UserSearchOptions.BY_ROLE);

        if(sortCriteria != null && userSortCriteriaMap.containsKey(sortCriteria))
            sc = userSortCriteriaMap.get(sortCriteria);

        else
            sc = DEFAULT_USER_SORT_CRITERIA;

        LOGGER.debug("Search Users using Filter Options {} and Sort Criteria {}", options, sc);

        if(options.isEmpty())
            return Optional.of(userDao.searchUsers(query, sc, pageNumber, pageSize));

        else if(options.contains(UserSearchOptions.BY_ROLE))
            return Optional.of(userDao.searchUsersByRole(query, userRoleOptionsMap.get(role), sc, pageNumber, pageSize));

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<Post>> searchDeletedPosts(String query, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        return Optional.of(postDao.searchDeletedPosts(query, SortCriteria.NEWEST, pageNumber, pageSize));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<Comment>> searchDeletedComments(String query, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        return Optional.of(commentDao.searchDeletedComments(query, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<User>> searchDeletedUsers(String query, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        return Optional.of(userDao.searchDeletedUsers(query, UserDao.SortCriteria.NEWEST, pageNumber, pageSize));
    }

    @Override
    public Collection<String> getAllPostSortCriteria() {
        return postSortCriteriaMap.keySet();
    }

    @Override
    public Collection<String> getAllMoviesSortCriteria() {
        return movieSortCriteriaMap.keySet();
    }

    @Override
    public Collection<String> getAllUserSortCriteria() {
        return userSortCriteriaMap.keySet();
    }

    @Override
    public Collection<String> getPostPeriodOptions() {
        return postPeriodOptionsMap.keySet();
    }

    @Override
    public Collection<String> getPostCategories() {
        return postCategoriesOptions;
    }

    @Override
    public Collection<String> getMoviesCategories() {
        return movieCategoriesOptions;
    }

    @Override
    public Collection<String> getMoviesDecades() {
        return movieDecadeMap.keySet();
    }

    @Override
    public Collection<String> getUserRoleOptions() {
        return userRoleOptionsMap.keySet();
    }
}
