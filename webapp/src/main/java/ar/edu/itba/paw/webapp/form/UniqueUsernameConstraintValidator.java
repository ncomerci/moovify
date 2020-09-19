package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsername, String> {

   @Autowired
   private UserService userService;

   public void initialize(UniqueUsername constraint) {
   }

   public boolean isValid(String obj, ConstraintValidatorContext context) {
      return !userService.findByUsername(obj).isPresent();
   }
}
