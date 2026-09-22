package application.rdv.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import application.consultations.models.Consultation;
import application.patients.models.Patient;
import application.rdv.models.Rdv;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;

public class RdvController {
	
  @FXML 
  private TableView<Rdv> rdvTable ;
  @FXML
  private TableColumn<Rdv, Integer> idColumn;
  @FXML
  private TableColumn<Rdv, String> patientColumn;
  @FXML
  private TableColumn<Rdv, LocalDateTime> dateColumn;
  @FXML
  private TableColumn<Rdv, String> actionColumn;
  @FXML
  private TextField searchField ;
  
  private ObservableList<Rdv> appointments;
  
  private FilteredList<Rdv> filteredAppointments;
 
	private RdvData rdvData ;
	
	public void initialize() {
            
		rdvTable.getItems().clear();
		rdvData = RdvData.getInstance();
		 appointments = rdvData.getAppointments();
		 

		 // Initialize the filtered list with all consultations
	        filteredAppointments = new FilteredList<>(appointments, p -> true);

	        // Set the filtered list as the data source for the consultation table
	        rdvTable.setItems(filteredAppointments);
	        
	        rdvTable.setEditable(true);
	        
	      
	        
	        
	        
	       searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	            filteredAppointments.setPredicate(rdv -> {
	                // If the search field is empty, show all appointments
	                if (newValue == null || newValue.isEmpty()) {
	                    return true;
	                }

	                // Check if the search term matches the appointment's date or patient name
	                String lowerCaseSearch = newValue.toLowerCase();
	                LocalDateTime dateTime = rdv.getDateTime();
	                String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm"));
	                String time = dateTime.toLocalTime().toString();
	                String patientName = getPatientById(rdv.getPatientId());
	                return formattedDateTime.toLowerCase().contains(lowerCaseSearch)
	                        || time.toLowerCase().contains(lowerCaseSearch)
	                        || patientName.toLowerCase().contains(lowerCaseSearch);
	            });
	        });


	 
	    // Define a custom cell factory for the date column
	       Callback<TableColumn<Rdv, LocalDateTime>, TableCell<Rdv, LocalDateTime>> dateCellFactory = column -> new TableCell<Rdv, LocalDateTime>() {
	           @Override
	           protected void updateItem(LocalDateTime dateTime, boolean empty) {
	               super.updateItem(dateTime, empty);

	               if (empty || dateTime == null) {
	                   setText(null);
	               } else {
	                   // Format the date using the desired pattern
	                   String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

	                   setText(formattedDate);
	               }
	           }
	       };

idColumn.setCellValueFactory(new PropertyValueFactory<Rdv, Integer>("id"));
dateColumn.setCellValueFactory(new PropertyValueFactory<Rdv, LocalDateTime>("dateTime"));
patientColumn.setCellValueFactory(cellData -> {
    int patientId = cellData.getValue().getPatientId();
    String patient = getPatientById(patientId);
    return new SimpleStringProperty(patient);
});


for (TableColumn<Rdv, ?> column : rdvTable.getColumns()) {
	  /*
 if (column.getCellData(0) instanceof LocalDateTime) {	System.out.println("hi") ;
        TableColumn<Rdv, LocalDateTime>dateColumn = (TableColumn<Rdv, LocalDateTime>) column;
        dateColumn.setEditable(true);
	    dateColumn.setCellFactory(TextFieldTableCell.<Rdv, LocalDateTime>forTableColumn(new LocalDateTimeStringConverter()));
    dateColumn.setOnEditCommit(event -> {
   	        Rdv rdv = event.getRowValue();
   	        Object newValue = event.getNewValue();
   	       
   	        String columnName = column.getText();

   	     
   	            updateRdvInDb(rdv,columnName, newValue);
  
   	    });   }*/
	if (column.getCellData(0) instanceof LocalDateTime) {
	    System.out.println("hi");
	    TableColumn<Rdv, LocalDateTime> dateColumn = (TableColumn<Rdv, LocalDateTime>) column;
	    dateColumn.setEditable(true);
	    
	    // Create a custom StringConverter for LocalDateTime to format the date
	    StringConverter<LocalDateTime> dateConverter = new StringConverter<>() {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy,HH:mm");

	        @Override
	        public String toString(LocalDateTime dateTime) {
	            if (dateTime == null) {
	                return "";
	            }
	            return dateTime.format(formatter);
	        }

	        @Override
	        public LocalDateTime fromString(String string) {
	            if (string == null || string.trim().isEmpty()) {
	                return null;
	            }
	            return LocalDateTime.parse(string, formatter);
	        }
	    };

	    dateColumn.setCellFactory(TextFieldTableCell.forTableColumn(dateConverter));
	    
	    dateColumn.setOnEditCommit(event -> {
	        Rdv rdv = event.getRowValue();
	        LocalDateTime newValue = event.getNewValue();
	        String columnName = column.getText();
	        updateRdvInDb(rdv, columnName, newValue);
	    });
	}

   else if (column.getCellData(0) instanceof String) {
	    TableColumn<Rdv, String> stringColumn = (TableColumn<Rdv, String>) column;
        stringColumn.setEditable(true);
        stringColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        stringColumn.setOnEditCommit(event -> {
   	        Rdv rdv = event.getRowValue();
   	        Object newValue = event.getNewValue();
   	       
   	        String columnName = column.getText();

   	     
   	            updateRdvInDb(rdv,columnName, newValue);
  
   	    });
   
 }
 
   	 
   	    }
 




