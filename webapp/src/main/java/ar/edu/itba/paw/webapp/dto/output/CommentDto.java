package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Comment;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentDto {

    public static Collection<CommentDto> mapCommentsToDto(Collection<Comment> comments, UriInfo uriInfo) {
        return comments.stream().map(c -> new CommentDto(c, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getCommentUriBuilder(Comment comment, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("comment").path(String.valueOf(comment.getId()));
    }

    private String url;

    public CommentDto() {
        // For Jersey Reflection - Do not use
    }

    public CommentDto(Comment comment, UriInfo uriInfo) {

        url = getCommentUriBuilder(comment, uriInfo).build().toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
