

import com.google.gson.Gson;

import javax.mail.MessagingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/register")
public class UsuarioRegisterAction extends HttpServlet {
    private Mailer mailer;

    @Override
    public void init() throws ServletException {
        super.init();
        mailer = new Mailer(
                "smtp.gmail.com", 465,
                "redwoodgrillzgz@gmail.com",
                "ffnplneffykvpwru",
                "redwoodgrillzgz@gmail.com"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        RegisterRequest data = gson.fromJson(request.getReader(), RegisterRequest.class);
        UsuarioDAO dao = new UsuarioDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));

        try {
            Usuario nuevo = dao.registrar(data.getEmail(), data.getUsername(), data.getPassword());
            boolean emailOK = true;
            try {
                String subject = "¡Welcome to Redwood Grill!";
                String body = String.format(
                        "Hi %s,%n%n¡Thank you so much for signing in Redwood Grill!...",
                        nuevo.getUsername()
                );
                mailer.send(nuevo.getEmail(), subject, body);
            } catch (MessagingException me) {
                emailOK = false;
                log("Error sending welcome", me);
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            Map<String,Object> out = new HashMap<>();
            out.put("id", nuevo.getId());
            out.put("email", nuevo.getEmail());
            out.put("username", nuevo.getUsername());
            out.put("typeUser", nuevo.getTypeUser());
            if (!emailOK) out.put("warning", "It wasn't possible to send welcome e-mail.");
            gson.toJson(out, response.getWriter());

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                gson.toJson(new ErrorResponse("This e-mail is already subscribed."), response.getWriter());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Error signing in: " + e.getMessage()), response.getWriter());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse("Internal error: " + e.getMessage()), response.getWriter());
        } finally {
            response.getWriter().flush();
        }
    }
}


// DTO para parsear JSON
class RegisterRequest {
    private String email;
    private String username;
    private String password;
    public String getEmail()    { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

