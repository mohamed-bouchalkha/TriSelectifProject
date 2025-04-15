package dao;
import model.*;
import java.sql.*;
import java.util.UUID;

public class ContratPartenariatDAO {
    private Connection conn;

    public ContratPartenariatDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(ContratPartenariat cp) {
        String sql = "INSERT INTO ContratPartenariat (idCentreP, idCommerceP, estPartenaire, dateDebut, dateFin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cp.getIdCentreP());
            stmt.setString(2, cp.getIdCommerceP().toString());
            stmt.setBoolean(3, cp.getEstPartner());
            stmt.setDate(4, Date.valueOf(cp.getDateDP()));
            stmt.setDate(5, Date.valueOf(cp.getDateFP()));
            stmt.executeUpdate();
            System.out.println("Contrat de partenariat ajouté.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ContratPartenariat find(UUID idCommerce, int idCentre) {
        String sql = "SELECT * FROM ContratPartenariat WHERE idCommerceP = ? AND idCentreP = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idCommerce.toString());
            stmt.setInt(2, idCentre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date dateDebut = rs.getDate("dateDebut");
                Date dateFin = rs.getDate("dateFin");
                int centreId = rs.getInt("idCentreP");
                String nomCentre = rs.getString("nomCentre");
                Adresse adresseCentre = new Adresse(centreId, "Nom Rue", 75000, "Ville");
                CentreTri centre = new CentreTri(nomCentre, adresseCentre);
                String nomCommerce = rs.getString("nomCommerce");
                Commerce commerce = new Commerce(nomCommerce, adresseCentre);
                return new ContratPartenariat(centre, commerce, dateDebut.toLocalDate(), dateFin.toLocalDate());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void delete(UUID idCommerce, int idCentre) {
        String sql = "DELETE FROM ContratPartenariat WHERE idCommerceP = ? AND idCentreP = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idCommerce.toString());
            stmt.setInt(2, idCentre);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contrat de partenariat supprimé avec succès.");
            }
            else {
                System.out.println("Aucun Contrat de partenariat trouvé avec ces identifiants.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
