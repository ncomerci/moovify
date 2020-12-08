package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostBookmarkException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostUnbookmarkException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalUserFollowException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalUserUnfollowException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JwtUtil;
import ar.edu.itba.paw.webapp.dto.error.BeanValidationErrorDto;
import ar.edu.itba.paw.webapp.dto.error.DuplicateUniqueUserAttributeErrorDto;
import ar.edu.itba.paw.webapp.dto.error.GenericErrorDto;
import ar.edu.itba.paw.webapp.dto.generic.GenericBooleanResponseDto;
import ar.edu.itba.paw.webapp.dto.input.*;
import ar.edu.itba.paw.webapp.dto.output.CommentDto;
import ar.edu.itba.paw.webapp.dto.output.PostDto;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import ar.edu.itba.paw.webapp.exceptions.AvatarNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.InvalidResetPasswordToken;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("user")
@Component
public class AuthenticatedUserController {

    @Context
    private UriInfo uriInfo;

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
    public Response getUser(@Context SecurityContext securityContext) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        return Response.ok(new UserDto(user, uriInfo)).build();
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

            return Response.noContent()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtUtil.generateToken(user)
                    )
                    .build();

        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response updateUser(@Context SecurityContext securityContext, final UserEditDto userEditDto) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        try {
            userService.updateUser(user, userEditDto.getName(), userEditDto.getUsername(), userEditDto.getDescription(),
                    userEditDto.getPassword());
        }
        catch(DuplicateUniqueUserAttributeException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new DuplicateUniqueUserAttributeErrorDto(e))
                    .build();
        }

        return Response.noContent()
                .contentLocation(UserDto.getUserUriBuilder(user, uriInfo).build())
                .build();
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/avatar")
    public Response updateAvatar(@Context SecurityContext securityContext, @Valid final UpdateAvatarDto updateAvatarDto) throws IOException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        userService.updateAvatar(user, updateAvatarDto.getAvatar().getBytes());

        return Response.noContent()
                .contentLocation(UserDto.getUserUriBuilder(user, uriInfo).path("/avatar").build())
                .build();
    }

    @Produces("image/*")
    @GET
    @Path("/avatar")
    public Response getAvatar(@Context SecurityContext securityContext) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final byte[] imageData = userService.getAvatar(user).orElseThrow(AvatarNotFoundException::new);

        // TODO: Set conditional cache?
        return Response.ok(imageData).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/posts")
    public Response getUserPosts(@Context SecurityContext securityContext,
                                 @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                 @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                 @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.findPostsByUser(user, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", posts.getPageSize())
                .queryParam("orderBy", orderBy);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/comments")
    public Response getUserComments(@Context SecurityContext securityContext,
                                    @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                    @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                    @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Comment> comments = commentService.findCommentsByUser(user, orderBy, pageNumber, pageSize);

        final Collection<CommentDto> commentsDto = CommentDto.mapCommentsToDto(comments.getResults(), uriInfo);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", comments.getPageSize())
                .queryParam("orderBy", orderBy);

        return buildGenericPaginationResponse(comments, new GenericEntity<Collection<CommentDto>>(commentsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/following")
    public Response getFollowedUsers(@Context SecurityContext securityContext,
                                     @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                     @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                     @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<User> users = userService.getFollowedUsers(user, orderBy, pageNumber, pageSize);

        final Collection<UserDto> usersDto = UserDto.mapUsersToDto(users.getResults(), uriInfo);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", users.getPageSize())
                .queryParam("orderBy", orderBy);

        return buildGenericPaginationResponse(users, new GenericEntity<Collection<UserDto>>(usersDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/following/{userId}")
    public Response isFollowingUser(@Context SecurityContext securityContext, @PathParam("userId") long userId) {

        final User loggedUser = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        final boolean result = userService.isFollowingUser(loggedUser, user);

        return Response.ok(new GenericBooleanResponseDto(result)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/following/{userId}")
    public Response followUser(@PathParam("userId") long userId, @Context SecurityContext securityContext) throws IllegalUserFollowException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final User followedUser = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.followUser(user, followedUser);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/following/{userId}")
    public Response unfollowUser(@PathParam("userId") long userId, @Context SecurityContext securityContext) throws IllegalUserUnfollowException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final User unfollowedUser = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.unfollowUser(user, unfollowedUser);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/bookmarked")
    public Response getBookmarkedPosts(@Context SecurityContext securityContext,
                                       @QueryParam("orderBy") @DefaultValue("newest") String orderBy,
                                       @QueryParam("pageNumber") @DefaultValue("0") int pageNumber,
                                       @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final PaginatedCollection<Post> posts = postService.getUserBookmarkedPosts(user, orderBy, pageNumber, pageSize);

        final Collection<PostDto> postsDto = PostDto.mapPostsToDto(posts.getResults(), uriInfo);

        final UriBuilder linkUriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .queryParam("pageSize", posts.getPageSize())
                .queryParam("orderBy", orderBy);

        return buildGenericPaginationResponse(posts, new GenericEntity<Collection<PostDto>>(postsDto) {}, linkUriBuilder);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/bookmarked/{postId}")
    public Response hasUserBookmarkedPost(@Context SecurityContext securityContext, @PathParam("postId") long postId) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        final boolean result = userService.hasUserBookmarkedPost(user, post);

        return Response.ok(new GenericBooleanResponseDto(result)).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/bookmarked/{postId}")
    public Response bookmarkPost(@PathParam("postId") long postId, @Context SecurityContext securityContext) throws IllegalPostBookmarkException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Post bookmarkedPost = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        userService.bookmarkPost(user, bookmarkedPost);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/bookmarked/{postId}")
    public Response unbookmarkPost(@PathParam("postId") long postId, @Context SecurityContext securityContext) throws IllegalPostUnbookmarkException {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        final Post unbookmarkedPost = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        userService.unbookmarkPost(user, unbookmarkedPost);

        return Response.noContent().build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/email_confirmation")
    public Response resendConfirmationEmail(@Context SecurityContext securityContext, @Context HttpServletRequest request) {

        final User user = userService.findUserByUsername(securityContext.getUserPrincipal().getName()).orElseThrow(UserNotFoundException::new);

        userService.createConfirmationEmail(user, "confirmEmail", request.getLocale());

        return Response.noContent().build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/email_confirmation")
    public Response confirmRegistration(final TokenDto tokenDto,
                                        @Context SecurityContext securityContext, @Context HttpServletRequest request) {

        final Optional<User> optUser = userService.confirmRegistration(tokenDto.getToken());

        if(optUser.isPresent()) {

            final User user = optUser.get();

            final Response.ResponseBuilder responseBuilder = Response.noContent();

            if(user.isEnabled() && securityContext.getUserPrincipal() != null)
                responseBuilder.header(HttpHeaders.AUTHORIZATION, jwtUtil.generateToken(user));

            return Response.noContent().build();
        }

        // TODO: Descablear String
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericErrorDto("The token provided is invalid"))
                .build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/password_reset")
    public Response sendPasswordResetEmail(@Valid final PasswordResetEmailDto passwordResetEmailDto, @Context HttpServletRequest request) {

        final Optional<User> optUser = userService.findUserByEmail(passwordResetEmailDto.getEmail());

        if(!optUser.isPresent() || !optUser.get().isValidated()) {

            final String errorMessage;

            // TODO: Descablear String
            if(!optUser.isPresent())
                errorMessage = "Email doesn't belong to any user";

            else
                errorMessage = "Email is not confirmed";

            final List<BeanValidationErrorDto> emailError =
                    Collections.singletonList(
                        new BeanValidationErrorDto("email", passwordResetEmailDto.getEmail(), errorMessage)
                    );

            return Response.status(Response.Status.BAD_REQUEST).entity(emailError).build();
        }

        final User user = optUser.get();

        userService.createPasswordResetEmail(user, "passwordResetEmail", request.getLocale());

        return Response.noContent().build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/password_reset")
    public Response resetPassword(@Valid final PasswordResetDto passwordResetDto, @Context SecurityContext securityContext) {

        final boolean isTokenValid = userService.validatePasswordResetToken(passwordResetDto.getToken());

        if(!isTokenValid) {

            // TODO: Descablear String
            final List<BeanValidationErrorDto> emailError =
                    Collections.singletonList(
                            new BeanValidationErrorDto("token", passwordResetDto.getToken(), "Invalid Token")
                    );

            return Response.status(Response.Status.BAD_REQUEST).entity(emailError).build();
        }

        final User user = userService.updatePassword(passwordResetDto.getPassword(), passwordResetDto.getToken())
                .orElseThrow(InvalidResetPasswordToken::new);

        final Response.ResponseBuilder responseBuilder = Response.noContent();

        if(user.isEnabled() && securityContext.getUserPrincipal() != null)
            responseBuilder.header(HttpHeaders.AUTHORIZATION, jwtUtil.generateToken(user));

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
}
