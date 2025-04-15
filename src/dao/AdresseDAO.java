package dao;
import model.Adresse;
import java.sql.*;

public class AdresseDAO {
    private Connection conn;

    public AdresseDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(Adresse a) {
        String sql = "INSERT INTO Adresse (numero, nomRue, codePostal, ville) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getNum());
            stmt.setString(2, a.getNomRue());
            stmt.setInt(3, a.getCodeP());
            stmt.setString(4, a.getVille());
            stmt.executeUpdate();
            System.out.println("Adresse ajoutée.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Adresse find(int adresseId) {
        String sql = "SELECT * FROM Adresse WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adresseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Adresse(
                    rs.getInt("numero"),
                    rs.getString("nomRue"),
                    rs.getInt("codePostal"),
                    rs.getString("ville")
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(int adresseId) {
        String sql = "DELETE FROM Adresse WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adresseId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Adresse supprimée avec succès.");
            }
            else {
                System.out.println("Aucune adresse trouvée avec cet ID.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


}