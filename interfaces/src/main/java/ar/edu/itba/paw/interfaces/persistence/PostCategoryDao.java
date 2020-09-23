package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.PostCategory;

import java.util.Collection;
import java.util.Optional;

public interface PostCategoryDao {

    Optional<PostCategory> findById(long id);

    Collection<PostCategory> getAllPostCategories();
}
