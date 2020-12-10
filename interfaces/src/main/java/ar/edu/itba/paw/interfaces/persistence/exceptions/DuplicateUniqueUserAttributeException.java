package ar.edu.itba.paw.interfaces.persistence.exceptions;

import java.util.EnumSet;

public class DuplicateUniqueUserAttributeException extends Exception {

    public enum UniqueAttributes {
        USERNAME,
        EMAIL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private final EnumSet<UniqueAttributes> attributes;
    private final String messageCode;

    public DuplicateUniqueUserAttributeException(EnumSet<UniqueAttributes> attributes) {
        this.attributes = attributes;
        messageCode = "error.DuplicateUniqueUserAttributeException";
    }

    public EnumSet<UniqueAttributes> getDuplicatedUniqueAttributes() {
        return attributes;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
