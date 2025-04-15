package dao;
import model.*;
import java.sql.*;

public class CentreTriDAO {
    private Connection conn;

    public CentreTriDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(CentreTri centre, int adresseId) {
        String sql = "INSERT INTO CentreTri (nomCentre, adresse_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, centre.getNomC());
            stmt.setInt(2, adresseId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idCentre = rs.getInt(1);
                centre.setIdCentre(idCentre);
            }
            System.out.println("Centre de tri ajouté.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public CentreTri find(int idCentre) {
        String sql = "SELECT * FROM CentreTri WHERE idCentre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nomCentre = rs.getString("nomCentre");
                int adresseId = rs.getInt("adresse_id");
                Adresse adresse = new AdresseDAO(conn).find(adresseId);
                return new CentreTri(nomCentre, adresse);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void delete(int idCentre) {
        String sql = "DELETE FROM CentreTri WHERE idCentre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCentre);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Centre de tri supprimé avec succès.");
            }
            else {
                System.out.println("Aucun Centre de tri trouvé avec cet ID.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}