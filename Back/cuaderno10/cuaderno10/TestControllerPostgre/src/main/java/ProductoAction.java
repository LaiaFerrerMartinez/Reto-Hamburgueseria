import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/products")
public class ProductoAction extends HttpServlet {
    private UsuarioDAO usuarioDao;
    private ProductoDAO productoDao;
    private Gson gson = new Gson();

    @Override
    public void init() {
        var motor = FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE);
        usuarioDao  = new UsuarioDAO(motor);
        productoDao = new ProductoDAO(motor);
    }

    // CORS preflight
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setStatus(200);
    }

    // GET ?typeId=...&type=...&sort=...
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        String sort   = req.getParameter("sort");
        String tid    = req.getParameter("typeId"); // filtro por ID
        String nombre = req.getParameter("type");   // filtro por nombre

        try {
            List<Producto> lista;
            if ("mejores".equalsIgnoreCase(sort)) {
                lista = productoDao.listarMasVotadosYCaros();
            } else if (tid != null && !tid.isBlank()) {
                lista = productoDao.listarPorTipoId(Integer.parseInt(tid), sort);
            } else if (nombre != null && !nombre.isBlank()) {
                lista = productoDao.listarPorTipo(nombre, sort);
            } else {
                lista = productoDao.listarPorTipo("", sort);
            }


            gson.toJson(lista, resp.getWriter());
        } catch (Exception e) {
            resp.setStatus(500);
            gson.toJson(new ErrorResponse(e.getMessage()), resp.getWriter());
        }
    }

    // POST → create
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        handleCrud(req, resp, Action.CREATE);
    }
    // PUT → update
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        handleCrud(req, resp, Action.UPDATE);
    }
    // DELETE → delete
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        handleCrud(req, resp, Action.DELETE);
    }

    private enum Action { CREATE, UPDATE, DELETE }

    private void handleCrud(HttpServletRequest req, HttpServletResponse resp, Action action) throws IOException {
        setCors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        RequestData d = gson.fromJson(req.getReader(), RequestData.class);

        // 1) Autenticación
        try {
            if (d.userId == null || !usuarioDao.checkPassword(d.userId, d.password)) {
                resp.setStatus(403);
                resp.getWriter().write("{\"error\":\"Invalid password\"}");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
            return;
        }

        // 2) Operación CRUD
        try {
            switch (action) {
                case CREATE:
                    productoDao.create(d.toProducto());
                    resp.setStatus(201);
                    return;
                case UPDATE:
                    productoDao.update(d.toProducto());
                    resp.setStatus(200);
                    return;
                case DELETE:
                    productoDao.delete(d.id);
                    resp.setStatus(204);
                    return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
            return;
        }
    }


    /** Mapea el JSON entrante a un objeto Producto listo para DAO */
    private static class RequestData {
        public Integer id;         // para update/delete
        public Integer typeId;     // para create/update
        public String name;
        public String description;
        public String price;
        public String img;
        public Integer userId;
        public String password;

        Producto toProducto() {
            BigDecimal precio = new BigDecimal(price.replace(",", "."));
            int pid = (id != null) ? id : 0;
            Producto p = new Producto(
                    pid,
                    name,
                    null,
                    precio,
                    img,
                    description,
                    0,
                    0.0
            );
            p.setTypeId(typeId);
            return p;
        }
    }

    /** Ponemos CORS abierto para facilitar las pruebas */
    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
