package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    private final PostDao postDao;

    private enum SearchOptions{
        BY_CATEGORY, OLDER_THAN
    }

    private final List<String> categoriesOptions = Arrays.asList("critique", "debate", "watchlist", "news");
    private final Map<String, PostDao.SortCriteria> sortCriteriaMap;
    private final Map<String, LocalDateTime> periodOptions;

    @Autowired
    public SearchServiceImpl(PostDao postDao) {

        this.postDao = postDao;

        sortCriteriaMap = new HashMap<>();
        sortCriteriaMap.put("newest", PostDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", PostDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("default", PostDao.SortCriteria.NEWEST);

        periodOptions = new HashMap<>();
        periodOptions.put("past-year", LocalDateTime.now().minusYears(1));
        periodOptions.put("past-month", LocalDateTime.now().minusMonths(1));
        periodOptions.put("past-week", LocalDateTime.now().minusWeeks(1));
        periodOptions.put("past-day", LocalDateTime.now().minusDays(1));
    }

    @Override
    public Collection<Post> searchPosts(String query, String category, String period, String sortCriteria) {

        EnumSet<PostDao.FetchRelation> fetchRelation = EnumSet.noneOf(PostDao.FetchRelation.class);
        EnumSet<SearchOptions> options = EnumSet.noneOf(SearchOptions.class);
        PostDao.SortCriteria sc;

        if(category != null && categoriesOptions.contains(category))
            options.add(SearchOptions.BY_CATEGORY);


        if(period != null && periodOptions.containsKey(period))
            options.add(SearchOptions.OLDER_THAN);


        if(sortCriteria != null && sortCriteriaMap.containsKey(sortCriteria))
            sc = sortCriteriaMap.get(sortCriteria);

        else
            sc = sortCriteriaMap.get("default");


        if(options.equals(EnumSet.noneOf(SearchOptions.class)))
            return postDao.searchPosts(query, fetchRelation, sc);

        else if(options.equals(EnumSet.of(SearchOptions.OLDER_THAN)))
            return postDao.searchPostsOlderThan(query, periodOptions.get(period), fetchRelation, sc);

        else if(options.equals(EnumSet.of(SearchOptions.BY_CATEGORY)))
            return postDao.searchPostsByCategory(query, category, fetchRelation, sc);

        else if(options.equals(EnumSet.of(SearchOptions.BY_CATEGORY, SearchOptions.OLDER_THAN)))
            return postDao.searchPostsByCategoryAndOlderThan(query, category, periodOptions.get(period), fetchRelation, sc);

        else
            return new ArrayList<>();
    }
}
