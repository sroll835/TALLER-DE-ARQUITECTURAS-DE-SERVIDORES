package edu.escuelaing.arep.app.web;
import edu.escuelaing.arep.app.anotaciones.Web;
/**
 * Clase que se encarga de realizar la respectiva ejecucion y despliegue del fondo de pantalla y el js en formato HTML.
 * @author  Alejandro Toro Daza
 * @version 1.0.  (11 de Febrero del 2021) 
 */
public class WebServiceJs {
	/**
     * Metodo que se encarga de ejecutar la anotacion @Web y retornar el contenido de la pagina HTML en el recurso /agradecimiento.html.
     * @return Retorna un fondo de pantalla y el js en formato HTML.
     */
    @Web("/agradecimiento.html")
    public static String returnHtmlWithJS(){
        return "<html><title>Agradecimiento</title><head><script src=\"/agradecimiento.js\"></script></head>"
        		+ "<body style = \"background: url(https://www.setaswall.com/wp-content/uploads/2017/03/Artistic-Landscape-4K-Wallpaper-3840x2160.jpg) no-repeat ; background-size: 100% 100%;\">\r\n"
        		+ "</body>"
        		+ "</html>";
    }
}