import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Devuelve el histórico de carrito (o pedidos guardados).
 * Antes estaba en /api/pedido y chocaba con PedidoServlet.
 * Ahora lo movemos a /api/pedido/historico.
 */
@WebServlet("/api/pedido/historico")
public class CartAction extends HttpServlet {
    private CartDAO cartDao;
    private Gson gson = new Gson();

    @Override
    public void init() {
        var motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
        cartDao = new CartDAO(motor);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        resp.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // CORS
        resp.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        resp.setContentType("application/json;charset=UTF-8");

        String sUser = req.getParameter("userId");
        if (sUser == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Falta parámetro userId"), resp.getWriter());
            return;
        }
        try {
            int userId = Integer.parseInt(sUser);
            List<CartItem> items = cartDao.getCartItems(userId);
            gson.toJson(items, resp.getWriter());
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("userId no es un número"), resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse(e.getMessage()), resp.getWriter());
        }
    }
}
