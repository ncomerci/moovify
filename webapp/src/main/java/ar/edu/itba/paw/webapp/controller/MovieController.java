package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.webapp.dto.input.MovieCreateDto;
import ar.edu.itba.paw.webapp.dto.input.UpdateMoviePosterDto;
import ar.edu.itba.paw.webapp.dto.output.MovieDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.SearchOptionDto;
import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.MoviePosterNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Path("movies")
@Component
public class MovieController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PostService postService;

    @Autowired
    private SearchService searchService;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response listMovies(@QueryParam("query") String query,
                               @QueryParam("movieCategory") String movieCategory,
                               @QueryParam("decade") String decade,
                               @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                               @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                               @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final PaginatedCollection<Movie> movies;

        if(query != null){
            movies = searchService.searchMovies(query, movieCategory, decade, orderBy, pageNumber, pageSize).orElseThrow(MovieNotFoundException::new);
        }
        else {
            movies = movieService.getAllMovies(orderBy, pageNumber, pageSize);
        }

        final Collection<MovieDto> moviesDto = MovieDto.mapMoviesToDto(movies.getResults(), uriInfo);

        return buildGenericPaginationResponse(movies, new GenericEntity<Collection<MovieDto>>(moviesDto) {}, uriInfo, orderBy);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response addMovie(@Valid final MovieCreateDto movieCreateDto){

        final Movie movie = movieService.register(movieCreateDto.getTitle(), movieCreateDto.getOriginalTitle(), movieCreateDto.getTmdbId(), movieCreateDto.getImdbId(), movieCreateDto.getOriginalLanguage(),
                movieCreateDto.getOverview(), movieCreateDto.getPopularity(), movieCreateDto.getRuntime(), movieCreateDto.getVoteAverage(),  movieCreateDto.getReleaseDate(),  movieCreateDto.getCategories());

        return Response.created(MovieDto.getMovieUriBuilder(movie, uriInfo).build()).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/options")
    public Response getMovieSearchOptions(){

        Collection<SearchOptionDto> options = new ArrayList<>();
        options.add(new SearchOptionDto("movieCategory", searchService.getMoviesCategories(), null));
        options.add(new SearchOptionDto("decades", searchService.getMoviesDecades(), null));
        options.add(new SearchOptionDto("sortCriteria", movieService.getMovieSortOptions(), "newest"));

        return Response.ok(new GenericEntity<Collection<SearchOptionDto>>(options) {}).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Response getMovie(@PathParam("id") long id) {

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        return Response.ok(new MovieDto(movie, uriInfo)).build();
    }

    @Produces("image/*")
    @GET
    @Path("/{id}/poster")
    public Response getPoster(@PathVariable("id") long id) {

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        final byte[] imageData = movieService.getPoster(movie.getPosterId()).orElseThrow(MoviePosterNotFoundException::new);

        return Response.ok(imageData).build();
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{id}/poster")
    public Response updatePoster(@PathParam("id") long id, @Valid final UpdateMoviePosterDto updateMoviePosterDto) throws IOException {

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        movieService.updatePoster(movie, updateMoviePosterDto.getPoster().getBytes());

        return Response.noContent()
                .location(MovieDto.getMovieUriBuilder(movie, uriInfo).path("/" + id).build())
                .build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/posts")
    public Response getMoviePosts(@PathParam("id") long id,
                                  @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                  @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                  @QueryParam("pageSize") @DefaultValue("10") int pageSize){

        final Movie movie = movieService.findMovieById(id).orElseThrow(MovieNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.findPostsByMovie(movie, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, uriInfo, orderBy);
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
