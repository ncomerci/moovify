package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

public interface HomeService {

    PaginatedCollection<Post> getHottestPosts(int pageNumber, int pageSize);

    PaginatedCollection<Post> getNewestPosts(int pageNumber, int pageSize);

    PaginatedCollection<Post> getFollowedUsersPosts(User user, int pageNumber, int pageSize);

    PaginatedCollection<User> getHottestUsers(int pageNumber, int pageSize);
}
