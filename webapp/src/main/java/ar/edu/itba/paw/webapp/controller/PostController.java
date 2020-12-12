package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.generic.GenericIntegerValueDto;
import ar.edu.itba.paw.webapp.dto.input.PostCreateDto;
import ar.edu.itba.paw.webapp.dto.input.PostEditDto;
import ar.edu.itba.paw.webapp.dto.output.*;
import ar.edu.itba.paw.webapp.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;

@Path("posts")
@Component
public class PostController {

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SearchService searchService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listPosts(@QueryParam("query") @DefaultValue("") String query,
                              @QueryParam("postCategory") String postCategory,
                              @QueryParam("postAge") String postAge,
                              @QueryParam("enabled") Boolean enabled,
                              @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                              @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<Post> posts;

        if(!query.equals("") || postCategory != null || postAge != null)
            posts = searchService.searchPosts(query, postCategory, postAge, enabled, orderBy, pageNumber, pageSize).orElseThrow(InvalidSearchArgumentsException::new);

        else
            posts = postService.getAllPosts(enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        linkUriBuilder.queryParam("query", query);

        if(postCategory != null)
            linkUriBuilder.queryParam("postCategory", postCategory);

        if(postAge != null)
            linkUriBuilder.queryParam("postAge", postAge);


        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createPost(@Valid final PostCreateDto postCreateDto) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PostCategory postCategory = postService.findCategoryById(postCreateDto.getCategory()).orElseThrow(InvalidPostCategoryException::new);

        final Post post = postService.register(postCreateDto.getTitle(), postCreateDto.getBody(), postCategory,
                user, postCreateDto.getTags(), postCreateDto.getMovies());

        return Response.created(PostDto.getPostUriBuilder(post, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/options")
    public Response getPostOptions(){

        Collection<SearchOptionDto> options = new ArrayList<>();

        options.add(new SearchOptionDto("postCategory", searchService.getPostCategories()));
        options.add(new SearchOptionDto("postAge", searchService.getPostPeriodOptions()));
        options.add(new SearchOptionDto("orderBy", postService.getPostSortOptions()));

        return Response.ok(new GenericEntity<Collection<SearchOptionDto>>(options) {}).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getPost(@PathParam("id") long id) {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        return Response.ok(new PostDto(post, uriInfo, securityContext)).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}")
    public Response editPost(@PathParam("id") long id, @Valid final PostEditDto postEditDto) throws MissingPostEditPermissionException, IllegalPostEditionException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        postService.editPost(user, post, postEditDto.getBody());

        return Response.noContent()
                .contentLocation(PostDto.getPostUriBuilder(post, uriInfo).build())
                .build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/enabled")
    public Response restorePost(@PathParam("id") long id) throws RestoredEnabledModelException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        postService.restorePost(post);

        return Response.noContent().contentLocation(PostDto.getPostUriBuilder(post, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}/enabled")
    public Response deletePost(@PathParam("id") long id) throws DeletedDisabledModelException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        postService.deletePost(post);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/movies")
    public Response getPostMovies(@PathParam("id") long id) {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        guaranteePostRelationshipAccessPermissions(securityContext, post);

        final Collection<MovieDto> moviesDto = MovieDto.mapMoviesToDto(post.getMovies(), uriInfo);

        return Response.ok(new GenericEntity<Collection<MovieDto>>(moviesDto) {}).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/votes")
    public Response getPostVotes(@PathParam("id") long id,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        guaranteePostRelationshipAccessPermissions(securityContext, post);

        final PaginatedCollection<PostVote> postVotes = postService.getPostVotes(post, pageNumber, pageSize);

        final Collection<PostVoteDto> postVotesDto = PostVoteDto.mapPostsVoteToDto(postVotes.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize);

        return buildGenericPaginationResponse(postVotes, new GenericEntity<Collection<PostVoteDto>>(postVotesDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/votes/{userId}")
    public Response getVoteValue(@PathParam("id") long id, @PathParam("userId") long userId) {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        guaranteePostRelationshipAccessPermissions(securityContext, post);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        final int value = postService.getVoteValue(post, user);

        return Response.ok(new GenericIntegerValueDto(value)).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/votes")
    public Response votePost(@PathParam("id") long id,
                             final GenericIntegerValueDto valueDto) throws IllegalPostLikeException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        postService.likePost(post, user, valueDto.getValue());

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/comments")
    public Response getPostComments(@PathParam("id") long id,
                                    @QueryParam("enabled") Boolean enabled,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        guaranteePostRelationshipAccessPermissions(securityContext, post);

        final PaginatedCollection<Comment> comments = commentService.findPostChildrenComments(post, enabled, orderBy, pageNumber, pageSize);

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
    @Path("/categories")
    public Response getPostCategories() {

        final Collection<PostCategory> postCategories = postService.getAllPostCategories();

        final Collection<PostCategoryDto> postCategoriesDto = PostCategoryDto.mapPostCategoryToDto(postCategories);

        return Response.ok(new GenericEntity<Collection<PostCategoryDto>>(postCategoriesDto) {}).build();
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

    private void guaranteePostRelationshipAccessPermissions(SecurityContext securityContext, Post post) {
        if(!post.isEnabled() && !securityContext.isUserInRole(Role.ADMIN.name()))
            throw new ForbiddenEntityRelationshipAccessException(securityContext.getUserPrincipal() != null);
    }
}
