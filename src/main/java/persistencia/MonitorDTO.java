package persistencia;
/*public class MonitorDTO extends ComponenteDTO {
    public MonitorDTO() {}

    public MonitorDTO(String id, String descripcion, String marca, String modelo, 
                      double costo, double precioBase) {
        super(id, descripcion, marca, modelo, costo, precioBase);
    }
}*/

/*
public class MonitorDTO extends ComponenteDTO {
    public MonitorDTO() {
        super();
    }

    public MonitorDTO(String id, String descripcion, String marca, String modelo, 
                     double costo, double precioBase) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
    }
}*/
public class MonitorDTO extends ComponenteDTO {
    private int cantidad;  // Nuevo campo agregado

    public MonitorDTO() {
        super();
        this.cantidad = 1;  // Valor por defecto
    }

    public MonitorDTO(String id, String descripcion, String marca, String modelo, 
                     double costo, double precioBase) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.cantidad = 1;  // Valor por defecto
    }

    public MonitorDTO(String id, String descripcion, String marca, String modelo,
                     double costo, double precioBase, int cantidad) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.cantidad = cantidad;
    }

    // Nuevos métodos para cantidad
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "MonitorDTO{" +
               "id='" + getId() + '\'' +
               ", descripcion='" + getDescripcion() + '\'' +
               ", marca='" + getMarca() + '\'' +
               ", modelo='" + getModelo() + '\'' +
               ", costo=" + getCosto() +
               ", precioBase=" + getPrecioBase() +
               ", cantidad=" + cantidad +
               '}';
    }
}