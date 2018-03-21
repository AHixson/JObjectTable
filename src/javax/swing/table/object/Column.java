package javax.swing.table.object;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation for variables/methods that are used to construct a behavioural
 * trait for how that information is handled.
 * </p>
 * <p>
 * Note: You cannot have two columns with the same name.
 * </p>
 * <p>
 * Version 1.1.0 - Basic attributes (column name, can edit, can be null, and can
 * be a unique value within that column)
 * </p>
 * 
 * @author Antony Hixson
 *
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Column {

	/**
	 * 
	 * @return Column name
	 */
	String name();
	
	/**
	 * 
	 * @return index of column order
	 */
	int index() default -1;
	
	/**
	 * 
	 * @return <tt>Column values are editable</tt>
	 */
	boolean editable() default true;
	
	/**
	 * 
	 * @return <tt>Column values can be null</tt>
	 */
	boolean required() default false;
	
	/**
	 * 
	 * @return <tt>Column values must be unique</tt>
	 */
	boolean unique() default false;
	
	/**
	 * 
	 * @return Placeholder value
	 */
	String placeholder() default "";
	
	/**
	 * 
	 * @return Placeholder value
	 */
	String tooltip() default "";
	
	/**
	 * 
	 * @return Default value
	 */
	String value() default "";
	
	/**
	 * 
	 * @return Minimum value
	 */
	String min() default "";
	
	/**
	 * 
	 * @return Maximum value
	 */
	String max() default "";
	
	/**
	 * 
	 * @return Numeric step size
	 */
	String step() default "";
	
	/**
	 * 
	 * @return <tt>Column enabled</tt>
	 */
	boolean enabled() default true;
	
	/**
	 * 
	 * @return <tt>Run {@link Runnable} in a new {@link Thread}</tt>
	 */
	boolean concurrent() default false;
}