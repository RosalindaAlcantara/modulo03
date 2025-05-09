package casosdeuso;
import java.sql.*;
import java.util.ArrayList;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import persistencia.ComponenteDAO;
import persistencia.CotizacionDAO;
import persistencia.CotizacionDTO;
import persistencia.DetalleCotizacionDTO;
import persistencia.DiscoDuroDAO;
import persistencia.DiscoDuroDTO;
import persistencia.MonitorDAO;
import persistencia.MonitorDTO;
import persistencia.PCDAO;
import persistencia.PCDTO;
import persistencia.TarjetaVideoDAO;
import persistencia.TarjetaVideoDTO;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cotizador2025";
        String user = "root";
        String password = "myOdito77";

        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            // 1. Crear instancias de los DAOs necesarios
            ComponenteDAO componenteDAO = new ComponenteDAO(conexion);
            DiscoDuroDAO discoDuroDAO = new DiscoDuroDAO(conexion);
            MonitorDAO monitorDAO = new MonitorDAO(conexion);
            TarjetaVideoDAO tarjetaVideoDAO = new TarjetaVideoDAO(conexion);
            PCDAO pcDAO = new PCDAO(conexion);
            CotizacionDAO cotizacionDAO = new CotizacionDAO(conexion);

            // 2. Crear algunos componentes de prueba si no existen
            crearComponentesDePrueba(componenteDAO, discoDuroDAO, monitorDAO, tarjetaVideoDAO, pcDAO);

            // 3. Crear una nueva cotización
            CotizacionDTO cotizacion = new CotizacionDTO();
            cotizacion.setFecha(Date.valueOf(LocalDate.now()));
            cotizacion.setTotal(0); // Se calculará automáticamente

            // 4. Agregar detalles a la cotización (componentes individuales y PCs)
            List<DetalleCotizacionDTO> detalles = new ArrayList<>();

            // Detalle 1: Un monitor individual
            MonitorDTO monitor = monitorDAO.obtenerPorId("mon001");
            if (monitor != null) {
                DetalleCotizacionDTO detalleMonitor = new DetalleCotizacionDTO();
                detalleMonitor.setNum(1);
                detalleMonitor.setDescripcion(monitor.getDescripcion());
                detalleMonitor.setCantidad(2);
                detalleMonitor.setPrecioBase(monitor.getPrecioBase());
                detalleMonitor.setImporteCotizado(monitor.getPrecioBase() * 2);
                detalleMonitor.setComponenteId(monitor.getId()); // Usar getId() directamente del DTO
                detalleMonitor.setEsCompuesto(false);
                detalleMonitor.setComponente(monitor); // Opcional: establecer el componente completo
                detalles.add(detalleMonitor);
            }

            // Detalle 2: Una PC completa
            PCDTO pc = pcDAO.obtenerPC("pc001");
            if (pc != null) {
                DetalleCotizacionDTO detallePC = new DetalleCotizacionDTO();
                detallePC.setNum(2);
                detallePC.setDescripcion(pc.getDescripcion() + " (Configuración Gamer)");
                detallePC.setCantidad(1);
                detallePC.setPrecioBase(pc.getPrecioBase());
                detallePC.setImporteCotizado(pc.getPrecioBase());
                detallePC.setComponenteId(pc.getId()); // Usar getId() directamente del DTO
                detallePC.setEsCompuesto(true);
                detallePC.setComponente(pc); // Opcional: establecer el componente completo
                detalles.add(detallePC);
            }

            // Detalle 3: Un disco duro adicional
            DiscoDuroDTO disco = discoDuroDAO.buscarPorId("dd001");
            if (disco != null) {
                DetalleCotizacionDTO detalleDisco = new DetalleCotizacionDTO();
                detalleDisco.setNum(3);
                detalleDisco.setDescripcion(disco.getDescripcion() + " (Adicional)");
                detalleDisco.setCantidad(1);
                detalleDisco.setPrecioBase(disco.getPrecioBase());
                detalleDisco.setImporteCotizado(disco.getPrecioBase());
                detalleDisco.setComponenteId(disco.getId()); // Usar getId() directamente del DTO
                detalleDisco.setEsCompuesto(false);
                detalleDisco.setComponente(disco); // Opcional: establecer el componente completo
                detalles.add(detalleDisco);
            }

            // 5. Asignar los detalles a la cotización y calcular total
            cotizacion.setDetalles(detalles);
            double total = detalles.stream()
                .mapToDouble(DetalleCotizacionDTO::getImporteCotizado)
                .sum();
            cotizacion.setTotal(total);

            // 6. Guardar la cotización en la base de datos
            int idCotizacion = cotizacionDAO.insertar(cotizacion);
            System.out.println("Cotización creada con ID: " + idCotizacion);

            // 7. Mostrar la cotización recién creada
            CotizacionDTO cotizacionCreada = cotizacionDAO.obtenerCotizacion(idCotizacion);
            if (cotizacionCreada != null) {
                imprimirCotizacion(cotizacionCreada);
            }

        } catch (SQLException e) {
            System.err.println("Error en la base de datos:");
            e.printStackTrace();
        }
    }

    private static void crearComponentesDePrueba(ComponenteDAO componenteDAO, DiscoDuroDAO discoDuroDAO,
                                               MonitorDAO monitorDAO, TarjetaVideoDAO tarjetaVideoDAO,
                                               PCDAO pcDAO) throws SQLException {
    	 // Verificar si ya existen los componentes para no duplicarlos
        if (componenteDAO.obtenerPorId("dd001") == null) {
            // Crear disco duro
            DiscoDuroDTO disco = new DiscoDuroDTO(
                "dd001", "SSD 1TB", "Samsung", "870 EVO", 80.00, 129.99, "1TB SSD"
            );
            discoDuroDAO.insertar(disco);
        }

        if (componenteDAO.obtenerPorId("mon001") == null) {
            // Crear monitor
            MonitorDTO monitor = new MonitorDTO(
                "mon001", "Monitor 27\" 4K", "LG", "27UL850", 300.00, 499.99
            );
            monitorDAO.crearMonitor(monitor);
        }

        if (componenteDAO.obtenerPorId("tv001") == null) {
            // Crear tarjeta de video
            TarjetaVideoDTO tarjeta = new TarjetaVideoDTO(
                "tv001", "RTX 3080", "NVIDIA", "RTX 3080 FE", 700.00, 999.99, "10GB GDDR6X"
            );
            tarjetaVideoDAO.crearTarjetaVideo(tarjeta);
        }

        if (componenteDAO.obtenerPorId("pc001") == null) {
            // Crear PC
            PCDTO pc = new PCDTO();
            pc.setId("pc001");
            pc.setDescripcion("PC Gamer Premium");
            pc.setMarca("Custom");
            pc.setModelo("2023");
            
            // Agregar componentes a la PC
            DiscoDuroDTO disco = (DiscoDuroDTO) componenteDAO.obtenerPorId("dd001");
            MonitorDTO monitor = (MonitorDTO) componenteDAO.obtenerPorId("mon001");
            TarjetaVideoDTO tarjeta = (TarjetaVideoDTO) componenteDAO.obtenerPorId("tv001");
            
            if (disco != null) pc.agregarDiscoDuro(disco, 1);
            if (monitor != null) pc.agregarMonitor(monitor, 1);
            if (tarjeta != null) pc.agregarTarjetaVideo(tarjeta, 1);
            
            // Calcular precios
            pc.setCosto(pc.getDiscosDuros().stream().mapToDouble(d -> d.getCosto() * d.getCantidad()).sum() +
                       pc.getMonitores().stream().mapToDouble(m -> m.getCosto() * m.getCantidad()).sum() +
                       pc.getTarjetasVideo().stream().mapToDouble(t -> t.getCosto() * t.getCantidad()).sum());
            
            pc.setPrecioBase(pc.getDiscosDuros().stream().mapToDouble(d -> d.getPrecioBase() * d.getCantidad()).sum() +
                           pc.getMonitores().stream().mapToDouble(m -> m.getPrecioBase() * m.getCantidad()).sum() +
                           pc.getTarjetasVideo().stream().mapToDouble(t -> t.getPrecioBase() * t.getCantidad()).sum());
            
            // Guardar PC
            pcDAO.guardarPC(pc);
        }
    }

    private static void imprimirCotizacion(CotizacionDTO cotizacion) {
        System.out.println("\n====================================");
        System.out.println("       DETALLE DE COTIZACIÓN");
        System.out.println("====================================");
        System.out.println("ID: " + cotizacion.getId());
        System.out.println("Fecha: " + cotizacion.getFecha());
        System.out.println("Total: $" + String.format("%,.2f", cotizacion.getTotal()));
        System.out.println("\nITEMS:");
        
        for (DetalleCotizacionDTO detalle : cotizacion.getDetalles()) {
            System.out.println("\n# " + detalle.getNum());
            System.out.println("Descripción: " + detalle.getDescripcion());
            System.out.println("Cantidad: " + detalle.getCantidad());
            System.out.println("Precio unitario: $" + String.format("%,.2f", detalle.getPrecioBase()));
            System.out.println("Importe: $" + String.format("%,.2f", detalle.getImporteCotizado()));
            
            // Corrección: Verificar si el componente no es null antes de acceder a él
            if (detalle.getComponente() != null) {
                System.out.println("Tipo: " + detalle.getComponente().getClass().getSimpleName());
                System.out.println("ID Componente: " + detalle.getComponente().getId());
            } else {
                System.out.println("Tipo: Componente no cargado");
                System.out.println("ID Componente: " + detalle.getComponenteId());
            }
        }
        
        System.out.println("\n====================================");
    }
}
/*
public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cotizador2025";
        String user = "root";
        String password = "myOdito77";

        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            // 1. Crear instancias de los DAOs necesarios
            ComponenteDAO componenteDAO = new ComponenteDAO(conexion);
            DiscoDuroDAO discoDuroDAO = new DiscoDuroDAO(conexion);
            MonitorDAO monitorDAO = new MonitorDAO(conexion);
            TarjetaVideoDAO tarjetaVideoDAO = new TarjetaVideoDAO(conexion);
            PCDAO pcDAO = new PCDAO(conexion);
            CotizacionDAO cotizacionDAO = new CotizacionDAO(conexion);

            // 2. Crear algunos componentes de prueba si no existen
            crearComponentesDePrueba(componenteDAO, discoDuroDAO, monitorDAO, tarjetaVideoDAO, pcDAO);

            // 3. Crear una nueva cotización
            CotizacionDTO cotizacion = new CotizacionDTO();          
            cotizacion.setFecha(Date.valueOf(LocalDate.now()));
            cotizacion.setTotal(0); // Se calculará automáticamente

            // 4. Agregar detalles a la cotización (componentes individuales y PCs)
            List<DetalleCotizacionDTO> detalles = new ArrayList<>();

            // Detalle 1: Un monitor individual
            MonitorDTO monitor = monitorDAO.obtenerPorId("mon001");
            if (monitor != null) {
                DetalleCotizacionDTO detalleMonitor = new DetalleCotizacionDTO();
                detalleMonitor.setNum(1);
                detalleMonitor.setDescripcion(monitor.getDescripcion());
                detalleMonitor.setCantidad(2);
                detalleMonitor.setPrecioBase(monitor.getPrecioBase());
                detalleMonitor.setImporteCotizado(monitor.getPrecioBase() * 2);
                detalleMonitor.setComponenteId(monitor.getId());
                detalleMonitor.setEsCompuesto(false);
                detalles.add(detalleMonitor);
            }

            // Detalle 2: Una PC completa
            PCDTO pc = pcDAO.obtenerPC("pc001");
            if (pc != null) {
                DetalleCotizacionDTO detallePC = new DetalleCotizacionDTO();
                detallePC.setNum(2);
                detallePC.setDescripcion(pc.getDescripcion() + " (Configuración Gamer)");
                detallePC.setCantidad(1);
                detallePC.setPrecioBase(pc.getPrecioBase());
                detallePC.setImporteCotizado(pc.getPrecioBase());
                detallePC.setComponenteId(pc.getId());
                detallePC.setEsCompuesto(true);
                detalles.add(detallePC);
            }

            // Detalle 3: Un disco duro adicional
            DiscoDuroDTO disco = discoDuroDAO.buscarPorId("dd001");
            if (disco != null) {
                DetalleCotizacionDTO detalleDisco = new DetalleCotizacionDTO();
                detalleDisco.setNum(3);
                detalleDisco.setDescripcion(disco.getDescripcion() + " (Adicional)");
                detalleDisco.setCantidad(1);
                detalleDisco.setPrecioBase(disco.getPrecioBase());
                detalleDisco.setImporteCotizado(disco.getPrecioBase());
                detalleDisco.setComponenteId(disco.getId());
                detalleDisco.setEsCompuesto(false);
                detalles.add(detalleDisco);
            }

            // 5. Asignar los detalles a la cotización y calcular total
            cotizacion.setDetalles(detalles);
            double total = detalles.stream()
                .mapToDouble(DetalleCotizacionDTO::getImporteCotizado)
                .sum();
            cotizacion.setTotal(total);

            // 6. Guardar la cotización en la base de datos
            int idCotizacion = cotizacionDAO.insertar(cotizacion);
            System.out.println("Cotización creada con ID: " + idCotizacion);

            // 7. Mostrar la cotización recién creada
            CotizacionDTO cotizacionCreada = cotizacionDAO.obtenerCotizacion(idCotizacion);
            if (cotizacionCreada != null) {
                imprimirCotizacion(cotizacionCreada);
            }

        } catch (SQLException e) {
            System.err.println("Error en la base de datos:");
            e.printStackTrace();
        }
    }

    private static void crearComponentesDePrueba(ComponenteDAO componenteDAO, DiscoDuroDAO discoDuroDAO,
                                               MonitorDAO monitorDAO, TarjetaVideoDAO tarjetaVideoDAO,
                                               PCDAO pcDAO) throws SQLException {
        // Verificar si ya existen los componentes para no duplicarlos
        if (componenteDAO.obtenerPorId("dd001") == null) {
            // Crear disco duro
            DiscoDuroDTO disco = new DiscoDuroDTO(
                "dd001", "SSD 1TB", "Samsung", "870 EVO", 80.00, 129.99, "1TB SSD"
            );
            discoDuroDAO.insertar(disco);
        }

        if (componenteDAO.obtenerPorId("mon001") == null) {
            // Crear monitor
            MonitorDTO monitor = new MonitorDTO(
                "mon001", "Monitor 27\" 4K", "LG", "27UL850", 300.00, 499.99
            );
            monitorDAO.crearMonitor(monitor);
        }

        if (componenteDAO.obtenerPorId("tv001") == null) {
            // Crear tarjeta de video
            TarjetaVideoDTO tarjeta = new TarjetaVideoDTO(
                "tv001", "RTX 3080", "NVIDIA", "RTX 3080 FE", 700.00, 999.99, "10GB GDDR6X"
            );
            tarjetaVideoDAO.crearTarjetaVideo(tarjeta);
        }

        if (componenteDAO.obtenerPorId("pc001") == null) {
            // Crear PC
            PCDTO pc = new PCDTO();
            pc.setId("pc001");
            pc.setDescripcion("PC Gamer Premium");
            pc.setMarca("Custom");
            pc.setModelo("2023");
            
            // Agregar componentes a la PC
            DiscoDuroDTO disco = (DiscoDuroDTO) componenteDAO.obtenerPorId("dd001");
            MonitorDTO monitor = (MonitorDTO) componenteDAO.obtenerPorId("mon001");
            TarjetaVideoDTO tarjeta = (TarjetaVideoDTO) componenteDAO.obtenerPorId("tv001");
            
            if (disco != null) pc.agregarDiscoDuro(disco, 1);
            if (monitor != null) pc.agregarMonitor(monitor, 1);
            if (tarjeta != null) pc.agregarTarjetaVideo(tarjeta, 1);
            
            // Calcular precios
            pc.setCosto(pc.getDiscosDuros().stream().mapToDouble(d -> d.getCosto() * d.getCantidad()).sum() +
                       pc.getMonitores().stream().mapToDouble(m -> m.getCosto() * m.getCantidad()).sum() +
                       pc.getTarjetasVideo().stream().mapToDouble(t -> t.getCosto() * t.getCantidad()).sum());
            
            pc.setPrecioBase(pc.getDiscosDuros().stream().mapToDouble(d -> d.getPrecioBase() * d.getCantidad()).sum() +
                           pc.getMonitores().stream().mapToDouble(m -> m.getPrecioBase() * m.getCantidad()).sum() +
                           pc.getTarjetasVideo().stream().mapToDouble(t -> t.getPrecioBase() * t.getCantidad()).sum());
            
            // Guardar PC
            pcDAO.guardarPC(pc);
        }
    }

    private static void imprimirCotizacion(CotizacionDTO cotizacion) {
        System.out.println("\n====================================");
        System.out.println("       DETALLE DE COTIZACIÓN");
        System.out.println("====================================");
        System.out.println("ID: " + cotizacion.getId());
        System.out.println("Fecha: " + cotizacion.getFecha());
        System.out.println("Total: $" + String.format("%,.2f", cotizacion.getTotal()));
        System.out.println("\nITEMS:");
        
        for (DetalleCotizacionDTO detalle : cotizacion.getDetalles()) {
            System.out.println("\n# " + detalle.getNum());
            System.out.println("Descripción: " + detalle.getDescripcion());
            System.out.println("Cantidad: " + detalle.getCantidad());
            System.out.println("Precio unitario: $" + String.format("%,.2f", detalle.getPrecioBase()));
            System.out.println("Importe: $" + String.format("%,.2f", detalle.getImporteCotizado()));
            System.out.println("Tipo: " + (detalle.isEsCompuesto() ? "PC (Compuesto)" : "Componente individual"));
        }
        
        System.out.println("\n====================================");
    }
    
}
*/