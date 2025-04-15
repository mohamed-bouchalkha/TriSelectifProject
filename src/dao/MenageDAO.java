package dao;
import main.Main;
import model.Menage;
import java.sql.*;

public class MenageDAO {
    private Connection conn;

    public MenageDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(Menage m, int adresseId) {
        String sql = "INSERT INTO Menage (nomCompte, motDePasse, pointsFidelite, adresse_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getMDP());
            stmt.setInt(3, m.getPoints());
            stmt.setInt(4, adresseId);
            stmt.executeUpdate();
            System.out.println("Ménage ajouté avec succès.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Menage find(String nomCompte) {
        String sql = "SELECT * FROM Menage WHERE nomCompte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomCompte);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String mdp = rs.getString("motDePasse");
                return new Menage(nomCompte, mdp, null);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePoints(String nomCompte, int nouveauxPoints) {
        String sql = "UPDATE Menage SET pointsFidelite = ? WHERE nomCompte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nouveauxPoints);
            stmt.setString(2, nomCompte);
            stmt.executeUpdate();
            System.out.println("Points mis à jour.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String nomCompte) {
        String sql = "DELETE FROM Menage WHERE nomCompte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomCompte);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ménage supprimé avec succès.");
            }
            else {
                System.out.println("Aucun Ménage trouvé avec ce nom de compte.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authentifier(String username, String password) {
        return true;
    }
}