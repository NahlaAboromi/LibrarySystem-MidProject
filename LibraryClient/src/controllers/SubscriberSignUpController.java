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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.ResponseWrapper;
import logic.Subscriber;

/**
 * Controller managing subscriber sign-up process
 * 
 * Handles: - User input validation - Subscriber registration - Navigation
 * between screens
 * 
 * @author [Developer Name]
 * @version 1.0
 * @since 2025-01-25
 */
public class SubscriberSignUpController {

	@FXML
	private TextField SubscriberNameField;

	@FXML
	private TextField idField;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField phoneField;

	@FXML
	private Label errorLabel;

	private static SubscriberSignUpController instance;

	private static Image icon; // Static variable to hold the icon

	public SubscriberSignUpController() {
		instance = this;
	}

	public static SubscriberSignUpController getInstance() {
		return instance;
	}

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	/**
	 * Navigates back to librarian dashboard
	 * 
	 * Key Actions: - Close current window - Load librarian dashboard - Pass
	 * librarian name - Set application icon
	 * 
	 * @param event Navigation trigger event
	 * @throws IOException if screen loading fails
	 */
	@FXML
	private void hendleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
			Parent root = loader.load();
			LibrarianDashboardController librarianDashboard = loader.getController();
			librarianDashboard.setLibrarianName(librarianName);
			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Librarian Dashboard");

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			home.show();

		} catch (IOException e) {
			errorLabel.setText("Error opening reset password screen");
			e.printStackTrace();
		}
	}

	/**
	 * Validates and processes subscriber sign-up
	 * 
	 * Comprehensive validation checks: - All fields filled - ID format (9 digits) -
	 * Phone number format (10 digits) - Email format (@gmail.com)
	 * 
	 * Workflow: 1. Validate input fields 2. Highlight error fields 3. Create
	 * subscriber object 4. Send sign-up request to server
	 * 
	 * @throws NumberFormatException if ID parsing fails
	 */
	@FXML
	private void handleSignUp() {
		// Flag to track if any error is found
		boolean hasError = false;
		StringBuilder errorMessages = new StringBuilder();

		// Check if all fields are filled
		if (SubscriberNameField.getText().trim().isEmpty() || idField.getText().trim().isEmpty()
				|| usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()
				|| emailField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
			errorLabel.setText("Please fill all fields");
			errorLabel.setTextFill(Color.RED); // Set text color to red
			errorMessages.append("Please fill in all fields.\n");
			hasError = true;

			// Highlight empty fields
			if (SubscriberNameField.getText().trim().isEmpty()) {
				SubscriberNameField.getStyleClass().add("error");
			} else {
				SubscriberNameField.getStyleClass().removeAll("error");
			}
			if (idField.getText().trim().isEmpty()) {
				idField.getStyleClass().add("error");
			} else {
				idField.getStyleClass().removeAll("error");
			}

			if (usernameField.getText().trim().isEmpty()) {
				usernameField.getStyleClass().add("error");
			} else {
				usernameField.getStyleClass().removeAll("error");
			}

			if (passwordField.getText().trim().isEmpty())
			{
				passwordField.getStyleClass().add("error");
			} else
			{
				passwordField.getStyleClass().removeAll("error");
			}

			if (emailField.getText().trim().isEmpty()) {
				emailField.getStyleClass().add("error");
			} else {
				emailField.getStyleClass().removeAll("error");
			}

			if (phoneField.getText().trim().isEmpty()) {
				phoneField.getStyleClass().add("error");
			} else {
				phoneField.getStyleClass().removeAll("error");
			}
		}

		// Validate subscriber ID (must be exactly 9 digits)
		String idText = idField.getText().trim();
		if (!idText.matches("\\d{9}")) {
			errorLabel.setText("The ID number must contain exactly 9 digits.");
			errorMessages.append("The ID number must contain exactly 9 digits.\n");
			errorLabel.setTextFill(Color.RED); // Set text color to red
			idField.getStyleClass().add("error");
			hasError = true;
		} else {
			idField.getStyleClass().removeAll("error");
		}

		// Validate phone number (must be exactly 10 digits)
		String phoneText = phoneField.getText().trim();
		if (!phoneText.matches("^\\d{10}$")) {
			errorLabel.setText("The phone number must contain exactly 10 digits, without any other characters.");
			errorLabel.setTextFill(Color.RED); // Set text color to red
			phoneField.getStyleClass().add("error");
			hasError = true;
			errorMessages.append("The phone number must contain exactly 10 digits, without any other characters.\n");
		} else {
			phoneField.getStyleClass().removeAll("error");
		}

		// Validate email (must contain '@gmail.com')
		String email = emailField.getText().trim();
		if (!email.endsWith("@gmail.com") || !email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
			errorLabel.setText("Email must end with '@gmail.com'.");
			errorLabel.setTextFill(Color.RED); // Set text color to red
			errorMessages.append("The email address must end with '@gmail.com'.\n");
			emailField.getStyleClass().add("error");
			hasError = true;
		} else {
			emailField.getStyleClass().removeAll("error");
		}

		// If any error is found, stop further execution
		if (hasError) {
			errorLabel.setText(errorMessages.toString());
			errorLabel.setTextFill(Color.RED);
			return;
		}
		// If all validations pass, create the subscriber
		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String subscriberName = SubscriberNameField.getText().trim();
		int subscriberId = Integer.parseInt(idText);

		Subscriber newSubscriber = new Subscriber(subscriberId, subscriberName, username, email, phoneText, password,
				1);
		ResponseWrapper signUpRequest = new ResponseWrapper("SUBSCRIBER_SIGNUP", newSubscriber);
		ClientUI.chat.accept(signUpRequest);
	}

	/**
	 * Handles server response messages for subscriber registration process
	 * 
	 * Manages different registration scenarios: - Successful registration -
	 * Username already exists - Registration failure
	 * 
	 * Thread-safe UI update using Platform.runLater()
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Registration Subscriber Successful")) {
				// Set success message and color to green
				errorLabel.setText("Registration Successful!");
				errorLabel.setTextFill(Color.GREEN);

				// Clear all input fields
				idField.clear();
				usernameField.clear();
				passwordField.clear();
				emailField.clear();
				phoneField.clear();
				SubscriberNameField.clear();

				// Clear any previous error styling
				idField.getStyleClass().removeAll("error");
				usernameField.getStyleClass().removeAll("error");
				passwordField.getStyleClass().removeAll("error");
				emailField.getStyleClass().removeAll("error");
				phoneField.getStyleClass().removeAll("error");
				SubscriberNameField.getStyleClass().removeAll("error");
			} else if (message.equals("Username subscriber already exists")) {
				// Set error message and color to red
				errorLabel.setText("Username already exists");
				errorLabel.setTextFill(Color.RED);

				// Highlight the username field
				usernameField.getStyleClass().add("error");
			} else if (message.equals("Registration Subscriber Failed")) {
				// Set error message and color to red
				errorLabel.setText("Registration Subscriber Failed");
				errorLabel.setTextFill(Color.RED);
			} else {
				// Set the received message as the error message
				errorLabel.setText(message);
				errorLabel.setTextFill(Color.RED);
			}
		});
	}

	/**
	 * Loads application icon with lazy initialization
	 * 
	 * Key Responsibilities: - Load icon only once - Use relative resource path -
	 * Handle potential loading errors
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
