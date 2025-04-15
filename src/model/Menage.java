package model;
import java.time.*;
import java.util.*;

public class Menage {
	private String nomCompte;
	private String motDePasse;
	private int pointsFidelite;
	private HashMap<UUID, Depot> historiqueDepotsM;
	private Adresse adresseMenage;
	private HashMap<UUID, BonReduction> mapBonsM;
	private static HashMap<String, Menage> mapMenage = new HashMap<String, Menage>();
	
	public String getNom() {
		return this.nomCompte;
	}
	
	public String getMDP() {
		return this.motDePasse;
	}
	
	public int getPoints() {
		return this.pointsFidelite;
	}
	
	public HashMap<UUID, Depot> getHistorique(){
		return this.historiqueDepotsM;
	}
	
	public Adresse getAdresse() {
		return this.adresseMenage;
	}
	
	public HashMap<UUID, BonReduction> getMapBons(){
		return this.mapBonsM;
	}
	
	public static HashMap<String, Menage> getMapMenage(){
		return mapMenage;
	}
	
	public void setNomCompte(String nNom) {
		if (nNom != "") {
			this.nomCompte = nNom;
		}
	}
	
	public void setMDP(String nMDP) {
		if (nMDP != "") {
			this.motDePasse = nMDP;
		}
	}
	
	public void addPoints(int points) {
		this.pointsFidelite += points;
	}
	
	public void ajouterDepot(Depot nDepot) {
		this.historiqueDepotsM.put(nDepot.getIdDepot(), nDepot);
		this.addPoints(nDepot.getPtsGagnes());
	}
	
	public void setAdresse(Adresse nAdresse) {
		this.adresseMenage = nAdresse;
	}
	
	public void echangerPts(int points, Commerce commerce, String produit) {
		if (points <= this.pointsFidelite) {
			double val = commerce.getReduction(points, produit.toLowerCase());
			if (val > 0) {
				BonReduction nBon = new BonReduction(val, commerce, this, LocalDate.now().plusMonths(1));
				this.pointsFidelite -= points;
				this.mapBonsM.put(nBon.getIdBon(), nBon);
				commerce.getMapBons().put(nBon.getIdBon(), nBon);
			}
		}
	}
	
	public boolean utiliserBon(BonReduction bon) {
		if(bon.getBonUtilise() || bon.getDateExp().isBefore(LocalDate.now())) {
			return false;
		}
		else {
			bon.utiliser();
			return true;
		}
	}
	
	public boolean equals(Menage m) {
		if (this == m) {
			return true;
		}
		if (m == null || getClass() != m.getClass()) {
			return false;
		}
		return this.motDePasse.equals(m.motDePasse) && this.nomCompte.equals(m.nomCompte);
	}

	public String toString() {
		return "Menage {\n\tNom compte : " + this.nomCompte + "\n\tMot de passe : "
			+ this.motDePasse + "\n\tPoints fidélité : " + this.pointsFidelite + "\n\tAdresse ménage : "
			+ this.adresseMenage + "\n}\n"
		;
	}
	
	public Menage(String nCompte, String nMDP, Adresse nAdresse) {
		if (nCompte != "" && nMDP != "") {
			this.nomCompte = nCompte;
			this.motDePasse = nMDP;
			this.pointsFidelite = 0;
			this.historiqueDepotsM = new HashMap<UUID, Depot>();
			this.adresseMenage = nAdresse;
			this.mapBonsM = new HashMap<UUID, BonReduction>();
		}
	}
}