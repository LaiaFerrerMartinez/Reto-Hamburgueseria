import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/products")
public class ProductoAction extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String tipo = request.getParameter("type");
        if (tipo == null || tipo.isEmpty()) {
            tipo = "side";
        }

        // 1) Leemos el parámetro sort
        String sort = request.getParameter("sort");
        boolean asc  = "asc".equalsIgnoreCase(sort);
        boolean desc = "desc".equalsIgnoreCase(sort);

        try {
            ProductoDAO dao = new ProductoDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));
            List<Producto> lista;

            // 2) Llamamos a la versión correcta
            if (asc || desc) {
                lista = dao.listarPorTipo(tipo, sort);
            } else {
                lista = dao.listarPorTipo(tipo);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            new Gson().toJson(lista, response.getWriter());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Gson().toJson(
                    new ErrorResponse("Error al listar productos: " + e.getMessage()),
                    response.getWriter()
            );
        }
        response.getWriter().flush();
    }
}

