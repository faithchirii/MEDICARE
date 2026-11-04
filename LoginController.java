package org.example.medicare;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Button loginButton;
    @FXML private Button registerModeButton;
    @FXML private Label formTitleLabel;
    @FXML private Hyperlink toggleModeLink;

    private boolean isRegisterMode = false;
    private boolean isPasswordVisible = false;

    @FXML
    private void handleTogglePassword() {
        if (isPasswordVisible) {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
            isPasswordVisible = false;
        } else {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
            isPasswordVisible = true;
        }
    }

    @FXML
    private void handleToggleRegisterMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            formTitleLabel.setText("Register New Staff Account");
            loginButton.setText("Submit & Create Account");
            registerModeButton.setText("Back to Sign In");
            toggleModeLink.setText("Already have an account? Click here to Sign In.");
        } else {
            formTitleLabel.setText("Please sign in to continue");
            loginButton.setText("Sign In");
            registerModeButton.setText("Create New Account");
            toggleModeLink.setText("Need help? Click here to register.");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? passwordTextField.getText().trim() : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please enter both username and password.");
            return;
        }

        if (isRegisterMode) {
            // Register Logic
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'Staff')")) {
                pstmt.setString(1, username);
                pstmt.setString(2, PasswordUtils.hashPassword(password));
                pstmt.executeUpdate();
                showAlert("Success", "Account created successfully! You can now log in.");
                handleToggleRegisterMode();
            } catch (Exception e) {
                showAlert("Registration Error", "Username already exists or database error.");
            }
        } else {
            // Login Logic
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, PasswordUtils.hashPassword(password));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/medicare/dashboard.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Medicare Hospital Management System");
                    stage.setMaximized(true);
                    stage.show();
                } else {
                    showAlert("Login Failed", "Invalid username or password.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Could not connect to database.");
            }
        }
    }

    @FXML private void handleMinimize() { ((Stage) loginButton.getScene().getWindow()).setIconified(true); }
    @FXML private void handleClose() { ((Stage) loginButton.getScene().getWindow()).close(); }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}