package controllers;

import java.io.IOException;
import java.net.URL;

import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Librarian;
import logic.ResponseWrapper;
/**
 * Controller for librarian password reset process.
 * 
 * Responsibilities:
 * - Manage password reset form
 * - Validate new password input
 * - Communicate with server for password update
 * - Handle navigation and error scenarios
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class NewPasswordToLibrarianController {
    @FXML
    private TextField NewPasswordId;

    @FXML
    private TextField ConfirmNewPasswordId;

    @FXML
    private Label errorLabel;

    private Stage stage;
    private static Image icon; // Static variable to hold the application icon

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private String username;
    private int id;

    public void setLibrarianDetails(String username, int id) {
        this.username = username;
        this.id = id;
    }

    private static NewPasswordToLibrarianController instance;

    public NewPasswordToLibrarianController() {
        instance = this;
    }

    public static NewPasswordToLibrarianController getInstance() {
        return instance;
    }

    /**
     * Loads application icon from resources.
     * 
     * Key Characteristics:
     * - Implements singleton pattern for icon loading
     * - Ensures icon is loaded only once
     * - Provides fallback logging for missing icon
     */
    private static void loadIcon() {
        if (icon == null) {
            URL iconUrl = NewPasswordToLibrarianController.class.getResource("/common/resources/icon.png");
            if (iconUrl != null) {
                icon = new Image(iconUrl.toString());
            } else {
                System.out.println("Icon file not found in resources!");
            }
        }
    }
    /**
     * Handles password reset request validation and submission.
     * 
     * Validation steps:
     * - Check for empty input fields
     * - Validate password match
     * - Apply error styling
     * - Send password reset request to server
     */
    @FXML
    private void handleResetPassword() {
        String newPassword = NewPasswordId.getText().trim();
        String confirmPassword = ConfirmNewPasswordId.getText().trim();

        if (newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            // Set the error message
            errorLabel.setText("Please fill all fields");
            errorLabel.setTextFill(Color.RED); // Set text color to red

            // Highlight empty fields
            if (newPassword.trim().isEmpty()) {
            	NewPasswordId.getStyleClass().add("error"); // Add error styling
            } else {
            	NewPasswordId.getStyleClass().removeAll("error"); // Remove error styling if not empty
            }

            if (confirmPassword.trim().isEmpty()) {
            	ConfirmNewPasswordId.getStyleClass().add("error"); // Add error styling
            } else {
            	ConfirmNewPasswordId.getStyleClass().removeAll("error"); // Remove error styling if not empty
            }

            return; // Stop further execution
        } else {
            // Clear any previous error styling if both fields are filled
        	NewPasswordId.getStyleClass().removeAll("error");
        	ConfirmNewPasswordId.getStyleClass().removeAll("error");
        }

        if (!newPassword.equals(confirmPassword)) {
            // Set the error message
            errorLabel.setText("Passwords do not match");
            errorLabel.setTextFill(Color.RED); // Set text color to red

            // Highlight both password fields to indicate mismatch
            NewPasswordId.getStyleClass().add("error");
            ConfirmNewPasswordId.getStyleClass().add("error");

            return; // Stop further execution
        } else {
            // Clear any previous error styling if passwords match
        	NewPasswordId.getStyleClass().removeAll("error");
        	ConfirmNewPasswordId.getStyleClass().removeAll("error");
        }

        Librarian librarian = new Librarian(id, "", "", username, newPassword, "");
     //   ClientUI.chat.accept(librarian);

        // יצירת ResponseWrapper עם סוג הבקשה
        ResponseWrapper resetPasswordRequest = new ResponseWrapper(
            "LIBRARIAN_RESET_PASSWORD", 
            librarian
        );

        // שליחת הבקשה לשרת
        ClientUI.chat.accept(resetPasswordRequest);
    
    }
    /**
     * Navigates back to librarian login screen.
     * 
     * Actions:
     * - Load librarian login FXML
     * - Configure new stage
     * - Apply application icon
     * - Close current window
     */
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianLoginView.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

            loginStage.setScene(scene);
            loginStage.setTitle("Librarian Login");

            // Load and set the application icon
            loadIcon();
            if (icon != null) {
                loginStage.getIcons().add(icon);
            }

            loginStage.show();

            // Close the current window
            Stage currentStage = (Stage) NewPasswordId.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            errorLabel.setText("Error opening login screen");
            e.printStackTrace();
        }
    }
    /**
     * Updates UI based on server response for password reset.
     * 
     * Handles scenarios:
     * - Successful password update
     * - Failed password reset
     * 
     * @param message Server response message
     */
    public void updateMessage(String message) {
        Platform.runLater(() -> {
            if (message.equals("Password updated successfully")) {
                errorLabel.setText("Password updated successfully");
                errorLabel.setTextFill(Color.GREEN);
                NewPasswordId.clear();
                ConfirmNewPasswordId.clear();
            } else if (message.equals("Reset Password Failed")) {
                errorLabel.setText("Reset Password Failed");
                errorLabel.setTextFill(Color.RED);
                NewPasswordId.clear();
                ConfirmNewPasswordId.clear();
            }
        });
    }
}