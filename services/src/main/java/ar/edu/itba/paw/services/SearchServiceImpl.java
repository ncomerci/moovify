package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class SearchServiceImpl implements SearchService {

    private PostDao postDao;

    private enum SortCriteria {
        NEWEST(0, "newest"),
        OLDEST(1, "oldest");

        public final int id;
        public final String name;

        SortCriteria(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static SortCriteria getSortByName(String name) {
            for(SortCriteria sort : SortCriteria.values()) {
                if(sort.name.equals(name))
                    return sort;
            }

            return null;
        }
    }

    // Enum id's are powers of 2 so the sum is unique no matter the combination
    private enum FilterCriteria{
        BY_POST_TITLE(1, "by_post_title"),
        BY_TAGS(2, "by_tags"),
        BY_MOVIE_TITLE(4, "by_movie_title");

        public final int id;
        public final String name;

        FilterCriteria(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static FilterCriteria getFilterByName(String name) {
            for(FilterCriteria filter : FilterCriteria.values()) {
                if(filter.name.equals(name))
                    return filter;
            }

            return null;
        }
    }

    private final Map<Integer, Map<Integer, PostFindMethod>> findMethodsMap;

    @Autowired
    public SearchServiceImpl(PostDao postDao) {
        this.postDao = postDao;

        findMethodsMap = initializeFindMethodsMap();
    }

    private Map<Integer, Map<Integer, PostFindMethod>> initializeFindMethodsMap() {
        Map<Integer, Map<Integer, PostFindMethod>> resultMap = new HashMap<>();

        // Newest
        resultMap.put(SortCriteria.NEWEST.id, new HashMap<>());
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_POST_TITLE.id, postDao::findPostsByTitleOrderByNewest);
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_TAGS.id, postDao::findPostsByTagsOrderByNewest);
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByMoviesOrderByNewest);
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_POST_TITLE.id + FilterCriteria.BY_TAGS.id, postDao::findPostsByTitleAndTagsOrderByNewest);
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_POST_TITLE.id + FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByTitleAndMoviesOrderByNewest);
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_TAGS.id + FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByTagsAndMoviesOrderByNewest);
        resultMap.get(SortCriteria.NEWEST.id).put(FilterCriteria.BY_POST_TITLE.id + FilterCriteria.BY_TAGS.id + FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByTitleAndTagsAndMoviesOrderByNewest);

        // Oldest
        resultMap.put(SortCriteria.OLDEST.id, new HashMap<>());
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_POST_TITLE.id , postDao::findPostsByTitleOrderByOldest);
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_TAGS.id, postDao::findPostsByTagsOrderByOldest);
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByMoviesOrderByOldest);
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_POST_TITLE.id + FilterCriteria.BY_TAGS.id, postDao::findPostsByTitleAndTagsOrderByOldest);
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_POST_TITLE.id + FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByTitleAndMoviesOrderByOldest);
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_TAGS.id + FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByTagsAndMoviesOrderByOldest);
        resultMap.get(SortCriteria.OLDEST.id).put(FilterCriteria.BY_POST_TITLE.id + FilterCriteria.BY_TAGS.id + FilterCriteria.BY_MOVIE_TITLE.id, postDao::findPostsByTitleAndTagsAndMoviesOrderByOldest);

        return resultMap;
    }

    public Optional<Collection<Post>> findPostsBy(String query, Collection<String> filterCriteria, String sortCriteria, boolean withMovies, boolean withComments){
        SortCriteria sort = SortCriteria.getSortByName(sortCriteria.toLowerCase());
        Stream<FilterCriteria> filterStream = filterCriteria.stream().map(fc -> FilterCriteria.getFilterByName(fc.toUpperCase()));

        if(filterStream.anyMatch(Objects::isNull) || sort == null)
            return Optional.empty();

        return Optional.of(
                findMethodsMap.get(sort.id)
                .get(filterStream.map(f -> f.id).reduce(0, Integer::sum))
                .find(query, withMovies, withComments)
        );
    }

    @FunctionalInterface
    private interface PostFindMethod{
        Collection<Post> find(String query, boolean withMovies, boolean withComments);
    }
}
