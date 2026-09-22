package application.consultations.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.consultations.models.Consultation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class ConsultationsData {

    private static ConsultationsData instance = new ConsultationsData();
    private ObservableList<Consultation> consultations ;
    private Connection connection;

    private ConsultationsData()  {
        consultations = FXCollections.observableArrayList();

        // Establish database connection
        try {
			connection = DriverManager.getConnection("jdbc:derby:pms");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        loadConsultationsFromDatabase();
    }

    public static ConsultationsData getInstance() {
        return instance;
    }

    public ObservableList<Consultation> getConsultations() {
        return consultations;
    }

    private void loadConsultationsFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM consultations");

            while (results.next()) {
                int id = results.getInt("id");
                int patient_id = results.getInt("patient_id");
                LocalDate date = results.getDate("date").toLocalDate();
                String motif = results.getString("motif");
                String diagnostic = results.getString("diagnostic");
                consultations.add(new Consultation(id, patient_id, date, motif,diagnostic));
            }

            results.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error loading consultations from database: " + e.getMessage());
        }
    }
}
