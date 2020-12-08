package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class InvalidUserPromotionException extends CustomException {

    public InvalidUserPromotionException() {
        super("Invalid user promotion");
        responseStatus = 400;
    }
}
