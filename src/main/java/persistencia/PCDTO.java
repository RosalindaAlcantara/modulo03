package persistencia;

import java.util.ArrayList;
import java.util.List;


/*
public class PCDTO extends ComponenteDTO {
    private List<DiscoDuroDTO> discosDuros;
    private List<MonitorDTO> monitores;
    private List<TarjetaVideoDTO> tarjetasVideo;

    public PCDTO() {
        discosDuros = new ArrayList<>();
        monitores = new ArrayList<>();
        tarjetasVideo = new ArrayList<>();
    }

    public PCDTO(String id, String descripcion, String marca, String modelo, 
                double costo, double precioBase) {
        super(id, descripcion, marca, modelo, costo, precioBase);
        discosDuros = new ArrayList<>();
        monitores = new ArrayList<>();
        tarjetasVideo = new ArrayList<>();
    }

    // Métodos para agregar componentes
    public void agregarDiscoDuro(DiscoDuroDTO disco, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            discosDuros.add(disco);
        }
    }

    public void agregarMonitor(MonitorDTO monitor, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            monitores.add(monitor);
        }
    }

    public void agregarTarjetaVideo(TarjetaVideoDTO tarjeta, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            tarjetasVideo.add(tarjeta);
        }
    }

    // Getters y setters
    // ...
}
*/

public class PCDTO extends ComponenteDTO {
    private List<DiscoDuroDTO> discosDuros;
    private List<MonitorDTO> monitores;
    private List<TarjetaVideoDTO> tarjetasVideo;

    public PCDTO() {
        super();
        this.discosDuros = new ArrayList<>();
        this.monitores = new ArrayList<>();
        this.tarjetasVideo = new ArrayList<>();
        super.setEsPC(true);
    }

    public PCDTO(String id, String descripcion, String marca, String modelo) {
        super(id, descripcion, marca, modelo, 0, 0, true);
        this.discosDuros = new ArrayList<>();
        this.monitores = new ArrayList<>();
        this.tarjetasVideo = new ArrayList<>();
    }

    // Métodos para agregar componentes
    public void agregarDiscoDuro(DiscoDuroDTO disco, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            discosDuros.add(disco);
        }
    }

    public void agregarMonitor(MonitorDTO monitor, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            monitores.add(monitor);
        }
    }

    public void agregarTarjetaVideo(TarjetaVideoDTO tarjeta, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            tarjetasVideo.add(tarjeta);
        }
    }

	public List<DiscoDuroDTO> getDiscosDuros() {
		return discosDuros;
	}

	public void setDiscosDuros(List<DiscoDuroDTO> discosDuros) {
		this.discosDuros = discosDuros;
	}

	public List<MonitorDTO> getMonitores() {
		return monitores;
	}

	public void setMonitores(List<MonitorDTO> monitores) {
		this.monitores = monitores;
	}

	public List<TarjetaVideoDTO> getTarjetasVideo() {
		return tarjetasVideo;
	}

	public void setTarjetasVideo(List<TarjetaVideoDTO> tarjetasVideo) {
		this.tarjetasVideo = tarjetasVideo;
	}

    // Getters y Setters
    // ... (implementar todos los getters y setters)
}
