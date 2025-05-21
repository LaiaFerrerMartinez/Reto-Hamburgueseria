import com.google.gson.Gson;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/products/rating")
public class ProductoRatingAction extends HttpServlet {
    private static final String ORIGIN = "http://127.0.0.1:5500";

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", ORIGIN);
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        // 1) Leer params
        String productIdStr = req.getParameter("productId");
        String ratingStr    = req.getParameter("rating");
        Integer productId   = productIdStr != null ? Integer.valueOf(productIdStr) : null;
        int rating          = ratingStr != null ? Integer.parseInt(ratingStr) : 0;
        // (Opcional) extraer idUsuario de sesión/autenticación
        Integer userId      = null;

        try {
            // 2) Guardar rating
            ProductoDAO dao = new ProductoDAO( FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE) );
            dao.saveRating(productId, userId, rating);
            // 3) Confirmar con 204 No Content
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Gson().toJson(
                    new ErrorResponse("Error when saving rating: " + e.getMessage()),
                    resp.getWriter()
            );
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        String productIdStr = req.getParameter("productId");
        if (productIdStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new Gson().toJson(
                    new ErrorResponse("We need productId"),
                    resp.getWriter()
            );
            return;
        }
        int productId = Integer.parseInt(productIdStr);

        ProductoDAO dao = new ProductoDAO( FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE) );
        RatingSummary summary = dao.getRatingSummary(productId);
        resp.setStatus(HttpServletResponse.SC_OK);
        new Gson().toJson(summary, resp.getWriter());
    }
}
