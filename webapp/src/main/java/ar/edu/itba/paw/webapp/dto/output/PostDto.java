package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Post;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.stream.Collectors;

// Remove type attribute added automatically by jersey when extending class
@XmlType(name="")
public class PostDto extends BasePostDto {

    public static Collection<PostDto> mapPostsToDto(Collection<Post> posts, UriInfo uriInfo, SecurityContext securityContext) {
        return posts.stream().map(p -> new PostDto(p, uriInfo, securityContext)).collect(Collectors.toList());
    }

    private Collection<MovieDto> movies;

    public PostDto() {
        super();
        // For Jersey - Do not use
    }

    public PostDto(Post post, UriInfo uriInfo, SecurityContext securityContext) {
        super(post, uriInfo, securityContext);

        if(isProtected(securityContext))
            return;

        movies = MovieDto.mapMoviesToDto(post.getMovies(), uriInfo);
    }

    public Collection<MovieDto> getMovies() {
        return movies;
    }

    public void setMovies(Collection<MovieDto> movies) {
        this.movies = movies;
    }
}
