package persistencia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarjetaVideoDAO {
    private final Connection conexion;

    public TarjetaVideoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Crea una nueva tarjeta de video en la base de datos
     */
    public boolean crearTarjetaVideo(TarjetaVideoDTO tarjeta) throws SQLException {
        // Primero insertamos el componente base
        ComponenteDAO componenteDAO = new ComponenteDAO(conexion);
        if (!componenteDAO.insertarComponente(tarjeta)) {
            return false;
        }

        // Luego insertamos el registro específico de tarjeta de video
        String sql = "INSERT INTO tarjeta_video (id, memoria) VALUES (?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, tarjeta.getId());
            stmt.setString(2, tarjeta.getMemoria());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Obtiene una tarjeta de video por su ID
     */
    public TarjetaVideoDTO obtenerPorId(String id) throws SQLException {
        String sql = "SELECT c.*, tv.memoria FROM tarjeta_video tv " +
                    "JOIN componente c ON tv.id = c.id " +
                    "WHERE tv.id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearTarjetaVideo(rs);
            }
            return null;
        }
    }

    /**
     * Obtiene todas las tarjetas de video
     */
    public List<TarjetaVideoDTO> obtenerTodas() throws SQLException {
        List<TarjetaVideoDTO> tarjetas = new ArrayList<>();
        String sql = "SELECT c.*, tv.memoria FROM tarjeta_video tv " +
                    "JOIN componente c ON tv.id = c.id";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tarjetas.add(mapearTarjetaVideo(rs));
            }
        }
        return tarjetas;
    }

    /**
     * Actualiza una tarjeta de video existente
     */
    public boolean actualizarTarjetaVideo(TarjetaVideoDTO tarjeta) throws SQLException {
        // Iniciamos transacción
        boolean autoCommit = conexion.getAutoCommit();
        conexion.setAutoCommit(false);
        
        try {
            // Actualizamos primero el componente base
            ComponenteDAO componenteDAO = new ComponenteDAO(conexion);
            if (!componenteDAO.actualizarComponente(tarjeta)) {
                conexion.rollback();
                return false;
            }

            // Actualizamos los campos específicos de tarjeta de video
            String sql = "UPDATE tarjeta_video SET memoria = ? WHERE id = ?";
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, tarjeta.getMemoria());
                stmt.setString(2, tarjeta.getId());
                
                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas <= 0) {
                    conexion.rollback();
                    return false;
                }
            }
            
            conexion.commit();
            return true;
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(autoCommit);
        }
    }

    /**
     * Elimina una tarjeta de video por su ID
     */
    public boolean eliminarTarjetaVideo(String id) throws SQLException {
        // La eliminación en cascada se encargará de borrar el componente asociado
        String sql = "DELETE FROM tarjeta_video WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Obtiene tarjetas de video por memoria mínima
     */
    public List<TarjetaVideoDTO> obtenerPorMemoriaMinima(String memoriaMinima) throws SQLException {
        List<TarjetaVideoDTO> tarjetas = new ArrayList<>();
        String sql = "SELECT c.*, tv.memoria FROM tarjeta_video tv " +
                    "JOIN componente c ON tv.id = c.id " +
                    "WHERE tv.memoria >= ? " +
                    "ORDER BY tv.memoria";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, memoriaMinima);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarjetas.add(mapearTarjetaVideo(rs));
            }
        }
        return tarjetas;
    }

    /**
     * Mapea un ResultSet a un objeto TarjetaVideoDTO
     */
    private TarjetaVideoDTO mapearTarjetaVideo(ResultSet rs) throws SQLException {
        return new TarjetaVideoDTO(
            rs.getString("id"),
            rs.getString("descripcion"),
            rs.getString("marca"),
            rs.getString("modelo"),
            rs.getDouble("costo"),
            rs.getDouble("precio_base"),
            rs.getString("memoria")
        );
    }
}