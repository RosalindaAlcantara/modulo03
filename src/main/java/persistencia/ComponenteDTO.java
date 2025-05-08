package persistencia;
/*public class ComponenteDTO {
    private String id;
    private String descripcion;
    private String marca;
    private String modelo;
    private double costo;
    private double precioBase;

    // Constructor, getters y setters
    public ComponenteDTO() {}

    public ComponenteDTO(String id, String descripcion, String marca, String modelo, double costo, double precioBase) {
        this.id = id;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.costo = costo;
        this.precioBase = precioBase;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public double getPrecioBase() {
		return precioBase;
	}

	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}

    // Getters y setters para todos los campos
    // ...
}

*/
public class ComponenteDTO {
    private String id;
    private String descripcion;
    private String marca;
    private String modelo;
    private double costo;
    private double precioBase;
    private boolean esPC;

    // Constructores
    public ComponenteDTO() {}

    
    public ComponenteDTO(String id, String descripcion, String marca, String modelo, 
                       double costo, double precioBase, boolean esPC) {
        this.id = id;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.costo = costo;
        this.precioBase = precioBase;
        this.esPC = esPC;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public double getPrecioBase() {
		return precioBase;
	}

	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}

	public boolean isEsPC() {
		return esPC;
	}

	public void setEsPC(boolean esPC) {
		this.esPC = esPC;
	}

    // Getters y Setters
    // ... (implementar todos los getters y setters)
}
