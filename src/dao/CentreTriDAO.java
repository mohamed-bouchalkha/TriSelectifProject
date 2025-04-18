package dao;

import model.Adresse;
import model.CentreTri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CentreTriDAO {
    private Connection conn;

    public CentreTriDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(CentreTri centre, int adresseId) {
        String sql = "INSERT INTO CentreTri (nomCentre, adresse_id) VALUES (?, ?) RETURNING idCentre";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, centre.getNomC());
            stmt.setInt(2, adresseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idCentre = rs.getInt("idCentre");
                centre.setIdCentre(idCentre);
                System.out.println("Centre de tri ajouté avec ID: " + idCentre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du centre de tri: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible d'insérer le centre de tri", e);
        }
    }

    public CentreTri find(int idCentre) {
        String sql = "SELECT ct.idCentre, ct.nomCentre, ct.adresse_id, a.numero, a.nomRue, a.codePostal, a.ville " +
                "FROM CentreTri ct JOIN Adresse a ON ct.adresse_id = a.id WHERE ct.idCentre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCentre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Adresse adresse = new Adresse(
                        rs.getInt("adresse_id"),
                        rs.getInt("numero"),
                        rs.getString("nomRue"),
                        rs.getInt("codePostal"),
                        rs.getString("ville")
                );
                CentreTri centre = new CentreTri(rs.getString("nomCentre"), adresse);
                centre.setIdCentre(rs.getInt("idCentre"));
                return centre;
            } else {
                System.out.println("Aucun centre de tri trouvé pour idCentre: " + idCentre);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du centre de tri (idCentre: " + idCentre + "): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche du centre de tri", e);
        }
    }

    public void delete(int idCentre) {
        String sql = "DELETE FROM CentreTri WHERE idCentre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCentre);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Centre de tri supprimé avec succès (ID: " + idCentre + ").");
            } else {
                System.out.println("Aucun centre de tri trouvé avec l'ID: " + idCentre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du centre de tri: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression du centre de tri", e);
        }
    }
}