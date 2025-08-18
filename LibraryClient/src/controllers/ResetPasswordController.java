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
 * - Manage password reset verification
 * - Validate librarian ID and username
 * - Handle server communication
 * - Manage navigation between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ResetPasswordController {
    @FXML
    private TextField librarianIdField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorLabel;

    private static ResetPasswordController instance;
    private static Image icon; // Static variable to hold the application icon

    public ResetPasswordController() {
        instance = this;
    }

    public static ResetPasswordController getInstance() {
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
            URL iconUrl = ResetPasswordController.class.getResource("/common/resources/icon.png");
            if (iconUrl != null) {
                icon = new Image(iconUrl.toString());
            } else {
                System.out.println("Icon file not found in resources!");
            }
        }
    }
    /**
     * Handles password reset verification request.
     * 
     * Validation steps:
     * - Check for empty input fields
     * - Validate librarian ID (9 digits)
     * - Apply error styling
     * - Send verification request to server
     */
    @FXML
    private void handleResetPassword() {
        try {
            String librarianId = librarianIdField.getText().trim();
            String username = usernameField.getText().trim();

            if (librarianId.trim().isEmpty() || username.trim().isEmpty()) {
                // Set the error message
                errorLabel.setText("Please fill all fields");
                errorLabel.setTextFill(Color.RED); // Set text color to red

                // Highlight empty fields
                if (librarianId.trim().isEmpty()) {
                    librarianIdField.getStyleClass().add("error"); // Add error styling
                } else {
                    librarianIdField.getStyleClass().removeAll("error"); // Remove error styling if not empty
                }

                if (username.trim().isEmpty()) {
                    usernameField.getStyleClass().add("error"); // Add error styling
                } else {
                    usernameField.getStyleClass().removeAll("error"); // Remove error styling if not empty
                }

                return; // Stop further execution
            } else {
                // Clear any previous error styling if both fields are filled
                librarianIdField.getStyleClass().removeAll("error");
                usernameField.getStyleClass().removeAll("error");
            }

            // Check if librarianId is exactly 9 digits
            if (!librarianId.matches("\\d{9}")) {
                // Set the error message
                errorLabel.setText("Invalid Librarian ID: Must be exactly 9 digits");
                errorLabel.setTextFill(Color.RED); // Set text color to red

                // Highlight the librarianId field
                librarianIdField.getStyleClass().add("error");

                return; // Stop further execution
            } else {
                // Clear any previous error styling if the ID is valid
                librarianIdField.getStyleClass().removeAll("error");
            }

            // Create an object to send to the server
            Librarian librarian = new Librarian(
                Integer.parseInt(librarianId), "", "", username, "", "");

            // Send to the server
            //ClientUI.chat.accept(librarian);
            ResponseWrapper resetPasswordVerificationRequest = new ResponseWrapper(
            	    "LIBRARIAN_VERIFY_RESET_PASSWORD", 
            	    librarian
            	);
            ClientUI.chat.accept(resetPasswordVerificationRequest);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid ID format");
            errorLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            errorLabel.setText("Error resetting password");
            errorLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }
    /**
     * Displays server response messages.
     * 
     * Handles scenarios:
     * - Successful ID and username verification
     * - Failed verification
     * - Navigation to new password screen
     * 
     * @param message Server response message
     */
    @FXML
    public void showError(String message) {
        Platform.runLater(() -> {
            if (message.equals("finded id and username for librarian")) {
                try {
                    // Close the current window
                    Stage currentStage = (Stage) errorLabel.getScene().getWindow();
                    currentStage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/NewPasswordToLibrarian.fxml"));
                    Parent root = loader.load();
                    // Get the controller of the new screen
                    NewPasswordToLibrarianController newPasswordController = loader.getController();
                    newPasswordController.setLibrarianDetails(usernameField.getText().trim(), Integer.parseInt(librarianIdField.getText().trim()));

                    // Create a new window
                    Stage newPassword = new Stage();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

                    newPassword.setScene(scene);
                    newPassword.setTitle("Reset Password");

                    // Load and set the application icon
                    loadIcon();
                    if (icon != null) {
                        newPassword.getIcons().add(icon);
                    }

                    newPassword.show();

                } catch (IOException e) {
                    errorLabel.setText("Error opening screen");
                    e.printStackTrace();
                }
            } else if (message.equals("Reset Password Failed") || message.equals("Invalid ID or Username")) {
            	// Clear the input fields
            	librarianIdField.clear();
            	usernameField.clear();

            	// Set the error message
            	errorLabel.setText("Invalid username or ID");
            	errorLabel.setTextFill(Color.RED); // Set text color to red

            	// Highlight the fields to indicate an error
            	librarianIdField.getStyleClass().add("error"); // Add error styling
            	usernameField.getStyleClass().add("error"); // Add error styling
            } else {
                errorLabel.setText(message);
            }
        });
    }

    /**
     * Handles return to librarian login screen.
     * 
     * Actions:
     * - Close current window
     * - Load librarian login FXML
     * - Configure new stage
     * - Apply application icon
     * 
     * @param event ActionEvent triggering navigation
     */
    @FXML
    private void handleReturnButtonAction(ActionEvent event) {
        try {
            // Close the current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Load the previous screen (e.g., the login screen or dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianLoginView.fxml")); // Replace with your previous screen FXML
            Parent root = loader.load();
            Stage previousStage = new Stage();
            Scene scene = new Scene(root, 600, 400);
            previousStage.setScene(scene);
            previousStage.setTitle("Librarian Login");

            // Load and set the application icon
            loadIcon();
            if (icon != null) {
                previousStage.getIcons().add(icon);
            }

            previousStage.show();
        } catch (IOException e) {
            errorLabel.setText("Error returning to the previous screen");
            e.printStackTrace();
        }
    }
}