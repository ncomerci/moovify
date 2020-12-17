package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.webapp.dto.input.MovieCreateDto;
import ar.edu.itba.paw.webapp.dto.input.validation.annotations.Image;
import ar.edu.itba.paw.webapp.dto.output.MovieDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.SearchOptionDto;
import ar.edu.itba.paw.webapp.exceptions.InvalidSearchArgumentsException;
import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.MoviePosterNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.PayloadRequiredException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;

@Path("movies")
@Component
public class MovieController {

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PostService postService;

    @Autowired
    private SearchService searchService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listMovies(@QueryParam("query") @DefaultValue("") String query,
                               @QueryParam("movieCategory") String movieCategory,
                               @QueryParam("decade") String decade,
                               @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                               @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                               @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final PaginatedCollection<Movie> movies;

        if(!query.equals("") || movieCategory != null || decade != null)
            movies = searchService.searchMovies(query, movieCategory, decade, orderBy, pageNumber, pageSize).orElseThrow(InvalidSearchArgumentsException::new);

        else
            movies = movieService.getAllMovies(orderBy, pageNumber, pageSize);

        final Collection<MovieDto> moviesDto = MovieDto.mapMoviesToDto(movies.getResults(), uriInfo);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        linkUriBuilder.queryParam("query", query);

        if(movieCategory != null)
            linkUriBuilder.queryParam("movieCategory", movieCategory);

        if(decade != null)
            linkUriBuilder.queryParam("decade", decade);


        return buildGenericPaginationResponse(movies, new GenericEntity<Collection<MovieDto>>(moviesDto) {}, linkUriBuilder);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createMovie(@Valid final MovieCreateDto movieCreateDto) {

        if(movieCreateDto == null)
            throw new PayloadRequiredException();

        final Movie movie = movieService.register(movieCreateDto.getTitle(), movieCreateDto.getOriginalTitle(),
                movieCreateDto.getTmdbId(), movieCreateDto.getImdbId(), movieCreateDto.getOriginalLanguage(),
                movieCreateDto.getOverview(), movieCreateDto.getPopularity(), movieCreateDto.getRuntime(),
                movieCreateDto.getVoteAverage(),  movieCreateDto.getReleaseDate(),  movieCreateDto.getCategories());

        return Response.created(MovieDto.getMovieUriBuilder(movie, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/options")
    public Response getMovieSearchOptions() {

        Collection<SearchOptionDto> options = new ArrayList<>();

        options.add(new SearchOptionDto("movieCategory", searchService.getMoviesCategories()));
        options.add(new SearchOptionDto("decade", searchService.getMoviesDecades()));
        options.add(new SearchOptionDto("orderBy", movieService.getMovieSortOptions()));

        return Response.ok(new GenericEntity<Collection<SearchOptionDto>>(options) {}).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getMovie(@PathParam("id") long id) {

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        return Response.ok(new MovieDto(movie, uriInfo)).build();
    }

    @Produces({ "image/*", MediaType.APPLICATION_JSON })
    @GET
    @Path("/{id}/poster")
    public Response getPoster(@PathParam("id") long id, @Context Request request) {

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        final EntityTag eTag = new EntityTag(String.valueOf(movie.getPosterId()));
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(eTag);

        if(responseBuilder == null) {

            final byte[] imageData = movieService.getPoster(movie).orElseThrow(MoviePosterNotFoundException::new);

            responseBuilder = Response.ok(imageData).type(movie.getPosterType()).tag(eTag);
        }

        return responseBuilder.cacheControl(cacheControl).build();
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/poster")
    public Response updatePoster(@PathParam("id") long id,
                                 @Image @FormDataParam("poster") final FormDataBodyPart posterBody,
                                 @Size(max = 1024 * 1024) @FormDataParam("poster") byte[] posterBytes) {

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        movieService.updatePoster(movie, posterBytes, posterBody.getMediaType().toString());

        return Response.noContent()
                .contentLocation(MovieDto.getMovieUriBuilder(movie, uriInfo).path("poster").build())
                .build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/posts")
    public Response getMoviePosts(@PathParam("id") long id,
                                  @QueryParam("enabled") Boolean enabled,
                                  @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                  @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                  @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.findPostsByMovie(movie, enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", pageSize)
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
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
}
