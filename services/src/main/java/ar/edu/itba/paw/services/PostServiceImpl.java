package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.interfaces.services.PostService;

import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Override
    public Optional<Post> findById(long id) {
        return postDao.findById(id);
    }

    @Override
    public Post register(String title, String email, String body){
        return postDao.register(title, email, body);
    }



}