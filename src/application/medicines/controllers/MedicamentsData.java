
package application.medicines.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import application.DatabaseInit;
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

    public void loadMedicaments() {

        String sql = """
            SELECT ID, NOM_DE_MARQUE, FORME, DOSAGE
            FROM MEDICAMENT
            ORDER BY ID
            """;

        medicaments.clear();

        try (
            Connection conn = DriverManager.getConnection(DatabaseInit.JDBC_URL);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                Medicament medicament = new Medicament(
                    rs.getInt("ID"),
                    rs.getString("NOM_DE_MARQUE"),
                    rs.getString("FORME"),
                    rs.getString("DOSAGE")
                );

                medicaments.add(medicament);
            }

            System.out.println(
                "Loaded " + medicaments.size() + " medicaments"
            );

        } catch (SQLException e) {
            System.err.println("Error loading medicaments:");
            e.printStackTrace();
        }
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

        int index = medicaments.indexOf(medicament);

        if (index >= 0) {
            medicaments.set(index, medicament);
        }
    }
}
