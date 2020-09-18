package ar.edu.itba.paw.webapp.form;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class MoviesSizeConstraintValidator implements ConstraintValidator<MoviesSizeConstraint, Set<Long>> {
   public void initialize(MoviesSizeConstraint constraint) {
   }

   public boolean isValid(Set<Long> movies, ConstraintValidatorContext context) {
      return movies != null && movies.size() < 20;
   }
}
