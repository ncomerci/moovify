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
    public Response listUsers(@QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                              @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<User> users = userService.getAllUsers(orderBy, pageNumber, pageSize);

        if(users.isEmpty()) {
            if(pageNumber == 0)
                return Response.noContent().build();

            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Response.ResponseBuilder responseBuilder =
                Response.ok(new GenericEntity<Collection<UserDto>>(UserDto.mapUsersToDto(users.getResults(), uriInfo)) {});

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        responseBuilder.link(linkUriBuilder.clone().queryParam("pageNumber", 0).build(), "first");

        responseBuilder.link(linkUriBuilder.clone().queryParam("pageNumber", users.getLastPageNumber()).build(), "last");

        if(pageNumber != 0)
            responseBuilder.link(linkUriBuilder.clone().queryParam("pageNumber", pageNumber - 1).build(), "prev");

        if(pageNumber != users.getLastPageNumber())
            responseBuilder.link(linkUriBuilder.clone().queryParam("pageNumber", pageNumber + 1).build(), "next");

        return responseBuilder.build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") long id) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(new UserDto(user, uriInfo)).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
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

        return Response.created(UserDto.getUserUriBuilder(user, uriInfo).build()).build();
    }

}

