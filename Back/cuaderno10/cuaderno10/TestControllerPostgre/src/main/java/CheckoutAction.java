import com.google.gson.Gson;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/checkout")
public class CheckoutAction extends HttpServlet {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();

        try {
            Map<?,?> body = gson.fromJson(req.getReader(), Map.class);
            int userId = ((Number) body.get("userId")).intValue();

            IMotorSQL motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
            UsuarioDAO udao = new UsuarioDAO(motor);
            PedidoAntiguoDAO dao = new PedidoAntiguoDAO(motor);

            boolean emailOK = true;
            try {
                Usuario u = udao.getById(userId);
                if (u == null) throw new Exception("User not found.");

                String subject = "Confirmation of your order at Redwood Grill:";
                String bodyMail = String.format(
                        "¡Hi %s!\n\nThank you so much for your order. It will be ready in 30 minutes.\n\nProducts:\n%s\n\n—Redwood Grill's team",
                        u.getUsername(),
                        dao.listProductsForEmail(userId)
                );
                mailer.send(u.getEmail(), subject, bodyMail);
            } catch (Exception e) {
                emailOK = false;
                log("Error sending e-mail", e);
            }

            int moved = dao.checkout(userId);
            Map<String,Object> out = new HashMap<>();
            out.put("moved", moved);
            if (!emailOK) {
                out.put("warning", "E-mail NOT sent, but order processed.");
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            gson.toJson(out, resp.getWriter());

        } catch (Exception e) {
            log("Error en /api/checkout", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String,String> err = new HashMap<>();
            err.put("error", e.getMessage());

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            err.put("stack", sw.toString());

            gson.toJson(err, resp.getWriter());
        } finally {
            resp.getWriter().flush();
        }
    }
}
