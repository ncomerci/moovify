package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.Optional;

public interface SearchService {

    Collection<Post> searchPosts(String query, String category, String period, String sortCriteria);

}
