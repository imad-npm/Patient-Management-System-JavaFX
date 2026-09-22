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

    // ============================================================
    // DATABASE
    // ============================================================

    private static final String JDBC_URL =
            "jdbc:derby:db;derby.sequence.preallocator=1;create=true";

    // ============================================================
    // FXML
    // ============================================================

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

    // ============================================================
    // UI
    // ============================================================

    private final Lighting lighting = new Lighting();

    private ImageView lastClicked;

    // ============================================================
    // INITIALIZE
    // ============================================================

    public void initialize() throws IOException {

        /*
         * IMPORTANT:
         *
         * Database initialization MUST happen before loading
         * patients.fxml.
         *
         * Otherwise PatientsController.initialize() can try to
         * access Derby before the database exists.
         */

        if (!initializeDatabase()) {
            System.err.println("Database initialization failed.");
            return;
        }

        System.out.println("Database initialization finished.");

        // --------------------------------------------------------
        // Lighting configuration
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // Load initial page
        // --------------------------------------------------------

        loadView("/application/patients/views/patients.fxml");

        // --------------------------------------------------------
        // Default selected icon
        // --------------------------------------------------------

        Platform.runLater(() -> {

            System.out.println("UI initialization finished");

            toggleLighting(patientsIcon);
        });

        // --------------------------------------------------------
        // Patients
        // --------------------------------------------------------

        patientsIcon.setOnMouseClicked(event -> {

            toggleLighting(patientsIcon);

            try {

                loadView(
                    "/application/patients/views/patients.fxml"
                );

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        // --------------------------------------------------------
        // Consultations
        // --------------------------------------------------------

        consultationsIcon.setOnMouseClicked(event -> {

            toggleLighting(consultationsIcon);

            try {

                loadView(
                    "/application/consultations/views/consultations.fxml"
                );

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        // --------------------------------------------------------
        // Medicines
        // --------------------------------------------------------

        medecinesIcon.setOnMouseClicked(event -> {

            toggleLighting(medecinesIcon);

            try {

                loadView(
                    "/application/medicines/views/medicines.fxml"
                );

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        // --------------------------------------------------------
        // Appointments
        // --------------------------------------------------------

        appointementsIcon.setOnMouseClicked(event -> {

            toggleLighting(appointementsIcon);

            try {

                loadView(
                    "/application/rdv/views/rdv.fxml"
                );

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        // --------------------------------------------------------
        // Settings
        // --------------------------------------------------------

        parametresIcon.setOnMouseClicked(event -> {

            toggleLighting(parametresIcon);

            try {

                loadView(
                    "/application/parametres/parametres.fxml"
                );

            } catch (IOException e) {

                e.printStackTrace();
            }
        });
    }

    // ============================================================
    // DATABASE INITIALIZATION
    // ============================================================

    private boolean initializeDatabase() {

        try {

            /*
             * First connection:
             *
             * create=true means Derby creates the database if
             * "db" does not exist yet.
             */

            try (Connection conn =
                    DriverManager.getConnection(JDBC_URL)) {

                System.out.println(
                    "Derby database opened successfully."
                );
            }

            // ----------------------------------------------------
            // Create tables
            // ----------------------------------------------------

            createPatientsTable();

            createConsultationsTable();

            createMedecinTable();

            createMedicamentsTable();

            createPrescriptionsTable();

            createAppointmentsTable();

            return true;

        } catch (SQLException e) {

            System.err.println(
                "Database initialization error:"
            );

            e.printStackTrace();

            return false;
        }
    }

    // ============================================================
    // PATIENTS
    // ============================================================

    private void createPatientsTable() {

        String sql =
            "CREATE TABLE PATIENTS (" +
            "id INT GENERATED ALWAYS AS IDENTITY " +
            "(START WITH 1, INCREMENT BY 1) NOT NULL, " +
            "nom VARCHAR(255), " +
            "prenom VARCHAR(255), " +
            "date_naissance DATE, " +
            "telephone VARCHAR(255), " +
            "genre VARCHAR(255), " +
            "PRIMARY KEY (id)" +
            ")";

        createTableIfNotExists(
            "PATIENTS",
            sql
        );
    }

    // ============================================================
    // CONSULTATIONS
    // ============================================================

    private void createConsultationsTable() {

        String sql =
            "CREATE TABLE CONSULTATIONS (" +
            "id INT GENERATED ALWAYS AS IDENTITY " +
            "(START WITH 1, INCREMENT BY 1) NOT NULL, " +
            "patient_id INT, " +
            "date DATE, " +
            "motif VARCHAR(255), " +
            "diagnostic VARCHAR(255), " +
            "PRIMARY KEY (id), " +
            "FOREIGN KEY (patient_id) " +
            "REFERENCES PATIENTS(id) " +
            "ON DELETE CASCADE" +
            ")";

        createTableIfNotExists(
            "CONSULTATIONS",
            sql
        );
    }

    // ============================================================
    // MEDECIN
    // ============================================================

    private void createMedecinTable() {

        String sql =
            "CREATE TABLE MEDECIN (" +
            "id INT NOT NULL, " +
            "nom VARCHAR(255), " +
            "adresse VARCHAR(255), " +
            "telephone VARCHAR(20), " +
            "specialite VARCHAR(255)" +
            ")";

        createTableIfNotExists(
            "MEDECIN",
            sql
        );
    }

    // ============================================================
    // MEDICAMENT
    // ============================================================
    //
    // Your PRESCRIPTIONS table references:
    //
    //     MEDICAMENT(id)
    //
    // so this table must exist BEFORE PRESCRIPTIONS.
    //
    // If your project already creates this table somewhere else,
    // this method simply detects that it already exists.
    // ============================================================

    private void createMedicamentsTable() {

        String sql =
            "CREATE TABLE MEDICAMENT (" +
            "id INT GENERATED ALWAYS AS IDENTITY " +
            "(START WITH 1, INCREMENT BY 1) NOT NULL, " +
            "nom VARCHAR(255), " +
            "description VARCHAR(255), " +
            "PRIMARY KEY (id)" +
            ")";

        createTableIfNotExists(
            "MEDICAMENT",
            sql
        );
    }

    // ============================================================
    // PRESCRIPTIONS
    // ============================================================

    private void createPrescriptionsTable() {

        String sql =
            "CREATE TABLE PRESCRIPTIONS (" +
            "id INT NOT NULL GENERATED ALWAYS AS IDENTITY " +
            "(START WITH 1, INCREMENT BY 1), " +
            "consultation_id INT NOT NULL, " +
            "medicament_id INT NOT NULL, " +
            "observation VARCHAR(255), " +
            "quantite INT, " +
            "PRIMARY KEY (id), " +
            "FOREIGN KEY (consultation_id) " +
            "REFERENCES CONSULTATIONS(id) " +
            "ON DELETE CASCADE, " +
            "FOREIGN KEY (medicament_id) " +
            "REFERENCES MEDICAMENT(id) " +
            "ON DELETE CASCADE" +
            ")";

        createTableIfNotExists(
            "PRESCRIPTIONS",
            sql
        );
    }

    // ============================================================
    // APPOINTMENTS
    // ============================================================

    private void createAppointmentsTable() {

        String sql =
            "CREATE TABLE APPOINTMENTS (" +
            "id INT NOT NULL GENERATED ALWAYS AS IDENTITY " +
            "(START WITH 1, INCREMENT BY 1), " +
            "Date TIMESTAMP NOT NULL, " +
            "patient_id INT NOT NULL, " +
            "PRIMARY KEY (id), " +
            "FOREIGN KEY (patient_id) " +
            "REFERENCES PATIENTS(id) " +
            "ON DELETE CASCADE" +
            ")";

        createTableIfNotExists(
            "APPOINTMENTS",
            sql
        );
    }

    // ============================================================
    // GENERIC TABLE CREATION
    // ============================================================

    private void createTableIfNotExists(
            String tableName,
            String sql
    ) {

        try (Connection conn =
                DriverManager.getConnection(JDBC_URL);
             Statement stmt =
                conn.createStatement()) {

            DatabaseMetaData meta =
                conn.getMetaData();

            try (ResultSet rs =
                    meta.getTables(
                        null,
                        null,
                        tableName.toUpperCase(),
                        null
                    )) {

                if (rs.next()) {

                    System.out.println(
                        tableName +
                        " table already exists."
                    );

                } else {

                    stmt.executeUpdate(sql);

                    System.out.println(
                        tableName +
                        " table created successfully."
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                "Error creating table " +
                tableName +
                ": " +
                e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // ============================================================
    // LOAD VIEW
    // ============================================================

    private void loadView(String fxmlPath)
            throws IOException {

        Parent root =
            FXMLLoader.load(
                getClass().getResource(fxmlPath)
            );

        mainContent.getChildren().clear();

        mainContent.getChildren().add(root);
    }

    // ============================================================
    // LIGHTING
    // ============================================================

    private void toggleLighting(ImageView imageView) {

        if (lastClicked != null) {

            lastClicked.setEffect(null);
        }

        imageView.setEffect(lighting);

        lastClicked = imageView;
    }

    // ============================================================
    // DELETE TABLE
    // ============================================================

    public void deleteTable(String tableName) {

        try (Connection conn =
                DriverManager.getConnection(JDBC_URL);
             Statement stmt =
                conn.createStatement()) {

            stmt.executeUpdate(
                "DROP TABLE " + tableName
            );

            System.out.println(
                tableName +
                " table deleted successfully."
            );

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}