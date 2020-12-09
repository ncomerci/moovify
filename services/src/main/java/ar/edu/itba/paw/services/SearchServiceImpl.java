package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PostDao postDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private UserDao userDao;

    private enum PostSearchOptions {
        BY_CATEGORY, OLDER_THAN
    }

    private enum MovieSearchOptions {
        BY_CATEGORY, BY_RELEASE_DATE
    }

    private enum UserSearchOptions {
        BY_ROLE
    }

    private final List<String> postCategoriesOptions;
    private final static Map<String, LocalDateTime> postPeriodOptionsMap = getPostPeriodOptionsMap();

    private final List<String> movieCategoriesOptions;
    private final static Map<String, LocalDate> movieDecadeMap = getMovieDecadeMap();

    private final static Map<String, Role> userRoleOptionsMap = getUserRoleOptionsMap();

    private static Map<String, LocalDateTime> getPostPeriodOptionsMap() {
        final Map<String, LocalDateTime> periodOptions = new LinkedHashMap<>();

        periodOptions.put("pastYear", LocalDateTime.now().minusYears(1));
        periodOptions.put("pastMonth", LocalDateTime.now().minusMonths(1));
        periodOptions.put("pastWeek", LocalDateTime.now().minusWeeks(1));
        periodOptions.put("pastDay", LocalDateTime.now().minusDays(1));

        return periodOptions;
    }

    private static Map<String, LocalDate> getMovieDecadeMap() {
        final Map<String, LocalDate> decadeMap = new LinkedHashMap<>();

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

    private static Map<String, Role> getUserRoleOptionsMap() {
        final Map<String, Role> roleOptions = new LinkedHashMap<>();

        roleOptions.put("user", Role.USER);
        roleOptions.put("admin", Role.ADMIN);

        return roleOptions;
    }

    @Autowired
    public SearchServiceImpl(PostCategoryDao postCategoryDao, MovieCategoryDao movieCategoryDao) {
        postCategoriesOptions = postCategoryDao.getAllPostCategories().stream().map(PostCategory::getName).collect(Collectors.toList());
        movieCategoriesOptions = movieCategoryDao.getAllCategories().stream().map(MovieCategory::getName).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<Post>> searchPosts(String query, String category, String period, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        final EnumSet<PostSearchOptions> options = EnumSet.noneOf(PostSearchOptions.class);

        final PostDao.SortCriteria sc = postService.getPostSortCriteria(sortCriteria);

        if(category != null && postCategoriesOptions.contains(category))
            options.add(PostSearchOptions.BY_CATEGORY);


        if(period != null && postPeriodOptionsMap.containsKey(period))
            options.add(PostSearchOptions.OLDER_THAN);

        LOGGER.debug("Search Posts using Filter Options {} and Sort Criteria {}", options, sc);

        if(options.isEmpty())
            return Optional.of(postDao.searchPosts(query, enabled, sc, pageNumber, pageSize));

        else if(options.size() == 1) {

            if(options.contains(PostSearchOptions.OLDER_THAN))
                return Optional.of(postDao.searchPostsOlderThan(query, postPeriodOptionsMap.get(period), enabled, sc, pageNumber, pageSize));

            else if(options.contains(PostSearchOptions.BY_CATEGORY))
                return Optional.of(postDao.searchPostsByCategory(query, category, enabled, sc, pageNumber, pageSize));
        }

        else if(options.contains(PostSearchOptions.OLDER_THAN) && options.contains(PostSearchOptions.BY_CATEGORY) && options.size() == 2)
            return Optional.of(postDao.searchPostsByCategoryAndOlderThan(query, category, postPeriodOptionsMap.get(period), enabled, sc, pageNumber, pageSize));

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PaginatedCollection<Movie>> searchMovies(String query, String category, String decade, String sortCriteria, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        final EnumSet<MovieSearchOptions> options = EnumSet.noneOf(MovieSearchOptions.class);

        final MovieDao.SortCriteria sc = movieService.getMovieSortCriteria(sortCriteria);

        LocalDate since = LocalDate.ofYearDay(1900,1);
        LocalDate upTo = LocalDate.ofYearDay(2100, 1);

        if(category != null && movieCategoriesOptions.contains(category))
            options.add(MovieSearchOptions.BY_CATEGORY);

        if(decade != null && movieDecadeMap.containsKey(decade)){
            options.add(MovieSearchOptions.BY_RELEASE_DATE);
            since = movieDecadeMap.get(decade);
            upTo = since.plusYears(10);
        }

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
    public Optional<PaginatedCollection<User>> searchUsers(String query, String role, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {

        if(query == null)
            return Optional.empty();

        final EnumSet<UserSearchOptions> options = EnumSet.noneOf(UserSearchOptions.class);

        final UserDao.SortCriteria sc = userService.getUserSortCriteria(sortCriteria);

        if(role != null && userRoleOptionsMap.containsKey(role))
            options.add(UserSearchOptions.BY_ROLE);

        LOGGER.debug("Search Users using Filter Options {} and Sort Criteria {}", options, sc);

        if(options.isEmpty())
            return Optional.of(userDao.searchUsers(query, enabled, sc, pageNumber, pageSize));

        else if(options.contains(UserSearchOptions.BY_ROLE))
            return Optional.of(userDao.searchUsersByRole(query, userRoleOptionsMap.get(role), enabled, sc, pageNumber, pageSize));

        return Optional.empty();
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
