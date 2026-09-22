package application.parametres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class ParametresController {
	@FXML
	private TextField nomField;
	@FXML
	private TextField telField;
	
	@FXML
	private TextField adresseField;
	
	@FXML
	private TextField specialiteField;
	
	public void initialize() {
		
		 Connection conn = null;
	        try {
	            String url = "jdbc:derby:pms;";
	            conn = DriverManager.getConnection(url);

	            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM medecin");
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                // Récupérer les valeurs des colonnes de la ligne actuelle
	                String nom = rs.getString("nom");
	                String tel = rs.getString("telephone");
	                String adresse = rs.getString("adresse");
	                String specialite = rs.getString("specialite");

	                // Initialiser les champs de saisie avec les valeurs récupérées
	                nomField.setText(nom);
	                telField.setText(tel);
	                adresseField.setText(adresse);
	                specialiteField.setText(specialite);
	            }

	           

	            // Fermer la connexion
	            conn.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	}
	

	public void handleValiderBtn() {
	    String nom = nomField.getText();
	    String tel = telField.getText();
	    String adresse = adresseField.getText();
	    String specialite = specialiteField.getText();
	    
	    if (nom.isEmpty() || tel.isEmpty() || adresse.isEmpty() || specialite.isEmpty()) {
	        // Vérifier si tous les champs sont remplis
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Erreur");
	        alert.setHeaderText("Veuillez remplir tous les champs.");
	        alert.showAndWait();
	    } 
	    
	    else {
	    	
	    	if (!tel.matches("^(\\+213|00213|0)(5|6|7)[0-9]{8}$")&& 
   				 !tel.matches("^0\\d{8}$")) {
		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Numero invalid");
		        alert.setHeaderText("ex numeros valides : 0541236589 , 041235658");
		        alert.showAndWait();
	    		return ;
	    	}
	    	
	    	
	        // Établir une connexion à la base de données
	        Connection conn = null;
	        try {
	            String url = "jdbc:derby:pms;";
	            conn = DriverManager.getConnection(url);

	            // Vérifier si le médecin existe déjà dans la base de données
	            Statement stmt = conn.createStatement();
	            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM MEDECIN");
	            rs.next();
	            int count = rs.getInt(1);
	            if (count == 0) {
	                // Le médecin n'existe pas encore dans la base de données, l'insérer avec ID 1
	                String sql = "INSERT INTO MEDECIN (ID, NOM, TELephone, ADRESSE, SPECIALITE) VALUES (1, ?, ?, ?, ?)";
	                PreparedStatement pstmt = conn.prepareStatement(sql);
	                pstmt.setString(1, nom);
	                pstmt.setString(2, tel);
	                pstmt.setString(3, adresse);
	                pstmt.setString(4, specialite);
	                pstmt.executeUpdate();
	                pstmt.close();
	                System.out.println("Médecin inséré avec succès.");
	                Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Success");
	                alert.setHeaderText(null);
	                alert.setContentText("Operation completed successfully");
	                alert.showAndWait();

	            } else {
	                // Le médecin existe déjà dans la base de données, mettre à jour ses informations
	                String sql = "UPDATE MEDECIN SET NOM=?, TELephone=?, ADRESSE=?, SPECIALITE=? WHERE ID=1";
	                PreparedStatement pstmt = conn.prepareStatement(sql);
	                pstmt.setString(1, nom);
	                pstmt.setString(2, tel);
	                pstmt.setString(3, adresse);
	                pstmt.setString(4, specialite);
	                pstmt.executeUpdate();
	                pstmt.close();
	                System.out.println("Médecin mis à jour avec succès.");
	                Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Success");
	                alert.setHeaderText(null);
	                alert.setContentText("Operation completed successfully");
	                alert.showAndWait();

	            }

	            // Fermer la connexion
	            conn.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	    }
	}

}
