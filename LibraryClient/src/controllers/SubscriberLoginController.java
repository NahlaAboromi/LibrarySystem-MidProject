package controllers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;

import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.ResponseWrapper;
import logic.Subscriber;

public class SubscriberLoginController {

	@FXML
	private TextField usernameField;

	@FXML
	private TextField subscriberIDField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Label errorLabel;

	private static SubscriberLoginController instance;

	private static Image icon; // Static variable to hold the icon

	public SubscriberLoginController() {
		instance = this;
	}

	public static SubscriberLoginController getInstance() {
		return instance;
	}

	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Handles subscriber login process with comprehensive input validation
	 * 
	 * Performs multiple validation checks: - Checks for empty fields - Validates
	 * subscriber ID format (exactly 9 digits) - Manages UI error styling
	 * 
	 * Validation Flow: 1. Checks for completely empty form 2. Validates individual
	 * field combinations 3. Ensures subscriber ID is exactly 9 digits
	 * 
	 * @throws NumberFormatException if subscriber ID cannot be parsed
	 */
	@FXML
	private void handleLogin() {
		String subscriberId = subscriberIDField.getText().trim();
		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();


	    boolean hasError = false;

	    // Clear previous error styles
	    subscriberIDField.getStyleClass().removeAll("error");
	    usernameField.getStyleClass().removeAll("error");
	    passwordField.getStyleClass().removeAll("error");

	    // Check all possible combinations of empty fields
	    if (subscriberId.isEmpty() && username.isEmpty() && password.isEmpty()) {
	        errorLabel.setText("Please fill all fields");
	        subscriberIDField.getStyleClass().add("error");
	        usernameField.getStyleClass().add("error");
	        passwordField.getStyleClass().add("error");
	        hasError = true;
	    } else if (subscriberId.isEmpty() && username.isEmpty()) {
	        errorLabel.setText("Please fill Subscriber ID and Username");
	        subscriberIDField.getStyleClass().add("error");
	        usernameField.getStyleClass().add("error");
	        hasError = true;
	    } else if (subscriberId.isEmpty() && password.isEmpty()) {
	        errorLabel.setText("Please fill Subscriber ID and Password");
	        subscriberIDField.getStyleClass().add("error");
	        passwordField.getStyleClass().add("error");
	        hasError = true;
	    } else if (username.isEmpty() && password.isEmpty()) {
	        errorLabel.setText("Please fill Username and Password");
	        usernameField.getStyleClass().add("error");
	        passwordField.getStyleClass().add("error");
	        hasError = true;
	    } else if (subscriberId.isEmpty()) {
	        errorLabel.setText("Please fill Subscriber ID");
	        subscriberIDField.getStyleClass().add("error");
	        hasError = true;
	    } else if (username.isEmpty()) {
	        errorLabel.setText("Please fill Username");
	        usernameField.getStyleClass().add("error");
	        hasError = true;
	    } else if (password.isEmpty()) {
	        errorLabel.setText("Please fill Password");
	        passwordField.getStyleClass().add("error");
	        hasError = true;
	    }

	    // Check if Subscriber ID is valid when it's not empty
	    if (!subscriberId.isEmpty() && !subscriberId.matches("\\d{9}")) {
	        errorLabel.setText("Subscriber ID must consist of exactly 9 digits");
	        subscriberIDField.getStyleClass().add("error");
	        hasError = true;
	    }

	    if (hasError) {
	        errorLabel.setTextFill(Color.RED);
	        return;
	    }

	    // If we reach here, all fields are correctly filled
	    errorLabel.setText("");

		// Create a Subscriber object with the minimal details required for login
		Subscriber subscriber = new Subscriber(Integer.parseInt(subscriberId), "", // subscriber_name
				username, "", // email
				"", // phone_number
				password, 0 // detailed_subscription_history
		);
		ClientUI.chat.accept(new ResponseWrapper("subscriberLogin", subscriber));
	}

	/**
	 * Handles server response messages for subscriber login process
	 * 
	 * Manages different login scenarios: - Successful login - Failed login - Other
	 * server messages
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Subscriber Login Successful")) {
				try {
					// Close the current window
					Stage currentStage = (Stage) errorLabel.getScene().getWindow();
					currentStage.close();

					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/gui_subscriber/TheSubscribersMenu.fxml"));
					Parent root = loader.load();

					// Get the controller of the new screen
					SubscriberMenuController subscriberMenuController = loader.getController();
					subscriberMenuController.setSubscriberId(subscriberIDField.getText());

					Stage home = new Stage();
					Scene scene = new Scene(root, 500, 500);
					scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

					home.setScene(scene);
					home.setTitle("Subscriber Dashboard");

					// Load and set the icon
					loadIcon();
					if (icon != null) {
						home.getIcons().add(icon);
					} else {
						System.err.println("Failed to load application icon.");
					}

					home.show();

				} catch (IOException e) {
					errorLabel.setText("Error opening screen");
					e.printStackTrace();
				}
			} else if (message.equals("Subscriber Login Failed")) {
				subscriberIDField.clear();
				usernameField.clear();
				passwordField.clear();
		        subscriberIDField.getStyleClass().add("error");
		        usernameField.getStyleClass().add("error");
		        passwordField.getStyleClass().add("error");
				errorLabel.setText("Invalid id, username or password");
				errorLabel.setTextFill(Color.RED);
			} else {
				errorLabel.setText(message);
			}
		});
	}

	/**
	 * Handles navigation to subscriber password reset screen
	 * 
	 * Responsibilities: - Load reset password FXML - Create new stage for reset
	 * password - Apply custom stylesheet - Set application icon - Close current
	 * login window
	 * 
	 * @throws IOException if FXML loading fails
	 */
	@FXML
	private void handleSubsceiberResetPassword() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/ResetPasswordSubscriber.fxml"));
			Parent root = loader.load();

			Stage resetPasswordStage = new Stage();
			Scene scene = new Scene(root, 400, 500);
			scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

			resetPasswordStage.setScene(scene);
			resetPasswordStage.setTitle("Reset Password");

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				resetPasswordStage.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
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
	 * Handles navigation to subscriber password reset screen
	 * 
	 * Responsibilities: - Load reset password FXML - Create new stage for reset
	 * password - Apply custom stylesheet - Set application icon - Close current
	 * login window
	 * 
	 * @throws IOException if FXML loading fails
	 */
	@FXML
	private void hendleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			// Close the current window
			((Node) event.getSource()).getScene().getWindow().hide();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
			Parent root = loader.load();

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Home");

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
	 * Handles navigation to subscriber password reset screen
	 * 
	 * Responsibilities: - Load reset password FXML - Create new stage for reset
	 * password - Apply custom stylesheet - Set application icon - Close current
	 * login window
	 * 
	 * @throws IOException if FXML loading fails
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