/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The definition of a speech command annotation
 * 
 * @author Leon Kiefer
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(java.lang.annotation.ElementType.TYPE)
public @interface SpeechCommand {
	/**
	 * @return the keyword of the speech command
	 */
	String value();
}
