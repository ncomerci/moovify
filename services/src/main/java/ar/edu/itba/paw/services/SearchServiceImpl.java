package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.exceptions.NonReachableStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@DependsOn("dataSourceInitializer")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private UserDao userDao;

    private enum PostSearchOptions {
        BY_CATEGORY, OLDER_THAN
    }

    private enum UserSearchOptions {
        BY_ROLE
    }

    private static final PostDao.SortCriteria DEFAULT_POST_SORT_CRITERIA = PostDao.SortCriteria.NEWEST;
    private static final UserDao.SortCriteria DEFAULT_USER_SORT_CRITERIA = UserDao.SortCriteria.NAME;

    private final List<String> postCategoriesOptions;
    private final static Map<String, PostDao.SortCriteria> postSortCriteriaMap = getPostSortCriteriaMap();
    private final static Map<String, LocalDateTime> postPeriodOptionsMap = getPostPeriodOptionsMap();

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
    public SearchServiceImpl(PostCategoryDao postCategoryDao) {
        postCategoriesOptions = postCategoryDao.getAllPostCategories().stream().map(PostCategory::getName).collect(Collectors.toList());
    }

    @Override
    public Collection<String> getAllPostSortCriteria() {
        return postSortCriteriaMap.keySet();
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
    public Collection<String> getUserRoleOptions() {
        return userRoleOptionsMap.keySet();
    }

    @Override
    public PaginatedCollection<Post> searchPosts(String query, String category, String period, String sortCriteria, int pageNumber, int pageSize) {

        Objects.requireNonNull(query);

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


        if(options.isEmpty())
            return postDao.searchPosts(query, sc, pageNumber, pageSize);

        else if(options.size() == 1) {

            if(options.contains(PostSearchOptions.OLDER_THAN))
                return postDao.searchPostsOlderThan(query, postPeriodOptionsMap.get(period), sc, pageNumber, pageSize);

            else if(options.contains(PostSearchOptions.BY_CATEGORY))
                return postDao.searchPostsByCategory(query, category, sc, pageNumber, pageSize);
        }

        else if(options.contains(PostSearchOptions.OLDER_THAN) && options.contains(PostSearchOptions.BY_CATEGORY) && options.size() == 2)
            return postDao.searchPostsByCategoryAndOlderThan(query, category, postPeriodOptionsMap.get(period), sc, pageNumber, pageSize);

        throw new NonReachableStateException();
    }

    @Override
    public PaginatedCollection<Movie> searchMovies(String query, int pageNumber, int pageSize){

        Objects.requireNonNull(query);

        return movieDao.searchMovies(query, MovieDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<User> searchUsers(String query, String role, String sortCriteria, int pageNumber, int pageSize) {

        Objects.requireNonNull(query);

        final EnumSet<UserSearchOptions> options = EnumSet.noneOf(UserSearchOptions.class);
        final UserDao.SortCriteria sc;

        if(role != null && userRoleOptionsMap.containsKey(role))
            options.add(UserSearchOptions.BY_ROLE);

        if(sortCriteria != null && userSortCriteriaMap.containsKey(sortCriteria))
            sc = userSortCriteriaMap.get(sortCriteria);

        else
            sc = DEFAULT_USER_SORT_CRITERIA;

        if(options.isEmpty())
            return userDao.searchUsers(query, sc, pageNumber, pageSize);

        else if(options.contains(UserSearchOptions.BY_ROLE))
                return userDao.searchUsersByRole(query, userRoleOptionsMap.get(role), sc, pageNumber, pageSize);

        throw new NonReachableStateException();
    }
}
