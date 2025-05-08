package persistencia;
/*public class TarjetaVideoDTO extends ComponenteDTO {
    private String memoria;

    public TarjetaVideoDTO() {}

    public TarjetaVideoDTO(String id, String descripcion, String marca, String modelo, 
                          double costo, double precioBase, String memoria) {
        super(id, descripcion, marca, modelo, costo, precioBase);
        this.memoria = memoria;
    }

	public String getMemoria() {
		return memoria;
	}

	public void setMemoria(String memoria) {
		this.memoria = memoria;
	}

    // Getter y setter para memoria
    // ...
}

*/
/*
public class TarjetaVideoDTO extends ComponenteDTO {
    private String memoria;

    public TarjetaVideoDTO() {
        super();
    }

    public TarjetaVideoDTO(String id, String descripcion, String marca, String modelo, 
                          double costo, double precioBase, String memoria) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.memoria = memoria;
    }

    // Getters y Setters
    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }
}
*/

public class TarjetaVideoDTO extends ComponenteDTO {
    private String memoria;
    private int cantidad;  // Nuevo campo agregado

    public TarjetaVideoDTO() {
        super();
        this.cantidad = 1;  // Valor por defecto
    }

    public TarjetaVideoDTO(String id, String descripcion, String marca, String modelo, 
                          double costo, double precioBase, String memoria) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.memoria = memoria;
        this.cantidad = 1;  // Valor por defecto
    }

    public TarjetaVideoDTO(String id, String descripcion, String marca, String modelo,
                         double costo, double precioBase, String memoria, int cantidad) {
        super(id, descripcion, marca, modelo, costo, precioBase, false);
        this.memoria = memoria;
        this.cantidad = cantidad;
    }

    // Getters y Setters existentes
    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
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
        return "TarjetaVideoDTO{" +
               "id='" + getId() + '\'' +
               ", descripcion='" + getDescripcion() + '\'' +
               ", marca='" + getMarca() + '\'' +
               ", modelo='" + getModelo() + '\'' +
               ", costo=" + getCosto() +
               ", precioBase=" + getPrecioBase() +
               ", memoria='" + memoria + '\'' +
               ", cantidad=" + cantidad +
               '}';
    }
}