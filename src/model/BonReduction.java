package model;
import java.time.*;
import java.util.*;

public class BonReduction {
	private UUID idBon;
	private double valeur;
	private boolean bonUtilise;
	private Commerce commerceBon;
	private Menage menageBon;
	private LocalDate dateExpiration;
	
	public UUID getIdBon() {
		return this.idBon;
	}
	
	public double getValeur() {
		return this.valeur;
	}
	
	public boolean getBonUtilise() {
		return this.bonUtilise;
	}
	
	public Commerce getCommerceBon() {
		return this.commerceBon;
	}
	
	public Menage getMenageBon() {
		return this.menageBon;
	}
	
	public LocalDate getDateExp() {
		return this.dateExpiration;
	}
	
	public void setIdBon(UUID nId) {
		if (!this.commerceBon.getMapBons().containsKey(nId)) {
			this.idBon = nId;
		}
	}
	
	public void utiliser() {
		this.bonUtilise = true;
	}

	public String toString() {
		return "BonReduction {\n\tId bon : " + this.idBon + "\n\tValeur : " + this.valeur + "\n\tBon utilisé : "
			+ this.bonUtilise + "\n\tCommerce bon : " + this.commerceBon.getIdCommerce() + "\n\tMénage bon : "
			+ this.menageBon.getNom() + "\n\tDate expiration : " + this.dateExpiration + "\n}\n"
		;
	}
	
	public BonReduction(double nValeur, Commerce nCommerce, Menage nMenage, LocalDate nDateExp) {
		if (nValeur > 0 && nCommerce != null && nMenage != null && !LocalDate.now().isAfter(nDateExp)) {
	    	UUID id = UUID.randomUUID();
	        while (nCommerce.getMapBons().containsKey(id)) {
	            id = UUID.randomUUID();
	        }
			this.idBon = id;
			this.valeur = nValeur;
			this.bonUtilise = false;
			this.commerceBon = nCommerce;
			this.menageBon = nMenage;
			this.dateExpiration = nDateExp;
		}
	}
}
