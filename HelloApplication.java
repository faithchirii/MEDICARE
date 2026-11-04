package org.example.medicare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            DatabaseConnection.initializeDatabase();

            URL fxmlLocation = HelloApplication.class.getResource("/org/example/medicare/login.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ ERROR: Could not find 'login.fxml'!");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(fxmlLoader.load(), 350, 320);

            stage.setTitle("MediCare - Hospital Management System");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}