package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    private PostDao postDao;

    public enum SortCriteria {
        NEWEST(0),
        OLDEST(1);

        int id;

        SortCriteria(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private enum FilterCriteria{
        BY_POST_TITLE(1),
        BY_TAGS(2),
        BY_MOVIE_TITLE(4);

        int id;

        FilterCriteria(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private final Map<Integer, Map<Integer, PostFindMethod>> findMethodsMap;

    @Autowired
    public SearchServiceImpl(PostDao postDao) {
        this.postDao = postDao;
        findMethodsMap = new HashMap<>();
        findMethodsMap.put(SortCriteria.NEWEST.getId(), new HashMap<>());
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId(), postDao::findPostsByTitleOrderByNewest);
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_TAGS.getId(), postDao::findPostsByTagsOrderByNewest);
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByMoviesOrderByNewest);
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() + FilterCriteria.BY_TAGS.getId(), postDao::findPostsByTitleAndTagsOrderByNewest);
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() + FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByTitleAndMoviesOrderByNewest);
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_TAGS.getId() + FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByTagsAndMoviesOrderByNewest);
        findMethodsMap.get(SortCriteria.NEWEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() + FilterCriteria.BY_TAGS.getId() + FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByTitleAndTagsAndMoviesOrderByNewest);
        findMethodsMap.put(SortCriteria.OLDEST.getId(), new HashMap<>());
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() , postDao::findPostsByTitleOrderByOldest);
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_TAGS.getId(), postDao::findPostsByTagsOrderByOldest);
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByMoviesOrderByOldest);
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() + FilterCriteria.BY_TAGS.getId(), postDao::findPostsByTitleAndTagsOrderByOldest);
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() + FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByTitleAndMoviesOrderByOldest);
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_TAGS.getId() + FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByTagsAndMoviesOrderByOldest);
        findMethodsMap.get(SortCriteria.OLDEST.getId()).put(FilterCriteria.BY_POST_TITLE.getId() + FilterCriteria.BY_TAGS.getId() + FilterCriteria.BY_MOVIE_TITLE.getId(), postDao::findPostsByTitleAndTagsAndMoviesOrderByOldest);
    }

    /*@Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies) {
        return postDao.findPostsByPostAndMovieTitle(title, withMovies);
    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies, boolean withComments) {
        return postDao.findPostsByPostAndMovieTitle(title, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByTitle(String title, boolean withMovies, boolean withComments) {
        return postDao.findPostsByTitle(title, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies, boolean withComments) {
        return postDao.findPostsByMovieTitle(movie_title, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies, boolean withComments) {
        return postDao.findPostsByMovieId(movie_id, withMovies, withComments);
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies) {
        return postDao.findPostsByMovieId(movie_id, withMovies);*/


    public Collection<Post> findPostsBy(String query, Collection<String> filterCriteria, String sortCriteria, boolean withMovies, boolean withComments){
        return findMethodsMap.get(SortCriteria.valueOf(sortCriteria.toUpperCase()).getId()).get(
                filterCriteria.stream().map(fc -> FilterCriteria.valueOf(fc.toUpperCase()).getId()).reduce(0, Integer::sum))
                .find(query, withMovies, withComments);
    }

    @FunctionalInterface
    private interface PostFindMethod{

        Collection<Post> find(String query, boolean withMovies, boolean withComments);

    }
}
