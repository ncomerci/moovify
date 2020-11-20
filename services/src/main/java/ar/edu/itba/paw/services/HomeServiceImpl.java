package ar.edu.itba.paw.services;

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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getHottestPosts( int pageNumber, int pageSize) {
        return postDao.getAllPosts(HOTTEST_POST_SORT_CRITERIA, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getNewestPosts(int pageNumber, int pageSize) {
        return postDao.getAllPosts(NEWEST_POST_SORT_CRITERIA, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getFollowedUsersPosts(User user, int pageNumber, int pageSize) {
        LOGGER.info("Search All Posts of {}'s followed users", user);

        return postDao.getFollowedUsersPosts(user, NEWEST_POST_SORT_CRITERIA, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<User> getHottestUsers(int pageNumber, int pageSize) {
        return userDao.getAllUsers(HOTTEST_USERS_SORT_CRITERIA, pageNumber, pageSize);
    }
}
