package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.models.MovieCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;

@Repository
public class MovieCategoryDaoImpl implements MovieCategoryDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieCategoryDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<MovieCategory> findCategoriesByTmdbId(Collection<Long> categories) {

        LOGGER.info("Find Movie Categories by Tmdb Ids {}", categories);

        if(categories.isEmpty())
            return Collections.emptyList();

        final TypedQuery<MovieCategory> query =
                em.createQuery("FROM MovieCategory mc WHERE mc.tmdbCategoryId IN :categories", MovieCategory.class);

        query.setParameter("categories", categories);

        return query.getResultList();
    }

    @Override
    public Collection<MovieCategory> getAllCategories() {

        LOGGER.info("Get All Movie Categories");

        final TypedQuery<MovieCategory> query = em.createQuery("FROM MovieCategory", MovieCategory.class);

        return query.getResultList();
    }
}
