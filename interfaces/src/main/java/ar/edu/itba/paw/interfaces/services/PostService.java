package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Optional;
import java.util.Set;

public interface PostService {

    Optional<Post> findById(long id);

    Post register(String title, String email, String body, Set<Long> movies);

    Set<Post> getAllPosts();

}