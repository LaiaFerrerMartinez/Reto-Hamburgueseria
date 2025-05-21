import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/delivery")
public class DeliveryAction extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();

        DeliveryRequest data = gson.fromJson(req.getReader(), DeliveryRequest.class);
        IMotorSQL motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
        DeliveryDAO dao = new DeliveryDAO(motor);

        try {
            boolean ok = dao.crearDelivery(data);
            if (ok) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                gson.toJson(Map.of("message","Address saved"), resp.getWriter());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                gson.toJson(new ErrorResponse("Adress could not be saved."), resp.getWriter());
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Error: " + e.getMessage()), resp.getWriter());
        }
        resp.getWriter().flush();
    }
}
