package application.patients.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import application.patients.models.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class AddPatientController {
	@FXML
	   private TextField nomTextField;
	   @FXML
	   private TextField prenomTextField;
	   @FXML
	   private DatePicker dateNaissancePicker;
	   @FXML
	   private TextField telephoneTextField;
	   @FXML
	   private RadioButton hommeRadioButton;
	   @FXML
	   private Button validerButton;
	   

	   PatientsData patientsData = PatientsData.getInstance();

	   public void initalize() {
		   

		      
	   }
	   public void handleValiderButton() {
		   
		   String nom =nomTextField.getText() ;
		   String prenom =prenomTextField.getText() ;
		   String tel =telephoneTextField.getText() ;
		   
		   if(nom.isBlank() || prenom.isBlank() ||tel.isBlank()) {
			   Alert alert = new Alert(Alert.AlertType.ERROR);
			    alert.setTitle("Error");
			    alert.setHeaderText("champ vide");
			    alert.setContentText("remplir tous les champs");
			    alert.showAndWait();
			    return ;
			   
		   }
		// V�rifier si le nom ne contient que des lettres et des espaces
		   if (!nom.matches("^[a-zA-Z]+$")) {
		       // Le nom est invalide
			   Alert alert = new Alert(Alert.AlertType.ERROR);
			    alert.setTitle("Error");
			    alert.setHeaderText("nom invalid");
			    alert.setContentText("entrer un nom valid");
			    alert.showAndWait();
			    return ;
		   }

		   // V�rifier si le pr�nom ne contient que des lettres et des espaces
		   if (!prenom.matches("^[a-zA-Z]+$")) {
		       // Le pr�nom est invalide
			   Alert alert = new Alert(Alert.AlertType.ERROR);
			    alert.setTitle("Error");
			    alert.setHeaderText("prenom invalid");
			    alert.setContentText("entrer un prenom valid");
			    alert.showAndWait();
			    return ;
		   }
		// V�rifier si le num�ro de t�l�phone est valide
		   if (!tel.matches("^(\\+213|00213|0)(5|6|7)[0-9]{8}$")&& 
  				 !tel.matches("^0\\d{8}$")) {
		       // Le num�ro de t�l�phone est invalide
			   Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Numero invalid");
		        alert.setHeaderText("ex numeros valides : 0541236589 , 041235658");
		        alert.showAndWait();;
			    return ;
		   }

		   
		   try {
			   LocalDate dateNaissance = LocalDate.parse(dateNaissancePicker.getValue().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			   // The date is in a valid format
			   LocalDate today = LocalDate.now();
			   if (dateNaissance.isAfter(today)) {
				   if (dateNaissance.isAfter(today) && !dateNaissance.equals(today)) {
					   Alert alert = new Alert(Alert.AlertType.ERROR);
					    alert.setTitle("Error");
					    alert.setHeaderText("Invalid Date ");
					    alert.setContentText("Date cannot be in the future");
					    alert.showAndWait();
					    return ;
				   }
				
			   }
			 } catch (NullPointerException e) {
			   // The date is not in a valid format
				   Alert alert = new Alert(Alert.AlertType.ERROR);
				    alert.setTitle("Error");
				    alert.setHeaderText("Invalid Date Format");
				    alert.setContentText("Please enter a valid date in the format dd-MM-yyyy");
				    alert.showAndWait();
			 }
		   
	        
		         Patient patient = new Patient(nom, prenom, dateNaissancePicker.getValue(), tel, hommeRadioButton.isSelected() ? "Homme" : "Femme");
		         savePatient(patient);
		        
		         Alert alert = new Alert(Alert.AlertType.INFORMATION);
		         alert.setTitle("Success");
		         alert.setHeaderText(null);
		         alert.setContentText("The operation was successful.");
		         alert.showAndWait();

		         
		        // patientsData.addPatient(patient) ;
		

	   }
	   
	   private void savePatient(Patient patient) {
		    final String SELECT_PATIENT_SQL = "SELECT COUNT(*) FROM patients WHERE nom = ? AND prenom = ? AND date_naissance = ?";
		    final String INSERT_PATIENT_SQL = "INSERT INTO patients (nom, prenom, date_naissance, telephone, genre) VALUES (?, ?, ?, ?, ?)";

		    try (Connection connection = DriverManager.getConnection("jdbc:derby:db")) {
		        // Check if the patient already exists in the database
		        PreparedStatement selectStatement = connection.prepareStatement(SELECT_PATIENT_SQL);
		        selectStatement.setString(1, patient.getNom());
		        selectStatement.setString(2, patient.getPrenom());
		        selectStatement.setDate(3, java.sql.Date.valueOf(patient.getDateNaissance()));
		        ResultSet resultSet = selectStatement.executeQuery();
		        resultSet.next();
		        int count = resultSet.getInt(1);

		        if (count > 0) {
		            // Patient already exists in the database, show an error dialog
		            Alert alert = new Alert(AlertType.ERROR);
		            alert.setHeaderText("Error");
		            alert.setContentText("Patient already exists in the database.");
		            alert.showAndWait();
		        } else {
		            // Patient does not exist in the database, insert the patient
		            PreparedStatement insertStatement = connection.prepareStatement(INSERT_PATIENT_SQL, Statement.RETURN_GENERATED_KEYS);
		            insertStatement.setString(1, patient.getNom());
		            insertStatement.setString(2, patient.getPrenom());
		            insertStatement.setDate(3, java.sql.Date.valueOf(patient.getDateNaissance()));
		            insertStatement.setString(4, patient.getTelephone());
		            insertStatement.setString(5, patient.getGenre());
		            int affectedRows = insertStatement.executeUpdate();
		            
		           

		            if (affectedRows > 0) {
		            	
		            	 // Get the ID of the newly inserted patient
				        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
				        if (generatedKeys.next()) {
				            int id = generatedKeys.getInt(1);
	                patient.setId(id);

		             
					    patientsData.addPatient(patient);
		             
	
				        }
		            }
		        }
		    } catch (SQLException e) {
		        System.err.println("Error inserting patient: " + e.getMessage());
		    }
		}

	
}
