package edu.escuelaing.arep.app.framework;
import edu.escuelaing.arep.app.anotaciones.Web;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
public class WebLoader {
	/**
	 * Metodo constructor de la clase WebLoader que se encarga de almacenar en la variable urlMethod un HashMap.
	 */
    private Map<String, Method> urlMethod;
    public WebLoader() {
        urlMethod = new HashMap<>();
    }
    /**
     * Metodo encargado de cargar las clases del paquete 'edu.escuelaing.arep.web'.
     */
    public void init() {
        String webPackage = "edu.escuelaing.arep.app.web";
        Reflections reflections = new Reflections(webPackage, new SubTypesScanner(false));
        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
        for (Class cls : allClasses) {
            for (Method method : cls.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Web.class)) {
                    urlMethod.put(method.getAnnotation(Web.class).value(), method);
                }
            }
        }
    }
    /**
     * Metodo que se encarga de realizar la validacion si el recurso esta presente.
     * @param resource Parametro que representa el recurso requerido por el usuario.
     * @return Retorna True si esta presente el recurso, Falso d.l.c.
     */
    public boolean isResourcePresent(String resource) {
        return urlMethod.containsKey(resource);
    }
    /**
     * Metodo que se encarga de cargar el recurso requerido por el usuario.
     * @param resource Parametro que representa el recurso requerido por el usuario.
     * @return Retorna el recurso requerido por el usuario.
     */
    public String getResource(String resource) {
        String res = null;
        try {
            res = (String) urlMethod.get(resource).invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("Error invoking method");
            res = "ERROR";
        }
        return res;
    }
}