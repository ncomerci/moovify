package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

public class MovieController {

    @Autowired
    private MovieService movieService;
}
