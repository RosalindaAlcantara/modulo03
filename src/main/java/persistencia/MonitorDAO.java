package persistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MonitorDAO {
    private final Connection conexion;

    public MonitorDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Crea un nuevo monitor en la base de datos
     */
    public boolean crearMonitor(MonitorDTO monitor) throws SQLException {
        // Primero insertamos el componente base
        ComponenteDAO componenteDAO = new ComponenteDAO(conexion);
        if (!componenteDAO.insertarComponente(monitor)) {
            return false;
        }

        // Luego insertamos el registro específico de monitor
        String sql = "INSERT INTO monitor (id) VALUES (?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, monitor.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Obtiene un monitor por su ID
     */
    public MonitorDTO obtenerPorId(String id) throws SQLException {
        String sql = "SELECT c.* FROM monitor m " +
                    "JOIN componente c ON m.id = c.id " +
                    "WHERE m.id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearMonitor(rs);
            }
            return null;
        }
    }

    /**
     * Obtiene todos los monitores
     */
    public List<MonitorDTO> obtenerTodos() throws SQLException {
        List<MonitorDTO> monitores = new ArrayList<>();
        String sql = "SELECT c.* FROM monitor m " +
                    "JOIN componente c ON m.id = c.id";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                monitores.add(mapearMonitor(rs));
            }
        }
        return monitores;
    }

    /**
     * Actualiza un monitor existente
     */
    public boolean actualizarMonitor(MonitorDTO monitor) throws SQLException {
        // Actualizamos primero el componente base
        ComponenteDAO componenteDAO = new ComponenteDAO(conexion);
        return componenteDAO.actualizarComponente(monitor);
        
        // No hay campos específicos de monitor para actualizar
    }

    /**
     * Elimina un monitor por su ID
     */
    public boolean eliminarMonitor(String id) throws SQLException {
        // La eliminación en cascada se encargará de borrar el componente asociado
        String sql = "DELETE FROM monitor WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Mapea un ResultSet a un objeto MonitorDTO
     */
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

    /**
     * Obtiene monitores por marca
     */
    public List<MonitorDTO> obtenerPorMarca(String marca) throws SQLException {
        List<MonitorDTO> monitores = new ArrayList<>();
        String sql = "SELECT c.* FROM monitor m " +
                    "JOIN componente c ON m.id = c.id " +
                    "WHERE c.marca = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, marca);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                monitores.add(mapearMonitor(rs));
            }
        }
        return monitores;
    }
    


}