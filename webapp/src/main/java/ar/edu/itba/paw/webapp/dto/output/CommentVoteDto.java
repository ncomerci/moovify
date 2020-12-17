package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.CommentVote;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentVoteDto {

    public static Collection<CommentVoteDto> mapCommentsVoteToDto(Collection<CommentVote> commentVotes, UriInfo uriInfo, SecurityContext securityContext) {
        return commentVotes.stream().map(c -> new CommentVoteDto(c, uriInfo, securityContext)).collect(Collectors.toList());
    }

    private LightweightUserDto user;
    private int value;

    public CommentVoteDto() {
        //For Jersey Reflection- Do not use
    }

    public CommentVoteDto(CommentVote commentVote, UriInfo uriInfo, SecurityContext securityContext) {
        user = new LightweightUserDto(commentVote.getUser(), uriInfo, securityContext);
        value = commentVote.getValue();
    }

    public LightweightUserDto getUser() {
        return user;
    }

    public void setUser(LightweightUserDto user) {
        this.user = user;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
