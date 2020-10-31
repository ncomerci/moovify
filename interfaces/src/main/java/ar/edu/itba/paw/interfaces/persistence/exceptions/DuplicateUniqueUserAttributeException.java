package ar.edu.itba.paw.interfaces.persistence.exceptions;

import java.util.EnumSet;

public class DuplicateUniqueUserAttributeException extends Exception {

    public enum UniqueAttributes {
        USERNAME,
        EMAIL
    }

    private final EnumSet<UniqueAttributes> attributes;

    public DuplicateUniqueUserAttributeException(EnumSet<UniqueAttributes> attributes) {
        super();
        this.attributes = attributes;
    }

    public EnumSet<UniqueAttributes> getDuplicatedUniqueAttributes() {
        return attributes;
    }
}
