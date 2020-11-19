package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.HomeService;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HomeServiceImpl implements HomeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeServiceImpl.class);

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    private static final PostDao.SortCriteria HOTTEST_POST_SORT_CRITERIA = PostDao.SortCriteria.HOTTEST;
    private static final PostDao.SortCriteria NEWEST_POST_SORT_CRITERIA = PostDao.SortCriteria.NEWEST;
    private static final UserDao.SortCriteria HOTTEST_USERS_SORT_CRITERIA = UserDao.SortCriteria.LIKES;

    @Override
    public Optional<PaginatedCollection<Post>> getHottestPosts( int pageNumber, int pageSize) {

        LOGGER.info("Search All Posts Order By {}. Page number {}, Page Size {}", HOTTEST_POST_SORT_CRITERIA, pageNumber, pageSize);

        return Optional.of(postDao.getAllPosts(HOTTEST_POST_SORT_CRITERIA, pageNumber, pageSize));
    }

    @Override
    public Optional<PaginatedCollection<Post>> getNewestPosts(int pageNumber, int pageSize) {
        LOGGER.info("Search All Posts Order By {}. Page number {}, Page Size {}", NEWEST_POST_SORT_CRITERIA, pageNumber, pageSize);

        return Optional.of(postDao.getAllPosts(NEWEST_POST_SORT_CRITERIA, pageNumber, pageSize));
    }

    @Override
    public Optional<PaginatedCollection<Post>> getFollowedUsersPosts(User user, int pageNumber, int pageSize) {
        LOGGER.info("Search All Posts of {}'s followed users Order By {}. Page number {}, Page Size {}", user, NEWEST_POST_SORT_CRITERIA, pageNumber, pageSize);

        return Optional.of(postDao.getFollowedUsersPosts(user, NEWEST_POST_SORT_CRITERIA, pageNumber, pageSize));
    }

    @Override
    public Optional<PaginatedCollection<User>> getHottestUsers(int pageNumber, int pageSize) {
        LOGGER.info("Search All Users Order By {}. Page number {}, Page Size {}", HOTTEST_USERS_SORT_CRITERIA, pageNumber, pageSize);

        return Optional.of(userDao.getAllUsers(HOTTEST_USERS_SORT_CRITERIA, pageNumber, pageSize));
    }
}
