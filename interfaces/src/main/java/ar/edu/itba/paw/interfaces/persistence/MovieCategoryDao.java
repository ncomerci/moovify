package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MovieCategory;

import java.util.Collection;

public interface MovieCategoryDao {

    Collection<MovieCategory> findCategoriesById(Collection<Long> categoriesId);

    Collection<MovieCategory> findCategoriesByTmdbId(Collection<Long> categories);

    Collection<MovieCategory> getAllCategories();
}
