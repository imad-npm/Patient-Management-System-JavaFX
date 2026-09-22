package application;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseInit {

    public  static final String JDBC_URL =
            "jdbc:derby:db;create=true";

    public static void initialize() {

        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {

            System.out.println("Database opened successfully.");

            createPatientsTable(conn);
            createConsultationsTable(conn);
            createAndPopulateMedicaments(conn);

            createPrescriptionsTable(conn);
            createAppointmentsTable(conn);
            createMedecinTable(conn);

            System.out.println("Database initialization completed.");

        } catch (SQLException e) {

            System.err.println(
                    "Database initialization error: "
                    + e.getMessage()
            );
        }
    }

    private static void createPatientsTable(Connection conn) {

        String sql =
                "CREATE TABLE PATIENTS (" +
                "id INT GENERATED ALWAYS AS IDENTITY " +
                "(START WITH 1, INCREMENT BY 1) NOT NULL," +
                "nom VARCHAR(255)," +
                "prenom VARCHAR(255)," +
                "date_naissance DATE," +
                "telephone VARCHAR(255)," +
                "genre VARCHAR(255)," +
                "PRIMARY KEY (id)" +
                ")";

        executeCreate(conn, sql, "PATIENTS");
    }

    private static void createConsultationsTable(Connection conn) {

        String sql =
                "CREATE TABLE CONSULTATIONS (" +
                "id INT GENERATED ALWAYS AS IDENTITY " +
                "(START WITH 1, INCREMENT BY 1) NOT NULL," +
                "patient_id INT," +
                "date DATE," +
                "motif VARCHAR(255)," +
                "diagnostic VARCHAR(255)," +
                "PRIMARY KEY (id)," +
                "FOREIGN KEY (patient_id) " +
                "REFERENCES PATIENTS(id) " +
                "ON DELETE CASCADE" +
                ")";

        executeCreate(conn, sql, "CONSULTATIONS");
    }

private static void createAndPopulateMedicaments(Connection conn) {

    try {
        DatabaseMetaData metaData = conn.getMetaData();

        // Check whether table exists
        try (ResultSet rs = metaData.getTables(
                null,
                "APP",
                "MEDICAMENT",
                new String[]{"TABLE"})) {

            if (rs.next()) {
                try (Statement stmt = conn.createStatement();
                     ResultSet result = stmt.executeQuery(
                             "SELECT 1 FROM MEDICAMENT FETCH FIRST 1 ROW ONLY")) {

                    if (result.next()) {
                        return;
                    }
                }
            }
        }

        InputStream inputStream =
                DatabaseInit.class.getResourceAsStream(
                        "/application/medicaments.sql");

        if (inputStream == null) {
            return;
        }

        String script = new String(
                inputStream.readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8
        );

        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {

            for (String sql : script.split(";")) {

                sql = sql.trim();

                if (!sql.isEmpty()) {
                    stmt.addBatch(sql);
                }
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(autoCommit);
        }

    } catch (IOException | SQLException e) {
        e.printStackTrace();
    }
}


    private static void createPrescriptionsTable(Connection conn) {

        String sql =
                "CREATE TABLE PRESCRIPTIONS (" +
                "id INT NOT NULL GENERATED ALWAYS AS IDENTITY " +
                "(START WITH 1, INCREMENT BY 1)," +
                "consultation_id INT NOT NULL," +
                "medicament_id INT NOT NULL," +
                "observation VARCHAR(255)," +
                "quantite INT," +
                "PRIMARY KEY (id)," +
                "FOREIGN KEY (consultation_id) " +
                "REFERENCES CONSULTATIONS(id) " +
                "ON DELETE CASCADE," +
                "FOREIGN KEY (medicament_id) " +
                "REFERENCES MEDICAMENT(id) " +
                "ON DELETE CASCADE" +
                ")";

        executeCreate(conn, sql, "PRESCRIPTIONS");
    }

    private static void createAppointmentsTable(Connection conn) {

        String sql =
                "CREATE TABLE APPOINTMENTS (" +
                "id INT NOT NULL GENERATED ALWAYS AS IDENTITY " +
                "(START WITH 1, INCREMENT BY 1)," +
                "Date TIMESTAMP NOT NULL," +
                "patient_id INT NOT NULL," +
                "PRIMARY KEY (id)," +
                "FOREIGN KEY (patient_id) " +
                "REFERENCES PATIENTS(id) " +
                "ON DELETE CASCADE" +
                ")";

        executeCreate(conn, sql, "APPOINTMENTS");
    }

    private static void createMedecinTable(Connection conn) {

        String sql =
                "CREATE TABLE MEDECIN (" +
                "id INT NOT NULL," +
                "nom VARCHAR(255)," +
                "adresse VARCHAR(255)," +
                "telephone VARCHAR(20)," +
                "specialite VARCHAR(255)" +
                ")";

        executeCreate(conn, sql, "MEDECIN");
    }

    private static void executeCreate(
            Connection conn,
            String sql,
            String tableName) {

        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

            System.out.println(
                    tableName + " table created successfully."
            );

        } catch (SQLException e) {

            // X0Y32 = table already exists
            if ("X0Y32".equals(e.getSQLState())) {

                System.out.println(
                        tableName + " table already exists."
                );

            } else {

                System.err.println(
                        "Error creating " + tableName + ": "
                                + e.getMessage()
                );
            }
        }
    }
    public static void deleteTable(String tableName) {

    try (
        Connection conn = DriverManager.getConnection(JDBC_URL);
        Statement stmt = conn.createStatement()
    ) {

        stmt.executeUpdate("DROP TABLE " + tableName);

        System.out.println(
                tableName + " table deleted successfully."
        );

    } catch (SQLException e) {

        e.printStackTrace();
    }
}
}