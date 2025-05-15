
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/hamburguesas")
public class HamburguesaAction extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HamburguesaDAO dao = new HamburguesaDAO(FactoryMotorSQL.getInstance(FactoryMotorSQL.POSTGRE));
        List<Hamburguesa> lista = dao.listarHamburguesas();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        // Suponiendo que usas una librería como Gson para convertir lista a JSON:
        out.print(new Gson().toJson(lista));
        out.flush();
    }
}
