package controllers;

import java.io.IOException;
import java.net.URL;
import java.io.File;

import client.ClientUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Book;
import logic.ResponseWrapper;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class BarcodeBorrowBookController {

	@FXML
	private TextField barcodeTextField;

	@FXML
	private Label errorLabel;

	private static BarcodeBorrowBookController instance;
	private static Image icon;
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	public BarcodeBorrowBookController() {
		instance = this;
	}

	public static BarcodeBorrowBookController getInstance() {
		return instance;
	}

	/**
	 * Loads application icon from resources directory.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging if icon not
	 * found
	 * 
	 * @implNote Uses class resource loading mechanism
	 * @throws IllegalArgumentException If icon URL is invalid
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = BarcodeBorrowBookController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Handles book borrowing via barcode input.
	 * 
	 * Validation steps: - Check barcode input - Send book search request to server
	 * - Manage UI error states
	 * 
	 * @param event ActionEvent triggering book borrow process
	 */
	@FXML
	private void handleBorrowBook(ActionEvent event) {
		String barcode = barcodeTextField.getText().trim();

		if (barcode.trim().isEmpty()) {
			// Set the error message and color to red
			errorLabel.setText("Please enter a barcode");
			errorLabel.setTextFill(Color.RED);
			errorLabel.setVisible(true);

			// Highlight the barcode field
			barcodeTextField.getStyleClass().add("error");

			return; // Stop further execution
		} else {
			// Clear any previous error styling if the field is not empty
			barcodeTextField.getStyleClass().removeAll("error");
			errorLabel.setVisible(false); // Hide the error label
		}

		try {

			Book book = new Book(barcode, "", null, "", "", "", 0, 0, null, "", 0);
			ClientUI.chat.accept(new ResponseWrapper("SEARCH_BOOK_BY_BARCODE", book));
			errorLabel.setVisible(false);
		} catch (NumberFormatException e) {
			barcodeTextField.setStyle(
					"-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: #e8d3cf; -fx-font-size: 15px; -fx-font-style: italic; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
			errorLabel.setText("Invalid barcode format. Please enter a valid number.");
			barcodeTextField.getStyleClass().add("error");
			errorLabel.setVisible(true);
		} catch (Exception e) {
			errorLabel.setText("An error occurred. Please try again.");
			barcodeTextField.getStyleClass().add("error");
			errorLabel.setVisible(true);
			e.printStackTrace();
		}
	}

	/**
	 * Updates UI based on server response for book availability.
	 * 
	 * Handles scenarios: - Book available - No book copies - Book not in library
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.startsWith("book is available & there are copies")) {
				try {
					// Highlight the barcode field and display an error message
					barcodeTextField.getStyleClass().add("error");
					errorLabel.setText("The book book is available & there are copies");
					errorLabel.setTextFill(Color.GREEN);
					errorLabel.setVisible(true);

					// Close the current window
					Stage currentStage = (Stage) barcodeTextField.getScene().getWindow();
					currentStage.close();

					// Load the CheckSubscriberStatus screen
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/gui_librarian/CheckSubscriberStatus.fxml"));

					CheckSubscriberStatusController checkSubscriberStatusController = new CheckSubscriberStatusController();
					int bookId = Integer.parseInt(barcodeTextField.getText());
					System.out.println("Book Id: " + bookId);
					checkSubscriberStatusController.setSubscriberBookId(bookId);
					loader.setController(checkSubscriberStatusController);
					checkSubscriberStatusController.setLibrarianName(librarianName);
					checkSubscriberStatusController.setSource("BARCODE");

					Parent root = loader.load();
					Stage home = new Stage();
					Scene scene = new Scene(root, 540, 450);
					scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

					home.setScene(scene);
					home.setTitle("Subscriber Status");

					// Load and set the window icon
					loadIcon();
					if (icon != null) {
						home.getIcons().add(icon);
					}

					home.show();
				} catch (IOException e) {
					// Handle screen loading errors
					errorLabel.setText("Error opening screen");
					errorLabel.setTextFill(Color.RED);
					errorLabel.setVisible(true);
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// Handle invalid barcode format errors
					barcodeTextField.getStyleClass().add("error");
					errorLabel.setText("Invalid barcode format. Please enter a valid number.");
					errorLabel.setTextFill(Color.RED);
					errorLabel.setVisible(true);
				}
			} else if (message.equals("There are no copies of the book")) {
				// Handle case where no copies of the book are available
				barcodeTextField.clear();
				barcodeTextField.getStyleClass().add("error");
				errorLabel.setText("There are no copies of the book");
				errorLabel.setTextFill(Color.RED);
				errorLabel.setVisible(true);
			} else if (message.equals("The book doesn't even exist in our library!")) {
				// Handle case where the book does not exist in the library
				barcodeTextField.clear();
				barcodeTextField.getStyleClass().add("error");
				errorLabel.setText("The book doesn't even exist in our library!");
				errorLabel.setTextFill(Color.RED);
				errorLabel.setVisible(true);
			} else {
				// Handle unexpected server responses
				errorLabel.setText("Unexpected response from the server: " + message);
				errorLabel.setTextFill(Color.RED);
				errorLabel.setVisible(true);
			}

		});
	}

	/**
	 * Navigates back to borrow method selection screen.
	 * 
	 * Actions: - Close current window - Load borrow method selection FXML - Set
	 * librarian name - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleReturn(ActionEvent event) {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/BorrowMethodSelection.fxml"));
			Parent root = loader.load();
			BorrowMethodSelectionController BorrowMethodSelectionController = loader.getController();
			BorrowMethodSelectionController.setLibrarianName(librarianName);
			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Select Borrow Method");

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
