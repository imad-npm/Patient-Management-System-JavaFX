package application.patients.models;

import java.time.LocalDate;
import java.time.Period;

public class Patient {
	private int id; 
	
	private String nom;
	private String prenom;
	private LocalDate dateNaissance;
	private String telephone;
	private String genre;
	
	public Patient(String nom, String prenom, LocalDate dateNaissance, String telephone, String genre) {
		this.nom = nom;
		this.prenom = prenom;
		this.dateNaissance = dateNaissance;
		this.telephone = telephone;
		this.genre = genre;
	}
	public Patient(int id,String nom, String prenom, LocalDate dateNaissance, String telephone, String genre) {
		this.id=id ;
		this.nom = nom;
		this.prenom = prenom;
		this.dateNaissance = dateNaissance;
		this.telephone = telephone;
		this.genre = genre;
	}
	public Patient(String nom,String prenom) {
		
		this.nom = nom;
		this.prenom = prenom;
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
	
	public String getPrenom() {
		return prenom;
	}
	
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	public LocalDate getDateNaissance() {
		return dateNaissance;
	}
	
	public void setDateNaissance(LocalDate dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getGenre() {
		return genre;
	}
	
	public void setGenre(String genre) {
		this.genre = genre;
	}
	  public int getAge() {
	        LocalDate today = LocalDate.now();
	        Period period = Period.between(dateNaissance, today);
	        return period.getYears();
	    }
	
}
