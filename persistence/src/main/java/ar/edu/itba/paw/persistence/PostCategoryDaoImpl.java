package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.models.PostCategory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class PostCategoryDaoImpl implements PostCategoryDao {

    @Override
    public Optional<PostCategory> findPostCategoryById(long id) {
        return Optional.empty();
    }

    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return null;
    }
}