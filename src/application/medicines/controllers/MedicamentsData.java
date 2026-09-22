package application.medicines.controllers;

import java.util.List;

import application.medicines.models.Medicament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MedicamentsData {
    private static MedicamentsData instance = new MedicamentsData();
    private ObservableList<Medicament> medicaments;

    private MedicamentsData() {
        medicaments = FXCollections.observableArrayList();
    }

    public static MedicamentsData getInstance() {
        return instance;
    }

    public ObservableList<Medicament> getMedicaments() {
        return medicaments;
    }

    public void addMedicament(Medicament medicament) {
        medicaments.add(medicament);
    }
    
    public void clear() {
        medicaments.clear();
    }
    
    public void delete(Medicament medicament) {
    	medicaments.remove(medicament);
    }

    public void addMedicaments(List<Medicament> medicamentsFromDb) {
        medicaments.addAll(medicamentsFromDb);
    }
    public void update(Medicament medicament) {
    	
    	 MedicamentsData medicamentsData =MedicamentsData.getInstance() ;
    	// Get the index of the updated medicament in medicamentsData
    	int index = medicamentsData.getMedicaments().indexOf(medicament);

    	// Update the medicament in medicamentsData
    	medicamentsData.getMedicaments().set(index, medicament);

    	
    }
}
