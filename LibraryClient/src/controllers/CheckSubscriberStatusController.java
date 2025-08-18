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
import logic.Book;
import logic.ResponseWrapper;
import logic.Subscriber;

/**
 * Controller for checking subscriber status during book borrowing process.
 * 
 * Responsibilities: - Validate subscriber ID - Check subscriber account status
 * - Manage navigation between screens - Handle librarian context
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class CheckSubscriberStatusController {

	@FXML
	private TextField subscriberIdField;

	@FXML
	private Label errorLabel;

	private static CheckSubscriberStatusController instance;
	private static Image icon; // Static variable to hold the application icon
	private String source; // Field to track the source of the request

	public void setSource(String source) {
		this.source = source;
	}

	public CheckSubscriberStatusController() {
		instance = this;
	}

	private int BookId;

	public void setSubscriberBookId(int id) {
		this.BookId = id;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	public static CheckSubscriberStatusController getInstance() {
		return instance;
	}

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	// Method to load the application icon
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = CheckSubscriberStatusController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Updates UI based on subscriber status response.
	 * 
	 * Handles scenarios: - Subscriber exists and not frozen - Subscriber account is
	 * frozen - Subscriber does not exist
	 * 
	 * Actions: - Navigate to confirm borrow screen - Apply error styling - Display
	 * appropriate error messages
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("subscriber exist and not frozen")) {
				try {
					Stage currentStage = (Stage) subscriberIdField.getScene().getWindow();
					currentStage.close();
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ConfirmBorrow.fxml"));
					Parent root = loader.load();

					// Get the controller of the new screen
					ConfirmBorrowController confirmBorrowController = loader.getController();
					confirmBorrowController.setSource(source);

					confirmBorrowController.setSubscriberIDandBookId(BookId,
							Integer.parseInt(subscriberIdField.getText()));
					confirmBorrowController.setLibrarianName(librarianName);
					Stage home = new Stage();
					Scene scene = new Scene(root, 540, 550);
					scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

					home.setScene(scene);
					home.setTitle("Confirm Borrow");

					// Load and set the application icon
					loadIcon();
					if (icon != null) {
						home.getIcons().add(icon);
					}

					home.show();

				} catch (IOException e) {
					errorLabel.setText("Error opening screen");
					e.printStackTrace();
				}
			} else if (message.equals("subscriber is frozen")) {
				// Apply error styling to the subscriberIdField
				subscriberIdField.getStyleClass().add("error");

				// Clear the subscriberIdField
				subscriberIdField.clear();

				// Set the error message and color to red
				if (errorLabel != null) {
					errorLabel.setText("Subscriber is frozen");
					errorLabel.setTextFill(Color.RED);
				} else {
					System.out.println("Error: errorLabel is null");
				}
			} else if (message.equals("subscriber is not exist")) {
				// Apply error styling to the subscriberIdField
				subscriberIdField.getStyleClass().add("error");

				// Clear the subscriberIdField
				subscriberIdField.clear();

				// Set the error message and color to red
				errorLabel.setText("Subscriber does not exist");
				errorLabel.setTextFill(Color.RED);
			}
		});
	}

	/**
	 * Handles return to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard - Preserve
	 * librarian context
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
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

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			}

			home.show();

		} catch (IOException e) {
			errorLabel.setText("Error opening screen");
			e.printStackTrace();
		}
	}

	/**
	 * Handles subscriber status check request.
	 * 
	 * Validation steps: - Check for empty input - Validate 9-digit subscriber ID -
	 * Create subscriber object - Send status check request to server
	 * 
	 * @param event ActionEvent triggering status check
	 */
	@FXML
	private void handleCheckStatus(ActionEvent event) {
		String subscriberId = subscriberIdField.getText();
		System.out.println("Sorce:" + source);
		if (subscriberId.trim().isEmpty()) {
			// Apply error styling to the subscriberIdField
			subscriberIdField.getStyleClass().add("error");

			// Set the error message and color to red
			errorLabel.setText("Please fill the field");
			errorLabel.setTextFill(Color.RED);

			return; // Stop further execution
		} else {
			// Clear any previous error styling if the field is not empty
			subscriberIdField.getStyleClass().removeAll("error");

			// Validate if subscriberId is a valid 9-digit number
			if (!subscriberId.matches("\\d{9}")) {
				// If validation fails, highlight the field and display an error message
				subscriberIdField.getStyleClass().add("error");
				errorLabel.setText("Subscriber ID must be exactly 9 digits");
				errorLabel.setTextFill(Color.RED);
				return; // Stop further execution
			}

			// If validation passes, proceed with further logic
			errorLabel.setText("Subscriber ID is valid");
			errorLabel.setTextFill(Color.GREEN); // Set text color to green
		}

		// Create a Subscriber object
		Subscriber subscriber = new Subscriber(Integer.parseInt(subscriberId), // subscriber_id
				"", // subscriber_name
				"", // username
				"", // subscriber_email
				"", // subscriber_phone_number
				"", // password
				0 // detailed_subscription_history
		);

		// Send request to server
		System.out.println("nhla cliien1");
		ClientUI.chat.accept(new ResponseWrapper("Borrow", subscriber));
		System.out.println("nhla cliien2");
	}
}