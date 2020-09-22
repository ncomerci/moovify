package ar.edu.itba.paw.webapp.form.Constraints;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.form.Annotations.UniqueEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, String> {

   @Autowired
   private UserService userService;

   public void initialize(UniqueEmail constraint) {
   }

   public boolean isValid(String obj, ConstraintValidatorContext context) {

      return obj.isEmpty() || !userService.findByEmail(obj).isPresent();
   }
}
