package edu.escuelaing.arep.app;
import edu.escuelaing.arep.app.framework.WebLoader;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import org.apache.commons.io.FilenameUtils;

public class App {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private WebLoader webLoader;
    /**
     * Metodo constructor de la clase App que se encarga de manejar el Socket del servidor.
     */
    public App() {
        int port = getPort();
        webLoader = new WebLoader();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }
        clientSocket = null;
        out = null;
        in = null;
    }
    /**
     * Metodo que se encarga de iniciar el servidor, comienza a escuchar conexiones y carga metodos que usan la anotación @Web.
     * @throws IOException Excepcion que se lanza si no se escucha ningun tipo de conexion.
     */
    public void start() throws IOException {
        webLoader.init();
        while (true) {
            try {
                System.out.println("Ready to receive...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            Pattern pattern = Pattern.compile("GET (/[^\\s]*)");
            Matcher matcher = null;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                stringBuilder.append(inputLine);
                if (!in.ready()) {
                    matcher = pattern.matcher(stringBuilder.toString());
                    if (matcher.find()) {
                        String fileRequested = matcher.group().substring(4);
                        System.out.println("VALUE: " + fileRequested);
                        if (!webLoader.isResourcePresent(fileRequested)) {
                            handleRequest(fileRequested);
                        } else {
                            out.println("HTTP/1.1 200 \r\nAccess-Control-Allow-Origin: *\r\nContent-Type: text/html\r\n\r\n");
                            out.println(webLoader.getResource(fileRequested));
                        }
                    }
                    break;
                }
            }
            out.close();
            in.close();
            clientSocket.close();
        }
    }
    /**
     * Metodo que se encarga de manejar como devolver un recurso solicitado.
     * @param fileRequested Parametro que se encarga de obtener el archivo requerido por el usuario.
     * @throws IOException Excepcion que se lanza si no se encuentra el archivo requerido por el usuario.
     */
    private void handleRequest(String fileRequested) throws IOException {
        String filePath = "src/main/resources/";
        String ext = FilenameUtils.getExtension(fileRequested);
        boolean isImage = false;
        switch (ext) {
            case "jpg":
                filePath += "images/" + fileRequested;
                isImage = true;
                break;
            case "js":
                filePath += "js/" + fileRequested;
                break;
            case "html":
                filePath += "web-pages/" + fileRequested;
                break;
        }

        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            String header = generateHeader(isImage, ext, file.length());
            if (isImage) {
                FileInputStream fileIn = new FileInputStream(filePath);
                OutputStream os = clientSocket.getOutputStream();
                for (char c : header.toCharArray()) {
                    os.write(c);
                }
                int a;
                while ((a = fileIn.read()) > -1) {
                    os.write(a);
                }
                os.flush();
                fileIn.close();
                os.close();
            } else {
                out.println(header);
                BufferedReader br = new BufferedReader(new FileReader(file));

                StringBuilder stringBuilder = new StringBuilder();
                String st;
                while ((st = br.readLine()) != null) {
                    stringBuilder.append(st);
                }
                out.println(stringBuilder.toString());
                br.close();
            }
        } else {
            out.println("HTTP/1.1 404\r\nAccess-Control-Allow-Origin: *\r\n\r\n<html><body><h1>404 NOT FOUND (" + fileRequested + ")</h1></body></html>");
        }
    }
    /**
     * Metodo encargado de obtener el encabezado HTTP de la pagina web.
     * @param isImage Parametro que verifica si el recurso solicitado es una imagen.
     * @param ext Parametro que tiene la extensión del recurso.
     * @param length Parametro que tiene la longitud del recurso.
     * @return Retorna el encabezado de la pagina web.
     */
    private String generateHeader(boolean isImage, String ext, long length) {
        String header = null;
        if (isImage) {
            header = "HTTP/1.1 200 \r\nAccess-Control-Allow-Origin: *\r\nContent-Type: image/" + ext + "\r\nConnection: close\r\nContent-Length:" + length + "\r\n\r\n";
        } else {
            header = "HTTP/1.1 200 \r\nAccess-Control-Allow-Origin: *\r\nContent-Type: text/html\r\n\r\n";
        }
        return header;
    }
    /**
     * Este metodo lee el puerto predeterminado segun lo especificado por la variable PORT en el entorno.
     * @return returns Retorna el puerto predeterminado si el heroku-port no esta configurado (es decir, en localhost).
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
    /**
     * Metodo principal main que se encarga del funcionamiento de toda la clase App.
     * @param args Parametro que indica la lista de los elementos a evaluar.
     * @throws IOException Excepcion que se lanza si ocurre algun error en la ejecucion del programa.
     */
    public static void main(String[] args) throws IOException {
        new App().start();
    }
}