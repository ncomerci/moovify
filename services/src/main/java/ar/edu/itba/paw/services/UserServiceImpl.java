package ar.edu.itba.paw.services;

import org.springframework.stereotype.Service;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User findById(long id) {
        //TODO hacer de verdadz
        return new User(id, "PAW");
    }

}