package edu.escuelaing.arep.app.anotaciones;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Anotacion web  encargada de ejecutar el framework HTTP en cada uno de los tres recursos implementados.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Web {
	/**
	 * String que se encarga de llevar a cabo el valor de la anotacion.
	 * @return Retorna las paginas en las cuales se encuentra la anotacion.
	 */
    String value();
}