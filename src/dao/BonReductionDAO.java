package dao;
import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class BonReductionDAO {
    private Connection conn;

    public BonReductionDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(BonReduction b) throws SQLException {
        if (b.getIdBon() == null) {
            b.setIdBon(UUID.randomUUID());
        }
        if (b.getCommerceBon() == null) {
            throw new SQLException("Commerce est null, impossible de créer le bon de réduction");
        }
        String sql = "INSERT INTO BonReduction (idBon, valeur, bonUtilise, commerce_id, menage_id, dateExpiration) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getIdBon().toString());
            stmt.setDouble(2, b.getValeur());
            stmt.setBoolean(3, b.getBonUtilise());
            stmt.setString(4, b.getCommerceBon().getIdCommerce().toString());
            stmt.setString(5, b.getMenageBon().getNom());
            stmt.setDate(6, Date.valueOf(b.getDateExp()));
            stmt.executeUpdate();
            System.out.println("Bon de réduction ajouté.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public BonReduction find(UUID idBon) {
        String sql = "SELECT * FROM BonReduction WHERE idBon = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBon.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nomCommerce = rs.getString("nomCommerce");
                String nomMenage = rs.getString("nomCompte");
                double valeur = rs.getDouble("valeur");
                LocalDate dateExpiration = rs.getDate("dateExpiration").toLocalDate();
                Commerce commerce = new Commerce(nomCommerce, null);
                Menage menage = new Menage(nomMenage, null, null);
                return new BonReduction(valeur, commerce, menage, dateExpiration);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void delete(UUID idBon) {
        String sql = "DELETE FROM BonReduction WHERE idBon = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBon.toString());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bon de réduction supprimé avec succès.");
            }
            else {
                System.out.println("Aucun Bon de réduction trouvé avec cet ID.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}