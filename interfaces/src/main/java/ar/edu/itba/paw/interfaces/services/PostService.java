package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Optional;

public interface PostService {

    public Optional<Post> findById(long id);

    public Post register(String title, String email, String body);

}