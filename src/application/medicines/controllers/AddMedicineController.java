package application.medicines.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import application.medicines.models.Medicament;
import application.patients.models.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class AddMedicineController {
	
 @FXML
 private TextField nomTextField ;
 @FXML
 private TextField formeTextField ; 
 @FXML
 private TextField dosageTextField ;
 
 MedicamentsData medicamentsData =MedicamentsData.getInstance() ;

	   public void initalize() {
		   

		      
	   }
	   
	   public void handleValiderButton() {
		    String nom = nomTextField.getText();
		    String forme = formeTextField.getText();
		    String dosage = dosageTextField.getText();

		    // Check if any of the fields are empty
		    if (nom.isEmpty() || forme.isEmpty() || dosage.isEmpty()) {
		        Alert alert = new Alert(AlertType.WARNING);
		        alert.setTitle("Warning");
		        alert.setHeaderText("Empty fields");
		        alert.setContentText("Please fill in all fields.");
		        alert.showAndWait();
		        return;
		    }
		    
		   
		 String   regex = "^[a-z���������������]*";
		    if (!nom.matches(regex)) {
		        Alert alert = new Alert(AlertType.ERROR, "Invalid name. Please enter a valid name.");
		        alert.showAndWait();
		        return ;
		       
		    }
 // Define a regular expression to match the dosage format (e.g. "100mg")
		     regex = "^[a-zA-Z0-9].*";

		    // If the new value does not match the regular expression, show an error message
		    if (!dosage.matches(regex)) {
		        Alert alert = new Alert(AlertType.ERROR, "Invalid dosage format. Please use format like '100mg'.");
		        alert.showAndWait();
		        return ;
		       
		    }
		    
		    Medicament medicament = new Medicament(nom, forme, dosage);

	

		    // Insert the medicament into the database
		int id=   insertMedicament(medicament);

		 // Add the new medication to medicamentData
	    Medicament newMedicament = new Medicament(id, nom, forme, dosage);
	    medicamentsData.addMedicament(newMedicament);

		
		    // Clear the text fields
		    nomTextField.clear();
		    formeTextField.clear();
		    dosageTextField.clear();
		    Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Success");
	        alert.setHeaderText(null);
	        alert.setContentText("The operation was successful. ");
	        alert.showAndWait();

		}

	   
	   
	   
	   public int insertMedicament(Medicament medicament) {
		   
		   int newId = 0 ;
		   int lastId =0 ;
		    try {
		        // Connect to the database
		        Connection conn = DriverManager.getConnection("jdbc:derby:db");

		        // Retrieve the last ID from the database
		        Statement stmt = conn.createStatement();
		        ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM medicament");
		        if(rs.next())
		         lastId = rs.getInt(1);

		        // Increment the last ID to get the new ID
		         newId = lastId + 1;

		        // Insert the new medication into the database
		        PreparedStatement ps = conn.prepareStatement("INSERT INTO medicament (id, nom_de_marque, forme, dosage) VALUES (?, ?, ?, ?)");
		        ps.setInt(1, newId);
		        ps.setString(2, medicament.getNom());
		        ps.setString(3, medicament.getForme());
		        ps.setString(4, medicament.getDosage());
		        ps.executeUpdate();

		        // Close the database connection
		        conn.close();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return newId ;
		}

}
