package application.medicines.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import application.DatabaseInit;
import application.medicines.models.Medicament;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.collections.transformation.FilteredList;

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
    private TextField searchField;

    private final String jdbcUrl = DatabaseInit.JDBC_URL;

    private MedicamentsData medicamentsData = MedicamentsData.getInstance();

    private FilteredList<Medicament> filteredList =
            new FilteredList<>(medicamentsData.getMedicaments(), p -> true);

    public void addMedicine() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/application/medicines/views/addMedicine.fxml"
                )
        );

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void initialize() {
    medicamentsData.loadMedicaments();
    show();
}

    public void show() {

        medicinesTable.setItems(filteredList);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    filteredList.setPredicate(medicament -> {

                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        if (medicament.getNom()
                                .toLowerCase()
                                .startsWith(lowerCaseFilter)) {

                            return true;

                        } else if (medicament.getForme()
                                .toLowerCase()
                                .startsWith(lowerCaseFilter)) {

                            return true;

                        } else if (medicament.getDosage() != null
                                && medicament.getDosage()
                                .toLowerCase()
                                .startsWith(lowerCaseFilter)) {

                            return true;
                        }

                        return false;
                    });
                }
        );

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        nomColumn.setCellValueFactory(
                new PropertyValueFactory<>("nom")
        );

        formeColumn.setCellValueFactory(
                new PropertyValueFactory<>("forme")
        );

        dosageColumn.setCellValueFactory(
                new PropertyValueFactory<>("dosage")
        );

        for (TableColumn<Medicament, ?> column : medicinesTable.getColumns()) {

            if (column.getCellData(0) instanceof String) {

                TableColumn<Medicament, String> stringColumn =
                        (TableColumn<Medicament, String>) column;

                stringColumn.setEditable(true);
                stringColumn.setCellFactory(
                        TextFieldTableCell.forTableColumn()
                );

                stringColumn.setOnEditCommit(event -> {

                    Medicament medicament = event.getRowValue();
                    String newValue = event.getNewValue();

                    if (newValue.isBlank()) {

                        Alert alert = new Alert(AlertType.WARNING);

                        alert.setTitle("Warning");
                        alert.setHeaderText("Empty fields");
                        alert.setContentText(
                                "Please fill in all fields."
                        );

                        alert.showAndWait();

                        medicinesTable.refresh();

                        return;
                    }

                    if (stringColumn.equals(nomColumn)) {

                        String regex = "^[a-z���������������]*";

                        if (!newValue.matches(regex)) {

                            Alert alert = new Alert(
                                    AlertType.ERROR,
                                    "Invalid name."
                            );

                            alert.showAndWait();
                            medicinesTable.refresh();

                            return;
                        }

                        medicament.setNom(newValue);

                    } else if (stringColumn.equals(formeColumn)) {

                        medicament.setForme(newValue);

                    } else if (stringColumn.equals(dosageColumn)) {

                        String regex = "^[a-zA-Z0-9].*";

                        if (!newValue.matches(regex)) {

                            Alert alert = new Alert(
                                    AlertType.ERROR,
                                    "Invalid dosage format."
                            );

                            alert.showAndWait();
                            medicinesTable.refresh();

                            return;
                        }

                        medicament.setDosage(newValue);
                    }

                    updateMedicament(medicament);
                    medicamentsData.update(medicament);
                });
            }
        }

        Callback<TableColumn<Medicament, String>,
                TableCell<Medicament, String>> cellFactory =
                new Callback<TableColumn<Medicament, String>,
                        TableCell<Medicament, String>>() {

                    @Override
                    public TableCell<Medicament, String> call(
                            TableColumn<Medicament, String> param) {

                        return new TableCell<Medicament, String>() {

                            @Override
                            public void updateItem(
                                    String item,
                                    boolean empty) {

                                super.updateItem(item, empty);

                                if (empty) {

                                    setGraphic(null);
                                    setText("");

                                } else {

                                    ImageView img2 = new ImageView(
                                            new Image(
                                                    getClass()
                                                            .getResourceAsStream(
                                                                    "/application/medicines/images/icons8-remove-96.png"
                                                            )
                                            )
                                    );

                                    Button deleteBtn =
                                            new Button(
                                                    "Supprimer",
                                                    img2
                                            );

                                    deleteBtn.getStyleClass()
                                            .add("btnSupprimer");

                                    img2.setFitWidth(20);
                                    img2.setFitHeight(20);

                                    deleteBtn.setOnAction(
                                            (ActionEvent event) -> {

                                                Medicament medicament =
                                                        getTableView()
                                                                .getItems()
                                                                .get(getIndex());

                                                deleteMedicament(
                                                        medicament
                                                );
                                            }
                                    );

                                    setGraphic(deleteBtn);
                                    setText("");
                                }
                            }
                        };
                    }
                };

        actionColumn.setCellFactory(cellFactory);
    }

    private void updateMedicament(Medicament medicament) {

        String sql =
                "UPDATE medicament " +
                "SET nom_de_marque = ?, forme = ?, dosage = ? " +
                "WHERE id = ?";

        try (
                Connection conn =
                        DriverManager.getConnection(jdbcUrl);

                PreparedStatement statement =
                        conn.prepareStatement(sql)
        ) {

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

        Alert alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle("Supprimer un médicament");

        alert.setHeaderText(
                "Êtes-vous sûr de vouloir supprimer ce médicament?"
        );

        alert.setContentText(
                "Le médicament sera définitivement supprimé de la base de données."
        );

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try (
                    Connection conn =
                            DriverManager.getConnection(jdbcUrl);

                    PreparedStatement stmt =
                            conn.prepareStatement(
                                    "DELETE FROM medicament WHERE id = ?"
                            )
            ) {

                stmt.setInt(1, medicament.getId());

                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {

                    medicamentsData
                            .getMedicaments()
                            .remove(medicament);

                } else {

                    System.out.println(
                            "Aucune ligne supprimée de la table."
                    );
                }

            } catch (SQLException ex) {

                System.out.println(
                        "Une erreur s'est produite lors de la suppression du médicament: "
                                + ex.getMessage()
                );
            }
        }
    }
}