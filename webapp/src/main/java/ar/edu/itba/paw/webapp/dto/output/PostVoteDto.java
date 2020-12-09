package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.PostVote;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.stream.Collectors;

public class PostVoteDto {

    public static Collection<PostVoteDto> mapPostsVoteToDto(Collection<PostVote> postVotes, UriInfo uriInfo, SecurityContext securityContext) {
        return postVotes.stream().map(p -> new PostVoteDto(p, uriInfo, securityContext)).collect(Collectors.toList());
    }

    private UserDto user;
    private int value;

    public PostVoteDto() {
        //For Jersey - Do not use
    }

    public PostVoteDto(PostVote postVote, UriInfo uriInfo, SecurityContext securityContext){
        this.user = new UserDto(postVote.getUser(), uriInfo, securityContext);
        this.value = postVote.getValue();
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
