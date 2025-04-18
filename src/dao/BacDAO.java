package dao;

import model.Adresse;
import model.Bac;
import model.CentreTri;
import model.Couleur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BacDAO {
    private Connection conn;

    public BacDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insère un nouveau bac dans la base de données.
     * @param bac Le bac à insérer
     * @param centreId L'ID du centre de tri associé
     * @param adresseId L'ID de l'adresse associée
     */
    public void create(Bac bac, int centreId, int adresseId) {
        String sql = "INSERT INTO Bac (idBac, centre_id, couleur, capacite, contenu, adresse_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, bac.getIdBac(), java.sql.Types.OTHER); // Passer UUID directement
            stmt.setInt(2, centreId);
            stmt.setString(3, bac.getCouleurBac().name());
            stmt.setInt(4, bac.getCapacite());
            stmt.setInt(5, bac.getContenu());
            stmt.setInt(6, adresseId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bac ajouté avec succès (ID: " + bac.getIdBac() + ").");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du bac: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recherche tous les bacs associés à un centre de tri.
     * @param centreId L'ID du centre de tri
     * @return Liste des bacs
     */
    public List<Bac> findAllByCentre(int centreId) {
        List<Bac> bacs = new ArrayList<>();
        String sql = "SELECT b.idBac, b.couleur, b.capacite, b.contenu, b.adresse_id, b.centre_id, " +
                "a.numero, a.nomRue, a.codePostal, a.ville " +
                "FROM Bac b JOIN Adresse a ON b.adresse_id = a.id WHERE b.centre_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, centreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Adresse adresse = new Adresse(
                        rs.getInt("adresse_id"),
                        rs.getInt("numero"),
                        rs.getString("nomRue"),
                        rs.getInt("codePostal"),
                        rs.getString("ville")
                );
                CentreTri centre = new CentreTriDAO(conn).find(rs.getInt("centre_id"));
                Bac bac = new Bac(
                        (UUID) rs.getObject("idBac"),
                        centre,
                        Couleur.valueOf(rs.getString("couleur")),
                        rs.getInt("capacite")
                );
                bac.setContenu(rs.getInt("contenu"));
                bac.setAdresseBac(adresse);
                bacs.add(bac);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des bacs: " + e.getMessage());
            e.printStackTrace();
        }
        return bacs;
    }

    /**
     * Met à jour un bac dans la base de données.
     * @param bac Le bac à mettre à jour
     * @param adresseId L'ID de l'adresse associée
     */
    public void update(Bac bac, int adresseId) {
        String sql = "UPDATE Bac SET couleur = ?, capacite = ?, contenu = ?, adresse_id = ? WHERE idBac = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bac.getCouleurBac().name());
            stmt.setInt(2, bac.getCapacite());
            stmt.setInt(3, bac.getContenu());
            stmt.setInt(4, adresseId);
            stmt.setObject(5, bac.getIdBac(), java.sql.Types.OTHER); // Passer UUID directement
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bac mis à jour avec succès (ID: " + bac.getIdBac() + ").");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du bac: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime un bac de la base de données.
     * @param idBac L'ID du bac à supprimer
     */
    public void delete(UUID idBac) {
        String sql = "DELETE FROM Bac WHERE idBac = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, idBac, java.sql.Types.OTHER); // Passer UUID directement
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bac supprimé avec succès (ID: " + idBac + ").");
            } else {
                System.out.println("Aucun bac trouvé avec l'ID: " + idBac);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du bac: " + e.getMessage());
            e.printStackTrace();
        }
    }
}