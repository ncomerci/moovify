package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JwtUtil;
import ar.edu.itba.paw.webapp.dto.error.BeanValidationErrorDto;
import ar.edu.itba.paw.webapp.dto.error.DuplicateUniqueUserAttributeErrorDto;
import ar.edu.itba.paw.webapp.dto.generic.GenericBooleanResponseDto;
import ar.edu.itba.paw.webapp.dto.input.*;
import ar.edu.itba.paw.webapp.dto.input.validation.annotations.Image;
import ar.edu.itba.paw.webapp.dto.output.CommentDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import ar.edu.itba.paw.webapp.exceptions.AvatarNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("user")
@Component
public class AuthenticatedUserController {

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getUser() {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        return Response.ok(new UserDto(user, uriInfo, securityContext)).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response authenticateUser(final UserAuthenticationDto userAuthDto) {

        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    userAuthDto.getUsername(), userAuthDto.getPassword()
                            )
                    );

            User user = userService.findUserByUsername(authenticate.getName()).orElseThrow(UserNotFoundException::new);

            final Response.ResponseBuilder responseBuilder = Response.noContent();

            authenticateUser(responseBuilder, user);

            return responseBuilder.build();

        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response updateUser(final UserEditDto userEditDto) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final boolean shouldRevalidateAuthentication = !userEditDto.getUsername().equals(user.getUsername());

