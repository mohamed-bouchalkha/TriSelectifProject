package dao;
import model.Commerce;
import java.sql.*;
import java.util.UUID;

public class CommerceDAO {
    private Connection conn;

    public CommerceDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(Commerce c, int adresseId) {
        String sql = "INSERT INTO Commerce (idCommerce, nomCommerce, adresse_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getIdCommerce().toString());
            stmt.setString(2, c.getNomCommerce());
            stmt.setInt(3, adresseId);
            stmt.executeUpdate();
            System.out.println("Commerce inséré.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Commerce find(UUID idCommerce) {
        String sql = "SELECT * FROM Commerce WHERE idCommerce = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idCommerce.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Commerce(rs.getString("nomCommerce"), null);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void delete(UUID idCommerce) {
        String sql = "DELETE FROM Commerce WHERE idCommerce = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idCommerce.toString());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Commerce supprimé avec succès.");
            } else {
                System.out.println("Aucun Commerce trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}