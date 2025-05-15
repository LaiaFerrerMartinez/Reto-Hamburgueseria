// UsuarioAction.java
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/login")
public class UsuarioAction extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        // Leer credenciales desde el cuerpo JSON
        LoginRequest credentials = gson.fromJson(request.getReader(), LoginRequest.class);

        // Autenticar usuario
        UsuarioDAO dao = new UsuarioDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));
        Usuario user = dao.autenticar(credentials.getUsername(), credentials.getPassword());

        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(user, response.getWriter());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            gson.toJson(new ErrorResponse("Credenciales inválidas"), response.getWriter());
        }
        response.getWriter().flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Login vía query parameters (no recomendado en producción)
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Usuario user = new UsuarioDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE))
                .autenticar(username, password);

        response.setContentType("application/json");
        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            new Gson().toJson(user, response.getWriter());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            new Gson().toJson(new ErrorResponse("Credenciales inválidas"), response.getWriter());
        }
        response.getWriter().flush();
    }
}

// LoginRequest.java
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

// ErrorResponse.java
class ErrorResponse {
    private String error;

    public ErrorResponse(String error) { this.error = error; }
    public String getError() { return error; }
}