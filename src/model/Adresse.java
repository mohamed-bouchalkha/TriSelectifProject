package model;

public class Adresse {
	private int numero;
	private String nomRue;
	private int codePostal;
	private String ville;
	
	public int getNum() {
		return this.numero;
	}
	
	public String getNomRue() {
		return this.nomRue;
	}
	
	public int getCodeP() {
		return this.codePostal;
	}
	
	public String getVille() {
		return this.ville;
	}
	
	public void setNum(int nNum) {
		if (nNum > -1) {
			this.numero = nNum;
		}
	}
	
	public void setNomRue(String nRue) {
		if (nRue != "") {
			this.nomRue = nRue;
		}
	}
	
	public void setCodeP(int nCodeP) {
		if (nCodeP > 1000 && nCodeP < 100000) {
			this.codePostal = nCodeP;
		}
	}
	
	public void setVille(String nVille) {
		if (nVille != "") {
			this.ville = nVille;
		}
	}
	
	public boolean equals(Adresse a) {
		if (this == a) {
			return true;
		}
		if (a == null || this.getClass() != a.getClass()) {
			return false;
		}
		return this.codePostal == a.codePostal && this.nomRue.equals(a.nomRue) && this.numero == a.numero
			&& this.ville.equals(a.ville)
		;
	}
	
	public boolean rueEquals(Adresse a) {
		if (this.equals(a)) {
			return true;
		}
		if (a == null || this.getClass() != a.getClass()) {
			return false;
		}
		return this.codePostal == a.codePostal && this.nomRue.equals(a.nomRue) && this.ville.equals(a.ville);
	}

	public String toString() {
		return "Adresse {\n\t\tNumero : " + this.numero + "\n\t\tNom rue : " + this.nomRue + "\n\t\tCode postal : "
			+ this.codePostal + "\n\t\tVille : " + this.ville + "\n\t}\n"
		;
	}
	
	public Adresse(int nNum, Adresse nAdresse) {
		if (nNum > -2 && nAdresse != null) {
			this.numero = nNum;
			this.nomRue = nAdresse.getNomRue();
			this.codePostal = nAdresse.getCodeP();
			this.ville = nAdresse.getVille();
		}
	}
	
	public Adresse(int nNum, String nRue, int nCodeP, String nVille) {
		if (nNum > 0 && nRue != "" && nCodeP > 1000 && nCodeP < 100000 && nVille != "") {
			this.numero = nNum;
			this.nomRue = nRue;
			this.codePostal = nCodeP;
			this.ville = nVille;
		}
	}
}
