package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.stream.Collectors;

// Remove type attribute added automatically by jersey when extending class
@XmlType(name="")
public class UserDto extends BaseUserDto {

    public static Collection<UserDto> mapUsersToDto(Collection<User> users, UriInfo uriInfo, SecurityContext securityContext) {
        return users.stream().map(u -> new UserDto(u, uriInfo, securityContext)).collect(Collectors.toList());
    }

    private Long totalLikes;
    private Integer followerCount;
    private Boolean userFollowing;
    private Boolean userFollower;

    public UserDto() {
        super();
        // For Jersey - Do not use
    }

    public UserDto(User user, UriInfo uriInfo, SecurityContext securityContext) {
        super(user, uriInfo, securityContext);

        if(isProtected(securityContext))
            return;

        totalLikes = user.getTotalLikes();
        followerCount = user.getFollowerCount();

        if(securityContext.getUserPrincipal() != null) {
            userFollowing = user.isUserFollowing(securityContext.getUserPrincipal().getName());
            userFollower = user.isUserFollower(securityContext.getUserPrincipal().getName());
        }

    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public Boolean getUserFollowing() {
        return userFollowing;
    }

    public void setUserFollowing(Boolean userFollowing) {
        this.userFollowing = userFollowing;
    }

    public Boolean getUserFollower() {
        return userFollower;
    }

    public void setUserFollower(Boolean userFollower) {
        this.userFollower = userFollower;
    }
}
