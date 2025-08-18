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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Librarian;
import logic.ResponseWrapper;

/**
 * Controller for librarian registration process.
 * 
 * Responsibilities: - Manage librarian sign-up form - Validate registration
 * input - Communicate with server for registration - Handle navigation and
 * error scenarios
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class LibrarianSignUpController {
	@FXML
	private TextField librarianIdField;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField emailField;

	@FXML
	private Label errorLabel;

	private static LibrarianSignUpController instance;
	private static Image icon; // Static variable to hold the application icon

	public LibrarianSignUpController() {
		instance = this;
	}

	public static LibrarianSignUpController getInstance() {
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
			URL iconUrl = LibrarianSignUpController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Updates UI based on server response for librarian registration.
	 * 
	 * Handles scenarios: - Successful registration - Failed registration
	 * 
	 * Actions: - Display registration status message - Clear input fields on
	 * success
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Registration Successful")) {
				errorLabel.setText("Registration Successful!");
				clearFields();
			} else {
				errorLabel.setText("Registration Failed");
			}
		});
	}

	/**
	 * Handles navigation back to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard FXML - Configure
	 * new stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
			Parent root = loader.load();

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Librarian Dashboard");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			}

			home.show();
		} catch (IOException e) {
			errorLabel.setText("Error opening dashboard screen");
			e.printStackTrace();
		}
	}

	/**
	 * Handles librarian registration request with comprehensive validation.
	 * 
	 * Validation Steps: - Validate Librarian ID (9 digits) - Check first name -
	 * Check last name - Validate username - Validate password - Validate email
	 * (must end with @gmail.com)
	 * 
	 * Actions: - Apply dynamic error styling - Send registration request to server
	 * - Clear fields on successful submission
	 * 
	 * @throws NumberFormatException If librarian ID is invalid
	 * @throws Exception             For general registration errors
	 */
	@FXML
	private void handleSignUp() {
		try {
			// Flag to track if any error is found
			boolean hasError = false;

			// Check if librarianId is empty
			if (librarianIdField.getText().trim().isEmpty()) {
				librarianIdField.getStyleClass().add("error");
				hasError = true;
			} else if (!librarianIdField.getText().trim().matches("\\d{9}")) { // Check if ID is exactly 9 digits
				librarianIdField.getStyleClass().add("error");
				errorLabel.setText("Librarian ID must be exactly 9 digits");
				errorLabel.setTextFill(Color.RED);
				hasError = true;
			} else {
				librarianIdField.getStyleClass().removeAll("error");
			}

			// Check if firstName is empty
			if (firstNameField.getText().trim().isEmpty()) {
				firstNameField.getStyleClass().add("error");
				hasError = true;
			} else {
				firstNameField.getStyleClass().removeAll("error");
			}

			// Check if lastName is empty
			if (lastNameField.getText().trim().isEmpty()) {
				lastNameField.getStyleClass().add("error");
				hasError = true;
			} else {
				lastNameField.getStyleClass().removeAll("error");
			}

			// Check if username is empty
			if (usernameField.getText().trim().isEmpty()) {
				usernameField.getStyleClass().add("error");
				hasError = true;
			} else {
				usernameField.getStyleClass().removeAll("error");
			}

			// Check if password is empty
			if (passwordField.getText().trim().isEmpty()) {
				passwordField.getStyleClass().add("error");
				hasError = true;
			} else {
				passwordField.getStyleClass().removeAll("error");
			}

			// Check if email is empty
			if (emailField.getText().trim().isEmpty()) {
				emailField.getStyleClass().add("error");
				hasError = true;
			} else if (!emailField.getText().trim().matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) { // Validate email
				emailField.getStyleClass().add("error");
				errorLabel.setText("Email must be valid and end with @gmail.com");
				errorLabel.setTextFill(Color.RED);
				hasError = true;
			} else {
				emailField.getStyleClass().removeAll("error");
			}

			// If any error is found, set the error message and stop further execution
			if (hasError) {
				if (errorLabel.getText().isEmpty()) {
					errorLabel.setText("Please fill all fields");
				}
				errorLabel.setTextFill(Color.RED); // Set text color to red
				return;
			}

			int librarianId = Integer.parseInt(librarianIdField.getText().trim());
			String firstName = firstNameField.getText().trim();
			String lastName = lastNameField.getText().trim();
			String username = usernameField.getText().trim();
			String password = passwordField.getText().trim();
			String email = emailField.getText().trim();

			Librarian librarian = new Librarian(librarianId, firstName, lastName, username, password, email);
			ResponseWrapper signUpRequest = new ResponseWrapper("LIBRARIAN_SIGNUP", librarian

			);
			ClientUI.chat.accept(signUpRequest);
			// Clear fields after successful submission
			Platform.runLater(() -> {
				clearFields();
				errorLabel.setText("Registration request sent successfully");
			});

		} catch (NumberFormatException e) {
			Platform.runLater(() -> errorLabel.setText("Librarian ID must be a valid number"));
		} catch (Exception e) {
			Platform.runLater(() -> errorLabel.setText("Error occurred while registering"));
			e.printStackTrace();
		}
	}

	/**
	 * Clears all input fields in the sign-up form.
	 * 
	 * Resets: - Librarian ID - First name - Last name - Username - Password - Email
	 */
	private void clearFields() {
		librarianIdField.clear();
		firstNameField.clear();
		lastNameField.clear();
		usernameField.clear();
		passwordField.clear();
		emailField.clear();
	}

	/**
	 * Handles navigation to librarian login screen.
	 * 
	 * Actions: - Close current window - Load librarian login FXML - Configure new
	 * stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void goToLogin(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianLoginView.fxml"));
			Parent root = loader.load();

			Stage loginStage = new Stage();
			Scene scene = new Scene(root);
			loginStage.setScene(scene);
			loginStage.setTitle("Librarian Login");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				loginStage.getIcons().add(icon);
			}

			loginStage.show();
		} catch (IOException e) {
			errorLabel.setText("Error opening login screen");
			e.printStackTrace();
		}
	}
}