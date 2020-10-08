package ar.edu.itba.paw.webapp.form.Constraints;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.form.Annotations.UniqueUsername;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsername, String> {

   @Autowired
   private UserService userService;

   public void initialize(UniqueUsername constraint) {
   }

   public boolean isValid(String obj, ConstraintValidatorContext context) {
      return !userService.findUserByUsername(obj).isPresent();
   }
}
