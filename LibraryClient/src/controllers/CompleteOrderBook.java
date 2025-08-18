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
import logic.ResponseWrapper;

/**
 * Controller for completing book order process in library management system.
 * 
 * Responsibilities: - Validate subscriber and book IDs - Process order
 * completion request - Handle server response scenarios - Manage navigation
 * between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class CompleteOrderBook {

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	@FXML
	private TextField subscriberIdField;

	@FXML
	private TextField bookIdField;

	@FXML
	private Label errorLabel;

	private static CompleteOrderBook instance;
	private static Image icon; // Static variable to hold the application icon

	public CompleteOrderBook() {
		instance = this;
	}

	public static CompleteOrderBook getInstance() {
		return instance;
	}

	// Method to load the application icon
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = CompleteOrderBook.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Updates UI based on server response for order completion.
	 * 
	 * Handles scenarios: - Successful order status change - Book not yet returned -
	 * No order found - Subscriber/Book not found
	 * 
	 * Actions: - Navigate to confirm borrow screen - Clear input fields - Display
	 * appropriate error messages
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Order Found And Status Changed Now Borrow Book")) {
				try {
					Stage currentStage = (Stage) subscriberIdField.getScene().getWindow();
					currentStage.close();
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ConfirmBorrow.fxml"));
					Parent root = loader.load();

					// Get the controller of the new screen
					ConfirmBorrowController confirmBorrowController = loader.getController();
					confirmBorrowController.setSource("BOOK_ID");
					confirmBorrowController.isToCompleteOrder();
					confirmBorrowController.setSubscriberIDandBookId(Integer.parseInt(bookIdField.getText()),
							Integer.parseInt(subscriberIdField.getText()));
					confirmBorrowController.setLibrarianName(librarianName);
					
					Stage home = new Stage();
					Scene scene = new Scene(root, 540, 550);
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
			} else if (message.equals("The book doesn't returned yet")) {
				bookIdField.clear();
				subscriberIdField.clear();
				errorLabel.setText("The book isn't returned yet");
				errorLabel.setTextFill(Color.RED); // Set text color to red

			} else if (message.equals("No Order Was Found")) {
				bookIdField.clear();
				subscriberIdField.clear();
				errorLabel.setText("No Order Was Found");
				errorLabel.setTextFill(Color.RED); // Set text color to red

			} else if (message.equals("Subscriber Not Found For Order")) {
				bookIdField.clear();
				subscriberIdField.clear();
				errorLabel.setText("Subscriber Not Found");
				errorLabel.setTextFill(Color.RED); // Set text color to red

			} else if (message.equals("Book Not Found For Order")) {
				bookIdField.clear();
				subscriberIdField.clear();
				errorLabel.setText("Book Not Found");
				errorLabel.setTextFill(Color.RED); // Set text color to red

			}
		});
	}

	/**
	 * Handles order completion request validation and submission.
	 * 
	 * Validation steps: - Check for empty input fields - Validate subscriber ID (9
	 * digits) - Validate book ID (numeric) - Send order completion request to
	 * server
	 * 
	 * Features: - Dynamic error styling - Comprehensive input validation - Error
	 * message management
	 */
	@FXML
	private void handleCompleteOrder() {
		String subscriberId = subscriberIdField.getText();
		String bookId = bookIdField.getText();

		subscriberIdField.getStyleClass().removeAll("error");
		bookIdField.getStyleClass().removeAll("error");

		errorLabel.setText("");

		if (bookId.trim().isEmpty() || subscriberId.trim().isEmpty()) {
			// Set the error message and color to red
			errorLabel.setText("Please fill all required fields");
			errorLabel.setTextFill(Color.RED);

			// Highlight empty fields
			if (bookId.trim().isEmpty()) {
				bookIdField.getStyleClass().add("error");
			} else {
				bookIdField.getStyleClass().removeAll("error");
			}

			if (subscriberId.trim().isEmpty()) {
				subscriberIdField.getStyleClass().add("error");
			} else {
				subscriberIdField.getStyleClass().removeAll("error");
			}

			return; // Stop further execution
		}

		// Check if subscriberId is exactly 9 digits
		if (subscriberId.trim().length() != 9) {
			// Set the error message and color to red
			errorLabel.setText("Subscriber ID must be exactly 9 digits");
			errorLabel.setTextFill(Color.RED);

			// Highlight the subscriberId field
			subscriberIdField.getStyleClass().add("error");

			return; // Stop further execution
		}

		// Check if bookId contains only digits
		if (!bookId.matches("\\d+")) {
			// Set the error message and color to red
			errorLabel.setText("Book ID must contain only digits");
			errorLabel.setTextFill(Color.RED);

			// Highlight the bookId field
			bookIdField.getStyleClass().add("error");

			return; // Stop further execution
		}

		// Clear any previous error styling
		bookIdField.getStyleClass().removeAll("error");
		subscriberIdField.getStyleClass().removeAll("error");

		ClientUI.chat.accept(new ResponseWrapper("CompleteOrder", subscriberId + ", " + bookId));
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
			errorLabel.setText("Error opening screen");
			e.printStackTrace();
		}
	}
}