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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.ResponseWrapper;
import logic.Subscriber;

/**
 * Controller for subscriber password reset process.
 * 
 * Responsibilities: - Manage password reset verification for subscribers -
 * Validate subscriber ID and username - Handle server communication - Manage
 * navigation between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ResetPasswordSubscriberController {
	@FXML
	private TextField SubscruberIdField;

	@FXML
	private TextField usernameField;

	@FXML
	private Label errorLabel;

	private static ResetPasswordSubscriberController instance;
	private static Image icon; // Static variable to hold the application icon

	public ResetPasswordSubscriberController() {
		instance = this;
	}

	public static ResetPasswordSubscriberController getInstance() {
		return instance;
	}

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = ResetPasswordSubscriberController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Handles subscriber password reset verification request.
	 * 
	 * Validation steps: - Check for empty input fields - Validate subscriber ID (9
	 * digits) - Apply error styling - Send verification request to server
	 */
	@FXML
	private void handleSubscriberResetPassword() {
		try {
			String SubscriberId = SubscruberIdField.getText().trim();
			String username = usernameField.getText().trim();

			if (SubscriberId.trim().isEmpty() || username.trim().isEmpty()) {
				// Set the error message
				errorLabel.setText("Please fill all fields");
				errorLabel.setTextFill(Color.RED); // Set text color to red

				// Highlight empty fields
				if (SubscriberId.trim().isEmpty()) {
					SubscruberIdField.getStyleClass().add("error"); // Add error styling
				} else {
					SubscruberIdField.getStyleClass().removeAll("error"); // Remove error styling if not empty
				}

				if (username.trim().isEmpty()) {
					usernameField.getStyleClass().add("error"); // Add error styling
				} else {
					usernameField.getStyleClass().removeAll("error"); // Remove error styling if not empty
				}

				return; // Stop further execution
			} else {
				// Clear any previous error styling if both fields are filled
				SubscruberIdField.getStyleClass().removeAll("error");
				usernameField.getStyleClass().removeAll("error");
			}

			// Check if SubscriberId is exactly 9 digits
			if (!SubscriberId.matches("\\d{9}")) {
				// Set the error message
				errorLabel.setText("Subscriber ID must be exactly 9 digits");
				errorLabel.setTextFill(Color.RED); // Set text color to red

				// Highlight the SubscriberId field
				SubscruberIdField.getStyleClass().add("error");

				return; // Stop further execution
			} else {
				// Clear any previous error styling if the ID is valid
				SubscruberIdField.getStyleClass().removeAll("error");
			}

			// Create an object to send to the server
			Subscriber subscriber = new Subscriber(Integer.parseInt(SubscriberId), "", username, "", "", "", 2);

			// Send to the server
			// ClientUI.chat.accept(subscriber);

			// יצירת ResponseWrapper עם סוג הבקשה
			ResponseWrapper resetPasswordVerificationRequest = new ResponseWrapper("SUBSCRIBER_VERIFY_RESET_PASSWORD",
					subscriber);

			// שליחת הבקשה לשרת
			ClientUI.chat.accept(resetPasswordVerificationRequest);

		} catch (NumberFormatException e) {
			errorLabel.setText("Invalid ID format");
		} catch (Exception e) {
			errorLabel.setText("Error resetting password");
			e.printStackTrace();
		}
	}

	/**
	 * Updates UI based on server response for password reset.
	 * 
	 * Handles scenarios: - Successful ID and username verification - Failed
	 * verification - Navigation to new password screen
	 * 
	 * @param message Server response message
	 */
	@FXML
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("finded id and username for Subscriber")) {
				try {
					// Close the current window
					Stage currentStage = (Stage) errorLabel.getScene().getWindow();
					currentStage.close();

					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/gui_subscriber/SetNewSubscriberPassword.fxml"));
					Parent root = loader.load();
					// Get the controller of the new screen
					SetNewSubscriberPasswordController setNewSubscriberPasswordController = loader.getController();
					setNewSubscriberPasswordController.setSubscriberDetails(usernameField.getText().trim(),
							Integer.parseInt(SubscruberIdField.getText().trim()));

					// Create a new window
					Stage newPassword = new Stage();
					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

					newPassword.setScene(scene);
					newPassword.setTitle("Set New Subscriber Password");

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
			} else if (message.equals("Reset Subscriber Password Failed")
					|| message.equals("Invalid ID or Username for Subscriber")) {
				// Clear the input fields
				SubscruberIdField.clear();
				usernameField.clear();

				// Set the error message
				errorLabel.setText("Invalid username or ID");
				errorLabel.setTextFill(Color.RED); // Set text color to red

				// Highlight the fields to indicate an error
				SubscruberIdField.getStyleClass().add("error"); // Add error styling
				usernameField.getStyleClass().add("error"); // Add error styling
			} else {
				errorLabel.setText(message);
			}
		});
	}

	/**
	 * Handles return to subscriber login screen.
	 * 
	 * Actions: - Load subscriber login FXML - Configure new stage - Apply
	 * application icon - Close current window
	 */
	@FXML
	private void goToLogin1() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/gui_subscriber/SubscriberLoginController.fxml"));
			Parent root = loader.load();
			Stage loginStage = new Stage();
			Scene scene = new Scene(root);
			loginStage.setScene(scene);
			loginStage.setTitle("Subscriber Login");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				loginStage.getIcons().add(icon);
			}

			loginStage.show();

			// Close the current window
			Stage currentStage = (Stage) SubscruberIdField.getScene().getWindow();
			currentStage.close();

		} catch (IOException e) {
			errorLabel.setText("Error opening login screen");
			e.printStackTrace();
		}
	}
}