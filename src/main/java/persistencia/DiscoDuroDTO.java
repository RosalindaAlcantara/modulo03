package persistencia;

/*public class DiscoDuroDTO extends ComponenteDTO {
    private String capacidad;

    public DiscoDuroDTO() {}

    public DiscoDuroDTO(String id, String descripcion, String marca, String modelo, 
                       double costo, double precioBase, String capacidad) {
        super(id, descripcion, marca, modelo, costo, precioBase);
        this.capacidad = capacidad;
    }

	public String getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(String capacidad) {
		this.capacidad = capacidad;
	}

    // Getter y setter para capacidad
    // ...
}
*/

/*
public class DiscoDuroDTO extends ComponenteDTO {
    private String capacidad;

    public DiscoDuroDTO() {
        super();
    }

    public DiscoDuroDTO(String id, String descripcion, String marca, String modelo, 
                       double costo, double precioBase, String capacidad) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.capacidad = capacidad;
    }

    // Getters y Setters
    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }
}
*/

public class DiscoDuroDTO extends ComponenteDTO {
    private String capacidad;
    private int cantidad;  // Nuevo campo agregado

    public DiscoDuroDTO() {
        super();
        this.cantidad = 1;  // Valor por defecto
    }

    public DiscoDuroDTO(String id, String descripcion, String marca, String modelo, 
                       double costo, double precioBase, String capacidad) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.capacidad = capacidad;
        this.cantidad = 1;  // Valor por defecto
    }

    public DiscoDuroDTO(String id, String descripcion, String marca, String modelo,
                       double costo, double precioBase, String capacidad, int cantidad) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.capacidad = capacidad;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
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
        return "DiscoDuroDTO{" +
                "id='" + getId() + '\'' +
                ", descripcion='" + getDescripcion() + '\'' +
                ", marca='" + getMarca() + '\'' +
                ", modelo='" + getModelo() + '\'' +
                ", costo=" + getCosto() +
                ", precioBase=" + getPrecioBase() +
                ", capacidad='" + capacidad + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}