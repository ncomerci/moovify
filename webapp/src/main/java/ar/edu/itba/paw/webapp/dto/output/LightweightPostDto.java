package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Post;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlType;

// Remove type attribute added automatically by jersey when extending class
@XmlType(name="")
public class LightweightPostDto extends BasePostDto {

    private String movies;

    private String hasUserBookmarked;

    public LightweightPostDto() {
        super();
        // For Jersey - Do not use
    }

    public LightweightPostDto(Post post, UriInfo uriInfo, SecurityContext securityContext) {
        super(post, uriInfo, securityContext);

        if(isProtected(securityContext))
            return;

        movies = getPostUriBuilder(post, uriInfo).path("movies").build().toString();

        if(securityContext.getUserPrincipal() != null) {
            hasUserBookmarked = uriInfo.getBaseUriBuilder()
                    .path("user").path("bookmarked").path(String.valueOf(post.getId()))
                    .build().toString();
        }
    }

    public String getMovies() {
        return movies;
    }

    public void setMovies(String movies) {
        this.movies = movies;
    }

    public String getHasUserBookmarked() {
        return hasUserBookmarked;
    }

    public void setHasUserBookmarked(String hasUserBookmarked) {
        this.hasUserBookmarked = hasUserBookmarked;
    }
}
