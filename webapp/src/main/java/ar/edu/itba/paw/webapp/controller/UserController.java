package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.DeletedDisabledModelException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalUserFollowException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalUserUnfollowException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.error.DuplicateUniqueUserAttributeErrorDto;
import ar.edu.itba.paw.webapp.dto.input.UpdateAvatarDto;
import ar.edu.itba.paw.webapp.dto.input.UserCreateDto;
import ar.edu.itba.paw.webapp.dto.input.UserEditDto;
import ar.edu.itba.paw.webapp.dto.output.CommentDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import ar.edu.itba.paw.webapp.exceptions.AvatarNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

@Path("users")
@Component
public class UserController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listUsers(@QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                              @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<User> users = userService.getAllUsers(orderBy, pageNumber, pageSize);

        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo);

        return buildGenericPaginationResponse(users, usersDto, uriInfo, orderBy);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createUser(final UserCreateDto userCreateDto, @Context HttpServletRequest request) {

        final User user;

        try {
            user = userService.register(userCreateDto.getUsername(), userCreateDto.getPassword(), userCreateDto.getName(),
                    userCreateDto.getEmail(), userCreateDto.getDescription(), "confirmEmail",
                    request.getLocale());
        }
        catch(DuplicateUniqueUserAttributeException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new DuplicateUniqueUserAttributeErrorDto(e))
                    .build();
        }

        return Response.created(UserDto.getUserUriBuilder(user, uriInfo).build()).build();
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
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") long id, final UserEditDto userEditDto) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        try {
            userService.updateUser(user, userEditDto.getName(), userEditDto.getUsername(), userEditDto.getDescription(),
                    userEditDto.getPassword());
        }
        catch(DuplicateUniqueUserAttributeException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new DuplicateUniqueUserAttributeErrorDto(e))
                    .build();
        }

        return Response.noContent()
                .location(UserDto.getUserUriBuilder(user, uriInfo).build())
                .build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") long id, @Context Principal principal) throws DeletedDisabledModelException {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.deleteUser(user);

        // TODO: Revisar como hacer logout
        if(user.getUsername().equals(principal.getName()))
            return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path("/logout").build()).build();

        return Response.ok().build();
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/avatar")
    public Response updateUser(@PathParam("id") long id, final UpdateAvatarDto updateAvatarDto) throws IOException {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.updateAvatar(user, updateAvatarDto.getAvatar().getBytes());

        return Response.noContent()
                .location(UserDto.getUserUriBuilder(user, uriInfo).path("/avatar").build())
                .build();
    }

    @Produces("image/*")
    @GET
    @Path("/{id}/avatar")
    public Response updateUser(@PathParam("id") long id) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        final byte[] imageData = userService.getAvatar(user).orElseThrow(AvatarNotFoundException::new);

        // TODO: Set conditional cache?
        return Response.ok(imageData).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/posts")
    public Response getUserPosts(@PathParam("id") long id,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.findPostsByUser(user, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo);

        return buildGenericPaginationResponse(posts, postsDto, uriInfo, orderBy);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/comments")
    public Response getUserComments(@PathParam("id") long id,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Comment> comments = commentService.findCommentsByUser(user, orderBy, pageNumber, pageSize);

        final Collection<CommentDto> commentsDto = CommentDto.mapCommentsToDto(comments.getResults(), uriInfo);

        return buildGenericPaginationResponse(comments, commentsDto, uriInfo, orderBy);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/following")
    public Response getFollowedUsers(@PathParam("id") long id,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<User> users = userService.getFollowedUsers(user, orderBy, pageNumber, pageSize);

        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo);

        return buildGenericPaginationResponse(users, usersDto, uriInfo, orderBy);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/bookmarked")
    public Response getBookmarkedPosts(@PathParam("id") long id,
                                     @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                     @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                     @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.getUserBookmarkedPosts(user, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo);

        return buildGenericPaginationResponse(posts, postsDto, uriInfo, orderBy);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/follow")
    public Response followUser(@PathParam("id") long id, @Context Principal principal) throws IllegalUserFollowException {

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        final User followedUser = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.followUser(user, followedUser);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/unfollow")
    public Response unfollowUser(@PathParam("id") long id, @Context Principal principal) throws IllegalUserUnfollowException {

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        final User unfollowedUser = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.unfollowUser(user, unfollowedUser);

        return Response.noContent().build();
    }

//    TODO: Add /posts/{id}/bookmark and /posts/{id}/unbookmark



    private <Entity, Dto> Response buildGenericPaginationResponse(PaginatedCollection<Entity> paginatedResults,
                                                                  Collection<Dto> resultsDto, UriInfo uriInfo,
                                                                  String orderBy) {

        if(paginatedResults.isEmpty()) {
            if(paginatedResults.getPageNumber() == 0)
                return Response.noContent().build();

            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Response.ResponseBuilder responseBuilder =
                Response.ok(new GenericEntity<Collection<Dto>>(resultsDto) {});

        setPaginationLinks(responseBuilder, uriInfo, paginatedResults, orderBy);

        return responseBuilder.build();
    }

    private <T> void setPaginationLinks(Response.ResponseBuilder response, UriInfo uriInfo,
                                        PaginatedCollection<T> results, String orderBy) {

        final int pageNumber = results.getPageNumber();
        final String pageNumberParamName = "pageNumber";

        final int first = 0;
        final int last = results.getLastPageNumber();
        final int prev = pageNumber - 1;
        final int next = pageNumber + 1;

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", results.getPageSize())
                .queryParam("orderBy", orderBy);

        response.link(linkUriBuilder.clone().queryParam(pageNumberParamName, first).build(), "first");

        response.link(linkUriBuilder.clone().queryParam(pageNumberParamName, last).build(), "last");

        if(pageNumber != first)
            response.link(linkUriBuilder.clone().queryParam(pageNumberParamName, prev).build(), "prev");

        if(pageNumber != last)
            response.link(linkUriBuilder.clone().queryParam(pageNumberParamName, next).build(), "next");
    }
}

