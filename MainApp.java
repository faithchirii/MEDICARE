package org.example.medicare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DatabaseConnection.initializeDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/medicare/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);

        stage.setTitle("Medicare Hospital Management System");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}