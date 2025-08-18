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
import logic.ReturnBook;
/**
 * Controller for book return process in library management system.
 * 
 * Responsibilities:
 * - Manage book return request
 * - Validate input fields
 * - Handle server communication
 * - Provide user feedback
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ReturnBookController {

	@FXML
	private TextField SubscriberIdField;

	@FXML
	private TextField BookIdField;

	@FXML
	private Label errorLabel;

	private static ReturnBookController instance;
	private static Image icon; // Static variable to hold the application icon

	public ReturnBookController() {
		instance = this;
	}

	public static ReturnBookController getReturnBookController() {
		return instance;
	}
    private String librarianName; // Field to store librarian's name

    public void setLibrarianName(String name) {
        this.librarianName = name; // Method to set librarian's name
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
			URL iconUrl = ReturnBookController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}
	/**
     * Handles book return request submission.
     * 
     * Validation steps:
     * - Check subscriber ID (9 digits)
     * - Validate book ID (numeric)
     * - Ensure all fields are filled
     * - Send return request to server
     */
	@FXML
	private void handleReturnBook() {
		String subscriberId = SubscriberIdField.getText();
		String bookId = BookIdField.getText();

		// Clear any previous error styling and messages
		SubscriberIdField.getStyleClass().removeAll("error");
		BookIdField.getStyleClass().removeAll("error");
		errorLabel.setText("");

		// Flag to track if any error is found
		boolean hasError = false;

		// Check if bookId is empty
		if (bookId.trim().isEmpty()) {
		    BookIdField.getStyleClass().add("error");
		    hasError = true;
		}

		// Check if subscriberId is empty
		if (subscriberId.trim().isEmpty()) {
		    SubscriberIdField.getStyleClass().add("error");
		    hasError = true;
		}

		// Check if subscriberId is exactly 9 digits
		if (subscriberId.trim().length() != 9) {
		    if (!SubscriberIdField.getStyleClass().contains("error")) { // Prevent adding duplicate "error"
		        SubscriberIdField.getStyleClass().add("error");
		    }
		    hasError = true;
		}

		// Check if bookId contains only digits
		if (!bookId.matches("\\d+")) {
		    BookIdField.getStyleClass().add("error");
		    hasError = true;
		}

		// If any error is found, set the error message and stop further execution
		if (hasError) {
		    if (bookId.trim().isEmpty() || subscriberId.trim().isEmpty()) {
		        errorLabel.setText("Please fill all required fields");
		    } else if (subscriberId.trim().length() != 9 || !bookId.matches("\\d+")) {
		        errorLabel.setText("Please check the entered details");
		    }
		    errorLabel.setTextFill(Color.RED); // Set text color to red
		    return;
		}


		ReturnBook borrowToReturn = new ReturnBook(Integer.parseInt(subscriberId), Integer.parseInt(bookId), null,
				null);
		System.out.println(borrowToReturn.toString());
		ClientUI.chat.accept(borrowToReturn);
	}
	 /**
     * Handles navigation back to librarian dashboard.
     * 
     * Actions:
     * - Close current window
     * - Load librarian dashboard FXML
     * - Preserve librarian context
     * - Apply application icon
     * 
     * @param event ActionEvent triggering navigation
     * @throws IOException If FXML loading fails
     */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
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
			errorLabel.setText("Error opening the form");
			e.printStackTrace();
		}
	}
	  /**
     * Updates UI based on server response for book return.
     * 
     * Handles scenarios:
     * - Successful book return
     * - No matching borrow record
     * - Unexpected errors
     * 
     * @param message Server response message
     */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Return Book Successfully")) {
			    // Set success message and color to green
			    errorLabel.setText("Return process done Successfully!");
			    errorLabel.setTextFill(Color.GREEN);

			    // Clear input fields
			    SubscriberIdField.clear();
			    BookIdField.clear();

			    // Clear any previous error styling
			    SubscriberIdField.getStyleClass().removeAll("error");
			    BookIdField.getStyleClass().removeAll("error");
			} else if (message.equals("Return Book Failed -> No existing borrow record matches the provided details.")) {
			    // Set error message and color to red
			    errorLabel.setText("No existing borrow record matches the provided details.");
			    errorLabel.setTextFill(Color.RED);

			    // Clear input fields
			    SubscriberIdField.clear();
			    BookIdField.clear();

			    // Highlight the fields to indicate an error
			    SubscriberIdField.getStyleClass().add("error");
			    BookIdField.getStyleClass().add("error");
			} else {
			    // Handle any other unexpected messages
			    errorLabel.setText("An unexpected error occurred: " + message);
			    errorLabel.setTextFill(Color.RED);
			}
		});
	}
}