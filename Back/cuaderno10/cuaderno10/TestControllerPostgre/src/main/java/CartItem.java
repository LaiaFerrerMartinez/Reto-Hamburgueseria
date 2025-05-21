import java.math.BigDecimal;

public class CartItem {
    private int productId;
    private String nombre;
    private int cantidad;
    private BigDecimal precioValor;
    private String imageUrl;    // mapeo desde img_producto

    public CartItem(int productId,
                    String nombre,
                    int cantidad,
                    BigDecimal precioValor,
                    String imageUrl) {
        this.productId   = productId;
        this.nombre      = nombre;
        this.cantidad    = cantidad;
        this.precioValor = precioValor;
        this.imageUrl    = imageUrl;
    }

    public int getProductId()        { return productId; }
    public String getNombre()        { return nombre; }
    public int getCantidad()         { return cantidad; }
    public BigDecimal getPrecioValor() { return precioValor; }
    public String getImageUrl()      { return imageUrl; }
}