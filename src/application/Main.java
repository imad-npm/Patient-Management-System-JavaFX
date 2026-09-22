package application;
	
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			Parent root=FXMLLoader.load(getClass().getResource("main.fxml")) ;

			Scene scene = new Scene(root);
			System.out.println("JavaFX version: " + System.getProperties().get("javafx.version"));

			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			/*
			    try {
			        Connection conn = DriverManager.getConnection("jdbc:derby:db");
			        Statement stmt = conn.createStatement();
			        
			        stmt.executeUpdate("ALTER TABLE consultations ALTER COLUMN id RESTART WITH 1");

			    } catch (SQLException e) {
			        e.printStackTrace();
			    }
	*/		// Register a shutdown hook to shut down the Derby engine when the JVM exits
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    @Override
			    public void run() {
			        try {
			            DriverManager.getConnection("jdbc:derby:db;shutdown=true");
			        } catch (SQLException e) {
			            // Ignore the exception; it is expected when shutting down the engine
			        }
			    }
			});

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
