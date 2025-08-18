package controllers;

import java.io.IOException;
import java.net.URL;

import client.ClientUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.ResponseWrapper;
import logic.Subscriber;
/**
 * Controller responsible for managing the process of setting a new password for a subscriber
 * 
 * This class handles password reset functionality, including validation and server communication
 * 
 * @author [Developer Name]
 * @version 1.0
 * @since 2025-01-25
 */
public class SetNewSubscriberPasswordController {

    @FXML            
    private TextField  NewPasswordId;//NewPasswordId;

    @FXML
    private TextField ConfirmNewPasswordId; //ConfirmNewPasswordId;

    @FXML
    private PasswordField newPasswordField; // New Password field

    @FXML
    private PasswordField confirmPasswordField; // Confirm Password field

    @FXML
    private Label errorLabel; // Error label for displaying messages

    private Stage stage;
    private String username;
    private int id;

    private static Image icon; // Static variable to hold the icon

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets subscriber details for password reset
     * 
     * @param username Subscriber's username
     * @param id Subscriber's unique identifier
     */
    public void setSubscriberDetails(String username, int id) {
        this.username = username;
        this.id = id;
    }

    private static SetNewSubscriberPasswordController instance;

    public SetNewSubscriberPasswordController() {
        instance = this;
    }

    public static SetNewSubscriberPasswordController getInstance() {
        return instance;
    }
    /**
     * Handles subscriber password reset process
     * 
     * Performs validation checks:
     * - Checks for empty fields
     * - Validates password matching
     * - Communicates with server to update password
     * 
     * @throws IllegalArgumentException if password validation fails
     */
    @FXML
    private void handleSubscriberResetPassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            // Set the error message
            errorLabel.setText("Please fill all fields");
            errorLabel.setTextFill(Color.RED); // Set text color to red

            // Highlight empty fields
            if (newPassword.trim().isEmpty()) {
                newPasswordField.getStyleClass().add("error"); // Add error styling
            } else {
                newPasswordField.getStyleClass().removeAll("error"); // Remove error styling if not empty
            }

            if (confirmPassword.trim().isEmpty()) {
                confirmPasswordField.getStyleClass().add("error"); // Add error styling
            } else {
                confirmPasswordField.getStyleClass().removeAll("error"); // Remove error styling if not empty
            }

            return; // Stop further execution
        } else {
            // Clear any previous error styling if both fields are filled
            newPasswordField.getStyleClass().removeAll("error");
            confirmPasswordField.getStyleClass().removeAll("error");
        }

        if (!newPassword.equals(confirmPassword)) {
            // Set the error message
            errorLabel.setText("Passwords do not match");
            errorLabel.setTextFill(Color.RED); // Set text color to red

            // Highlight both password fields to indicate mismatch
            newPasswordField.getStyleClass().add("error");
            confirmPasswordField.getStyleClass().add("error");

            return; // Stop further execution
        } else {
            // Clear any previous error styling if passwords match
            newPasswordField.getStyleClass().removeAll("error");
            confirmPasswordField.getStyleClass().removeAll("error");
        }

        Subscriber subscriber = new Subscriber(
            id,
            "", // subscriber_name
            username,
            "", // email
            "", // phone_number
            newPassword,
            3 // detailed_subscription_history
        );
        
        // יצירת ResponseWrapper עם סוג הבקשה
        ResponseWrapper resetPasswordRequest = new ResponseWrapper(
            "SUBSCRIBER_SET_NEW_PASSWORD", 
            subscriber
        );

        // שליחת הבקשה לשרת
        ClientUI.chat.accept(resetPasswordRequest);
    }
    
    /**
     * Navigates back to subscriber login screen
     * 
     * Loads login screen and closes current window
     * 
     * @throws IOException if screen loading encounters an error
     */
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/SubscriberLoginController.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setTitle("Subscriber Login");

            // Load and set the icon
            loadIcon();
            if (icon != null) {
                loginStage.getIcons().add(icon);
            } else {
                System.err.println("Failed to load application icon.");
            }

            loginStage.show();

            // Close the current window
            Stage currentStage = (Stage) newPasswordField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            errorLabel.setText("Error opening login screen");
            e.printStackTrace();
        }
    }
    /**
     * Updates user feedback messages
     * 
     * Handles server response messages for password reset
     * 
     * @param message Feedback message from server
     */
    public void updateMessage(String message) {
        Platform.runLater(() -> {
            if (message.equals("Subscriber Password updated successfully")) {
                errorLabel.setText("Password updated successfully");
                errorLabel.setTextFill(Color.GREEN);

                // Clear fields
                newPasswordField.clear();
                confirmPasswordField.clear();
            }
            if (message.equals("Reset Password Failed")) {
                errorLabel.setText("Reset Subscriber Password Failed");
                errorLabel.setTextFill(Color.RED);

                newPasswordField.clear();
                confirmPasswordField.clear();
            }
        });
    }

    /**
     * Loads the application icon from resources
     * 
     * This method ensures that the application icon is loaded only once
     * and handles potential resource loading errors
     * 
     * Key Responsibilities:
     * - Check if icon is already loaded
     * - Retrieve icon from specified resource path
     * - Handle scenarios where icon file might be missing
     * 
     * @throws RuntimeException if icon resource cannot be located
     */
    private void loadIcon() {
        if (icon == null) {
            // Adjust the path to point to the resources directory
            String iconPath = "/common/resources/icon.png";
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                icon = new Image(iconUrl.toString());
            } else {
                System.err.println("Icon file not found in resources! Expected path: " + iconPath);
            }
        }
    }
}