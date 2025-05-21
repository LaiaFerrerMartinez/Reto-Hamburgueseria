// src/main/java/Producto.java
import java.math.BigDecimal;

public class Producto {
    private int id;
    private String nombre;
    private String precioFormateado;
    private BigDecimal precioValor;
    private String img;
    private String descripcion;
    private int totalVotes;
    private double avgRating;
    private int typeId;

    // Constructor CORREGIDO para CREATE / LIST
    public Producto(int id, String nombre, String precioFormateado,
                    BigDecimal precioValor, String img,
                    String descripcion, int totalVotes,
                    double avgRating) {
        this.id                 = id;
        this.nombre             = nombre;
        this.precioFormateado   = precioFormateado;
        this.precioValor        = precioValor;
        this.img                = img;
        this.descripcion        = descripcion;
        this.totalVotes         = totalVotes;
        this.avgRating          = avgRating;
    }

    // Constructor para LIST con typeId
    public Producto(int id, int typeId, String nombre,
                    String precioFormateado, BigDecimal precioValor,
                    String img, String descripcion,
                    int totalVotes, double avgRating) {
        this(id, nombre, precioFormateado, precioValor, img, descripcion, totalVotes, avgRating);
        this.typeId = typeId;
    }

    // getters & setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPrecioFormateado() { return precioFormateado; }
    public void setPrecioFormateado(String precioFormateado) { this.precioFormateado = precioFormateado; }

    public BigDecimal getPrecioValor() { return precioValor; }
    public void setPrecioValor(BigDecimal precioValor) { this.precioValor = precioValor; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getTotalVotes() { return totalVotes; }
    public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
}
