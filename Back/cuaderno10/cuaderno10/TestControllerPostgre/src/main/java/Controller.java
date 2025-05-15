import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "Controller", urlPatterns = {"/Controller"})
public class Controller extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");

        String action = req.getParameter("ACTION");

        if (action != null && action.contains(".")) {
            String[] partes = action.split("\\.");
            if (partes.length == 2) {
                String objeto = partes[0].toUpperCase();
                String operacion = partes[1].toUpperCase();

                switch (objeto) {
                    case "USUARIO":
                        UsuarioService usuarioService = new UsuarioService();
                        switch (operacion) {
                            case "LOGIN":
                                usuarioService.login(req, resp);
                                break;
                            case "REGISTER":
                                usuarioService.register(req, resp);
                                break;
                            default:
                                writer.println("{\"status\":\"error\",\"mensaje\":\"Acción de usuario no reconocida\"}");
                        }
                        break;

                    case "PRODUCTO":
                        writer.println("{\"status\":\"info\",\"mensaje\":\"Operaciones de PRODUCTO solo por POST\"}");
                        break;

                    default:
                        writer.println("{\"status\":\"error\",\"mensaje\":\"Objeto no soportado: " + objeto + "\"}");
                }
            } else {
                writer.println("{\"status\":\"error\",\"mensaje\":\"Parámetro ACTION mal formado: debe tener un punto\"}");
            }
        } else {
            writer.println("{\"status\":\"error\",\"mensaje\":\"Parámetro ACTION mal formado o no presente\"}");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter escritor = resp.getWriter();
        resp.setContentType("application/json");

        String action = req.getParameter("ACTION");

        if (action != null && action.contains(".")) {
            String[] partes = action.split("\\.");
            if (partes.length == 2) {
                String objeto = partes[0].toUpperCase();
                String operacion = partes[1].toUpperCase();

                switch (objeto) {
                    case "PRODUCTO":
                        if (operacion.equals("ADD")) {
                            String nombre = req.getParameter("nombre");
                            String precio = req.getParameter("precio");

                            if (nombre != null && precio != null) {
                                String json = "{\"status\":\"ok\",\"mensaje\":\"Producto '" + nombre + "' añadido con precio " + precio + "\"}";
                                escritor.println(json);
                            } else {
                                String json = "{\"status\":\"error\",\"mensaje\":\"Faltan parámetros: nombre o precio\"}";
                                escritor.println(json);
                            }

                        } else if (operacion.equals("LIST")) {
                            ArrayList<String> productos = new ArrayList<>();
                            productos.add("{\"nombre\":\"Hamburguesa Clásica\",\"precio\":7.5}");
                            productos.add("{\"nombre\":\"Hamburguesa BBQ\",\"precio\":8.0}");
                            productos.add("{\"nombre\":\"Patatas Deluxe\",\"precio\":3.0}");

                            String json = "[";
                            for (int i = 0; i < productos.size(); i++) {
                                json += productos.get(i);
                                if (i < productos.size() - 1) json += ",";
                            }
                            json += "]";
                            escritor.println(json);

                        } else {
                            String json = "{\"status\":\"error\",\"mensaje\":\"Acción no reconocida para objeto PRODUCTO\"}";
                            escritor.println(json);
                        }
                        break;

                    case "USUARIO":
                        UsuarioService usuarioService = new UsuarioService();
                        switch (operacion) {
                            case "LOGIN":
                                usuarioService.login(req, resp);
                                break;
                            case "REGISTER":
                                usuarioService.register(req, resp);
                                break;
                            default:
                                escritor.println("{\"status\":\"error\",\"mensaje\":\"Acción de usuario no reconocida\"}");
                        }
                        break;

                    default:
                        escritor.println("{\"status\":\"error\",\"mensaje\":\"Objeto no reconocido\"}");
                }
            } else {
                escritor.println("{\"status\":\"error\",\"mensaje\":\"Parámetro ACTION mal formado\"}");
            }
        } else {
            escritor.println("{\"status\":\"error\",\"mensaje\":\"Parámetro ACTION mal formado o no presente\"}");
        }
    }
}
