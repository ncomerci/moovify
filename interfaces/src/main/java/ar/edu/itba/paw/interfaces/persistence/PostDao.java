package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

public interface PostDao {

    Post findById(long id);

}
