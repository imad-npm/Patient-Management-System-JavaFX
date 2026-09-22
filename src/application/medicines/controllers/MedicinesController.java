package application.medicines.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import java.util.Optional;

import application.medicines.models.Medicament;
import application.patients.models.Patient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;

public class MedicinesController {

	   @FXML
	    private TableView<Medicament> medicinesTable;

	    @FXML
	    private TableColumn<Medicament, Integer> idColumn;
	    @FXML
	    private TableColumn<Medicament, String> nomColumn;
	    @FXML
	    private TableColumn<Medicament, String> formeColumn;

	    @FXML
	    private TableColumn<Medicament, String> dosageColumn;
	    @FXML
		  private TableColumn<Medicament, String> actionColumn;
	    @FXML
	    private TextField searchField ;
	
    private final String jdbcUrl = "jdbc:derby:pms";
	
 private   MedicamentsData medicamentsData =MedicamentsData.getInstance() ;
 
 FilteredList<Medicament> filteredList = new FilteredList<>(medicamentsData.getMedicaments(), p -> true);

 
 
 public void addMedicine() throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/medicines/views/addMedicine.fxml"));

		Parent root = loader.load();
		      Stage stage = new Stage();
		      stage.setScene(new Scene(root));
		      stage.show();

	}
 

    public void initialize() throws IOException {
        if (!tableExists()) {
            executeScript();
        }
        
        show() ;

    }
    
    
  

public void show() { // Load data from the database to medicamentData
   
    medicamentsData.clear();
    medicamentsData.addMedicaments(getMedicamentsFromDb());

   
 // Set the filtered items to the table
    medicinesTable.setItems(filteredList);
    
    
   
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        filteredList.setPredicate(medicament -> {
            // If search field is empty, show all items
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            // Convert search text to lowercase
            String lowerCaseFilter = newValue.toLowerCase();

            // Check if any column contains the search text
            if (medicament.getNom().toLowerCase().startsWith(lowerCaseFilter)) {
                return true;
            }else  if (medicament.getForme().toLowerCase().startsWith(lowerCaseFilter)) {
                return true;
            }else  if (medicament.getDosage() != null&& medicament.getDosage().toLowerCase().startsWith(lowerCaseFilter)) {
                return true;
            }
                    
            // Return false if search text is not found in any column
            return false;
        });
    });

    // Set up the columns to display in the table
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
    formeColumn.setCellValueFactory(new PropertyValueFactory<>("forme"));
    dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
  //  actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
    
    for (TableColumn<Medicament, ?> column : medicinesTable.getColumns()) {
        if (column.getCellData(0) instanceof String) {
            TableColumn<Medicament, String> stringColumn = (TableColumn<Medicament, String>) column;
            stringColumn.setEditable(true);
            stringColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            
            stringColumn.setOnEditCommit(event -> {
                // Get the medicament object from the table row
                Medicament medicament = event.getRowValue();

                // Get the new value from the edit event
                String newValue = event.getNewValue();
                // Check if any of the fields are empty
                if (newValue.isBlank()) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Empty fields");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                 // Update the medicament object in the table
                    medicinesTable.refresh();
                    return;
                }


                // Determine which column was edited and update the medicament object accordingly
                if (stringColumn.equals(nomColumn)) {

           		 String   regex = "^[a-zàâæçéèêëîïôœùûü]*";
           		    if (!newValue.matches(regex)) {
           		        Alert alert = new Alert(AlertType.ERROR, "Invalid name.");
           		        alert.showAndWait();
                        medicinesTable.refresh();

           		        return ;
           		       
           		    }
           
           		  
                    medicament.setNom(newValue);
                } else if (stringColumn.equals(formeColumn)) {
                    medicament.setForme(newValue);
                } else if (stringColumn.equals(dosageColumn)) {

           		
            // Define a regular expression to match the dosage format (e.g. "100mg")
           		    String    regex = "^[a-zA-Z0-9].*";

           		    // If the new value does not match the regular expression, show an error message
           		    if (!newValue.matches(regex)) {
           		        Alert alert = new Alert(AlertType.ERROR, "Invalid dosage format. ");
           		        alert.showAndWait();
                        medicinesTable.refresh();

           		        return ;
           		       
           		    }
           		  
                    medicament.setDosage(newValue);
                }

                // Update the medicament object in the database
                updateMedicament(medicament);
                
                medicamentsData.update(medicament);


                // Update the medicament object in the table
                //medicinesTable.refresh();
            });
        }
    }


    // Add an edit button to the action column for each row
    Callback<TableColumn<Medicament, String>, TableCell<Medicament, String>> cellFactory = new Callback<TableColumn<Medicament, String>, TableCell<Medicament, String>>() {
        @Override
        public TableCell<Medicament, String> call(TableColumn<Medicament, String> param) {
            final TableCell<Medicament, String> cell = new TableCell<Medicament, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText("");
                    } else {
                    	ImageView img2= new ImageView(new Image(getClass().getResourceAsStream("/application/medicines/images/icons8-remove-96.png")));

                    	 Button deleteBtn = new Button("Supprimer",img2);
                    	
                    	
                    	 deleteBtn.getStyleClass().add("btnSupprimer");
                    	

                    	
                    	 img2.setFitWidth(20);
                    	 img2.setFitHeight(20);
                    	 
                    deleteBtn.setOnAction((ActionEvent event) -> {
                        Medicament medicament = getTableView().getItems().get(getIndex());
                        deleteMedicament(medicament);
                    });
                    
                   // HBox box = new HBox(deleteBtn);
                    setGraphic(deleteBtn);
                    setText("");
                    }
                }

			
            };
            return cell;
        }
    };
    actionColumn.setCellFactory(cellFactory);


	
	  
}

    

