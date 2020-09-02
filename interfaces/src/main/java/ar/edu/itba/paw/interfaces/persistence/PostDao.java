package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.Optional;

public interface PostDao {

    public Optional<Post> findById(long id);

    public Post register(String title, String email, String body);
}
