package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlType;

// Remove type attribute added automatically by jersey when extending class
@XmlType(name="")
public class LightweightUserDto extends BaseUserDto {

    private String userFollowing;

    public LightweightUserDto() {
        super();
        // For Jersey - Do not use
    }

    public LightweightUserDto(User user, UriInfo uriInfo, SecurityContext securityContext) {
        super(user, uriInfo, securityContext);

        if(isProtected(securityContext))
            return;

        if(securityContext.getUserPrincipal() != null) {
            userFollowing = uriInfo.getBaseUriBuilder()
                    .path("user").path("following").path(String.valueOf(user.getId()))
                    .build().toString();
        }
    }

    public String getUserFollowing() {
        return userFollowing;
    }

    public void setUserFollowing(String userFollowing) {
        this.userFollowing = userFollowing;
    }
}
