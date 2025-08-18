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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.ResponseWrapper;
import logic.Subscriber;

/**
 * Controller for entering and validating subscriber ID in library management
 * system.
 * 
 * Responsibilities: - Manage subscriber ID input validation - Handle server
 * communication for subscriber verification - Navigate between screens - Manage
 * librarian context
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class EnterSubscriberIDFormController {
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	@FXML
	private TextField subscriberIdField;

	@FXML
	private Label errorLabel;

	private static EnterSubscriberIDFormController instance;
	private static Image icon; // Static variable to hold the application icon
	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public EnterSubscriberIDFormController() {
		instance = this;
	}

	public static EnterSubscriberIDFormController getInstance() {
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
			URL iconUrl = EnterSubscriberIDFormController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Updates UI based on server response for subscriber verification.
	 * 
	 * Handles scenarios: - Subscriber ID exists - Subscriber ID not found
	 * 
	 * Actions: - Navigate to reader card view - Display error messages
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("SubscriberIdExists")) {
				try {
					Subscriber subscriberRequestFethDetails = new Subscriber(
							Integer.parseInt(subscriberIdField.getText()), "", "", "", "", "", 2);
					ClientUI.chat.accept(subscriberRequestFethDetails);
					Stage currentStage = (Stage) subscriberIdField.getScene().getWindow();
					currentStage.close();

					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ReaderCardView.fxml"));
					Parent root = loader.load();
					System.out.println("lib name3:" + librarianName);

					ReaderCardViewController readerCardController = loader.getController();
					readerCardController.loadSubscriber(ChatClient.s1);
					readerCardController.setSubscriberId(subscriberIdField.getText());
					readerCardController.setLibrarianName(librarianName);

					Stage readerCardStage = new Stage();
					Scene scene = new Scene(root);
					readerCardStage.setScene(scene);
					readerCardStage.setTitle("Reader Card");

					// Load and set the application icon
					loadIcon();
					if (icon != null) {
						readerCardStage.getIcons().add(icon);
					}

					readerCardStage.show();
				} catch (IOException e) {
					errorLabel.setText("Error opening Reader Card screen");
					e.printStackTrace();
				}
			} else if (message.equals("SubscriberIdNotExists")) {
				subscriberIdField.clear();
				errorLabel.setText("Subscriber not found");
				errorLabel.setTextFill(Color.RED);

			}
		});
	}

	/**
	 * Handles subscriber ID submission and validation.
	 * 
	 * Validation steps: - Check for empty input - Validate 9-digit subscriber ID
	 * format - Send subscriber verification request to server
	 * 
	 * Features: - Dynamic error styling - Comprehensive input validation - Error
	 * message management
	 * 
	 * @param event ActionEvent triggering submission
	 */
	@FXML
	private void handleSubmit(ActionEvent event) {
		String subscriberId = subscriberIdField.getText();
		if (subscriberId.trim().isEmpty()) {
			// Set the error message and color to red
			errorLabel.setText("Please enter a Subscriber ID");
			errorLabel.setTextFill(Color.RED);

			// Highlight the subscriberId field
			subscriberIdField.getStyleClass().add("error");

			return; // Stop further execution
		} else {
			// Clear any previous error styling if the field is not empty
			subscriberIdField.getStyleClass().removeAll("error");
		}

		// Check if subscriberId is exactly 9 digits
		if (!subscriberId.matches("\\d{9}")) {
			// Set the error message and color to red
			errorLabel.setText("Subscriber ID must be exactly 9 digits.");
			errorLabel.setTextFill(Color.RED);

			// Highlight the subscriberId field
			subscriberIdField.getStyleClass().add("error");

			return; // Stop further execution
		}

		try {
			// Attempt to parse the subscriberId as an integer
			int id = Integer.parseInt(subscriberId);

			// Create a Subscriber object and send it to the server
			Subscriber subscriber = new Subscriber(id, "", "", "", "", "", 0);
			ClientUI.chat.accept(new ResponseWrapper("CheckSubscriber", subscriber));

			// Clear any previous error message
			errorLabel.setText("");
		} catch (NumberFormatException e) {
			// Set the error message and color to red
			errorLabel.setText("Invalid Subscriber ID. Please enter a number.");
			errorLabel.setTextFill(Color.RED);

			// Highlight the subscriberId field
			subscriberIdField.getStyleClass().add("error");
		}
	}

	/**
	 * Handles return to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard - Preserve
	 * librarian context
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleReturn(ActionEvent event) {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
			Parent root = loader.load();

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Librarian Dashboard");
			LibrarianDashboardController librarianDashboard = loader.getController();
			librarianDashboard.setLibrarianName(librarianName);
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
}