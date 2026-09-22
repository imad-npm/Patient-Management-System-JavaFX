package application.patients.controllers;
import java.util.ArrayList;
import java.util.List;

import application.patients.models.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PatientsData {
    private static PatientsData instance = new PatientsData();
    private ObservableList<Patient> patients;

    private PatientsData() {
        patients = FXCollections.observableArrayList();
    }

    public static PatientsData getInstance() {
        return instance;
    }

    public ObservableList<Patient> getPatients() {
        return patients;
    }

    public void addPatient(Patient patient) {
        patients.addAll(patient);
    }
    public void clear() {
        patients.clear();
    }
    public void delete(Patient patient) {
    	patients.remove(patient) ;
    }

	public void addPatients(List<Patient> patientsFromDb) {
		// TODO Auto-generated method stub
		patients.addAll(patientsFromDb) ;
		
	}
}
