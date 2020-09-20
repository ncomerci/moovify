package ar.edu.itba.paw.services.exceptions;

public class NonReachableStateException extends RuntimeException{

    public NonReachableStateException() {
        super("This state shouldn't be accessible in normal conditions. Condition should prevent this state is achieved.");
    }
}
