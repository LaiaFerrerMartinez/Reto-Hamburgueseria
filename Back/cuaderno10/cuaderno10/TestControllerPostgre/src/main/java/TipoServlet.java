// src/main/java/TipoServlet.java
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/types")
public class TipoServlet extends HttpServlet {
    private TipoDAO tipoDao;
    private Gson gson = new Gson();

    @Override
    public void init() {
        var motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
        tipoDao = new TipoDAO(motor);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            List<TipoDAO.Tipo> tipos = tipoDao.listarTodos();
            gson.toJson(tipos, resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            gson.toJson(new ErrorResponse(e.getMessage()), resp.getWriter());
        }
    }
}
