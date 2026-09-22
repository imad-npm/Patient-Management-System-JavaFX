package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Properties;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class MainController {
	
	@FXML
	 private AnchorPane mainContent ;
	@FXML
	private ImageView patientsIcon ;
	@FXML
	private ImageView consultationsIcon ;
	@FXML
	private ImageView medecinesIcon ;
	@FXML
	private ImageView appointementsIcon ;

	@FXML
	private ImageView parametresIcon ;	
	
	private Lighting lighting = new Lighting();
	private ImageView lastClicked;
	Parent root;

	
	public void initialize() throws IOException {
		Parent root=FXMLLoader.load(getClass().getResource("/application/patients/views/patients.fxml")) ;
 mainContent.getChildren().add(root) ;


 lighting.setSpecularConstant(1);
 lighting.setSpecularExponent(50);
 lighting.setSurfaceScale(5.0);
 lighting.setDiffuseConstant(1.8);
 lighting.setLight(new Light.Distant(100, 100, Color.rgb(23, 236, 118)));

 
 Platform.runLater(() -> {
     // This code will be executed after the UI has been fully initialized
     System.out.println("UI initialization finished");
     toggleLighting(patientsIcon);
 });

 
 

 patientsIcon.setOnMouseClicked(event -> {
	 toggleLighting(patientsIcon);
 
 
	try {
		Parent root1=FXMLLoader.load(getClass().getResource("/application/patients/views/patients.fxml")) ;
		mainContent.getChildren().clear() ;
mainContent.getChildren().add(root1) ;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


});
 
 consultationsIcon.setOnMouseClicked(event ->{
	toggleLighting(consultationsIcon) ;
	
	try {
		Parent root1=FXMLLoader.load(getClass().getResource("/application/consultations/views/consultations.fxml")) ;
		mainContent.getChildren().clear() ;
mainContent.getChildren().add(root1) ;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 });
 
 medecinesIcon.setOnMouseClicked(event -> {
	 
	 toggleLighting(medecinesIcon);
 
 
		try {
			Parent root1=FXMLLoader.load(getClass().getResource("/application/medicines/views/medicines.fxml")) ;
			mainContent.getChildren().clear() ;
 mainContent.getChildren().add(root1) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 
 });
 appointementsIcon.setOnMouseClicked(event ->{ toggleLighting(appointementsIcon) ;
	try {
		Parent root1=FXMLLoader.load(getClass().getResource("/application/rdv/views/rdv.fxml")) ;
		mainContent.getChildren().clear() ;
mainContent.getChildren().add(root1) ;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   
 });
 parametresIcon.setOnMouseClicked(event ->{ toggleLighting(parametresIcon) ;
	try {
		Parent root1=FXMLLoader.load(getClass().getResource("/application/parametres/parametres.fxml")) ;
		mainContent.getChildren().clear() ;
mainContent.getChildren().add(root1) ;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

});

 //deleteTable("appointments") ;
 //deleteTable("consultations") ;

 String jdbcUrl = "jdbc:derby:pms;derby.sequence.preallocator=1;create=true";
	try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
	    // Do something with the connection
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
		     Statement stmt = conn.createStatement()) {
		    // Check if the table exists
		    ResultSet rs = conn.getMetaData().getTables(null, null, "PATIENTS", null);
		    if (!rs.next()) {
		        // Table does not exist, create it
		        stmt.executeUpdate("CREATE TABLE PATIENTS (id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL, nom VARCHAR(255), prenom VARCHAR(255), date_naissance DATE, telephone VARCHAR(255), genre VARCHAR(255), PRIMARY KEY (id))");
		    }
		} catch (SQLException e) {
		    System.err.println("Error creating table: " + e.getMessage());
		}
	

	try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
	     Statement stmt = conn.createStatement()) {
	    // Check if the table exists
	    ResultSet rs = conn.getMetaData().getTables(null, null, "CONSULTATIONS", null);
	    if (!rs.next()) {
	        // Table does not exist, create it
	        stmt.executeUpdate("CREATE TABLE CONSULTATIONS (id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL, patient_id INT, date DATE, motif VARCHAR(255), diagnostic VARCHAR(255),  PRIMARY KEY (id), FOREIGN KEY (patient_id) REFERENCES PATIENTS(id) ON DELETE CASCADE)");
		    System.err.println("succes creating table: ");

	    
	    }
	} catch (SQLException e) {
	    System.err.println("Error creating table: " + e.getMessage());
	}
	
	try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
	    DatabaseMetaData dbm = conn.getMetaData();
	    ResultSet tables = dbm.getTables(null, null, "PRESCRIPTIONS", null);
	    if (!tables.next()) {
	        try (Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate("CREATE TABLE PRESCRIPTIONS ("
	                    + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
	                    + "consultation_id INT NOT NULL,"
	                    + "medicament_id INT NOT NULL,"
	                    + "observation VARCHAR(255),"
	                    + "quantite INT,"
	                    + "PRIMARY KEY (id),"
	                    + "FOREIGN KEY (consultation_id) REFERENCES CONSULTATIONS (id) ON DELETE CASCADE,"
	                    + "FOREIGN KEY (medicament_id) REFERENCES MEDICAMENT (id) ON DELETE CASCADE"
	                    + ")");
	            System.out.println("Prescriptions table created successfully.");
	        }
	    }
	    
	} catch (SQLException e) {
	}
	  // Create a connection to the database
    try (Connection conn = DriverManager.getConnection("jdbc:derby:pms")) {
        // Create a statement for executing SQL commands
        Statement stmt = conn.createStatement();
        
        // Check if the appointments table already exists
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "appointments", null);
        if (rs.next()) {
            System.out.println("Appointments table already exists.");
        } else {
            // Create the appointments table with an auto-generated ID, a DATETIME column for the date and time, and a foreign key to link to the patients table
            stmt.executeUpdate("CREATE TABLE appointments ("
                    + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + "Date TIMESTAMP NOT NULL,"
                    + "patient_id INT NOT NULL,"
                    + "PRIMARY KEY (id),"
                    + "FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE"
                    + ")");
            
            // Print a message indicating that the table was created successfully
            System.out.println("Appointments table created successfully.");
        }
    } catch (SQLException e) {
        // Handle any errors that occur
    }


	try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
     Statement stmt = conn.createStatement()) {

    DatabaseMetaData meta = conn.getMetaData();

    try (ResultSet rs = meta.getTables(null, null, "MEDECIN", null)) {
        if (!rs.next()) {
            stmt.executeUpdate(
                "CREATE TABLE MEDECIN (" +
                "id INT NOT NULL," +
                "nom VARCHAR(255)," +
                "adresse VARCHAR(255)," +
                "telephone VARCHAR(20)," +
                "specialite VARCHAR(255)" +
                ")"
            );

            System.out.println("MEDECIN table created successfully.");
        } 
    }

} catch (SQLException e) {
    e.printStackTrace();
}
	}
	
	private void toggleLighting(ImageView imageView) {
	    if (lastClicked != null) {
	        lastClicked.setEffect(null);
	    }
	    imageView.setEffect(lighting);
	    lastClicked = imageView;

	   
	}public void deleteTable(String tableName) {
	    try (Connection conn = DriverManager.getConnection("jdbc:derby:pms")) {
	        // Create a statement for executing SQL commands
	        Statement stmt = conn.createStatement();

	      

	    
	        // Delete the table
	        stmt.executeUpdate("DROP TABLE " + tableName);
	        System.out.println(tableName + " table deleted successfully.");
	    } catch (SQLException e) {
	        // Handle any errors that occur
	        e.printStackTrace();
	    }
	}


}
/*CREATE TABLE consultations (
    id INT NOT NULL AUTO_INCREMENT,
    patient_id INT NOT NULL,
    date DATE NOT NULL,
    motif VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);
*/
/*CREATE TABLE patients (
id INT NOT NULL AUTO_INCREMENT,
nom VARCHAR(255),
prenom VARCHAR(255),
date_naissance DATE,
telephone VARCHAR(255),
genre VARCHAR(255),
PRIMARY KEY (id)
);
*/