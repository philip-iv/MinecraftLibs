package me.philipiv.libs.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark fields as being save- and/or load-able from config
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
	/**
	 * The parent {@link ConfigSection}.
	 * Leave blank to use the default, or the parent class' if it exists.
	 */
	ConfigSection parent() default @ConfigSection;
	/**
	 * Whether or not the value should be saved to config. Defaults to true.
	 */
	boolean save() default true;
	/**
	 * Whether or not the value should be loaded from config. Defaults to false.
	 * Items which can't be loaded will use their initialized value, if set.
	 */
	boolean load() default false;
	/**
	 * The name of the value in the config. Defaults to the variable name.
	 */
	String name() default "";
}