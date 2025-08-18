package controllers;

import java.io.IOException;
import java.net.URL;

import client.ChatClient;
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
 * Controller for librarian login process.
 * 
 * Responsibilities: - Manage librarian authentication - Handle login form
 * validation - Navigate between screens - Provide password reset functionality
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class LibrarianLoginController {
	private String librarianName; // Field to store librarian's name
	private static Image icon; // Static variable to hold the application icon

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	@FXML
	private TextField usernameField;

	@FXML
	private Label errorLabel;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button loginButton;

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private static LibrarianLoginController instance;

	public LibrarianLoginController() {
		instance = this;
	}

	public static LibrarianLoginController getInstance() {
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
			URL iconUrl = LibrarianLoginController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Updates UI based on login response.
	 * 
	 * Handles scenarios: - Successful login - Failed login - Error messages
	 * 
	 * Actions: - Navigate to dashboard - Clear input fields - Display error
	 * messages
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Login Successful")) {
				try {
					System.out.println("lib name1:" + librarianName);
					Stage currentStage = (Stage) passwordField.getScene().getWindow();
					currentStage.close();

					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
					Parent root = loader.load();
					LibrarianDashboardController librarianDashboard = loader.getController();
					librarianDashboard.setLibrarianName(usernameField.getText());

					// Create a new stage for the dashboard
					Stage dashboardStage = new Stage();
					Scene scene = new Scene(root,800,500);
					dashboardStage.setScene(scene);
					dashboardStage.setTitle("Librarian Dashboard");

					// Load and set the application icon
					loadIcon();
					if (icon != null) {
						dashboardStage.getIcons().add(icon);
					}

					dashboardStage.show();

				} catch (IOException e) {
					errorLabel.setText("Error opening screen");
					e.printStackTrace();
				}
			} else if (message.equals("Login Failed")) {
				// Clear the input fields
				usernameField.clear();
				passwordField.clear();

				// Set the error message
				errorLabel.setText("Invalid username or password"); // Only one message is needed

				// Set the text color to red
				errorLabel.setTextFill(Color.RED);

				// Optionally, add error styling to the input fields
				usernameField.getStyleClass().add("error"); // Assuming you have a CSS class for error styling
				passwordField.getStyleClass().add("error");
			} else {
				errorLabel.setText(message);
				errorLabel.setTextFill(Color.RED);

			}
		});
	}

	/**
	 * Handles password reset navigation.
	 * 
	 * Actions: - Load reset password FXML - Configure new stage - Apply application
	 * icon
	 */
	@FXML
	private void handleResetPassword() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ResetPasswordLibrarian.fxml"));
			Parent root = loader.load();

			Stage resetPasswordStage = new Stage();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

			resetPasswordStage.setScene(scene);
			resetPasswordStage.setTitle("Reset Password");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				resetPasswordStage.getIcons().add(icon);
			}

			resetPasswordStage.show();

			Stage currentStage = (Stage) errorLabel.getScene().getWindow();
			currentStage.close();

		} catch (IOException e) {
			errorLabel.setText("Error opening reset password screen");
			e.printStackTrace();
		}
	}

	/**
	 * Handles return to user selection screen.
	 * 
	 * Actions: - Close current window - Load user selection FXML - Configure new
	 * stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
			Parent root = loader.load();

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Home");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			}

			home.show();

		} catch (IOException e) {
			errorLabel.setText("Error opening reset password screen");
			e.printStackTrace();
		}
	}

	/**
	 * Handles librarian login request.
	 * 
	 * Validation steps: - Check for empty username/password - Apply error styling -
	 * Create login request - Send authentication request to server
	 */
	@FXML
	private void handleLogin() {
		String username = usernameField.getText();
		String password = passwordField.getText();

		if (username.trim().isEmpty() || password.trim().isEmpty())
		{
			// Set the error message
			errorLabel.setText("Please fill all fields");
			errorLabel.setTextFill(Color.RED); // Set text color to red

			// Highlight empty fields
			if (username.trim().isEmpty()) {
				usernameField.getStyleClass().add("error"); // Add error styling
			} else {
				usernameField.getStyleClass().removeAll("error"); // Remove error styling if not empty
			}

			if (password.trim().isEmpty()) {
				passwordField.getStyleClass().add("error"); // Add error styling
			} else {
				passwordField.getStyleClass().removeAll("error"); // Remove error styling if not empty
			}

			return; // Stop further execution
		} else
		{
			// Clear any previous error styling if both fields are filled
			usernameField.getStyleClass().removeAll("error");
			passwordField.getStyleClass().removeAll("error");
		}

		Librarian librarian = new Librarian(0, "", "", username, password, "");
		ResponseWrapper loginRequest = new ResponseWrapper("LIBRARIAN_LOGIN", librarian);
		ClientUI.chat.accept(loginRequest);
	}

}