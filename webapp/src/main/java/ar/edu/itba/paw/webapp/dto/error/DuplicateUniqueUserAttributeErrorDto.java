package ar.edu.itba.paw.webapp.dto.error;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.stream.Collectors;

// Remove type attribute added automatically by jersey when extending class
@XmlType(name="")
public class DuplicateUniqueUserAttributeErrorDto extends BeanValidationErrorDto {

    public static Collection<DuplicateUniqueUserAttributeErrorDto> mapDuplicateUniqueUserAttributeExceptionToDtos(DuplicateUniqueUserAttributeException e, MessageSource messageSource) {

        final String message = messageSource.getMessage(e.getMessageCode(), null, LocaleContextHolder.getLocale());

        return e.getDuplicatedUniqueAttributes().stream()
                .map(attr -> new DuplicateUniqueUserAttributeErrorDto(attr, message)).collect(Collectors.toList());
    }

    public DuplicateUniqueUserAttributeErrorDto() {
        super();
        //For jersey - do not use
    }

    public DuplicateUniqueUserAttributeErrorDto(DuplicateUniqueUserAttributeException.UniqueAttributes attribute, String message) {
        super(attribute.toString(), message);
    }
}
