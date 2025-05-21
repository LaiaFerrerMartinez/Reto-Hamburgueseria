// src/main/java/CartServlet.java
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDao;
    private Gson gson = new Gson();

    @Override
    public void init() {
        var motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
        cartDao = new CartDAO(motor);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int userId = Integer.parseInt(req.getParameter("userId"));
        try {
            var items = cartDao.getCartItems(userId);
            gson.toJson(items, resp.getWriter());
        } catch (Exception e) {
            resp.setStatus(500);
            gson.toJson(new ErrorResponse(e.getMessage()), resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<?,?> body = gson.fromJson(req.getReader(), Map.class);
        int userId    = ((Number)body.get("userId")).intValue();
        int productId = ((Number)body.get("productId")).intValue();
        boolean ok;
        try {
            ok = cartDao.addCartItem(userId, productId);
        } catch (Exception e) {
            ok = false;
        }
        resp.setStatus(ok?201:500);
        Map<String,Object> out = new HashMap<>();
        out.put("success", ok);
        gson.toJson(out, resp.getWriter());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<?,?> body = gson.fromJson(req.getReader(), Map.class);
        int userId    = ((Number)body.get("userId")).intValue();
        int productId = ((Number)body.get("productId")).intValue();
        boolean ok;
        try {
            ok = cartDao.removeCartItem(userId, productId);
        } catch (Exception e) {
            ok = false;
        }
        resp.setStatus(ok?200:500);
        Map<String,Object> out = new HashMap<>();
        out.put("success", ok);
        gson.toJson(out, resp.getWriter());
    }
}
