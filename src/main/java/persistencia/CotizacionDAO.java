package persistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDAO {
    private Connection connection;

    public CotizacionDAO(Connection conexion) {
        this.connection = conexion;
    }
    
    /*public CotizacionDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }*/

    public List<CotizacionDTO> listarTodas() throws SQLException {
        List<CotizacionDTO> cotizaciones = new ArrayList<>();
        String sql = "SELECT * FROM cotizacion";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CotizacionDTO cotizacion = new CotizacionDTO();
                cotizacion.setId(rs.getInt("id"));
                cotizacion.setFecha(rs.getDate("fecha"));
                cotizacion.setTotal(rs.getDouble("total"));
                
                // Cargar detalles
                cotizacion.setDetalles(obtenerDetallesCotizacion(cotizacion.getId()));
                cotizaciones.add(cotizacion);
            }
        }
        return cotizaciones;
    }

    private List<DetalleCotizacionDTO> obtenerDetallesCotizacion(int cotizacionId) throws SQLException {
        List<DetalleCotizacionDTO> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_cotizacion WHERE cotizacion_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cotizacionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
                detalle.setId(rs.getInt("id"));
                detalle.setNum(rs.getInt("num"));
                detalle.setDescripcion(rs.getString("descripcion"));
                detalle.setCantidad(rs.getInt("cant"));
                detalle.setImporteCotizado(rs.getDouble("importe_cotizado"));
                detalle.setPrecioBase(rs.getDouble("precio_base"));
                detalle.setEsCompuesto(rs.getBoolean("es_compuesto"));
                
                // Obtener el componente asociado (requiere implementación)
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    public int insertar(CotizacionDTO cotizacion) throws SQLException {
        try {
            connection.setAutoCommit(false);
            
            String sql = "INSERT INTO cotizacion (fecha, total) VALUES (?, ?)";
            int generatedId = -1;
            
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, new java.sql.Date(cotizacion.getFecha().getTime()));
                stmt.setDouble(2, cotizacion.getTotal());
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    cotizacion.setId(generatedId);
                }
            }
            
            // Insertar detalles
            for (DetalleCotizacionDTO detalle : cotizacion.getDetalles()) {
                insertarDetalle(detalle, generatedId);
            }
            
            connection.commit();
            return generatedId;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void insertarDetalle(DetalleCotizacionDTO detalle, int cotizacionId) throws SQLException {
        String sql = "INSERT INTO detalle_cotizacion (num, descripcion, cant, importe_cotizado, " +
                     "precio_base, cotizacion_id, componente_id, es_compuesto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getNum());
            stmt.setString(2, detalle.getDescripcion());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setDouble(4, detalle.getImporteCotizado());
            stmt.setDouble(5, detalle.getPrecioBase());
            stmt.setInt(6, cotizacionId);
            stmt.setString(7, detalle.getComponente().getId());
            stmt.setBoolean(8, detalle.isEsCompuesto());
            stmt.executeUpdate();
        }
    }

    public CotizacionDTO obtenerCotizacion(int id) throws SQLException {
        CotizacionDTO cotizacion = null;
        String sqlCotizacion = "SELECT * FROM cotizacion WHERE id = ?";
        
        try (PreparedStatement stmtCotizacion = connection.prepareStatement(sqlCotizacion)) {
            stmtCotizacion.setInt(1, id);
            ResultSet rsCotizacion = stmtCotizacion.executeQuery();
            
            if (rsCotizacion.next()) {
                cotizacion = new CotizacionDTO();
                cotizacion.setId(rsCotizacion.getInt("id"));
                cotizacion.setFecha(rsCotizacion.getDate("fecha"));
                cotizacion.setTotal(rsCotizacion.getDouble("total"));
                
                // Obtener los detalles de la cotización
                String sqlDetalles = "SELECT dc.*, " +
                                    "c.descripcion AS componente_descripcion, " +
                                    "c.marca AS componente_marca, " +
                                    "c.modelo AS componente_modelo, " +
                                    "c.precio_base AS componente_precio_base " +
                                    "FROM detalle_cotizacion dc " +
                                    "JOIN componente c ON dc.componente_id = c.id " +
                                    "WHERE dc.cotizacion_id = ? " +
                                    "ORDER BY dc.num";
                
                try (PreparedStatement stmtDetalles = connection.prepareStatement(sqlDetalles)) {
                    stmtDetalles.setInt(1, id);
                    ResultSet rsDetalles = stmtDetalles.executeQuery();
                    
                    List<DetalleCotizacionDTO> detalles = new ArrayList<>();
                    ComponenteDAO componenteDAO = new ComponenteDAO(connection);
                    
                    while (rsDetalles.next()) {
                        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
                        detalle.setId(rsDetalles.getInt("id"));
                        detalle.setNum(rsDetalles.getInt("num"));
                        detalle.setDescripcion(rsDetalles.getString("descripcion"));
                        detalle.setCantidad(rsDetalles.getInt("cant"));
                        detalle.setImporteCotizado(rsDetalles.getDouble("importe_cotizado"));
                        detalle.setPrecioBase(rsDetalles.getDouble("precio_base"));
                        detalle.setCotizacionId(rsDetalles.getInt("cotizacion_id"));
                        detalle.setComponenteId(rsDetalles.getString("componente_id"));
                        detalle.setEsCompuesto(rsDetalles.getBoolean("es_compuesto"));
                        
                        // Obtener el componente completo si es necesario
                        String componenteId = rsDetalles.getString("componente_id");
                        ComponenteDTO componente = componenteDAO.obtenerPorId(componenteId);
                        detalle.setComponente(componente);
                        
                        detalles.add(detalle);
                    }
                    
                    cotizacion.setDetalles(detalles);
                }
            }
        }
        
        return cotizacion;
    }
}