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

    private String body;
    private Collection<MovieDto> movies;
    private Long totalLikes;
    private int userVote;
    private Boolean hasUserBookmarked;

    public PostDto() {
        super();
        // For Jersey - Do not use
    }

    public PostDto(Post post, UriInfo uriInfo, SecurityContext securityContext) {
        super(post, uriInfo, securityContext);

        if(isProtected(securityContext))
            return;

        body = post.getBody();
        movies = MovieDto.mapMoviesToDto(post.getMovies(), uriInfo);
        totalLikes = post.getTotalVotes();

        if(securityContext.getUserPrincipal() != null) {
            final String authenticatedUserUsername = securityContext.getUserPrincipal().getName();

            hasUserBookmarked = post.hasBookmarked(authenticatedUserUsername);
            userVote = post.getVoteValue(authenticatedUserUsername);
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Collection<MovieDto> getMovies() {
        return movies;
    }

    public void setMovies(Collection<MovieDto> movies) {
        this.movies = movies;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(int userVote) {
        this.userVote = userVote;
    }

    public Boolean getHasUserBookmarked() {
        return hasUserBookmarked;
    }

    public void setHasUserBookmarked(Boolean hasUserBookmarked) {
        this.hasUserBookmarked = hasUserBookmarked;
    }
}
