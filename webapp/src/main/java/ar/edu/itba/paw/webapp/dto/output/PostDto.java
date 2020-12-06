package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Post;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.stream.Collectors;

public class PostDto {

    public static Collection<PostDto> mapPostsToDto(Collection<Post> posts, UriInfo uriInfo) {
        return posts.stream().map(p -> new PostDto(p, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getPostUriBuilder(Post post, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(post.getId()));
    }

    private String url;

    public PostDto() {
        // For Jersey Reflection - Do not use
    }

    public PostDto(Post post, UriInfo uriInfo) {

        url = getPostUriBuilder(post, uriInfo).build().toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
