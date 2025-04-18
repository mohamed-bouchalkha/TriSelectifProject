package model;

import java.util.UUID;

public class Bac {
	private UUID idBac;
	private CentreTri centre;
	private Couleur couleurBac;
	private int capacite;
	private int contenu;
	private Adresse adresseBac;

	public Bac(UUID idBac, CentreTri centre, Couleur couleur, int capacite) {
		this.idBac = idBac;
		this.centre = centre;
		this.couleurBac = couleur;
		this.capacite = capacite;
		this.contenu = 0;
	}

	public UUID getIdBac() {
		return idBac;
	}

	public CentreTri getCentre() {
		return centre;
	}

	public Couleur getCouleurBac() {
		return couleurBac;
	}

	public int getCapacite() {
		return capacite;
	}

	public int getContenu() {
		return contenu;
	}

	public Adresse getAdresseBac() {
		return adresseBac;
	}

	public void setCapacite(int capacite) {
		if (capacite > 0) {
			this.capacite = capacite;
		}
	}

	public void setContenu(int contenu) {
		if (contenu >= 0 && contenu <= capacite) {
			this.contenu = contenu;
		}
	}

	public void setAdresseBac(Adresse adresse) {
		this.adresseBac = adresse;
	}

	public void vider() {
		this.contenu = 0;
	}

	@Override
	public String toString() {
		return "Bac {" +
				"idBac=" + idBac +
				", couleur=" + couleurBac +
				", capacite=" + capacite +
				", contenu=" + contenu +
				", adresse=" + adresseBac +
				'}';
	}
}