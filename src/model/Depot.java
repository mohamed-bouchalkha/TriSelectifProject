package model;
import java.time.*;
import java.util.*;

public class Depot {
	private UUID idDepot;
	private int poidsDepot;
	private Couleur colDepot;
	private Type typeDepot;
	private ResCat correct;
	private Adresse adresseDepot;
	private int pointsGagnes;
	private Menage utilDepot;
	private LocalDate dateDepot;
	private LocalTime horaireDepot;
	
	public UUID getIdDepot() {
		return this.idDepot;
	}
	
	public int getPoidsDepot() {
		return this.poidsDepot;
	}
	
	public Couleur getCouleurDepot() {
		return this.colDepot;
	}
	
	public Type getTypeDepot() {
		return this.typeDepot;
	}
	
	public ResCat getCorrect() {
		return this.correct;
	}
	
	public Adresse getAdresseDepot() {
		return this.adresseDepot;
	}
	
	public int getPtsGagnes() {
		return this.pointsGagnes;
	}
	
	public Menage getUtilDepot() {
		return this.utilDepot;
	}
	
	public LocalDate getDate() {
		return this.dateDepot;
	}
	
	public LocalTime getHoraire() {
		return this.horaireDepot;
	}
	
	public void modifPoints() {
		this.utilDepot.ajouterDepot(this);
	}
	
	public String toString() {
		return "Dépôt{\n\tId dépôt : " + this.idDepot + "\n\tPoids dépôt : " + this.poidsDepot + "\n\tCouleur dépôt : "
			+ this.colDepot + "\n\tType dépôt : " + this.typeDepot + "\n\tCorrect : " + this.correct
			+ "\n\tAdresse dépôt : " + this.adresseDepot + "\n\tPoints gagnés : " + this.pointsGagnes
			+ "\n\tUtilisateur dépôt: " + this.utilDepot.getNom() + "\n\tDate dépôt : " + this.dateDepot
			+ "\n\tHoraire dépôt : " + this.horaireDepot + "\n}\n"
		;
	}
	
	public Depot(UUID nId, int nPoids, Couleur nCol, Type nType, Adresse nAdresse, ResCat nCorrect, int nPts,
	Menage nUtil, LocalDate nDate, LocalTime nHoraire) {
		if (nPoids > 0 && nCol != Couleur.toutCol && nType != Type.toutType && nCorrect != ResCat.total
		&& nAdresse != null) {
			this.idDepot = nId;
			this.poidsDepot = nPoids;
			this.colDepot = nCol;
			this.typeDepot = nType;
			this.adresseDepot = nAdresse;
			this.correct = nCorrect;
			this.pointsGagnes = nPts;
			this.utilDepot = nUtil;
			this.utilDepot.addPoints(this.pointsGagnes);
			this.dateDepot = nDate;
			this.horaireDepot = nHoraire;
		}
	}
}