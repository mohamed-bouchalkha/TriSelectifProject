package model;
import java.time.*;
import java.util.*;

public class Bac {
    private UUID idBac;
    private Adresse adresseBac;
    private CentreTri centreTri;
    private Couleur couleur;
    private int capacite;			// en g
    private int contenu;			// poids actuel (en g)

    public UUID getIdBac() {
    	return this.idBac;
    }

    public Adresse getAdresseBac() {
    	return this.adresseBac;
    }

    public CentreTri getCentreDeTri() {
    	return this.centreTri;
    }
    
    public Couleur getCouleurBac() {
    	return this.couleur;
    }
    
    public int getCapacite() {
    	return this.capacite;
    }
    
    public int getContenu() {
    	return this.contenu;
    }
    
    public void setAdresseBac(Adresse adresse) {
    	this.adresseBac = adresse;
    }
    
    public void setContenu(int nContenu) {
    	if (nContenu >= 0 && nContenu <= this.capacite) {
    		this.contenu = nContenu;
    	}
    }
    
    public boolean estCorrect(Type t) {
    	if (t == Type.verre) {
    		if (this.couleur == Couleur.vert) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	else if (t == Type.carton || t == Type.plastique || t == Type.metal) {
    		if (this.couleur == Couleur.jaune) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	else if (t == Type.papier) {
    		if (this.couleur == Couleur.bleu) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	else {
    		if (this.couleur == Couleur.gris) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    }
    
    public boolean ajouterDechet(int aPoids, Type aType, Menage m) {
        if (this.contenu + aPoids <= this.capacite) {
            this.contenu += aPoids;
            System.out.println("Déchet ajouté au bac " + this.idBac);
	    	UUID id = UUID.randomUUID();
	        while (this.centreTri.getMapDepot().containsKey(id)) {
	            id = UUID.randomUUID();
	        }
	        Depot d;
	        if (this.estCorrect(aType)) {
	        	d = new Depot(id, aPoids, this.couleur, aType, this.adresseBac, ResCat.correct, aPoids,
	        	m, LocalDate.now(), LocalTime.now());
	        }
	        else {
	        	d = new Depot(id, aPoids, this.couleur, aType, this.adresseBac, ResCat.incorrect, -1 * aPoids,
	        	m, LocalDate.now(), LocalTime.now());
	        }
	        this.centreTri.getMapDepot().put(d.getIdDepot(), d);
	        this.centreTri.majBac(this.idBac);
            return true;
        } else {
            System.out.println("Bac " + this.idBac + " plein !");
            return false;
        }
    }
    
    public Menage identifierMenage(String nNom, String nMDP) {
    	if (Menage.getMapMenage().containsKey(nNom)) {
    		if (Menage.getMapMenage().get(nNom).getMDP().equals(nMDP)) {
    			return Menage.getMapMenage().get(nNom);
    		}
    		else {
    			System.out.println("Mot de passe incorrect");
    			return null;
    		}
    	}
    	else {
    		System.out.println("Identifiant inconnu");
    		return null;
    	}
    }
    
    public void vider() {
        this.contenu = 0;
        System.out.println("Bac " + idBac + " vidé.");
    }
    
    
	public String toString() {
		return "Bac {\n\tId Bac : " + this.idBac + "\n\tAdresse Bac : " + this.adresseBac
			+ "\n\tCentre de tri : " + this.centreTri.getIdCentre() + "\n\tCouleur : "
			+ this.couleur + "\n\tCapacite : " + this.capacite + "\n\tPoids : " + this.contenu + "\n}\n"
		;
	}

	public Bac(UUID idBac, CentreTri nCentre, Couleur nCol, int nCapa) {
    	if (nCentre != null && nCol != Couleur.toutCol && nCapa > 0) {
	        this.idBac = idBac;
	        this.adresseBac = new Adresse(-1, nCentre.getAdresseC());
	        this.centreTri = nCentre;
	        this.couleur = nCol;
	        this.capacite = nCapa;
	        this.contenu = 0;
    	}
    }
}
