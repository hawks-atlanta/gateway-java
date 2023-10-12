package gateway.services;

import gateway.soap.response.ResStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import java.util.Set;

public class UtilValidator
{
	public static <T> ResStatus validate (T obj)
	{
		ResStatus s = new ResStatus ();
		Set<ConstraintViolation<T>> val =
			Validation.buildDefaultValidatorFactory ().getValidator ().validate (obj);

		if (val.size () > 0) {
			for (ConstraintViolation<T> violation : val) {
				s.msg += String.format (
					"\"%s %s\",", violation.getPropertyPath ().toString (),
					violation.getMessage ());
			}

			s.msg = s.msg.substring (0, s.msg.length () - 1);
			s.code = 400;
			s.error = true;
			return s;
		}

		s.error = false;
		return s;
	}
}
