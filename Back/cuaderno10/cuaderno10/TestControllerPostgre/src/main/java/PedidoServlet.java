import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/pedido")
public class PedidoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        try {
            Map<?,?> body = gson.fromJson(req.getReader(), Map.class);
            int userId    = ((Number) body.get("userId")).intValue();
            int articleId = ((Number) body.get("articleId")).intValue();

            PedidoDAO dao = new PedidoDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));
            boolean ok = dao.crearPedido(userId, articleId);

            resp.setStatus(ok ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String,Object> out = new HashMap<>();
            out.put("success", ok);
            out.put(ok ? "message" : "error", ok ? "Product added to cart" : "It wasn't able to add the product to the cart.");
            gson.toJson(out, resp.getWriter());

        } catch (Exception e) {
            log("Error en /api/pedido", e);
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
