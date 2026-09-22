package application.rdv.controllers;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import application.patients.models.Patient;
import application.rdv.models.Rdv;
import application.utils.FilterableComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AddRdvController {
	 @FXML
	    private ComboBox<String> patientCombo;
	 @FXML
	 private DatePicker dateField ;
	 @FXML
	 private TextField heureField ;
	 
	  

	public void initialize() {
		
		// Populate the ComboBox with patients from the database
        List<Patient> patients = getPatientsFromDB(); // Replace with your own method to fetch patients
        ObservableList<String> patientNames = FXCollections.observableArrayList();

         
        for (Patient patient : patients) {
            patientNames.add(patient.getNom() + " " + patient.getPrenom());
         

        }

        //patientCombo.getComboBox().setItems(patientNames);
        patientCombo.setItems(patientNames);
         
        // Make the ComboBox filterable
        FilterableComboBox.makeFilterable(patientCombo);
        
   

		
	}
	public void handleValiderBtn() {
	    // Get the selected patient from the ComboBox
	    String selectedPatient = patientCombo.getSelectionModel().getSelectedItem();
	    
	    // Get the selected date from the DatePicker
	    LocalDate selectedDate = dateField.getValue();
	    
	    // Get the entered time from the TextField
	    String enteredTime = heureField.getText();
	    
	    if(selectedPatient.isBlank()|| !selectedPatient.matches("[a-zA-Z]+\\s[a-zA-Z]+")) {
	    	 Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Invalid Patient");
		        alert.setHeaderText(null);
		        alert.setContentText("enter a valid patient");
		        alert.showAndWait();
		        return;
	    }
	    
	   
	    
	    try {
			   LocalDate dateNaissance = LocalDate.parse(dateField.getValue().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			   // The date is in a valid format
			   LocalDate today = LocalDate.now();
			   System.out.println(today+" "+dateNaissance) ;
			   if (!dateNaissance.isAfter(today) && !dateNaissance.equals(today)) {
				   Alert alert = new Alert(Alert.AlertType.ERROR);
				    alert.setTitle("Error");
				    alert.setHeaderText("Invalid Date ");
				    alert.setContentText("Date cannot be in past");
				    alert.showAndWait();
				    return ;
			   }
			 } catch (NullPointerException e) {
			   // The date is not in a valid format
				   Alert alert = new Alert(Alert.AlertType.ERROR);
				    alert.setTitle("Error");
				    alert.setHeaderText("Invalid Date Format");
				    alert.setContentText("Please enter a valid date in the format dd-MM-yyyy");
				    alert.showAndWait();
			 }
	    
	    // Validate the time format
	    boolean isTimeValid = enteredTime.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");
	    if (!isTimeValid) {
	        // Show an error message and return if the time is not valid
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Invalid Time Format");
	        alert.setHeaderText(null);
	        alert.setContentText("The entered time is not in the correct format. Please enter a valid time in the format HH:mm.");
	        alert.showAndWait();
	        return;
	    }
	    
	    // Combine the date and time into a single LocalDateTime object
	    LocalDateTime dateTime = selectedDate.atTime(LocalTime.parse(enteredTime));
	    
	    // Store the date and time in the database
	    storeRdvInDB(selectedPatient,dateTime);
	}

	
	
	private void storeRdvInDB(String selectedPatient, LocalDateTime dateTime) {
	    // Get the ID of the selected patient from the database
	    int patientId = getPatientIdFromDB(selectedPatient);
	    if(patientId==-1) {
	    	 Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Invalid Patient");
		        alert.setHeaderText(null);
		        alert.setContentText("The entered patient doesn't exist");
		        alert.showAndWait();
		        return;
	    }
	    
	    /*Rdv rdv=new Rdv(patientId,dateTime) ;
	    RdvData rdvData = RdvData.getInstance();
	    rdvData.addAppointment(rdv);
        */
	    
	    // Create a connection to the database
	    try (Connection conn = DriverManager.getConnection("jdbc:derby:db")) {
	        // Create a statement for executing SQL commands
	        Statement stmt = conn.createStatement();
	        
	        // Check if the date/time is already taken
	        String sql = "SELECT * FROM appointments WHERE Date = ?";
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        java.sql.Date date = java.sql.Date.valueOf(dateTime.toLocalDate());
	        java.sql.Time time = java.sql.Time.valueOf(dateTime.toLocalTime());
	        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime() + time.getTime());
	        pstmt.setTimestamp(1, timestamp);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // The date/time is already taken, so print an error message and return
	        	   Alert alert = new Alert(Alert.AlertType.ERROR);
				    alert.setTitle("Error");
				    alert.setHeaderText("Invalid Date/Time ");
				    alert.setContentText("This date/time is already taken ");
				    alert.showAndWait();
	            return;
	        }
	        
	        // Insert the new appointment into the appointments table
	        sql = "INSERT INTO appointments (Date, patient_id) VALUES (?, ?)";
	        pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        pstmt.setTimestamp(1, timestamp);
	        pstmt.setInt(2, patientId);
	        pstmt.executeUpdate();
	        
	        // Get the ID of the newly inserted appointment
	        ResultSet generatedKeys = pstmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            int appointmentId = generatedKeys.getInt(1);
	            Rdv rdv=new Rdv(appointmentId,patientId,dateTime) ;
	    	    RdvData rdvData = RdvData.getInstance();
	    	    rdvData.addAppointment(rdv);
	    	    Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    	    alert.setTitle("Success");
	    	    alert.setHeaderText(null);
	    	    alert.setContentText("The operation was successful.");
	    	    alert.showAndWait();
	            
	        }
	        
	        
	        // Print a message indicating that the appointment was stored successfully
	        System.out.println("Appointment stored successfully.");
	    } catch (SQLException e) {
	        // Print the stack trace of the exception
	        e.printStackTrace();
	    }
	}

	
	public List<Patient> getPatientsFromDB() {
        List<Patient> patients = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM patients");
            while (rs.next()) {
          
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                  
                Patient patient = new Patient(nom,prenom);
                patients.add(patient);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
	
	
	private int getPatientIdFromDB(String patientInfo) {
        try {
            // Split the patientInfo string into nom and prenom
            String[] parts = patientInfo.split(" ");
            String nom = parts[0];
            String prenom = parts[1];
            
            // Create a connection to the database and prepare the query
            Connection conn = DriverManager.getConnection("jdbc:derby:db");
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM patients WHERE nom = ? AND prenom = ?");
            stmt.setString(1, nom);
            stmt.setString(2, prenom);

            // Execute the query and get the result
            ResultSet rs = stmt.executeQuery();
            int id = -1; // Default value in case no matching patient is found
            if (rs.next()) {
                id = rs.getInt("id");
            }

            // Close the resources
            rs.close();
            stmt.close();
            conn.close();

            return id;
        } catch (SQLException e) {
            System.err.println("Error fetching patient from database: " + e.getMessage());
            return -1;
        }
    }

	
}
