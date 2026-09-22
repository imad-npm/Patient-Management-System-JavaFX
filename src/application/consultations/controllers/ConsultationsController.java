package application.consultations.controllers;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import javax.print.PrintException;

import com.itextpdf.text.DocumentException;

import application.consultations.models.Consultation;
import application.consultations.models.Prescription;
import application.medicines.models.Medicament;
import application.patients.models.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ConsultationsController {
	
	 @FXML
	  private TableView<Consultation> consultationsTable;
	  @FXML
	  private TableColumn<Consultation, Integer> idColumn;
	  @FXML
	  private TableColumn<Consultation, String> patientColumn;
	  @FXML
	  private TableColumn<Consultation, String> dateColumn;
	  @FXML
	  private TableColumn<Consultation, String> actionColumn;
	  @FXML
	  private TextField searchField ;
	  private FilteredList<Consultation> filteredConsultations;

    
    private ObservableList<Consultation> consultations ;

	  
	public void initialize() {
		show() ;
		
	}
	
	
	

	private void show() {
		  // Clear the existing data from the table
	    consultationsTable.getItems().clear();
		// retrieve consultations data from ConsultationsData
        consultations = ConsultationsData.getInstance().getConsultations();

        // Initialize the filtered list with all consultations
        filteredConsultations = new FilteredList<>(consultations, p -> true);

        // Set the filtered list as the data source for the consultation table
        consultationsTable.setItems(filteredConsultations);
	  
	    
	    // Populate the table with the retrieved consultations
	 
	       // consultationsTable.setItems(consultations);
	    
        // Set up the search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredConsultations.setPredicate(consultation -> {
                // If the search field is empty, show all consultations
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Check if the search term matches the consultation's date or patient name
                String lowerCaseSearch = newValue.toLowerCase();
                LocalDate date = consultation.getDate();
                String patientName = getPatientById(consultation.getPatientId());
                return date.toString().toLowerCase().contains(lowerCaseSearch)
                        || patientName.toLowerCase().contains(lowerCaseSearch);
            });
        });
        
        
	    // Set up the columns to display the consultation data
	    idColumn.setCellValueFactory(new PropertyValueFactory<Consultation, Integer>("id"));
	    patientColumn.setCellValueFactory(cellData -> {
            int patientId = cellData.getValue().getPatientId();
            String patient = getPatientById(patientId);
            return new SimpleStringProperty(patient);
        });
	    dateColumn.setCellValueFactory(new PropertyValueFactory<Consultation, String>("date"));

	    Callback<TableColumn<Consultation, String>, TableCell<Consultation, String>> cellFactory = new Callback<TableColumn<Consultation, String>, TableCell<Consultation, String>>() {
	        @Override
	        public TableCell<Consultation, String> call(final TableColumn<Consultation, String> param) {
	            final TableCell<Consultation, String> cell = new TableCell<Consultation, String>() {

	                @Override
	                public void updateItem(String item, boolean empty) {

	                    super.updateItem(item, empty);

	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                    	ImageView img1= new ImageView(new Image(getClass().getResourceAsStream("/application/patients/images/icons8-eye-90.png")));
	                    	ImageView img2= new ImageView(new Image(getClass().getResourceAsStream("/application/patients/images/icons8-remove-96.png")));
	                      	 img1.setFitWidth(20);
	                    	 img1.setFitHeight(14);
	                    	 img2.setFitWidth(20);
	                    	 img2.setFitHeight(20);
	                    	 Button deleteButton = new Button("Supprimer",img2);
	                    	 Button viewButton = new Button("Voir",img1);
	                    	 viewButton.setMinHeight(27);


	                        {
	                            viewButton.setOnAction((ActionEvent event) -> {
	                                // Get the consultation associated with this row and show its details
	                                Consultation consultation = getTableView().getItems().get(getIndex());
	                                showConsultationDetails(consultation);
	                            });
	                            deleteButton.setOnAction((ActionEvent event) -> {
	                                // Get the consultation associated with this row and delete it
	                                Consultation consultation = getTableView().getItems().get(getIndex());
	                                deleteConsultation(consultation);
	                            });
	                        }
	                        deleteButton.getStyleClass().add("btnSupprimer");
	                        viewButton.getStyleClass().add("btnVoir") ;

	                        HBox buttons = new HBox();
	                        buttons.getChildren().addAll(viewButton, deleteButton);
	                        setGraphic(buttons);
	                    }
	                }

	            };
	            return cell;
	        }
	    };

	    actionColumn.setCellFactory(cellFactory);
	    
	}






	public void  addConsultation() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/consultations/views/addConsultation.fxml"));

		Parent root = loader.load();
		      Stage stage = new Stage();
		      stage.setScene(new Scene(root));
		      stage.show();

	}
	
	public String getPatientById(int patientId) {
	    String nom = null;
	    String prenom = null;

	    try (Connection connection = DriverManager.getConnection("jdbc:derby:db")) {
	        PreparedStatement statement = connection.prepareStatement("SELECT nom, prenom FROM patients WHERE id = ?");
	        statement.setInt(1, patientId);
	        ResultSet result = statement.executeQuery();

	        if (result.next()) {
	            nom = result.getString("nom");
	            prenom = result.getString("prenom");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return nom + " " + prenom;
	}

	private void deleteConsultation(Consultation consultation) {
	    // Create an alert dialog for confirmation
	    Alert alert = new Alert(AlertType.CONFIRMATION);
	    alert.setTitle("Confirmation");
	    alert.setHeaderText("Delete Consultation");
	    alert.setContentText("Are you sure you want to delete this consultation?");

	    // Wait for the user's response
	    Optional<ButtonType> result = alert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	        // User clicked OK, so delete the consultation
	        deleteConsultationFromDB(consultation); // Replace with your own method to delete the consultation from the database
	        
	        // Remove the consultation from the consultations list in the ConsultationsData singleton
	        consultations.remove(consultation);
	        
	        // Update the consultations table
	        consultationsTable.setItems(consultations);
	    }
	}

	public void deleteConsultationFromDB(Consultation consultation) {
	    // Assume there is a database connection object called "connection"
	    try (Connection connection = DriverManager.getConnection("jdbc:derby:db")){
	        PreparedStatement statement = connection.prepareStatement("DELETE FROM consultations WHERE id = ?");
	        statement.setInt(1, consultation.getId());
	        statement.executeUpdate();
	        System.out.println("Consultation with id " + consultation.getId() + " deleted from the database.");
	    } catch (SQLException e) {
	        System.out.println("Error deleting consultation from the database: " + e.getMessage());
	    }
	}



	private void showConsultationDetails(Consultation consultation) {
	    // Create a new stage
	    Stage consultationDetailsStage = new Stage();
	    consultationDetailsStage.setTitle("Details de la consultation");

	    VBox consultationDetails = new VBox();
	    consultationDetails.setSpacing(10);
	    consultationDetails.setPadding(new Insets(10));
	    Label dateLabel = new Label("Date: ");
	    dateLabel.setStyle("-fx-font-weight: bold;");
	    Label patientLabel = new Label("Patient: ");
	    patientLabel.setStyle("-fx-font-weight: bold;");
	    
	    Label motifLabel = new Label("Motif: ");
	    motifLabel.setStyle("-fx-font-weight: bold;");
	    Label diagLabel = new Label("Diagnostic: ");
	    diagLabel.setStyle("-fx-font-weight: bold;");
	    Label prescLabel = new Label("Prescriptions: ");
	    prescLabel.setStyle("-fx-font-weight: bold;");
	    
	    consultationDetails.getChildren().add( new HBox(dateLabel, new Label(""+ consultation.getDate())) );
	    consultationDetails.getChildren().add( new HBox(patientLabel, new Label(""+ getPatientById(consultation.getPatientId()) )) );

	    consultationDetails.getChildren().add(new HBox(motifLabel, new Label(""+ consultation.getMotif())) );
	    consultationDetails.getChildren().add(new HBox(diagLabel, new Label(""+ consultation.getDiagnostic()))  );

	    consultationDetails.getChildren().add(prescLabel);

	    // Get the prescriptions for this consultation
	    ArrayList<Prescription> prescriptions=new ArrayList<Prescription>() ;
	   
	    try( Connection connection = DriverManager.getConnection("jdbc:derby:db") ;) {
	        PreparedStatement stmt = connection.prepareStatement(
	            "SELECT m.nom_de_marque,m.forme,m.dosage,p.observation, p.quantite,c.date " +
	            "FROM Prescriptions p " +
	            "JOIN Consultations c ON p.consultation_id = c.id " +
	            "JOIN Medicament m ON p.medicament_id = m.id " +
	            "WHERE c.id = ?"
	        );
	        stmt.setInt(1, consultation.getId());
	        ResultSet rs = stmt.executeQuery();

	        // Display the prescription details
	        while (rs.next()) {
	            String medicamentInfo = rs.getString("nom_de_marque")+" "+rs.getString("forme")+" "+
	           rs.getString("dosage") ;
	           String observation=rs.getString("observation");
                //LocalDate date=rs.getDate("date").toLocalDate() ; 
	            int quantite = rs.getInt("quantite");
	            consultationDetails.getChildren().add(new Label(medicamentInfo+" "+observation + " : " + quantite+"boite"+(quantite>1 ? "s" :"" )));
	        prescriptions.add(new Prescription(medicamentInfo, observation, quantite)) ;
	        
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    Button impBtn =new Button("imprimer") ;
	    impBtn.setStyle("-fx-margin: 0 0 100px 0;");


	    impBtn.setOnAction(event -> {
	        try { AddConsultationController inst = new AddConsultationController() ;
	        inst.genererOrdonnance( getPatientById(consultation.getPatientId()) , consultation.getId(),consultation.getDate(),prescriptions);
	        } catch ( PrinterException | IOException | PrintException e) {
	            e.printStackTrace();
	            // Handle any exceptions that occur during the generation of the ordonnance
	            // For example, you can show an error message to the user
	            Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setHeaderText(null);
	            alert.setContentText("An error occurred while generating the ordonnance.");
	            alert.showAndWait();
	        } 
	    });

	    
	    consultationDetails.getChildren().add(impBtn) ;

	    // Create a Scene and set it on the stage
	    Scene scene = new Scene(consultationDetails, 500, 400);
	    consultationDetailsStage.setScene(scene);

	    // Show the stage
	    consultationDetailsStage.show();

	}
	

	
}
