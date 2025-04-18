package dao;

import model.Adresse;
import model.CentreTri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CentreTriDAO {
    private Connection conn;

    public CentreTriDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insère un nouveau centre de tri dans la base de données.
     * @param centre Le centre de tri à insérer
     * @param adresseId L'ID de l'adresse associée
     */
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
        }
    }

    /**
     * Recherche un centre de tri par son ID.
     * @param idCentre L'ID du centre de tri
     * @return Le CentreTri correspondant, ou null si non trouvé
     */
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
                centre.setIdCentre(rs.getInt("idCentre")); // Définir l'ID manuellement
                return centre;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du centre de tri: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Supprime un centre de tri de la base de données.
     * @param idCentre L'ID du centre de tri à supprimer
     */
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
        }
    }
}