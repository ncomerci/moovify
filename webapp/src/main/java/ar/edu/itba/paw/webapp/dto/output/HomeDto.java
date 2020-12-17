package ar.edu.itba.paw.webapp.dto.output;

import javax.ws.rs.core.UriInfo;

public class HomeDto {

    private String authorized_user_url;
    private String list_users_url;
    private String list_posts_url;
    private String list_movies_url;
    private String list_comments_url;

    public HomeDto() {
        // For Jersey - Do Not Use
    }

    public HomeDto(UriInfo uriInfo) {
        authorized_user_url = uriInfo.getBaseUriBuilder().path("user").build().toString();
        list_users_url = uriInfo.getBaseUriBuilder().path("users").build().toString();
        list_posts_url = uriInfo.getBaseUriBuilder().path("posts").build().toString();
        list_movies_url = uriInfo.getBaseUriBuilder().path("movies").build().toString();
        list_comments_url = uriInfo.getBaseUriBuilder().path("comments").build().toString();
    }

    public String getAuthorized_user_url() {
        return authorized_user_url;
    }

    public void setAuthorized_user_url(String authorized_user_url) {
        this.authorized_user_url = authorized_user_url;
    }

    public String getList_users_url() {
        return list_users_url;
    }

    public void setList_users_url(String list_users_url) {
        this.list_users_url = list_users_url;
    }

    public String getList_posts_url() {
        return list_posts_url;
    }

    public void setList_posts_url(String list_posts_url) {
        this.list_posts_url = list_posts_url;
    }

    public String getList_movies_url() {
        return list_movies_url;
    }

    public void setList_movies_url(String list_movies_url) {
        this.list_movies_url = list_movies_url;
    }

    public String getList_comments_url() {
        return list_comments_url;
    }

    public void setList_comments_url(String list_comments_url) {
        this.list_comments_url = list_comments_url;
    }
}
