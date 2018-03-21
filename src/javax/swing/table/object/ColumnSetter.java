package javax.swing.table.object;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation to be applied strictly to 'setter' methods, which makes the
 * setting of values mutable and setting of values can be validated beforehand.
 * </p>
 * <p>
 * Note: This annotation type must be paired with a {@link Column} by having the
 * same name.
 * </p>
 * 
 * @author Antony Hixson
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ColumnSetter {

	/**
	 * 
	 * @return Column name
	 */
	String name();
	
}
