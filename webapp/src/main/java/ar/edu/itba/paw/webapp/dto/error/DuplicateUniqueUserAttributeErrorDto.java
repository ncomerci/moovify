package ar.edu.itba.paw.webapp.dto.error;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.EnumSet;

public class DuplicateUniqueUserAttributeErrorDto {

    private String message;
    private EnumSet<DuplicateUniqueUserAttributeException.UniqueAttributes> duplicatedAttributes;

    public DuplicateUniqueUserAttributeErrorDto() {
        //For jersey - do not use
    }

    public DuplicateUniqueUserAttributeErrorDto(DuplicateUniqueUserAttributeException e, MessageSource messageSource) {

        message = messageSource.getMessage(e.getMessageCode(), null, LocaleContextHolder.getLocale());
        duplicatedAttributes = e.getDuplicatedUniqueAttributes();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EnumSet<DuplicateUniqueUserAttributeException.UniqueAttributes> getDuplicatedAttributes() {
        return duplicatedAttributes;
    }

    public void setDuplicatedAttributes(EnumSet<DuplicateUniqueUserAttributeException.UniqueAttributes> duplicatedAttributes) {
        this.duplicatedAttributes = duplicatedAttributes;
    }
}
