package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.SpacesNormalization;

import javax.validation.constraints.Size;

public class CommentEditForm {

    @Size( min = 1, max = 400)
    @SpacesNormalization
    private String commentBody;

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

}
