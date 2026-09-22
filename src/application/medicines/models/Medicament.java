package application.medicines.models;

public class Medicament {
    private int id;
    private String nom;
    private String forme;
    private String dosage;

    public Medicament(int id, String nom, String forme, String dosage) {
        this.id = id;
        this.nom = nom;
        this.forme = forme;
        this.dosage = dosage;
    }

    public Medicament(String nom, String forme, String dosage) {
		// TODO Auto-generated constructor stub
    	  this.nom = nom;
          this.forme = forme;
          this.dosage = dosage;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
}
