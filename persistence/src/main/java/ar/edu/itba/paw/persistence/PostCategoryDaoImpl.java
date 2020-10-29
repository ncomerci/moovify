package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.models.PostCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Optional;

@Repository
public class PostCategoryDaoImpl implements PostCategoryDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostCategoryDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<PostCategory> findPostCategoryById(long id) {

        LOGGER.info("Find Post Category By ID {}", id);
        final TypedQuery<PostCategory> categories = em.createQuery("SELECT p FROM PostCategory p where p.id = :id", PostCategory.class)
                .setParameter("id", id);

        return categories.getResultList().stream().findFirst();
    }

    @Override
    public Collection<PostCategory> getAllPostCategories() {

        LOGGER.info("Get All Post Categories");
        final TypedQuery<PostCategory> categories = em.createQuery("SELECT p FROM PostCategory p", PostCategory.class);

        return categories.getResultList();
    }
}