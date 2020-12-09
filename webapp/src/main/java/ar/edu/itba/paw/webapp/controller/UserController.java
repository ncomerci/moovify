package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.DeletedDisabledModelException;
import ar.edu.itba.paw.interfaces.services.exceptions.InvalidUserPromotionException;
import ar.edu.itba.paw.interfaces.services.exceptions.RestoredEnabledModelException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.error.DuplicateUniqueUserAttributeErrorDto;
import ar.edu.itba.paw.webapp.dto.generic.GenericBooleanResponseDto;
import ar.edu.itba.paw.webapp.dto.input.UserCreateDto;
import ar.edu.itba.paw.webapp.dto.output.CommentDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.SearchOptionDto;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import ar.edu.itba.paw.webapp.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;

@Path("users")
@Component
public class UserController {

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SearchService searchService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listUsers(@QueryParam("query") @DefaultValue("") String query,
                              @QueryParam("role") String role,
                              @QueryParam("enabled") Boolean enabled,
                              @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                              @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<User> users;

        if(!query.equals("") || role != null)
            users = searchService.searchUsers(query, role, enabled, orderBy, pageNumber, pageSize).orElseThrow(InvalidSearchArgumentsException::new);

        else
            users = userService.getAllUsers(enabled, orderBy, pageNumber, pageSize);


        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        linkUriBuilder.queryParam("query", query);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        if(role != null)
            linkUriBuilder.queryParam("role", role);


        return buildGenericPaginationResponse(users, new GenericEntity<Collection<UserDto>>(usersDto) {}, linkUriBuilder);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createUser(@Valid final UserCreateDto userCreateDto) {

        final User user;

        try {
            user = userService.register(userCreateDto.getUsername(), userCreateDto.getPassword(), userCreateDto.getName(),
                    userCreateDto.getEmail(), userCreateDto.getDescription(), "confirmEmail");
        }
        catch(DuplicateUniqueUserAttributeException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new DuplicateUniqueUserAttributeErrorDto(e, messageSource))
                    .build();
        }

        return Response.created(UserDto.getUserUriBuilder(user, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/options")
    public Response getUserSearchOptions(){

        Collection<SearchOptionDto> options = new ArrayList<>();

        options.add(new SearchOptionDto("role", searchService.getUserRoleOptions()));
        options.add(new SearchOptionDto("orderBy", userService.getUserSortOptions()));

        return Response.ok(new GenericEntity<Collection<SearchOptionDto>>(options) {}).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") long id) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(new UserDto(user, uriInfo, securityContext)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/privilege")
    public Response promoteUserToAdmin(@PathParam("id") long id) throws InvalidUserPromotionException {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.promoteUserToAdmin(user);

        return Response.noContent().contentLocation(UserDto.getUserUriBuilder(user, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/enabled")
    public Response restoreUser(@PathParam("id") long id) throws RestoredEnabledModelException {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.restoreUser(user);

        return Response.noContent().contentLocation(UserDto.getUserUriBuilder(user, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}/enabled")
    public Response deleteUser(@PathParam("id") long id) throws DeletedDisabledModelException {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        userService.deleteUser(user);

        return Response.noContent().build();
    }

    @Produces("image/*")
    @GET
    @Path("/{id}/avatar")
    public Response getAvatar(@PathParam("id") long id) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final byte[] imageData = userService.getAvatar(user).orElseThrow(AvatarNotFoundException::new);

        // TODO: Set conditional cache?
        return Response.ok(imageData).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/posts")
    public Response getUserPosts(@PathParam("id") long id,
                                 @QueryParam("enabled") Boolean enabled,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final PaginatedCollection<Post> posts = postService.findPostsByUser(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/comments")
    public Response getUserComments(@PathParam("id") long id,
                                    @QueryParam("enabled") Boolean enabled,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final PaginatedCollection<Comment> comments = commentService.findCommentsByUser(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<CommentDto> commentsDto = CommentDto.mapCommentsToDto(comments.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(comments, new GenericEntity<Collection<CommentDto>>(commentsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/following")
    public Response getFollowedUsers(@PathParam("id") long id,
                                     @QueryParam("enabled") Boolean enabled,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final PaginatedCollection<User> users = userService.getFollowedUsers(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", users.getPageSize())
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(users, new GenericEntity<Collection<UserDto>>(usersDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("{id}/following/{userId}")
    public Response isFollowingUser(@PathParam("id") long id, @PathParam("userId") long userId) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final User other = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        final boolean result = userService.isFollowingUser(user, other);

        return Response.ok(new GenericBooleanResponseDto(result)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/bookmarked")
    public Response getBookmarkedPosts(@PathParam("id") long id,
                                       @QueryParam("enabled") Boolean enabled,
                                     @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                     @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                     @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final PaginatedCollection<Post> posts = postService.getUserBookmarkedPosts(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
            .getAbsolutePathBuilder()
            .queryParam("pageSize", pageSize)
            .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("{id}/bookmarked/{postId}")
    public Response hasUserBookmarkedPost(@PathParam("id") long id, @PathParam("postId") long postId) {

        final User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);

        guaranteeUserRelationshipAccessPermissions(securityContext, user);

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        final boolean result = userService.hasUserBookmarkedPost(user, post);

        return Response.ok(new GenericBooleanResponseDto(result)).build();
    }

    private <Entity, Dto> Response buildGenericPaginationResponse(PaginatedCollection<Entity> paginatedResults,
                                                                  GenericEntity<Collection<Dto>> resultsDto, UriBuilder linkUriBuilder) {

        if(paginatedResults.isEmpty()) {
            if(paginatedResults.getPageNumber() == 0)
                return Response.noContent().build();

            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Response.ResponseBuilder responseBuilder =
                Response.ok(resultsDto);

        setPaginationLinks(responseBuilder, paginatedResults, linkUriBuilder);

        return responseBuilder.build();
    }

    private <T> void setPaginationLinks(Response.ResponseBuilder response,
                                        PaginatedCollection<T> results, UriBuilder baseUri) {

        final int pageNumber = results.getPageNumber();
        final String pageNumberParamName = "pageNumber";

        final int first = 0;
        final int last = results.getLastPageNumber();
        final int prev = pageNumber - 1;
        final int next = pageNumber + 1;

        response.link(baseUri.clone().queryParam(pageNumberParamName, first).build(), "first");

        response.link(baseUri.clone().queryParam(pageNumberParamName, last).build(), "last");

        if(pageNumber != first)
            response.link(baseUri.clone().queryParam(pageNumberParamName, prev).build(), "prev");

        if(pageNumber != last)
            response.link(baseUri.clone().queryParam(pageNumberParamName, next).build(), "next");
    }

    private void guaranteeUserRelationshipAccessPermissions(SecurityContext securityContext, User user) {
        if(!user.isEnabled() && !securityContext.isUserInRole(Role.ADMIN.name()))
            throw new ForbiddenEntityRelationshipAccessException(securityContext.getUserPrincipal() != null);
    }
}

