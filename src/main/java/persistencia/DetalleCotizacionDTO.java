package persistencia;
public class DetalleCotizacionDTO {
    private int id;
    private int num;
    private String descripcion;
    private int cantidad;
    private double importeCotizado;
    private double precioBase;
    private int cotizacionId;
    private String componenteId;
    private boolean esCompuesto;
    private ComponenteDTO componente;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public double getImporteCotizado() {
		return importeCotizado;
	}
	public void setImporteCotizado(double importeCotizado) {
		this.importeCotizado = importeCotizado;
	}
	public double getPrecioBase() {
		return precioBase;
	}
	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}
	public int getCotizacionId() {
		return cotizacionId;
	}
	public void setCotizacionId(int cotizacionId) {
		this.cotizacionId = cotizacionId;
	}
	public String getComponenteId() {
		return componenteId;
	}
	public void setComponenteId(String componenteId) {
		this.componenteId = componenteId;
	}
	public boolean isEsCompuesto() {
		return esCompuesto;
	}
	public void setEsCompuesto(boolean esCompuesto) {
		this.esCompuesto = esCompuesto;
	}
	public ComponenteDTO getComponente() {
		return componente;
	}
	public void setComponente(ComponenteDTO componente) {
		this.componente = componente;
	}

    // Getters y Setters
    // ... (implementar todos los getters y setters)
}