public void executeScript() throws IOException {
	
	String dbUrl = "jdbc:derby:pms";
	
    try (Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmt = conn.createStatement()) {
         /*  // Read the SQL script into a string
           String script = new String(Files.readAllBytes(Paths.get("medicaments.sql")));
*/
    	// Read the SQL script into a string
    	InputStream inputStream = getClass().getResourceAsStream("/application/medicines/controllers/medicaments.sql");
    	String script = new String(inputStream.readAllBytes());


           // Split the script into separate statements
           String[] statements = script.split(";");

           // Execute each statement in turn
           for (String statement : statements) {
               
               stmt.execute(statement.trim());
           }
           
           System.out.println("SQL script executed successfully");
       } catch (SQLException e) {
           System.err.println("SQL error: " + e.getMessage());
       }
	
}

public boolean tableExists() {
	
	  String url = "jdbc:derby:pms";
      try (Connection conn = DriverManager.getConnection(url);
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT 1 FROM SYS.SYSTABLES WHERE TABLENAME='MEDICAMENT'")) {
          if (rs.next()) {
              System.out.println("Table exists");
              return true ;
          } 
      } catch (SQLException e) {
          System.err.println("SQLException: " + e.getMessage());
          
      }
      return false ;
	
	
}

public List getMedicamentsFromDb() {
	System.out.println("succes") ;
	List<Medicament> medicamentsFromDb = new ArrayList<>();
	String sql = "SELECT * FROM medicament"; // SQL query to select all rows from the medicaments table

	try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery()) {
	    while (rs.next()) {
	        int id = rs.getInt("id");
	        String nom = rs.getString("nom_de_marque");
	        String forme = rs.getString("forme");
	        String dosage = rs.getString("dosage");
	  
	        medicamentsFromDb.add(new Medicament(id, nom, forme, dosage));
	    }
	     System.out.println("succes") ;
	}
	
	catch (SQLException ex) {
	    System.err.println("Error retrieving data from database: " + ex.getMessage());
	} 
	return medicamentsFromDb ;

}

private void updateMedicament(Medicament medicament) {
    String sql = "UPDATE medicament SET nom_de_marque = ?, forme = ?, dosage = ? WHERE id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:derby:pms");
         PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setString(1, medicament.getNom());
        statement.setString(2, medicament.getForme());
        statement.setString(3, medicament.getDosage());
        statement.setInt(4, medicament.getId());

        statement.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

private void deleteMedicament(Medicament medicament) {
    // Show a confirmation dialog before deleting the medicament
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Supprimer un médicament");
    alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce médicament?");
    alert.setContentText("Le médicament sera définitivement supprimé de la base de données.");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        // Delete the medicament from the database
        try {
            Connection conn = DriverManager.getConnection(jdbcUrl);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM medicament WHERE id = ?");
            stmt.setInt(1, medicament.getId());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                // Remove the medicament from the table
                medicamentsData.getMedicaments().remove(medicament);
            } else {
                System.out.println("Aucune ligne supprimée de la table.");
            }
            conn.close();
        } catch (SQLException ex) {
            System.out.println("Une erreur s'est produite lors de la suppression du médicament: " + ex.getMessage());
        }
    }
}


}






	


