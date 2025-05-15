public class Hamburguesa {
    private int id;
    private String nombre;
    private double precio;

    public Hamburguesa(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters opcionales si los necesitas
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

    @Override
    public String toString() {
        return "Hamburguesa{id=" + id + ", nombre='" + nombre + "', precio=" + precio + "}";
    }
}

