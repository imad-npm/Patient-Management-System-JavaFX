package application;

import java.io.IOException;

import javafx.animation.ScaleTransition;
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

    private Lighting lighting = new Lighting();

    private ImageView lastClicked;

    Parent root;

    public void initialize() throws IOException {

        // Initialize database before loading any controller
        DatabaseInit.initialize();

        Platform.runLater(() -> {

            System.out.println("UI initialization finished");

            toggleLighting(patientsIcon);
        });

        patientsIcon.setOnMouseClicked(event -> {

            toggleLighting(patientsIcon);

            try {

                Parent root1 = FXMLLoader.load(
                        getClass().getResource(
                                "/application/patients/views/patients.fxml"
                        )
                );

                mainContent.getChildren().clear();
                mainContent.getChildren().add(root1);

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        consultationsIcon.setOnMouseClicked(event -> {

            toggleLighting(consultationsIcon);

            try {

                Parent root1 = FXMLLoader.load(
                        getClass().getResource(
                                "/application/consultations/views/consultations.fxml"
                        )
                );

                mainContent.getChildren().clear();
                mainContent.getChildren().add(root1);

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        medecinesIcon.setOnMouseClicked(event -> {

            toggleLighting(medecinesIcon);

            try {

                Parent root1 = FXMLLoader.load(
                        getClass().getResource(
                                "/application/medicines/views/medicines.fxml"
                        )
                );

                mainContent.getChildren().clear();
                mainContent.getChildren().add(root1);

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        appointementsIcon.setOnMouseClicked(event -> {

            toggleLighting(appointementsIcon);

            try {

                Parent root1 = FXMLLoader.load(
                        getClass().getResource(
                                "/application/rdv/views/rdv.fxml"
                        )
                );

                mainContent.getChildren().clear();
                mainContent.getChildren().add(root1);

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        parametresIcon.setOnMouseClicked(event -> {

            toggleLighting(parametresIcon);

            try {

                Parent root1 = FXMLLoader.load(
                        getClass().getResource(
                                "/application/parametres/parametres.fxml"
                        )
                );

                mainContent.getChildren().clear();
                mainContent.getChildren().add(root1);

            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        Parent root = FXMLLoader.load(
                getClass().getResource(
                        "/application/patients/views/patients.fxml"
                )
        );

        mainContent.getChildren().add(root);

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

    private void toggleLighting(ImageView imageView) {

        if (lastClicked != null) {
            lastClicked.setEffect(null);
        }

        imageView.setEffect(lighting);

        lastClicked = imageView;
    }

    public void deleteTable(String tableName) {

        DatabaseInit.deleteTable(tableName);
    }
}