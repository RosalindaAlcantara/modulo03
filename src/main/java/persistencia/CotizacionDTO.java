package persistencia;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDTO {
    private int id;
    private Date fecha;
    private double total;
    private List<DetalleCotizacionDTO> detalles;

    public CotizacionDTO() {
        this.detalles = new ArrayList<>();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public List<DetalleCotizacionDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleCotizacionDTO> detalles) {
		this.detalles = detalles;
	}

    // Getters y Setters
    // ... (implementar todos los getters y setters)
}
