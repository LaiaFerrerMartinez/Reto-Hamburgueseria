
import com.google.gson.Gson;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/login")
public class UsuarioAction extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        LoginRequest credentials = gson.fromJson(request.getReader(), LoginRequest.class);
        UsuarioDAO dao = new UsuarioDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));

        Usuario user = dao.autenticar(credentials.getEmail(), credentials.getPassword());
        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(user, response.getWriter());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            gson.toJson(new ErrorResponse("Invalid credentials"), response.getWriter());
        }
        response.getWriter().flush();
    }
}

// LoginRequest.java
class LoginRequest {
    private String email;    // ahora email
    private String password;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}


// ErrorResponse.java
class ErrorResponse {
    private String error;

    public ErrorResponse(String error) { this.error = error; }
    public String getError() { return error; }
}