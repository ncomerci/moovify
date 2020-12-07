package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.CommentLike;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentVoteDto {

    public static Collection<CommentVoteDto> mapCommentsLikeToDto(Collection<CommentLike> commentLikes, UriInfo uriInfo) {
        return commentLikes.stream().map(c -> new CommentVoteDto(c, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getUserUriBuilder(User user, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(user.getId()));
    }

    private long userId;
    private int value;
    private String userUrl;

    public CommentVoteDto() {
        //For Jersey Reflection- Do not use
    }

    public CommentVoteDto(CommentLike commentLike, UriInfo uriInfo) {
        this.userId = commentLike.getUser().getId();
        this.value = commentLike.getValue();
        this.userUrl = getUserUriBuilder(commentLike.getUser(), uriInfo).build().toString();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
}
