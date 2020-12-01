package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService userService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listUsers() {
        final PaginatedCollection<User> users = userService.getAllUsers(0, 0);

        if(users.getResults().isEmpty())
            return Response.noContent().build();

        return Response.ok(new GenericEntity<Collection<User>>(users.getResults()) {}).build();
    }

}

