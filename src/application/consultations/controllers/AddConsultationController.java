package application.consultations.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import application.DatabaseInit;
import application.consultations.models.Consultation;
import application.consultations.models.Prescription;
import application.medicines.models.Medicament;
import application.patients.models.Patient;
import application.utils.FilterableComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
public class AddConsultationController {

    @FXML
    private ComboBox<String> patientCombo;

    @FXML
    private ComboBox<String> medicamentCombo;
    
    @FXML 
    private TextField observationField ;
   
    @FXML
	private TextField quantiteField;
    @FXML
    private TextField motifField ;
    @FXML
    private TextArea diagnosticField ;
     @FXML
    private TableView<Prescription> prescriptionsTable ;
     
     @FXML
     TableColumn<Prescription, String> medicamentColumn ;
     @FXML

     TableColumn<Prescription, String> observationColumn;
     @FXML

     TableColumn<Prescription, Integer> quantiteColumn;

     String jdbcUrl = DatabaseInit.JDBC_URL;

    private ConsultationsData consultations ;

    
    public void initialize() {
    	consultations=ConsultationsData.getInstance() ;

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

        
        
     // Populate the ComboBox with medicaments from the database
        List<Medicament> medicaments = getMedicamentsFromDB(); // Replace with your own method to fetch patients
        ObservableList<String> medicamentNames = FXCollections.observableArrayList();

         
        for (Medicament medicament : medicaments) {
            medicamentNames.add(medicament.getNom() + " - " + medicament.getForme()+ " " +medicament.getDosage());
        }

        medicamentCombo.setItems(medicamentNames);
         
        // Make the ComboBox filterable
        FilterableComboBox.makeFilterable(medicamentCombo);
        
        prescriptionsTable.setEditable(true);
      medicamentColumn.setEditable(false);

        medicamentColumn.setCellValueFactory(new PropertyValueFactory<>("medicament"));
        medicamentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        medicamentColumn.setOnEditCommit(event ->{
            
                Prescription prescription = event.getRowValue();
                prescription.setMedicament(event.getNewValue());
            
        }
) ;
        observationColumn.setCellValueFactory(new PropertyValueFactory<>("observation"));
        observationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        observationColumn.setOnEditCommit(event ->{
            
            Prescription prescription = event.getRowValue();
            prescription.setObservation(event.getNewValue());
        
    });
        
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantiteColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

    quantiteColumn.setOnEditCommit(event ->{
            
            Prescription prescription = event.getRowValue();
            prescription.setQuantity(event.getNewValue());
        
    });
  prescriptionsTable.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.DELETE || event.getCode()  == KeyCode.BACK_SPACE) {
            // Get the currently selected row
            int selectedIndex = prescriptionsTable.getSelectionModel().getSelectedIndex();
            
            // Remove the selected row from the table view's data
            prescriptionsTable.getItems().remove(selectedIndex);
            
            // Clear the selection
            prescriptionsTable.getSelectionModel().clearSelection();
        }
    });


    }
    
  

    // Other methods and event handlers go here

    public void addConsultation() throws SQLException {
    	Connection conn = DriverManager.getConnection(jdbcUrl);
    	  Statement stmt = conn.createStatement();
	      /*  ResultSet rs = stmt.executeQuery("SELECT * FROM Medecin");
	        rs.next();
	        if (!rs.next()) {
	           
	     	    return;
	            
	        }*/
	        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Medecin" );
	        rs.next();
	        int rowCount = rs.getInt(1);
	        if (rowCount == 0) {
	            // Table is empty
	        	 Alert alert = new Alert(AlertType.ERROR);
	     	    alert.setTitle("Error");
	     	    alert.setHeaderText(null);
	     	    alert.setContentText("veuiller remplir d'abord les infos du medecin ");
	     	    alert.showAndWait();
	        	
	            return;
	        }
    	
    	String patientInfo = patientCombo.getSelectionModel().getSelectedItem();
    	   if(patientInfo==null|| !patientInfo.matches("\\p{L}+\\s\\p{L}+")) {
  	    	 Alert alert = new Alert(AlertType.ERROR);
  		        alert.setTitle("Invalid Patient");
  		        alert.setHeaderText(null);
  		        alert.setContentText("enter a valid patient");
  		        alert.showAndWait();
  		        return;
  	    }
    	int patientId = getPatientIdFromDB(patientInfo); 
    	if (patientId == -1) {
    	    Alert alert = new Alert(AlertType.ERROR);
    	    alert.setTitle("Error");
    	    alert.setHeaderText(null);
    	    alert.setContentText("The selected patient does not exist.");
    	    alert.showAndWait();
    	    return;
    	}

    	
    	String motif = motifField.getText();
    	if (motif.isEmpty()) {
    	    Alert alert = new Alert(AlertType.ERROR);
    	    alert.setTitle("Error");
    	    alert.setHeaderText(null);
    	    alert.setContentText("Please enter the consultation motif.");
    	    alert.showAndWait();
    	    return;
    	}
    	String diagnostic = diagnosticField.getText();
    	if (diagnostic.isEmpty()) {
    	    Alert alert = new Alert(AlertType.ERROR);
    	    alert.setTitle("Error");
    	    alert.setHeaderText(null);
    	    alert.setContentText("Please enter the consultation motif.");
    	    alert.showAndWait();
    	    return;
    	}
    	  LocalDate date = LocalDate.now();
		Consultation consultation = new Consultation(patientId, date, motif,diagnostic);
    	int id=saveConsultationToDB(consultation); 
         consultation.setId(id); 
    	consultations.getConsultations().add(consultation) ;
    	System.out.println(id);
    	addPrescription(id) ;
   
			try {
				genererOrdonnance() ;
			} catch (IOException | PrintException | PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
    	
    }
    
   



	private int saveConsultationToDB(Consultation consultation) {
        int insertedId = -1;
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO CONSULTATIONS (patient_id, date, motif,diagnostic) VALUES (?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters of the prepared statement
            stmt.setInt(1, consultation.getPatientId());
            stmt.setDate(2, Date.valueOf(consultation.getDate()));
            stmt.setString(3, consultation.getMotif());
            stmt.setString(4, consultation.getDiagnostic());
            // Execute the statement
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new consultation was inserted successfully.");
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    insertedId = rs.getInt(1);
                    System.out.println("Inserted ID: " + insertedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting consultation: " + e.getMessage());
        }
        return insertedId;
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



	
	public void addMedicament() {
	    String medicamentName = medicamentCombo.getSelectionModel().getSelectedItem();
	    String observation = observationField.getText();
	    int quantite = Integer.parseInt(quantiteField.getText());

	    Prescription newPrescription = new Prescription(medicamentName, observation, quantite);

	    // Add the new prescription to the prescriptionsTable
	    prescriptionsTable.getItems().add(newPrescription);
	}



    
    	public List<Medicament> getMedicamentsFromDB() {
    	    List<Medicament> medicaments = new ArrayList<>();
    	    try {
    	        Connection conn = DriverManager.getConnection("jdbc:derby:db");
    	        Statement stmt = conn.createStatement();
    	        ResultSet rs = stmt.executeQuery("SELECT * FROM medicament");
    	        while (rs.next()) {
    	            String nom = rs.getString("nom_de_marque");
    	            String forme = rs.getString("forme");
    	            String dosage = rs.getString("dosage");
    	            
    	            Medicament medicament = new Medicament(nom, forme, dosage);
    	            medicaments.add(medicament);
    	        }
    	        rs.close();
    	        stmt.close();
    	        conn.close();
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
    	    return medicaments;
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
	
	
	
	private void addPrescription(int consultationId) {
	    String jdbcUrl = "jdbc:derby:db";
	    
	   try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
	        // Loop through all rows of prescriptionsTable
	        for (Prescription row : prescriptionsTable.getItems()) {
	            // Extract the data from the row
	          
	            String medicamentInfo = row.getMedicament();
	            String observation = row.getObservation();
	            int quantite = row.getQuantity();
	            
	            String medicamentName=medicamentInfo.split("-")[0] ;

	            // Get the medicament ID from the database based on its name
	            int medicamentId = getMedicamentIdFromDB(medicamentName); // Replace this with your code to query the database
	            // ...

	            // Insert the prescription into the database
	            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO PRESCRIPTIONS (consultation_id, medicament_id, observation, quantite) VALUES (?, ?, ?, ?)")) {
	                stmt.setInt(1, consultationId);
	                stmt.setInt(2, medicamentId);
	                stmt.setString(3, observation);
	                stmt.setInt(4, quantite);
	                stmt.executeUpdate();
	                System.err.println("Succes inserting prescription: ");

	            } catch (SQLException e) {
	                System.err.println("Error inserting prescription: " + e.getMessage());
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error connecting to database: " + e.getMessage());
	    }
	}

		private int getMedicamentIdFromDB(String nom) {
	        try {
	           
	            
	            // Create a connection to the database and prepare the query
	            Connection conn = DriverManager.getConnection("jdbc:derby:db");
	            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM medicament WHERE nom_de_marque = ?");
	            stmt.setString(1, nom);
	            

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
		public  void genererOrdonnance() throws IOException, PrintException, PrinterException {
	    	String patientInfo = patientCombo.getSelectionModel().getSelectedItem();
             
			
		    // �tablir une connexion � la base de donn�es
		    Connection conn = null;
		    try {
		        String url = "jdbc:derby:db;";
		        conn = DriverManager.getConnection(url);

		        // R�cup�rer les informations du m�decin
		        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		        ResultSet rs = stmt.executeQuery("SELECT NOM, ADRESSE, telephone, SPECIALITE FROM Medecin WHERE ID=1");
		        rs.next();
		   
		        String nomMedecin = rs.getString("NOM");
		        String adresseMedecin = rs.getString("ADRESSE");
		        String telMedecin = rs.getString("TELephone");
		        String specialiteMedecin = rs.getString("SPECIALITE");
		        
		       
		        // R�cup�rer les informations du patient
int id =getPatientIdFromDB(patientInfo) ;
        String ageString="";

    String sql = "SELECT date_naissance FROM patients WHERE id =";
   rs =   stmt.executeQuery(sql+id) ;
   
    if (rs.next()) {
        LocalDate birthDate = rs.getDate("date_naissance").toLocalDate();
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(birthDate, currentDate);
        if (age.getYears() < 1) {
            ageString = age.getMonths() + " mois";
        } else {
            ageString = age.getYears() + " ans";
        }
        
    } else {
        System.out.println("Person not found");
    }

		        // Cr�er un document PDF avec iText
		        Document document = new Document(new Rectangle(500, 700)); // A4 size
		        PdfWriter.getInstance(document, new FileOutputStream("ordonnance.pdf"));
		        document.open();
		       
		      
		        // Ajouter l'ent�te avec les informations du m�decin
		        Paragraph entete = new Paragraph();
		        
		        
		     //   document.add(image);
		       Paragraph p =new Paragraph(LocalDate.now().toString()) ;
		       p.setIndentationLeft(300f) ;
		        entete.add(p) ;
		        entete.add(new Paragraph("Dr "+nomMedecin));
		       		        entete.add(new Paragraph(specialiteMedecin));
                  entete.add(new Paragraph(adresseMedecin));
		        entete.add(new Paragraph( telMedecin));
		        entete.setAlignment(Element.ALIGN_CENTER);
		        entete.setSpacingAfter(10);
		        p =new Paragraph( patientInfo+" "+ageString) ;
		        p.setIndentationLeft(300f) ;
		        entete.add(p);
		        entete.setSpacingAfter(40);
		        document.add(entete);

		        // Ajouter le corps de l'ordonnance
		        Paragraph corps = new Paragraph("");
		        corps.setAlignment(Element.ALIGN_CENTER);
		     // Cr�er un Chunk pour le texte en gras
		        Chunk boldText = new Chunk("Ordonnance ");
		        boldText.setFont(FontFactory.getFont(FontFactory.TIMES_BOLD, 28));
		        corps.add(boldText);

		        corps.setSpacingAfter(30);
		        document.add(corps);

		        // Ajouter la liste des m�dicaments
		        
		        com.itextpdf.text.List medicinesList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);

		       for (Prescription prescription : prescriptionsTable.getItems()) {
		            

		            String medicineName = prescription.getMedicament();
		            String medicineObservation = prescription.getObservation();
		            int medicineQuantity = prescription.getQuantity();

		            StringBuilder medicineText = new StringBuilder();
		            medicineText.append(medicineName);
		            if (!medicineObservation.isEmpty()) {
		                medicineText.append(" (").append(medicineObservation).append(")");
		            }
		            medicineText.append(" - ").append(medicineQuantity).append(" unit�s");
                           
		            ListItem li =new ListItem(medicineText.toString()) ;
		            li.setSpacingAfter(10);
		            medicinesList.add(li );
		        }

		        // Ajouter la liste de m�dicaments au document PDF
		        document.add(medicinesList); 
		        
		     

		        
		        document.close();
		        
		     
		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("Success");
		        alert.setHeaderText(null);
		        alert.setContentText("Ordonnace generee avec succes ");
		        alert.showAndWait();

		        // Fermer la connexion
		        conn.close();
		        
		     // Load an existing PDF document
		        PDDocument doc = PDDocument.load(new File("/media/pc/B22A17AA2A176B1D/Users/pc/Desktop/doss/JavaFx/PMS1/ordonnance.pdf"));
		        PrinterJob job = PrinterJob.getPrinterJob();
		        job.setPageable(new PDFPageable(doc));
		        if (job.printDialog()) {
		            job.print();
		        }
		        document.close();
		        
		    } catch (SQLException se) {
		        se.printStackTrace();
		    } catch (FileNotFoundException | DocumentException  | PrinterException e) {
		        e.printStackTrace();
		    }
		}
		public void genererOrdonnance(String patientInfo, int consultationId, LocalDate date, ArrayList<Prescription> prescriptions) throws IOException, PrintException, PrinterException {
		    // Establish a connection to the database
		    Connection conn = null;
		    try {
		        String url = "jdbc:derby:db;";
		        conn = DriverManager.getConnection(url);

		        // Retrieve doctor's information
		        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		        ResultSet rs = stmt.executeQuery("SELECT NOM, ADRESSE, telephone, SPECIALITE FROM Medecin WHERE ID=1");
		        rs.next();

		        String nomMedecin = rs.getString("NOM");
		        String adresseMedecin = rs.getString("ADRESSE");
		        String telMedecin = rs.getString("TELephone");
		        String specialiteMedecin = rs.getString("SPECIALITE");

		        // Retrieve patient's information
		        int id = getPatientIdFromDB(patientInfo);
		        String ageString = "";

		        String sql = "SELECT date_naissance FROM patients WHERE id =";
		        rs = stmt.executeQuery(sql + id);

		        if (rs.next()) {
		            LocalDate birthDate = rs.getDate("date_naissance").toLocalDate();
		            LocalDate currentDate = LocalDate.now();
		            Period age = Period.between(birthDate, currentDate);
		            if (age.getYears() < 1) {
		                ageString = age.getMonths() + " mois";
		            } else {
		                ageString = age.getYears() + " ans";
		            }
		        } else {
		            System.out.println("Person not found");
		        }

		        // Create a document using iText for PDF generation
		        Document document = new Document(new Rectangle(500, 700)); // A4 size
		        PdfWriter.getInstance(document, new FileOutputStream("ordonnance1.pdf"));
		        document.open();

		        // Add the header with doctor's information
		        Paragraph entete = new Paragraph();
		        Paragraph p = new Paragraph(date.toString());
		        p.setIndentationLeft(300f);
		        entete.add(p);
		        entete.add(new Paragraph("Dr " + nomMedecin));
		        entete.add(new Paragraph(specialiteMedecin));
		        entete.add(new Paragraph(adresseMedecin));
		        entete.add(new Paragraph(telMedecin));
		        entete.setAlignment(Element.ALIGN_CENTER);
		        entete.setSpacingAfter(10);
		        p = new Paragraph(patientInfo + " " + ageString);
		        p.setIndentationLeft(300f);
		        entete.add(p);
		        entete.setSpacingAfter(40);
		        document.add(entete);

		        // Add the body of the prescription
		        Paragraph corps = new Paragraph("");
		        corps.setAlignment(Element.ALIGN_CENTER);

		        // Create a Chunk for the bold text
		        Chunk boldText = new Chunk("Ordonnance ");
		        boldText.setFont(FontFactory.getFont(FontFactory.TIMES_BOLD, 28));
		        corps.add(boldText);

		        corps.setSpacingAfter(30);
		        document.add(corps);

		        // Add the list of medications
		        com.itextpdf.text.List medicinesList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);

		        for (Prescription prescription : prescriptions) {
		            String medicineName = prescription.getMedicament();
		            String medicineObservation = prescription.getObservation();
		            int medicineQuantity = prescription.getQuantity();

		            StringBuilder medicineText = new StringBuilder();
		            medicineText.append(medicineName);
		            if (!medicineObservation.isEmpty()) {
		                medicineText.append(" (").append(medicineObservation).append(")");
		            }
		            medicineText.append(" - ").append(medicineQuantity).append(" unités");

		            ListItem li = new ListItem(medicineText.toString());
		            li.setSpacingAfter(10);
		            medicinesList.add(li);
		        }

		        // Add the list of medications to the PDF document
		        document.add(medicinesList);

		        document.close();

		        Alert alert = new Alert(Alert.AlertType.INFORMATION);
		        alert.setTitle("Success");
		        alert.setHeaderText(null);
		        alert.setContentText("Ordonnance générée avec succès");
		        alert.showAndWait();

		        // Close the database connection
		        conn.close();

		        // Load an existing PDF document
		        PDDocument doc = PDDocument.load(new File("ordonnance1.pdf"));
		        PrinterJob job = PrinterJob.getPrinterJob();
		        job.setPageable(new PDFPageable(doc));
		        if (job.printDialog()) {
		            job.print();
		        }
		        document.close();

		    } catch (SQLException se) {
		        se.printStackTrace();
		    } catch (FileNotFoundException | DocumentException | PrinterException e) {
		        e.printStackTrace();
		    }
		}

}
