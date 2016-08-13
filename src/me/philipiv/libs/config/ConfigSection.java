package me.philipiv.libs.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to specify the config name and path to save fields marked with
 * {@link Config} to
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSection {
	/**
	 * The path to save config values to
	 */
	String path() default "";
	/**
	 * The name of the config to use, without the extension
	 */
	String config() default "config";
}