Callback<TableColumn<Rdv, String>, TableCell<Rdv, String>> cellFactory = new Callback<TableColumn<Rdv, String>, TableCell<Rdv, String>>() {
    @Override
    public TableCell<Rdv, String> call(final TableColumn<Rdv, String> param) {
        final TableCell<Rdv, String> cell = new TableCell<Rdv, String>() {

            @Override
            public void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                	ImageView img2= new ImageView(new Image(getClass().getResourceAsStream("/application/patients/images/icons8-remove-96.png")));
                  	
                	 img2.setFitWidth(20);
                	 img2.setFitHeight(20);
                	 Button deleteButton = new Button("Supprimer",img2);
                	
                        deleteButton.setOnAction((ActionEvent event) -> {
                          rdvData.removeAppointment(getTableView().getItems().get(getIndex())) ;
                        });
                    
                    deleteButton.getStyleClass().add("btnSupprimer");

                    HBox buttons = new HBox();
                    buttons.getChildren().add(deleteButton);
                    setGraphic(buttons);
                }
                
            }

        };
        return cell;
    }
    
};

actionColumn.setCellFactory(cellFactory);

		
	}


 


	public void addRdv() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/rdv/views/addRdv.fxml"));

		Parent root = loader.load();
		      Stage stage = new Stage();
		      stage.setScene(new Scene(root));
		      stage.show();
	}
	
	
 	public String getPatientById(int patientId) {
	    String nom = null;
	    String prenom = null;

	    try (Connection connection = DriverManager.getConnection("jdbc:derby:pms")) {
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
 	
 	
 	private void updateRdvInDb(Rdv rdv, String columnName, Object newValue) {
 	    int rdvId = rdv.getId();
 	

 	    try {
 	        Connection conn = DriverManager.getConnection("jdbc:derby:pms");
 	        PreparedStatement stmt = null;

 	        // Update the appropriate column
 	        if (columnName.equalsIgnoreCase("Date")) {
 	        	
 	        	
 	        	try {
 	        	    LocalDateTime datetime = LocalDateTime.parse(newValue.toString());
 	        	    
 	        	    if (datetime.isBefore(LocalDateTime.now())) {
 	        	    	 Alert alert = new Alert(AlertType.ERROR);
  	 	   		        alert.setTitle("Invalid Date");
  	 	   		        alert.setHeaderText(null);
  	 	   		        alert.setContentText("Date cannot be in past");
  	 	   		        alert.showAndWait();
 	                          rdvTable.refresh();
 	                          return ;
 	           }
 	        	   for (Rdv appointment : rdvData.getAppointments()) {
 	                  if (appointment.getId() != rdvId && appointment.getDateTime().equals(datetime)) {
 	                 	 Alert alert = new Alert(AlertType.ERROR);
 	 	   		        alert.setTitle("Invalid Date");
 	 	   		        alert.setHeaderText(null);
 	 	   		        alert.setContentText("There is already an appointment with the same date");
 	 	   		        alert.showAndWait();
 	 	   		        rdvTable.refresh();
 	 	   		        return; 	                     
 	                  }
 	              }
 	        	
 	        	    java.util.Date dateValue = Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());
 	        	    java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(dateValue.getTime());
 	        	    String sql = "UPDATE appointments SET Date = ? WHERE id = ?";
 	        	    stmt = conn.prepareStatement(sql);
 	        	    stmt.setTimestamp(1, sqlTimestamp);
 	        	    rdv.setDateTime(datetime);
 	        	} catch (DateTimeParseException e) {
 	        	    System.out.println("Invalid datetime format. Please use the format yyyy-MM-dd'T'HH:mm:ss");
 	        	}

 	           
 	        } 
 	        else if (columnName.equalsIgnoreCase("Patient")) {
 	        	
 	           if((String)newValue==null|| !((String)newValue).matches("\\p{L}+\\s\\p{L}+")) {
 	   	    	 Alert alert = new Alert(AlertType.ERROR);
 	   		        alert.setTitle("Invalid Patient");
 	   		        alert.setHeaderText(null);
 	   		        alert.setContentText("enter a valid patient");
 	   		        alert.showAndWait();
 	   		        rdvTable.refresh();
 	   		        return;
 	   	    }
 	        	
 	        	
 	        	int patientId=getPatientIdFromDB((String)newValue) ;
 	        	if(patientId==-1){
 	        		 Alert alert = new Alert(Alert.AlertType.ERROR);
 				    alert.setTitle("Error");
 				  
 				    alert.setContentText("Patient not found  ");
 				    alert.showAndWait();
 	            return;
 	        	}
 	        	
 	            String sql = "UPDATE appointments SET patient_id = ? WHERE id = ?";
 	            stmt = conn.prepareStatement(sql);
 	            stmt.setInt(1, patientId);
 	            rdv.setPatientId(patientId);
 	        }

 	        // Set the ID of the appointment to update
 	        stmt.setInt(2, rdvId);

 	        // Execute the update
 	        int rowsUpdated = stmt.executeUpdate();

 	        if (rowsUpdated > 0) {
 	            System.out.println("Appointment updated successfully");
 	        } else {
 	            System.out.println("No appointment found with ID " + rdvId);
 	        }

 	        rdvData.updateAppointment(rdv);

 	        stmt.close();
 	        conn.close();

 	    } catch (SQLException e) {
 	        e.printStackTrace();
 	    }
 	}

 	private int getPatientIdFromDB(String patientInfo) {
        try {
            // Split the patientInfo string into nom and prenom
            String[] parts = patientInfo.split(" ");
            String nom = parts[0];
            String prenom = parts[1];
            
            // Create a connection to the database and prepare the query
            Connection conn = DriverManager.getConnection("jdbc:derby:pms");
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
