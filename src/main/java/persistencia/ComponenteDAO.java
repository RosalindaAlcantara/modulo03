package persistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComponenteDAO {
    private final Connection conexion;

    public ComponenteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public ComponenteDTO obtenerPorId(String id) throws SQLException {
        String sql = "SELECT * FROM componente WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearComponente(rs);
            }
            return null;
        }
    }
    private ComponenteDTO mapearComponente(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String descripcion = rs.getString("descripcion");
        String marca = rs.getString("marca");
        String modelo = rs.getString("modelo");
        double costo = rs.getDouble("costo");
        double precioBase = rs.getDouble("precio_base");
        
        // Primero verificamos si es una PC
        if (esPC(id)) {
            PCDTO pc = new PCDTO();
            pc.setId(id);
            pc.setDescripcion(descripcion);
            pc.setMarca(marca);
            pc.setModelo(modelo);
            pc.setCosto(costo);
            pc.setPrecioBase(precioBase);
            return pc;
        }
        
        // Si no es PC, verificamos otros tipos
        String tipoComponente = determinarTipoComponente(id);
        
        switch (tipoComponente) {
            case "DISCO_DURO":
                return mapearDiscoDuro(id, descripcion, marca, modelo, costo, precioBase);
            case "MONITOR":
                return new MonitorDTO(id, descripcion, marca, modelo, costo, precioBase);
            case "TARJETA_VIDEO":
                return mapearTarjetaVideo(id, descripcion, marca, modelo, costo, precioBase);
            default:
                return new ComponenteDTO(id, descripcion, marca, modelo, costo, precioBase, false);
        }
    }

    private boolean esPC(String id) throws SQLException {
        String sql = "SELECT 1 FROM pc WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String determinarTipoComponente(String id) throws SQLException {
        // Verificar disco duro
        if (existeEnTabla(id, "disco_duro")) return "DISCO_DURO";
        // Verificar monitor
        if (existeEnTabla(id, "monitor")) return "MONITOR";
        // Verificar tarjeta de video
        if (existeEnTabla(id, "tarjeta_video")) return "TARJETA_VIDEO";
        return "COMPONENTE_BASE";
    }

    private boolean existeEnTabla(String id, String tabla) throws SQLException {
        String sql = "SELECT 1 FROM " + tabla + " WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private DiscoDuroDTO mapearDiscoDuro(String id, String descripcion, String marca, 
                                       String modelo, double costo, double precioBase) throws SQLException {
        String capacidad = obtenerCapacidadDiscoDuro(id);
        return new DiscoDuroDTO(id, descripcion, marca, modelo, costo, precioBase, capacidad);
    }

    private String obtenerCapacidadDiscoDuro(String id) throws SQLException {
        String sql = "SELECT capacidad FROM disco_duro WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("capacidad") : null;
            }
        }
    }

    private TarjetaVideoDTO mapearTarjetaVideo(String id, String descripcion, String marca, 
                                             String modelo, double costo, double precioBase) throws SQLException {
        String memoria = obtenerMemoriaTarjetaVideo(id);
        return new TarjetaVideoDTO(id, descripcion, marca, modelo, costo, precioBase, memoria);
    }

    private String obtenerMemoriaTarjetaVideo(String id) throws SQLException {
        String sql = "SELECT memoria FROM tarjeta_video WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("memoria") : null;
            }
        }
    }
    
    /**
     * Inserta un nuevo componente en la base de datos
     * @param componente El DTO del componente a insertar
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertarComponente(ComponenteDTO componente) throws SQLException {
        String sql = "INSERT INTO componente (id, descripcion, marca, modelo, costo, precio_base) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, componente.getId());
            stmt.setString(2, componente.getDescripcion());
            stmt.setString(3, componente.getMarca());
            stmt.setString(4, componente.getModelo());
            stmt.setDouble(5, componente.getCosto());
            stmt.setDouble(6, componente.getPrecioBase());
            
            int filasAfectadas = stmt.executeUpdate();
            
            // Si es una PC, debemos insertarla en la tabla pc
            if (filasAfectadas > 0 && componente instanceof PCDTO) {
                return insertarPC((PCDTO) componente);
            }
            
            return filasAfectadas > 0;
        }
    }

    /**
     * Inserta un registro en la tabla pc
     */
    private boolean insertarPC(PCDTO pc) throws SQLException {
        String sql = "INSERT INTO pc (id) VALUES (?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, pc.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza un componente existente en la base de datos
     * @param componente El DTO del componente con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarComponente(ComponenteDTO componente) throws SQLException {
        String sql = "UPDATE componente SET descripcion = ?, marca = ?, modelo = ?, " +
                    "costo = ?, precio_base = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, componente.getDescripcion());
            stmt.setString(2, componente.getMarca());
            stmt.setString(3, componente.getModelo());
            stmt.setDouble(4, componente.getCosto());
            stmt.setDouble(5, componente.getPrecioBase());
            stmt.setString(6, componente.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
/*
    private ComponenteDTO mapearComponente(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        
        // Primero verificamos si es una PC
        boolean esPC = verificarSiEsPC(id);
        if (esPC) {
            return new PCDTO(
                id,
                rs.getString("descripcion"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getDouble("costo"),
                rs.getDouble("precio_base")
            );
            
     
        }
        
        // Si no es PC, verificamos otros tipos
        String tipoComponente = determinarTipoComponente(id);
        
        switch (tipoComponente) {
            case "DISCO_DURO":
                return mapearDiscoDuro(rs);
            case "MONITOR":
                return mapearMonitor(rs);
            case "TARJETA_VIDEO":
                return mapearTarjetaVideo(rs);
            default:
                return mapearComponenteBase(rs);
        }
    }

    private boolean verificarSiEsPC(String id) throws SQLException {
        String sql = "SELECT 1 FROM pc WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String determinarTipoComponente(String id) throws SQLException {
        // Verificar disco duro
        String sql = "SELECT 1 FROM disco_duro WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return "DISCO_DURO";
            }
        }
        
        // Verificar monitor
        sql = "SELECT 1 FROM monitor WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return "MONITOR";
            }
        }
        
        // Verificar tarjeta de video
        sql = "SELECT 1 FROM tarjeta_video WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return "TARJETA_VIDEO";
            }
        }
        
        return "COMPONENTE_BASE";
    }

    private DiscoDuroDTO mapearDiscoDuro(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        DiscoDuroDTO disco = new DiscoDuroDTO(
            id,
            rs.getString("descripcion"),
            rs.getString("marca"),
            rs.getString("modelo"),
            rs.getDouble("costo"),
            rs.getDouble("precio_base"),
            null
        );
        
        // Obtener capacidad del disco duro
        String sql = "SELECT capacidad FROM disco_duro WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rsDisco = stmt.executeQuery()) {
                if (rsDisco.next()) {
                    disco.setCapacidad(rsDisco.getString("capacidad"));
                }
            }
        }
        
        return disco;
    }

    private MonitorDTO mapearMonitor(ResultSet rs) throws SQLException {
        return new MonitorDTO(
            rs.getString("id"),
            rs.getString("descripcion"),
            rs.getString("marca"),
            rs.getString("modelo"),
            rs.getDouble("costo"),
            rs.getDouble("precio_base")
        );
    }

    private TarjetaVideoDTO mapearTarjetaVideo(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        TarjetaVideoDTO tarjeta = new TarjetaVideoDTO(
            id,
            rs.getString("descripcion"),
            rs.getString("marca"),
            rs.getString("modelo"),
            rs.getDouble("costo"),
            rs.getDouble("precio_base"),
            null
        );
        
        // Obtener memoria de la tarjeta de video
        String sql = "SELECT memoria FROM tarjeta_video WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rsTarjeta = stmt.executeQuery()) {
                if (rsTarjeta.next()) {
                    tarjeta.setMemoria(rsTarjeta.getString("memoria"));
                }
            }
        }
        
        return tarjeta;
    }

    private ComponenteDTO mapearComponenteBase(ResultSet rs) throws SQLException {
        return new ComponenteDTO(
            rs.getString("id"),
            rs.getString("descripcion"),
            rs.getString("marca"),
            rs.getString("modelo"),
            rs.getDouble("costo"),
            rs.getDouble("precio_base"),
            false
        );
    }
*/
    // Otros métodos CRUD...
}

