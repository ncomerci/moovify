package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PostDaoImpl implements PostDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Post register(String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, Set<Long> movies, boolean enabled) {
        return null;
    }


    @Override
    public Optional<Post> findPostById(long id) {
        return findPostByIdAndEnabled(id, true);
    }

    @Override
    public Optional<Post> findDeletedPostById(long id) {
        return findPostByIdAndEnabled(id, false);
    }

    private Optional<Post> findPostByIdAndEnabled(long id, boolean enabled) {

        TypedQuery<Post> query = em.createQuery("select p FROM Post p WHERE p.id = :postId AND enabled = :enabled", Post.class)
                .setParameter("postId", id)
                .setParameter("enabled", enabled);

        return query.getResultList().stream().findFirst();
//        return em.createQuery(
//                "SELECT p FROM Post p WHERE p.id = :id AND p.enabled = :enabled", Post.class)
//                .setParameter("id", id).setParameter("enabled", enabled).getResultList().stream().findFirst();
    }

    @Override
    public PaginatedCollection<Post> getAllPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        List<Post> posts = em.createQuery("SELECT p FROM Post p ORDER BY p.title", Post.class).getResultList();

        return new PaginatedCollection<>(posts, pageNumber, pageSize, posts.size());
    }

    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Post> getDeletedPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Post> searchPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        List<Post> posts = em.
                createQuery(
                "FROM Post p inner join p.movies movies where :query in movies.title and :query in p.tags and :query = p.title", Post.class)
                .setParameter("query", query).getResultList();
        return new PaginatedCollection<>(posts, pageNumber, pageSize, posts.size());
    }

    @Override
    public PaginatedCollection<Post> searchDeletedPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }
}