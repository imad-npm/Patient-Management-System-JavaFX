package application.patients.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import application.consultations.models.Consultation;
import application.patients.models.Patient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class PatientsController {
	
	@FXML
	private ImageView addPatientIcon ;
	 
	 @FXML
	  private TableView<Patient> patientsTable;
	  @FXML
	  private TableColumn<Patient, Integer> idColumn;
	  @FXML
	  private TableColumn<Patient, String> nomColumn;
	  @FXML
	  private TableColumn<Patient, String> prenomColumn;
	  @FXML
	  private TableColumn<Patient, Integer> ageColumn;
	  @FXML
	  private TableColumn<Patient, LocalDate> dateNaissanceColumn;
	  @FXML
	  private TableColumn<Patient, String> genreColumn;
	  @FXML
	  private TableColumn<Patient, String> telephoneColumn;
	  @FXML
	  private TableColumn<Patient, String> actionColumn;
	  
	  @FXML 
	  private TextField searchField ;
	  @FXML 
	  private TextField ageField ;
	  @FXML
	 private  ComboBox<String> genderFilterCombo ; 

	   PatientsData patientsData = PatientsData.getInstance();
 
	  

	   FilteredList<Patient> filteredData = new FilteredList<>(patientsData.getPatients(), p -> true);
	   SortedList<Patient> sortedData = new SortedList<>(filteredData);


	   
	   public void initialize()  {
		

		   show() ;
		     
		   
		   }
	 
	   
	 
	public void addPatient() throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/patients/views/addPatient.fxml"));

		Parent root = loader.load();
		      Stage stage = new Stage();
		      stage.setScene(new Scene(root));
		      stage.show();
   
	}
	 
	
	 
	
	public void show() {
	
		patientsData.clear();
		patientsData.addPatients(getPatientsFromDb());
		  patientsTable.setItems(patientsData.getPatients());

		
		idColumn.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));
		

		nomColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("nom"));
		prenomColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("prenom"));
		dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<Patient, LocalDate>("dateNaissance"));
		ageColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
		genreColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("genre"));
		telephoneColumn.setCellValueFactory(new PropertyValueFactory<Patient, String>("telephone"));
      
		for (TableColumn<Patient, ?> column : patientsTable.getColumns()) {
		  
		    if (column.getCellData(0) instanceof String) {
		        TableColumn<Patient, String> stringColumn = (TableColumn<Patient, String>) column;
		        stringColumn.setEditable(true);
		        stringColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		    }	/*	    else if (column.getText().equals("Date_naissance")) {
	    	    TableColumn<Patient, LocalDate> dateColumn = (TableColumn<Patient, LocalDate>) column;
	    	    dateColumn.setEditable(true);
	    	    dateColumn.setCellFactory(TextFieldTableCell.<Patient, LocalDate>forTableColumn(new LocalDateStringConverter()));
		    }*/
		    
		    else if (column.getText().equals("Date_naissance")) {
		        TableColumn<Patient, LocalDate> dateColumn = (TableColumn<Patient, LocalDate>) column;
		        dateColumn.setEditable(true);
		        
		        // Create a custom string converter to format the date as dd/MM/yyyy
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		        // Set the custom string converter to the cell factory
		        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<LocalDate>() {
		            
		            public String toString(LocalDate date) {
		                if (date != null) {
		                    return formatter.format(date);
		                } else {
		                    return "";
		                }
		            }

		            public LocalDate fromString(String string) {
		                if (string != null && !string.isEmpty()) {
		                    return LocalDate.parse(string, formatter);
		                } else {
		                    return null;
		                }
		            }
		        }));
		    }

		
		    	    column.setOnEditCommit(event -> {
		    	        Patient patient = event.getRowValue();
		    	        Object newValue = event.getNewValue();
		    	        int patientId = patient.getId();
		    	        String columnName = column.getText();

		    	     
		    	            updatePatientInDb(patientId,columnName, newValue);
		    	            ArrayList<Patient> patients = (ArrayList<Patient>) getPatientsFromDb();
		    	            ObservableList<Patient> patientData = FXCollections.observableArrayList(patients);
		    	            patientsTable.setItems(patientData);

		    	    });
		    	    
		    
		 

		    

		}


     
 		Callback<TableColumn<Patient,String>, TableCell<Patient,String>> cellFactory =
			    new Callback<TableColumn<Patient,String>, TableCell<Patient,String>>() {
			 
			        @Override
			        public TableCell<Patient,String> call(TableColumn<Patient,String> param) {
			            final TableCell<Patient,String> cell = new TableCell<Patient,String>() {
			                @Override
			                public void updateItem(String item, boolean empty) {
			                    super.updateItem(item, empty);
			                    if (empty) {
			                        setGraphic(null);
			                    } else {
			                    	ImageView img1= new ImageView(new Image(getClass().getResourceAsStream("/application/patients/images/icons8-eye-90.png")));
			                    	ImageView img2= new ImageView(new Image(getClass().getResourceAsStream("/application/patients/images/icons8-remove-96.png")));

			                    	 Button deleteButton = new Button("Supprimer",img2);
			                    	 Button voirButton = new Button("Voir",img1);
			                    	
			                    	 deleteButton.getStyleClass().add("btnSupprimer");
			                    	 voirButton.getStyleClass().add("btnVoir") ;
			                    	 voirButton.setMinHeight(27);

			                    	 img1.setFitWidth(20);
			                    	 img1.setFitHeight(14);
			                    	 img2.setFitWidth(20);
			                    	 img2.setFitHeight(20);
			                    	 
			                    	 voirButton.setOnAction((ActionEvent event) -> {
			                                // Get the consultation associated with this row and show its details
			                                Patient patient = getTableView().getItems().get(getIndex());
			                                showPatientDetails(patient);
			                            }); 
			                    	 
			                    	 deleteButton.setOnAction(event -> {
			                    		    // Code to delete the selected row
			                    		    TableRow<Patient> row = getTableRow();

			                    		    Patient patient = row.getItem();
			                    		    int id = patient.getId();

			                    		    Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this patient?", ButtonType.YES, ButtonType.NO);
			                    		    alert.setHeaderText(null);
			                    		    alert.showAndWait();

			                    		    if (alert.getResult() == ButtonType.YES) {
			                    		    	String sql = "DELETE FROM patients WHERE id = ? ";
			                    		    	          
			                    		    	try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
			                    		    	     PreparedStatement statement = conn.prepareStatement(sql)) {
			                    		    	    statement.setInt(1, id);
			                    		    	    statement.executeUpdate();
			                    		    	     sql="DELETE FROM consultations WHERE patient_id = ?";
						                    		    	    statement.setInt(1, id);
						                    		    	    statement.executeUpdate();
                                                   sql= "DELETE FROM appointments WHERE patient_id = ?";
			                    		    	    statement.setInt(1, id);

			                    		    	    statement.executeUpdate();
			                    		    	

			                    		            patientsData.delete(patient) ;
			                    		        } catch (SQLException e) {
			                    		            System.out.println(e.getMessage());
			                    		        }
			                    		    }
			                    		});

			                        HBox box = new HBox(voirButton, deleteButton);
			                        setGraphic(box);
			                    }
			                }
			            };
			            return cell;
			        }
			    };

			    actionColumn.setCellFactory(cellFactory);
			    genderFilterCombo.setCellFactory(param -> new ListCell<>() {
			        @Override
			        protected void updateItem(String item, boolean empty) {
			            super.updateItem(item, empty);
			            setText(empty ? "" : item.toUpperCase());
			        }
			    });

			    

			    





			   genderFilterCombo.getItems().addAll("Tous", "Homme", "Femme");
			    genderFilterCombo.setValue("Tous");
			 // Add a listener to the choice box to update the filter
			    genderFilterCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			        filteredData.setPredicate(patient -> {
			            // If the filter is set to "Tous", display all patients
			            if (newValue.equals("Tous")) {
			                return true;
			            }

			            // Filter by genre
			            if (patient.getGenre().equalsIgnoreCase(newValue.toLowerCase())) {
			                return true;
			            }

			            return false;
			        });
			     // Bind the sorted data to the table
			        sortedData.comparatorProperty().bind(patientsTable.comparatorProperty());
			        patientsTable.setItems(sortedData);
			    });

			    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			    	
			        filteredData.setPredicate(patient -> {
			            // If filter text is empty, display all patients
			            if (newValue == null || newValue.isEmpty()) {
			                return true;
			            }
			       

			            String lowerCaseFilter = newValue.toLowerCase();

			            // Filter by nom, prenom, age, genre, or date de naissance
			            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			            String dateNaissanceStr = patient.getDateNaissance().format(formatter);
			            if (patient.getNom().toLowerCase().startsWith(lowerCaseFilter) ||
			                patient.getPrenom().toLowerCase().startsWith(lowerCaseFilter) ||
			               Integer.toString( patient.getAge()).startsWith(lowerCaseFilter) ||
			                patient.getGenre().equals(lowerCaseFilter) ||
			                dateNaissanceStr.contains(lowerCaseFilter) ||
			                patient.getTelephone().startsWith(lowerCaseFilter) ) {
			                return true;
			            }

			            return false;
			        });

			        sortedData.comparatorProperty().bind(patientsTable.comparatorProperty());
			        patientsTable.setItems(sortedData);
			    });

