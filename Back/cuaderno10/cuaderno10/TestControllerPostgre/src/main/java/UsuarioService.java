import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UsuarioService {

    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter escritor = response.getWriter();
        response.setContentType("application/json");

        String user = request.getParameter("usuario");
        String password = request.getParameter("password");

        if (user == null || password == null) {
            escritor.println("{\"status\":\"error\",\"mensaje\":\"Faltan datos\"}");
        } else if (user.equals("admin") && password.equals("1234")) {
            escritor.println("{\"status\":\"ok\",\"mensaje\":\"Bienvenido, " + user + "\"}");
        } else {
            escritor.println("{\"status\":\"fail\",\"mensaje\":\"Usuario o contraseña incorrectos\"}");
        }
    }

    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter escritor = response.getWriter();
        response.setContentType("application/json");

        String user = request.getParameter("usuario");
        String password = request.getParameter("password");

        if (user != null && password != null) {
            escritor.println("{\"status\":\"ok\",\"mensaje\":\"Usuario " + user + " registrado con éxito\"}");
        } else {
            escritor.println("{\"status\":\"error\",\"mensaje\":\"Faltan campos para el registro\"}");
        }
    }
}
