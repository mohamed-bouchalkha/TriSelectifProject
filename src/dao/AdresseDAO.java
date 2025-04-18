package dao;

import model.Adresse;
import java.sql.*;

import static main.Main.conn;

public class AdresseDAO {
    private Connection conn;

    public AdresseDAO(Connection conn) {
        this.conn = conn;
    }

    public int create(Adresse a) {
        String sql = "INSERT INTO Adresse (numero, nomRue, codePostal, ville) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getNum());
            stmt.setString(2, a.getNomRue());
            stmt.setInt(3, a.getCodeP());
            stmt.setString(4, a.getVille());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                a.setId(id); // Mettre à jour l'ID dans l'objet Adresse
                System.out.println("Adresse ajoutée avec ID: " + id);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Adresse find(int adresseId) {
        String sql = "SELECT * FROM Adresse WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adresseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Adresse(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getString("nomRue"),
                        rs.getInt("codePostal"),
                        rs.getString("ville")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void update(Adresse a, int adresseId) {
        String sql = "UPDATE Adresse SET numero = ?, nomRue = ?, codePostal = ?, ville = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getNum());
            stmt.setString(2, a.getNomRue());
            stmt.setInt(3, a.getCodeP());
            stmt.setString(4, a.getVille());
            stmt.setInt(5, adresseId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Adresse mise à jour.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int findByAdresse(Adresse a) {
        String sql = "SELECT id FROM Adresse WHERE numero = ? AND nomRue = ? AND codePostal = ? AND ville = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getNum());
            stmt.setString(2, a.getNomRue());
            stmt.setInt(3, a.getCodeP());
            stmt.setString(4, a.getVille());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'adresse: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void delete(int adresseId) {
        String sql = "DELETE FROM Adresse WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adresseId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Adresse supprimée avec succès.");
            } else {
                System.out.println("Aucune adresse trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}