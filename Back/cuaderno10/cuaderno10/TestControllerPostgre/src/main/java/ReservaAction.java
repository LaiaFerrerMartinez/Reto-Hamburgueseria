import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/reserva")
public class ReservaAction extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();

        ReservaRequest data = gson.fromJson(req.getReader(), ReservaRequest.class);
        IMotorSQL motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
        ReservaDAO dao = new ReservaDAO(motor);

        try {
            boolean ok = dao.crearReserva(data);
            if (ok) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                gson.toJson(Map.of("message","Reserve created!"), resp.getWriter());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                gson.toJson(new ErrorResponse("It wasn't possible to create the reserve."), resp.getWriter());
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // Devolvemos el mensaje de capacidad excedida o de otro error
            gson.toJson(new ErrorResponse(e.getMessage()), resp.getWriter());
        }

        resp.getWriter().flush();
    }
}
