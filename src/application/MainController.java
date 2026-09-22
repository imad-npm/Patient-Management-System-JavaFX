
package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class MainController {

    private static final String DB_URL =
            "jdbc:derby:db;derby.sequence.preallocator=1;create=true";

    @FXML
    private AnchorPane mainContent;

    @FXML
    private ImageView patientsIcon;

    @FXML
    private ImageView consultationsIcon;

    @FXML
    private ImageView medecinesIcon;

    @FXML
    private ImageView appointementsIcon;

    @FXML
    private ImageView parametresIcon;

    private final Lighting lighting = new Lighting();
    private ImageView lastClicked;

    public void initialize() throws IOException {
        setupLighting();
        setupNavigation();
        setupDatabase();

        Platform.runLater(() -> toggleLighting(patientsIcon));

        loadView("/application/patients/views/patients.fxml");
    }

    // --------------------------------------------------
    // UI
    // --------------------------------------------------

    private void setupLighting() {
        lighting.setSpecularConstant(1);
        lighting.setSpecularExponent(50);
        lighting.setSurfaceScale(5.0);
        lighting.setDiffuseConstant(1.8);
        lighting.setLight(
                new Light.Distant(
                        100,
                        100,
                        Color.rgb(23, 236, 118)
                )
        );
    }

    private void setupNavigation() {
        patientsIcon.setOnMouseClicked(
                event -> openView(
                        patientsIcon,
                        "/application/patients/views/patients.fxml"
                )
        );

        consultationsIcon.setOnMouseClicked(
                event -> openView(
                        consultationsIcon,
                        "/application/consultations/views/consultations.fxml"
                )
        );

        medecinesIcon.setOnMouseClicked(
                event -> openView(
                        medecinesIcon,
                        "/application/medicines/views/medicines.fxml"
                )
        );

        appointementsIcon.setOnMouseClicked(
                event -> openView(
                        appointementsIcon,
                        "/application/rdv/views/rdv.fxml"
                )
        );

        parametresIcon.setOnMouseClicked(
                event -> openView(
                        parametresIcon,
                        "/application/parametres/parametres.fxml"
                )
        );
    }

    private void openView(ImageView icon, String fxmlPath) {
        toggleLighting(icon);
        loadView(fxmlPath);
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource(fxmlPath)
            );

            mainContent.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void toggleLighting(ImageView imageView) {
        if (lastClicked != null) {
            lastClicked.setEffect(null);
        }

        imageView.setEffect(lighting);
        lastClicked = imageView;
    }

    // --------------------------------------------------
    // DATABASE
    // --------------------------------------------------

    private void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            createPatientsTable(conn);
            createConsultationsTable(conn);
            createPrescriptionsTable(conn);
            createAppointmentsTable(conn);
            createMedecinTable(conn);

        } catch (SQLException e) {
            System.err.println("Database initialization failed:");
            e.printStackTrace();
        }
    }

    private void createPatientsTable(Connection conn) throws SQLException {
        createTableIfNotExists(
                conn,
                "PATIENTS",
                """
                CREATE TABLE PATIENTS (
                    id INT GENERATED ALWAYS AS IDENTITY
                        (START WITH 1, INCREMENT BY 1) NOT NULL,
                    nom VARCHAR(255),
                    prenom VARCHAR(255),
                    date_naissance DATE,
                    telephone VARCHAR(255),
                    genre VARCHAR(255),
                    PRIMARY KEY (id)
                )
                """
        );
    }

    private void createConsultationsTable(Connection conn) throws SQLException {
        createTableIfNotExists(
                conn,
                "CONSULTATIONS",
                """
                CREATE TABLE CONSULTATIONS (
                    id INT GENERATED ALWAYS AS IDENTITY
                        (START WITH 1, INCREMENT BY 1) NOT NULL,
                    patient_id INT,
                    date DATE,
                    motif VARCHAR(255),
                    diagnostic VARCHAR(255),
                    PRIMARY KEY (id),
                    FOREIGN KEY (patient_id)
                        REFERENCES PATIENTS(id)
                        ON DELETE CASCADE
                )
                """
        );
    }

    private void createPrescriptionsTable(Connection conn) throws SQLException {
        createTableIfNotExists(
                conn,
                "PRESCRIPTIONS",
                """
                CREATE TABLE PRESCRIPTIONS (
                    id INT GENERATED ALWAYS AS IDENTITY
                        (START WITH 1, INCREMENT BY 1) NOT NULL,
                    consultation_id INT NOT NULL,
                    medicament_id INT NOT NULL,
                    observation VARCHAR(255),
                    quantite INT,
                    PRIMARY KEY (id),
                    FOREIGN KEY (consultation_id)
                        REFERENCES CONSULTATIONS(id)
                        ON DELETE CASCADE,
                    FOREIGN KEY (medicament_id)
                        REFERENCES MEDICAMENT(id)
                        ON DELETE CASCADE
                )
                """
        );
    }

    private void createAppointmentsTable(Connection conn) throws SQLException {
        createTableIfNotExists(
                conn,
                "APPOINTMENTS",
                """
                CREATE TABLE APPOINTMENTS (
                    id INT NOT NULL GENERATED ALWAYS AS IDENTITY
                        (START WITH 1, INCREMENT BY 1),
                    date TIMESTAMP NOT NULL,
                    patient_id INT NOT NULL,
                    PRIMARY KEY (id),
                    FOREIGN KEY (patient_id)
                        REFERENCES PATIENTS(id)
                        ON DELETE CASCADE
                )
                """
        );
    }

    private void createMedecinTable(Connection conn) throws SQLException {
        createTableIfNotExists(
                conn,
                "MEDECIN",
                """
                CREATE TABLE MEDECIN (
                    id INT NOT NULL,
                    nom VARCHAR(255),
                    adresse VARCHAR(255),
                    telephone VARCHAR(20),
                    specialite VARCHAR(255)
                )
                """
        );
    }

    private void createTableIfNotExists(
            Connection conn,
            String tableName,
            String createSql
    ) throws SQLException {

        DatabaseMetaData metaData = conn.getMetaData();

        try (ResultSet resultSet = metaData.getTables(
                null,
                null,
                tableName,
                null
        )) {

            if (!resultSet.next()) {
                try (Statement statement = conn.createStatement()) {
                    statement.executeUpdate(createSql);
                    System.out.println(
                            tableName + " table created successfully."
                    );
                }
            }
        }
    }

    // --------------------------------------------------
    // DATABASE UTILITIES
    // --------------------------------------------------

    public void deleteTable(String tableName) {
        try (
                Connection conn = DriverManager.getConnection(DB_URL);
                Statement statement = conn.createStatement()
        ) {

            statement.executeUpdate("DROP TABLE " + tableName);

            System.out.println(
                    tableName + " table deleted successfully."
            );

        } catch (SQLException e) {
            System.err.println(
                    "Error deleting table " + tableName
            );
            e.printStackTrace();
        }
    }
}

