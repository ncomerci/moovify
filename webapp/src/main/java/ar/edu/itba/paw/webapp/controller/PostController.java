package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.DeletedDisabledModelException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostEditionException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostLikeException;
import ar.edu.itba.paw.interfaces.services.exceptions.MissingPostEditPermissionException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.input.PostCreateDto;
import ar.edu.itba.paw.webapp.dto.input.PostEditDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.exceptions.InvalidPostCategoryException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;
import java.util.Collection;

@Path("posts")
@Component
public class PostController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listPosts(@QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                              @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<Post> posts = postService.getAllPosts(orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo);

        return buildGenericPaginationResponse(posts, postsDto, uriInfo, orderBy);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createPost(@Valid final PostCreateDto postCreateDto, @Context Principal principal, @Context HttpServletRequest request){

        final Post post;

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        final PostCategory postCategory = postService.findCategoryById(postCreateDto.getCategory()).orElseThrow(InvalidPostCategoryException::new);

        post = postService.register(postCreateDto.getTitle(), postCreateDto.getBody(), postCategory, user, postCreateDto.getTags(), postCreateDto.getMovies());

        return Response.created(PostDto.getPostUriBuilder(post, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getPost(@PathParam("id") long id) {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        return Response.ok(new PostDto(post,uriInfo)).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}")
    public Response editPost(@PathParam("id") long id, @Valid final PostEditDto postEditDto, @Context Principal principal) throws MissingPostEditPermissionException, IllegalPostEditionException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        postService.editPost(user, post, postEditDto.getBody());

        return Response.noContent()
                .location(PostDto.getPostUriBuilder(post, uriInfo).build())
                .build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") long id, @Context Principal principal) throws DeletedDisabledModelException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        postService.deletePost(post);

        return Response.ok().build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/vote")
    public Response editPost(@PathParam("id") long id, @QueryParam("value") @DefaultValue("0") final int value, @Context Principal principal) throws IllegalPostLikeException {

        final Post post = postService.findPostById(id).orElseThrow(PostNotFoundException::new);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        postService.likePost(post, user, value);

        return Response.noContent().build();
    }

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
