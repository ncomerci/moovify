package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;

@Path("users")
@Component
public class UserController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private UserService userService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listUsers(@QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        final PaginatedCollection<User> users = userService.getAllUsers(pageNumber, pageSize);

        if(users.getResults().isEmpty())
            return Response.noContent().build();

        return Response.ok(new GenericEntity<Collection<UserDto>>(UserDto.mapUsersToDto(users.getResults(), uriInfo)) {})
                .link(
                        uriBuilder
                                .queryParam("pageNumber", pageNumber + 1)
                                .queryParam("pageSize", pageSize)
                        .build(),
                        "next")
                .build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") long id) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(new UserDto(user, uriInfo)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createUser(final UserDto userDto, @Context HttpServletRequest request) {

        final User user;

        try {
            user = userService.register(userDto.getUsername(), userDto.getPassword(), userDto.getName(), userDto.getEmail(),
                    userDto.getDescription(), new byte[0], "confirmEmail", request.getLocale());
        }
        catch(DuplicateUniqueUserAttributeException e) {
            // TODO
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(new UserDto(user, uriInfo)).build();
    }

}

