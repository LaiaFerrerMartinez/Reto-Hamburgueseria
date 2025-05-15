import java.math.BigDecimal;

public class Producto {
    private int id;
    private String nombre;
    private String precioFormateado;
    private BigDecimal precioValor;
    private String img;

    public Producto(int id, String nombre, String precioFormateado, BigDecimal precioValor, String img) {
        this.id = id;
        this.nombre = nombre;
        this.precioFormateado = precioFormateado;
        this.precioValor = precioValor;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecioFormateado() {
        return precioFormateado;
    }

    public void setPrecioFormateado(String precioFormateado) {
        this.precioFormateado = precioFormateado;
    }

    public BigDecimal getPrecioValor() {
        return precioValor;
    }

    public void setPrecioValor(BigDecimal precioValor) {
        this.precioValor = precioValor;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}