        try {
            userService.updateUser(user, userEditDto.getName(), userEditDto.getUsername(), userEditDto.getDescription(),
                    userEditDto.getPassword());
        }
        catch(DuplicateUniqueUserAttributeException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new DuplicateUniqueUserAttributeErrorDto(e, messageSource))
                    .build();
        }

        final Response.ResponseBuilder responseBuilder = Response.noContent()
                .contentLocation(UserDto.getUserUriBuilder(user, uriInfo).build());

        if(shouldRevalidateAuthentication)
            authenticateUser(responseBuilder, user);

        return responseBuilder.build();
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/avatar")
    public Response updateAvatar(@Image @FormDataParam("avatar") final FormDataBodyPart avatarBody,
                                 @Size(max = 1024 * 1024) @FormDataParam("avatar") byte[] avatarBytes) {

        User user = userService.findUserById(3).orElseThrow(UserNotFoundException::new);

        userService.updateAvatar(user, avatarBytes, avatarBody.getMediaType().toString());

        return Response.noContent()
                .contentLocation(UserDto.getUserUriBuilder(user, uriInfo).path("/avatar").build())
                .build();
    }

    @Produces("image/*")
    @GET
    @Path("/avatar")
    public Response getAvatar() {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final byte[] imageData = userService.getAvatar(user).orElseThrow(AvatarNotFoundException::new);

        // TODO: Set conditional cache?
        return Response.ok(imageData).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/posts")
    public Response getUserPosts(@QueryParam("enabled") Boolean enabled,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.findPostsByUser(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", posts.getPageSize())
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/comments")
    public Response getUserComments(@QueryParam("enabled") Boolean enabled,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Comment> comments = commentService.findCommentsByUser(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<CommentDto> commentsDto = CommentDto.mapCommentsToDto(comments.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", comments.getPageSize())
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(comments, new GenericEntity<Collection<CommentDto>>(commentsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/following")
    public Response getFollowedUsers(@QueryParam("enabled") Boolean enabled,
                                     @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                     @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                     @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<User> users = userService.getFollowedUsers(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", users.getPageSize())
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(users, new GenericEntity<Collection<UserDto>>(usersDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/following/posts")
    public Response getFollowedUsersPosts(@QueryParam("enabled") Boolean enabled,
                                         @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                         @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                         @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.getFollowedUsersPosts(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postDtos = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", posts.getPageSize())
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postDtos) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/following/{userId}")
    public Response isFollowingUser(@PathParam("userId") long userId) {

        final User loggedUser = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        final boolean result = userService.isFollowingUser(loggedUser, user);

        return Response.ok(new GenericBooleanResponseDto(result)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/following/{userId}")
    public Response followUser(@PathParam("userId") long userId) throws IllegalUserFollowException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final User followedUser = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.followUser(user, followedUser);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/following/{userId}")
    public Response unfollowUser(@PathParam("userId") long userId) throws IllegalUserUnfollowException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final User unfollowedUser = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.unfollowUser(user, unfollowedUser);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/bookmarked")
    public Response getBookmarkedPosts(@QueryParam("enabled") Boolean enabled,
                                       @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                       @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                       @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.getUserBookmarkedPosts(user, enabled, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo, securityContext);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", posts.getPageSize())
                .queryParam("orderBy", orderBy);

        if(enabled != null)
            linkUriBuilder.queryParam("enabled", enabled);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/bookmarked/{postId}")
    public Response hasUserBookmarkedPost(@PathParam("postId") long postId) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        final boolean result = userService.hasUserBookmarkedPost(user, post);

        return Response.ok(new GenericBooleanResponseDto(result)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/bookmarked/{postId}")
    public Response bookmarkPost(@PathParam("postId") long postId) throws IllegalPostBookmarkException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Post bookmarkedPost = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        userService.bookmarkPost(user, bookmarkedPost);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/bookmarked/{postId}")
    public Response unbookmarkPost(@PathParam("postId") long postId) throws IllegalPostUnbookmarkException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Post unbookmarkedPost = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        userService.unbookmarkPost(user, unbookmarkedPost);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/email_confirmation")
    public Response resendConfirmationEmail() {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        userService.createConfirmationEmail(user, "confirmEmail");

        return Response.noContent().build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/email_confirmation")
    public Response confirmRegistration(final TokenDto tokenDto, @Context HttpServletRequest request) throws InvalidEmailConfirmationTokenException {

        final User user = userService.confirmRegistration(tokenDto.getToken());

        final Response.ResponseBuilder responseBuilder = Response.noContent();

        if(user.isEnabled())
            authenticateUser(responseBuilder, user);

        return Response.noContent().build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/password_reset")
    public Response sendPasswordResetEmail(@Valid final PasswordResetEmailDto passwordResetEmailDto) {

        final Optional<User> optUser = userService.findUserByEmail(passwordResetEmailDto.getEmail());

        if(!optUser.isPresent() || !optUser.get().isValidated()) {

            final String errorMessageCode;

            if(!optUser.isPresent())
                errorMessageCode = "error.invalidResetPasswordEmail";

            else
                errorMessageCode = "error.notValidatedResetPasswordEmail";

            final List<BeanValidationErrorDto> emailError =
                    Collections.singletonList(
                        new BeanValidationErrorDto("email",
                                messageSource.getMessage(errorMessageCode, null, LocaleContextHolder.getLocale()))
                    );

            return Response.status(Response.Status.BAD_REQUEST).entity(emailError).build();
        }

        final User user = optUser.get();

        userService.createPasswordResetEmail(user, "passwordResetEmail");

        return Response.noContent().build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/password_reset")
    public Response resetPassword(@Valid final PasswordResetDto passwordResetDto) throws InvalidResetPasswordToken {

        final boolean isTokenValid = userService.validatePasswordResetToken(passwordResetDto.getToken());

        if(!isTokenValid) {

            final List<BeanValidationErrorDto> emailError =
                    Collections.singletonList(
                            new BeanValidationErrorDto("token",
                                    messageSource.getMessage(
                                            "error.invalidResetPasswordTokenException", null, LocaleContextHolder.getLocale()
                                    )
                            )
                    );

            return Response.status(Response.Status.BAD_REQUEST).entity(emailError).build();
        }

        final User user = userService.updatePassword(passwordResetDto.getPassword(), passwordResetDto.getToken());

        final Response.ResponseBuilder responseBuilder = Response.noContent();

        if(user.isEnabled() && securityContext.getUserPrincipal() != null)
            authenticateUser(responseBuilder, user);

        return responseBuilder.build();
    }

    private <Entity, Dto> Response buildGenericPaginationResponse(PaginatedCollection<Entity> paginatedResults,
                                                                  GenericEntity<Collection<Dto>> resultsDto, UriBuilder linkUriBuilder) {
        if(paginatedResults.isEmpty()) {
            if(paginatedResults.getPageNumber() == 0)
                return Response.noContent().build();

            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Response.ResponseBuilder responseBuilder =
                Response.ok(resultsDto);

        setPaginationLinks(responseBuilder, paginatedResults, linkUriBuilder);

        return responseBuilder.build();
    }

    private <T> void setPaginationLinks(Response.ResponseBuilder response, PaginatedCollection<T> results, UriBuilder baseUri) {

        final int pageNumber = results.getPageNumber();
        final String pageNumberParamName = "pageNumber";

        final int first = 0;
        final int last = results.getLastPageNumber();
        final int prev = pageNumber - 1;
        final int next = pageNumber + 1;

        response.link(baseUri.clone().queryParam(pageNumberParamName, first).build(), "first");

        response.link(baseUri.clone().queryParam(pageNumberParamName, last).build(), "last");

        if(pageNumber != first)
            response.link(baseUri.clone().queryParam(pageNumberParamName, prev).build(), "prev");

        if(pageNumber != last)
            response.link(baseUri.clone().queryParam(pageNumberParamName, next).build(), "next");
    }

    private void authenticateUser(Response.ResponseBuilder responseBuilder, User user) {
        responseBuilder.header(HttpHeaders.AUTHORIZATION, jwtUtil.generateToken(user));
    }
}
