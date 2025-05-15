import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/register")
public class UsuarioRegisterAction extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        // Leer datos de registro desde JSON
        RegisterRequest data = gson.fromJson(request.getReader(), RegisterRequest.class);

        UsuarioDAO dao = new UsuarioDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));
        try {
            Usuario nuevo = dao.registrar(data.getEmail(), data.getUsername(), data.getPassword());
            response.setStatus(HttpServletResponse.SC_CREATED);
            gson.toJson(nuevo, response.getWriter());
        } catch (SQLException e) {
            // Código de violación de restricción única en PostgreSQL: 23505
            if ("23505".equals(e.getSQLState())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                gson.toJson(new ErrorResponse("El correo ya está registrado"), response.getWriter());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Error al registrar usuario: " + e.getMessage()), response.getWriter());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse("Error interno: " + e.getMessage()), response.getWriter());
        }
        response.getWriter().flush();
    }
}

// RegisterRequest.java
class RegisterRequest {
    private String email;
    private String username;
    private String password;

    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