ageField.textProperty().addListener((observable, oldValue, newValue) -> {
			    	
			        filteredData.setPredicate(patient -> {
			            // If filter text is empty, display all patients
			            if (newValue == null || newValue.isEmpty()) {
			                return true;
			            }
			            if(Integer.toString(patient.getAge()).equals(newValue)  )
			            	return true;
			            
			            return false ;
			          
			        });  
			            sortedData.comparatorProperty().bind(patientsTable.comparatorProperty());
				        patientsTable.setItems(sortedData);
				    
});
			   



	}
	public void updatePatientInDb(int patientId, String columnName, Object newValue) {
	    String updateSql = "UPDATE patients SET " + columnName + " = ? WHERE id = ?";
	     
	    
	    
	    try (Connection connection =  DriverManager.getConnection("jdbc:derby:pms");
	         PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
	      /*  if (newValue instanceof String) {
	        	
	        	if(((String) newValue).isBlank()) {
	        		   Alert alert = new Alert(Alert.AlertType.ERROR);
			    alert.setTitle("Error");
			    alert.setHeaderText("champ vide");
			    alert.setContentText("remplir tous les champs");
			    alert.showAndWait();
			    return ;
	        	}
	        	*/
	    	System.out.println(columnName.equalsIgnoreCase("Nom"));
	    	
	    	if(columnName.equalsIgnoreCase("Nom")) {
	    		  if (!((String) newValue).matches("^[a-zA-Z]+$")) {
          		       // Le nom est invalide
          			   Alert alert = new Alert(Alert.AlertType.ERROR);
          			    alert.setTitle("Error");
          			    alert.setHeaderText("nom invalid");
          			    alert.setContentText("entrer un nom valid");
          			    alert.showAndWait();
          			    return ;
          		   }
	    		  preparedStatement.setString(1, (String) newValue);
	    	}
	    	else if (columnName.equalsIgnoreCase("Prenom")) {
	    		if (!((String) newValue).matches("^[a-zA-Z]+$")) {
              		       // Le nom est invalide
              			   Alert alert = new Alert(Alert.AlertType.ERROR);
              			    alert.setTitle("Error");
              			    alert.setHeaderText("prenom invalid");
              			    alert.setContentText("entrer un nom valid");
              			    alert.showAndWait();
              			    return ;
              		   }
	    		preparedStatement.setString(1, (String) newValue);
	    	}
	    	else if (columnName.equalsIgnoreCase("Telephone")) {
	    		 if (!((String) newValue).matches("^(\\+213|00213|0)(5|6|7)[0-9]{8}$")&& 
	    				 !((String) newValue).matches("^0\\d{8}$")) {
         		       // Le num�ro de t�l�phone est invalide
	    			  Alert alert = new Alert(AlertType.ERROR);
	  		        alert.setTitle("Numero invalid");
	  		        alert.setHeaderText("ex numeros valides : 0541236589 , 041235658");
	  		        alert.showAndWait();
         			    return ;
         		   }
	    		 preparedStatement.setString(1, (String) newValue);
	    	}

                
	        	
	           // preparedStatement.setString(1, (String) newValue);
	            
	        
	             if (columnName.equalsIgnoreCase("Date_naissance")) {
	        	   
	        	
	        	try {
	        		
	        		   LocalDate dateNaissance = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        		   // The date is in a valid format
	        		   LocalDate today = LocalDate.now();
	        		   if (dateNaissance.isAfter(today)) {
	        		     // Date is in the future, show error message
	        		     System.out.println("Date cannot be in the future");
	        		     Alert alert = new Alert(Alert.AlertType.ERROR);
	        		     alert.setTitle("Error");
	        		     alert.setHeaderText("Invalid Date");
	        		     alert.setContentText("Date cannot be in the future");
	        		     alert.showAndWait();
	        		return ;
	        		   }
	        		 }
	        	catch (NullPointerException e) {
	        		   e.printStackTrace();
	        		   // The date is not in a valid format
	        		   System.out.println("Date is not valid");
	        	
	        		 
	        			   // Your code here
	        				Alert alert = new Alert(Alert.AlertType.ERROR);
	        		    	alert.setTitle("Error");
	        		    	alert.setHeaderText("Invalid Date Format");
	        		    	alert.setContentText("Please enter a valid date in the format yyyy-MM-dd");
	        		    	alert.showAndWait();

	        		    
	        		    
	        			
	        		 }
	        	
	        	
	        	
	            LocalDate date = (LocalDate) newValue;
	            preparedStatement.setDate(1, Date.valueOf(date));
	        }
	        preparedStatement.setInt(2, patientId);
	        preparedStatement.executeUpdate();
	        // Close the result set, statement, and connection
			 
			  preparedStatement.close();
			  connection.close() ;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	public List<Patient> getPatientsFromDb() {
	    List<Patient> patients = new ArrayList<>();
	    Connection connection = null;
	    Statement statement = null;
	    ResultSet resultSet = null;

	    try {
	        connection = DriverManager.getConnection("jdbc:derby:pms");
	        statement = connection.createStatement();
	        resultSet = statement.executeQuery("SELECT * FROM patients");

	        while (resultSet.next()) {
	            int id = resultSet.getInt("id");
	            String nom = resultSet.getString("nom");
	            String prenom = resultSet.getString("prenom");
	            LocalDate dateNaissance = resultSet.getDate("date_naissance").toLocalDate();
	            String genre = resultSet.getString("genre");
			    String telephone = resultSet.getString("telephone");
	            Patient patient = new Patient(id, nom, prenom, dateNaissance,telephone,genre);
	         
	            patients.add(patient);
	          
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } 

	    return patients;
	}
	
	public void showPatientDetails(Patient patient) {
	    // Create a label to show the patient name
	    Label nameLabel = new Label( patient.getNom()+" "+patient.getPrenom());
	    Label patientLabel = new Label("Patient: ");
	    patientLabel.setStyle("-fx-font-weight: bold;");

	    // Retrieve the number of consultations for the patient from the database
	   int numConsultations=getConsultationCountForPatient(patient) ;

	    // Create a label to show the number of consultations for the patient
	    Label nbrLabel = new Label(""+numConsultations);
	    Label nombreConsulationsLabel = new Label("Nombre de Consultations: ");
	    nombreConsulationsLabel.setStyle("-fx-font-weight: bold;");

	    // Create a VBox to hold the labels
	    VBox vbox = new VBox();
	    vbox.getChildren().add(new HBox(patientLabel,nameLabel ) );
	    vbox.getChildren().add(new HBox(nombreConsulationsLabel,nbrLabel ) );

	    vbox.setPadding(new Insets(10));

	    // Create a new scene to show the patient details
	    Scene scene = new Scene(vbox, 400, 200);
	    Stage stage = new Stage();
	    stage.setScene(scene);
	    stage.setTitle("Patient Details");
	    stage.show();
	}


	public int getConsultationCountForPatient(Patient patient) {
	    int count = 0;

	    try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
	         PreparedStatement stmt = conn.prepareStatement(
	             "SELECT COUNT(*) AS count FROM Consultations " +
	             "INNER JOIN Patients ON Consultations.patient_id = Patients.id " +
	             "WHERE Patients.id = ?"
	         )
	    ) {
	        stmt.setInt(1, patient.getId());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                count = rs.getInt("count");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return count;
	}


}

