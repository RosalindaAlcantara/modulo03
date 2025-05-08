package persistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PCDAO {
    private final Connection conexion;
    private final ComponenteDAO componenteDAO;

    public PCDAO(Connection conexion) {
        this.conexion = conexion;
        this.componenteDAO = new ComponenteDAO(conexion);
    }

    private void cargarTarjetasVideo(PCDTO pc) throws SQLException {
        String sql = "SELECT tv.*, c.*, pt.cantidad " +
                    "FROM pc_tarjeta_video pt " +
                    "JOIN tarjeta_video tv ON pt.tarjeta_video_id = tv.id " +
                    "JOIN componente c ON tv.id = c.id " +
                    "WHERE pt.pc_id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, pc.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TarjetaVideoDTO tarjeta = new TarjetaVideoDTO(
                    rs.getString("id"),
                    rs.getString("descripcion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDouble("costo"),
                    rs.getDouble("precio_base"),
                    rs.getString("memoria"),
                    rs.getInt("cantidad")
                );
                pc.agregarTarjetaVideo(tarjeta, tarjeta.getCantidad());
            }
        }
    }
    
 // En PCDAO.java
    private void cargarMonitores(PCDTO pc) throws SQLException {
        String sql = "SELECT m.id, c.*, pm.cantidad " +
                    "FROM pc_monitor pm " +
                    "JOIN monitor m ON pm.monitor_id = m.id " +
                    "JOIN componente c ON m.id = c.id " +
                    "WHERE pm.pc_id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, pc.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MonitorDTO monitor = new MonitorDTO(
                    rs.getString("id"),
                    rs.getString("descripcion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDouble("costo"),
                    rs.getDouble("precio_base"),
                    rs.getInt("cantidad")
                );
                pc.agregarMonitor(monitor, monitor.getCantidad());
            }
        }
    }
    
    private void cargarDiscosDuros(PCDTO pc) throws SQLException {
        String sql = "SELECT dd.*, c.*, pdd.cantidad " +
                    "FROM pc_disco_duro pdd " +
                    "JOIN disco_duro dd ON pdd.disco_duro_id = dd.id " +
                    "JOIN componente c ON dd.id = c.id " +
                    "WHERE pdd.pc_id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, pc.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DiscoDuroDTO disco = new DiscoDuroDTO(
                    rs.getString("id"),
                    rs.getString("descripcion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDouble("costo"),
                    rs.getDouble("precio_base"),
                    rs.getString("capacidad"),
                    rs.getInt("cantidad")
                );
                pc.agregarDiscoDuro(disco, disco.getCantidad());
            }
        }
    }
    
    public PCDTO obtenerPC(String id) throws SQLException {
        PCDTO pc = new PCDTO();
        ComponenteDTO componente = componenteDAO.obtenerPorId(id);
        
        if (componente != null && componente.isEsPC()) {
            pc.setId(componente.getId());
            pc.setDescripcion(componente.getDescripcion());
            pc.setMarca(componente.getMarca());
            pc.setModelo(componente.getModelo());
            pc.setCosto(componente.getCosto());
            pc.setPrecioBase(componente.getPrecioBase());

            // Obtener discos duros
            String sqlDiscos = "SELECT dd.*, c.* FROM pc_disco_duro pdd " +
                              "JOIN disco_duro dd ON pdd.disco_duro_id = dd.id " +
                              "JOIN componente c ON dd.id = c.id " +
                              "WHERE pdd.pc_id = ?";
            try (PreparedStatement stmt = conexion.prepareStatement(sqlDiscos)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    DiscoDuroDTO disco = new DiscoDuroDTO(
                        rs.getString("id"),
                        rs.getString("descripcion"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDouble("costo"),
                        rs.getDouble("precio_base"),
                        rs.getString("capacidad")
                    );
                    pc.agregarDiscoDuro(disco, 1); // rs.getInt("cantidad"));
                }
            }

            // Similar para monitores y tarjetas de video...
        }
        return pc;
    }

    public void guardarPC(PCDTO pc) throws SQLException {
        // Implementar lógica para guardar la PC y sus componentes
    	try {
            conexion.setAutoCommit(false);
            
            // Insertar parte componente
            insertarComponenteBase(pc);
            
            // Insertar parte PC
            String sqlPc = "INSERT INTO pc (id) VALUES (?)";
            try (PreparedStatement stmt = conexion.prepareStatement(sqlPc)) {
                stmt.setString(1, pc.getId());
                stmt.executeUpdate();
            }
            
            // Insertar relaciones con componentes
            insertarComponentesPC(pc);
            
            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }
    
    protected void insertarComponenteBase(ComponenteDTO componente) throws SQLException {
        String sql = "INSERT INTO componente (id, descripcion, marca, modelo, costo, precio_base) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, componente.getId());
            stmt.setString(2, componente.getDescripcion());
            stmt.setString(3, componente.getMarca());
            stmt.setString(4, componente.getModelo());
            stmt.setDouble(5, componente.getCosto());
            stmt.setDouble(6, componente.getPrecioBase());
            stmt.executeUpdate();
        }
    }

    private void insertarComponentesPC(PCDTO pc) throws SQLException {
        // Insertar discos duros
        for (DiscoDuroDTO disco : pc.getDiscosDuros()) {
            String sql = "INSERT INTO pc_disco_duro (pc_id, disco_duro_id, cantidad) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, pc.getId());
                stmt.setString(2, disco.getId());
                stmt.setInt(3, 1); // Cantidad por defecto
                stmt.executeUpdate();
            }
        }
        
        // Insertar monitores y tarjetas de video de manera similar
    }
}
