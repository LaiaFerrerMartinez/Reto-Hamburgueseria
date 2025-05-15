import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="Controller", urlPatterns = {"/Controller"})
public class Controller extends HttpServlet {
    // GET      -> Java
    // POST     -> Java
    // DELETE
    // UPDATE
    // PUT
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            MotorSQL motorSQL = new MotorSQL();
            PrintWriter escritor = resp.getWriter();
            escritor.println("Estoy en Web con Java");
            String ACTION = req.getParameter("ACTION");
            String USER = req.getParameter("USER");
            escritor.println("Voy a: " + ACTION);
            escritor.println("Soy el usuario: " + USER);
            try {
                motorSQL.conectar();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MotorSQL motorSQL = new MotorSQL();
        PrintWriter escritor = resp.getWriter();
        escritor.println("Estoy en Web con Java");
        String ACTION = req.getParameter("ACTION");
        String USER = req.getParameter("USER");
        escritor.println("Voy a: " + ACTION);
        escritor.println("Soy el usuario: " + USER);
        try {
            motorSQL.conectar();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
