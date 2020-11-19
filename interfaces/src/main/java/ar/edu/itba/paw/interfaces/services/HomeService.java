package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface HomeService {

    Optional<PaginatedCollection<Post>> getHottestPosts(int pageNumber, int pageSize);

    Optional<PaginatedCollection<Post>> getNewestPosts(int pageNumber, int pageSize);

    Optional<PaginatedCollection<Post>> getFollowedUsersPosts(User user, int pageNumber, int pageSize);

    Optional<PaginatedCollection<User>> getHottestUsers(int pageNumber, int pageSize);
}
