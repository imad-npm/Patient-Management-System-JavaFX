package application.rdv.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.rdv.models.Rdv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class RdvData {
    private static RdvData instance = null;

    private ObservableList<Rdv> appointments;

    private RdvData() {
        appointments =  FXCollections.observableArrayList();
        initializeAppointmentsFromDB() ;
        
    }

    public static RdvData getInstance() {
        if (instance == null) {
            instance = new RdvData();
        }

        return instance;
    }

    public void initializeAppointmentsFromDB() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:pms");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM appointments");

            while (rs.next()) {
                int id = rs.getInt("id");
                int patientId = rs.getInt("patient_id");
                LocalDateTime dateTime = rs.getTimestamp("Date").toLocalDateTime();

                Rdv appointment = new Rdv(id, patientId, dateTime);
                appointments.add(appointment);
            }

            

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Rdv> getAppointments() {
        return appointments;
    }

    public void setAppointments(ObservableList<Rdv> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(Rdv appointment) {
        appointments.add(appointment);

    }

    public void removeAppointment(Rdv appointment) {
    	
    	
    	   // Create an alert dialog for confirmation
	    Alert alert = new Alert(AlertType.CONFIRMATION);
	    alert.setTitle("Confirmation");
	    alert.setHeaderText("Delete Rdv");
	    alert.setContentText("Are you sure you want to delete this Rdv?");

	    // Wait for the user's response
	    Optional<ButtonType> result = alert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
        appointments.remove(appointment);

        try {
            Connection conn = DriverManager.getConnection("jdbc:derby:pms");
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM appointments WHERE id = ?");

            pstmt.setInt(1, appointment.getId());

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }
    public void updateAppointment(Rdv updatedAppointment) {
        int index = -1;
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId() == updatedAppointment.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            appointments.set(index, updatedAppointment);}
    }
}

