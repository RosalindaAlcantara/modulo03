package persistencia;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscoDuroDAO  {
    private final Connection conexion;
    private final ComponenteDAO componenteDAO;
    
    public DiscoDuroDAO(Connection conexion) {
        this.conexion = conexion;
        this.componenteDAO = new ComponenteDAO(conexion);
    }

    
	/*public List<DiscoDuroDTO> listarTodos() throws SQLException {
        List<DiscoDuroDTO> discos = new ArrayList<>();
        String sql = "SELECT c.*, d.capacidad FROM componente c JOIN disco_duro d ON c.id = d.id";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                discos.add(new DiscoDuroDTO(
                    rs.getString("id"),
                    rs.getString("descripcion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDouble("costo"),
                    rs.getDouble("precio_base"),
                    rs.getString("capacidad")
                ));
            }
        }
        return discos;
    }*/

   

    public DiscoDuroDTO buscarPorId(String id) throws SQLException {
        String sql = "SELECT c.*, d.capacidad FROM componente c JOIN disco_duro d ON c.id = d.id WHERE c.id = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new DiscoDuroDTO(
                    rs.getString("id"),
                    rs.getString("descripcion"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getDouble("costo"),
                    rs.getDouble("precio_base"),
                    rs.getString("capacidad")
                );
            }
            return null;
        }
    }

   
    public void insertar(DiscoDuroDTO disco) throws SQLException {
        try {
            conexion.setAutoCommit(false);
            
            // Insertar parte componente
            insertarComponenteBase(disco);
            
            // Insertar parte disco duro
            String sql = "INSERT INTO disco_duro (id, capacidad) VALUES (?, ?)";
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, disco.getId());
                stmt.setString(2, disco.getCapacidad());
                stmt.executeUpdate();
            }
            
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
}

// Implementaciones similares para MonitorDAOImpl y TarjetaVideoDAOImpl