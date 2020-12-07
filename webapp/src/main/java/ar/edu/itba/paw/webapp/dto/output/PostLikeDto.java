package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.PostLike;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.stream.Collectors;

public class PostLikeDto {

    public static Collection<PostLikeDto> mapPostsLikeToDto(Collection<PostLike> postLikes, UriInfo uriInfo) {
        return postLikes.stream().map(p -> new PostLikeDto(p, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getUserUriBuilder(User user, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(user.getId()));
    }

    private long userId;
    private int value;
    private String userUrl;

    public PostLikeDto() {
        //For Jersey - Do not use
    }

    public PostLikeDto(PostLike postLike, UriInfo uriInfo){
        this.userId = postLike.getUser().getId();
        this.value = postLike.getValue();
        this.userUrl = getUserUriBuilder(postLike.getUser(), uriInfo).build().toString();
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
