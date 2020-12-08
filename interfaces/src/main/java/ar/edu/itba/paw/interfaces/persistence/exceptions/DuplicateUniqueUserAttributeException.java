package ar.edu.itba.paw.interfaces.persistence.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

import java.util.EnumSet;

public class DuplicateUniqueUserAttributeException extends CustomException {

    public enum UniqueAttributes {
        USERNAME,
        EMAIL
    }

    private final EnumSet<UniqueAttributes> attributes;

    public DuplicateUniqueUserAttributeException(EnumSet<UniqueAttributes> attributes) {
        super();
        this.attributes = attributes;
        responseStatus = 400;
    }

    public EnumSet<UniqueAttributes> getDuplicatedUniqueAttributes() {
        return attributes;
    }
}
