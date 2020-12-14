package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalCommentEditionException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalCommentLikeException;
import ar.edu.itba.paw.interfaces.services.exceptions.MissingCommentEditPermissionException;
import ar.edu.itba.paw.interfaces.services.exceptions.RestoredEnabledModelException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.generic.GenericIntegerValueDto;
import ar.edu.itba.paw.webapp.dto.input.CommentCreateDto;
import ar.edu.itba.paw.webapp.dto.input.CommentEditDto;
import ar.edu.itba.paw.webapp.dto.output.CommentDto;
import ar.edu.itba.paw.webapp.dto.output.CommentVoteDto;
import ar.edu.itba.paw.webapp.dto.output.SearchOptionDto;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.ForbiddenEntityRelationshipAccessException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;

@Path("comments")
@Component
public class CommentController {

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Produces
    @GET
    public Response listComments(@QueryParam("enabled") Boolean enabled,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<Comment> comments = commentService.getAllComments(enabled, orderBy, pageNumber, pageSize);

        final Collection<CommentDto> commentsDto = CommentDto.mapCommentsToDto(comments.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("orderBy", orderBy)
                .queryParam("pageSize", pageSize);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(comments, new GenericEntity<Collection<CommentDto>>(commentsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/options")
    public Response getCommentSearchOptions(){

        Collection<SearchOptionDto> options = new ArrayList<>();

        options.add(new SearchOptionDto("orderBy", commentService.getCommentSortOptions()));

        return Response.ok(new GenericEntity<Collection<SearchOptionDto>>(options) {}).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getComment(@PathParam("id") long id) {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        return Response.ok(new CommentDto(comment, uriInfo, securityContext)).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}")
    public Response editComment(@PathParam("id") long id, @Valid final CommentEditDto commentEditDto) throws MissingCommentEditPermissionException, IllegalCommentEditionException {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        commentService.editComment(user, comment, commentEditDto.getBody());

        return Response.noContent()
                .contentLocation(CommentDto.getCommentUriBuilder(comment, uriInfo).build())
                .build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/children")
    public  Response getCommentChildren(@PathParam("id") long id,
                                        @QueryParam("enabled") Boolean enabled,
                                        @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                        @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                        @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        final PaginatedCollection<Comment> comments = commentService.findCommentChildren(comment, enabled, orderBy, pageNumber, pageSize);

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
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{id}/children")
    public Response createCommentChild(@PathParam("id") long parentId, @Valid final CommentCreateDto commentCreateDto){

        final Comment parent = commentService.findCommentById(parentId).orElseThrow(CommentNotFoundException::new);

        final Post post = parent.getPost();

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Comment comment = commentService.register(post, parent, commentCreateDto.getBody(), user, "newCommentEmail");

        return Response.created(CommentDto.getCommentUriBuilder(comment, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/enabled")
    public Response restoreComment(@PathParam("id") long id) throws RestoredEnabledModelException {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        commentService.restoreComment(comment);

        return Response.noContent().contentLocation(CommentDto.getCommentUriBuilder(comment, uriInfo).build()).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}/enabled")
    public Response deleteComment(@PathParam("id") long id){

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        commentService.deleteComment(comment);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/votes")
    public Response getCommentVotes(@PathParam("id") long id,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        guaranteeCommentRelationshipAccessPermissions(securityContext, comment);

        final PaginatedCollection<CommentVote> commentVotes = commentService.getCommentVotes(comment, pageNumber, pageSize);

        final Collection<CommentVoteDto> commentVotesDto = CommentVoteDto.mapCommentsVoteToDto(commentVotes.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize);

        return buildGenericPaginationResponse(commentVotes, new GenericEntity<Collection<CommentVoteDto>>(commentVotesDto) {}, linkUriBuilder);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/votes")
    public Response voteComment(@PathParam("id") long id, final GenericIntegerValueDto valueDto) throws IllegalCommentLikeException {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        commentService.likeComment(comment, user, valueDto.getValue());

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/votes/{userId}")
    public Response getVoteValue(@PathParam("id") long id, @PathParam("userId") long userId) {

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        guaranteeCommentRelationshipAccessPermissions(securityContext, comment);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        int value = commentService.getVoteValue(comment, user);

        return Response.ok(new GenericIntegerValueDto(value)).build();
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

    private <T> void setPaginationLinks(Response.ResponseBuilder response, PaginatedCollection<T> results, UriBuilder baseUri) {

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

    private void guaranteeCommentRelationshipAccessPermissions(SecurityContext securityContext, Comment comment) {
        if(!comment.isEnabled() && !securityContext.isUserInRole(Role.ADMIN.name()))
            throw new ForbiddenEntityRelationshipAccessException(securityContext.getUserPrincipal() != null);
    }
}
