package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.RestoredEnabledModelException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.output.CommentDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;

@Path("disabled")
@Component
public class DisabledController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SearchService searchService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/comments")
    public Response deletedComments(@QueryParam("query") @DefaultValue("") String query,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final PaginatedCollection<Comment> comments = searchService.searchDeletedComments(query, orderBy, pageNumber, pageSize).orElseThrow(CommentNotFoundException::new);

        final Collection<CommentDto> commentsDto = CommentDto.mapCommentsToDto(comments.getResults(), uriInfo);

        return buildGenericPaginationResponse(comments, new GenericEntity<Collection<CommentDto>>(commentsDto) {}, uriInfo, orderBy);

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/users")
    public Response deletedUsers(@QueryParam("query") @DefaultValue("") String query,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final PaginatedCollection<User> users = searchService.searchDeletedUsers(query, orderBy, pageNumber, pageSize).orElseThrow(UserNotFoundException::new);

        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo);

        return buildGenericPaginationResponse(users, new GenericEntity<Collection<UserDto>>(usersDto) {}, uriInfo, orderBy);

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/posts/{id}")
    public Response deletedPost( @PathParam("id") long id,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final Post post = postService.findDeletedPostById(id).orElseThrow(PostNotFoundException::new);

        return Response.ok(new PostDto(post, uriInfo)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/comments/{id}")
    public Response deletedComment( @PathParam("id") long id,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final Comment comment = commentService.findDeletedCommentById(id).orElseThrow(CommentNotFoundException::new);

        return Response.ok(new CommentDto(comment, uriInfo)).build();

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/users/{id}")
    public Response deletedUser(@PathParam("id") long id,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final User user = userService.findDeletedUserById(id).orElseThrow(UserNotFoundException::new);

        return Response.ok(new UserDto(user, uriInfo)).build();

    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/posts/{id}")
    public Response restorePost( @PathParam("id") long id,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) throws RestoredEnabledModelException {

        final Post post = postService.findDeletedPostById(id).orElseThrow(PostNotFoundException::new);

        postService.restorePost(post);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/comments/{id}")
    public Response restoreComment( @PathParam("id") long id,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize) throws RestoredEnabledModelException {

        final Comment comment = commentService.findDeletedCommentById(id).orElseThrow(CommentNotFoundException::new);

        commentService.restoreComment(comment);

        return Response.noContent().build();

    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/users/{id}")
    public Response restoreUser(@PathParam("id") long id,
                                @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                @QueryParam("pageSize") @DefaultValue("10") int pageSize) throws RestoredEnabledModelException {

        final User user = userService.findDeletedUserById(id).orElseThrow(UserNotFoundException::new);

        userService.restoreUser(user);

        return Response.noContent().build();

    }

    private <Entity, Dto> Response buildGenericPaginationResponse(PaginatedCollection<Entity> paginatedResults,
                                                                  GenericEntity<Collection<Dto>> resultsDto, UriInfo uriInfo,
                                                                  String orderBy) {

        if(paginatedResults.isEmpty()) {
            if(paginatedResults.getPageNumber() == 0)
                return Response.noContent().build();

            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Response.ResponseBuilder responseBuilder =
                Response.ok(resultsDto);

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
