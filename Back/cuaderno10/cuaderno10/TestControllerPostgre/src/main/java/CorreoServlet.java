// CorreoServlet.java
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/correo")
public class CorreoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        try {
            Map<?,?> body = gson.fromJson(req.getReader(), Map.class);
            String email = (String) body.get("nombre_correo");
            if (email == null || email.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(Map.of("success", false, "error", "Invalid e-mail"), resp.getWriter());
                return;
            }

            CorreoDAO dao = new CorreoDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));
            boolean ok = dao.insertarCorreo(email);
            if (!ok) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                gson.toJson(Map.of("success", false, "error", "This e-mail is already subscribed."), resp.getWriter());
            } else {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                gson.toJson(Map.of("success", true, "message", "Thanks for subscribing!"), resp.getWriter());
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(Map.of("success", false, "error", "Database internal error"), resp.getWriter());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(Map.of("success", false, "error", "Internal error"), resp.getWriter());
        }
    }
}
