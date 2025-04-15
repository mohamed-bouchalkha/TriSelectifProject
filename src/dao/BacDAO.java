package dao;
import model.*;
import java.sql.*;
import java.util.UUID;

public class BacDAO {
    private Connection conn;

    public BacDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(Bac b, int centreId, int adresseId) {
        String sql = "INSERT INTO Bac (idBac, couleur, capacite, contenu, centre_id, adresse_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getIdBac().toString());
            stmt.setString(2, b.getCouleurBac().name());
            stmt.setInt(3, b.getCapacite());
            stmt.setInt(4, b.getContenu());
            stmt.setInt(5, centreId);
            stmt.setInt(6, adresseId);
            stmt.executeUpdate();
            System.out.println("Bac inséré.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Bac find(UUID idBac) {
        String sql = "SELECT * FROM Bac WHERE idBac = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBac.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String couleur = rs.getString("couleur");
                int capacite = rs.getInt("capacite");
                int contenu = rs.getInt("contenu");
                int centreId = rs.getInt("centre_id");
                CentreTri centre = new CentreTriDAO(conn).find(centreId);
                Couleur bacCouleur = Couleur.valueOf(couleur);
                Bac bac = new Bac(idBac, centre, bacCouleur, capacite);
                bac.setContenu(contenu);
                return bac;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void delete(UUID idBac) {
        String sql = "DELETE FROM Bac WHERE idBac = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBac.toString());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bac supprimé avec succès.");
            }
            else {
                System.out.println("Aucun Bac trouvé avec cet ID.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}