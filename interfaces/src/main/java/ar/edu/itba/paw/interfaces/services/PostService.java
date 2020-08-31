package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

public interface PostService {
    
    Post findById(long id);

}