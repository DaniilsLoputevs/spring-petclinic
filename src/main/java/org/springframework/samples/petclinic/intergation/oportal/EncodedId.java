package org.springframework.samples.petclinic.intergation.oportal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncodedId {

	String name();

}
