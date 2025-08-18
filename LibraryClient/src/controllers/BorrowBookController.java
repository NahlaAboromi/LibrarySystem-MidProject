package controllers;

import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.io.File;

import client.ChatClient;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.Book;
import logic.Librarian;
import logic.ResponseWrapper;
import logic.Subscriber;

/**
 * Controller for book borrowing process in library management system.
 * 
 * Responsibilities: - Manage book search and borrow input fields - Validate
 * book search parameters - Handle server communication for book availability -
 * Manage UI feedback and navigation
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class BorrowBookController {

	@FXML
	private TextField bookIdField;
	@FXML
	private TextField BookNameField;
	@FXML
	private TextField BookTopic;
	@FXML
	private TextField book_author;
	@FXML
	private Label errorLabel;

	private static BorrowBookController instance;
	private static Image icon;

	public BorrowBookController() {
		instance = this;
	}

	public static BorrowBookController getInstance() {
		return instance;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	/**
	 * Loads application icon from resources directory.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging if icon not
	 * found
	 */
	private static void loadIcon() {
		if (icon == null) {
			// Adjust the path to point to the resources directory
			URL iconUrl = BorrowBookController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Updates UI based on server response for book availability.
	 * 
	 * Handles scenarios: - Book available with copies - No book copies - Book not
	 * in library
	 * 
	 * Actions: - Clear input fields on error - Apply error styling - Navigate to
	 * subscriber status screen if book available
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("book is available & there are copies")) {
				try {
					Stage currentStage = (Stage) bookIdField.getScene().getWindow();
					currentStage.close();
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/gui_librarian/CheckSubscriberStatus.fxml"));

					CheckSubscriberStatusController checkSubscriberStatusController = new CheckSubscriberStatusController();
					checkSubscriberStatusController.setSubscriberBookId(Integer.parseInt(bookIdField.getText()));
					loader.setController(checkSubscriberStatusController);

					Parent root = loader.load();

					CheckSubscriberStatusController controller = loader.getController();
					System.out.println("Setting Subscriber ID: " + Integer.parseInt(bookIdField.getText()));
					controller.setSubscriberBookId(Integer.parseInt(bookIdField.getText()));
					controller.setLibrarianName(librarianName);
					controller.setSource("BOOK_ID");

					Stage home = new Stage();
					Scene scene = new Scene(root, 540, 450);
					scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

					home.setScene(scene);
					home.setTitle("Subscriber Status");

					loadIcon();
					if (icon != null) {
						home.getIcons().add(icon);
					}

					home.show();

				} catch (IOException e) {
					errorLabel.setText("Error opening screen");
					e.printStackTrace();
				}
			} else if (message.equals("There are no copies of the book")) {
				// Clear all input fields
				bookIdField.clear();
				BookNameField.clear();
				BookTopic.clear();
				book_author.clear();

				// Set the error message and color to red
				errorLabel.setText("There are no copies of the book");
				errorLabel.setTextFill(Color.RED);

				// Highlight the relevant fields
				bookIdField.getStyleClass().add("error");
				BookNameField.getStyleClass().add("error");
				BookTopic.getStyleClass().add("error");
				book_author.getStyleClass().add("error");
			} else if (message.equals("The book doesn't even exist in our library!")) {
				// Clear all input fields
				bookIdField.clear();
				BookNameField.clear();
				BookTopic.clear();
				book_author.clear();

				// Set the error message and color to red
				errorLabel.setText("The book doesn't even exist in our library!");
				errorLabel.setTextFill(Color.RED);

				// Highlight the relevant fields
				bookIdField.getStyleClass().add("error");
				BookNameField.getStyleClass().add("error");
				BookTopic.getStyleClass().add("error");
				book_author.getStyleClass().add("error");
			}
		});
	}

	/**
	 * Handles book search and validation process.
	 * 
	 * Validation steps: - Check book ID (integer) - Validate book name - Validate
	 * book topic - Validate book author - Send book availability request to server
	 * 
	 * Features: - Dynamic error styling - Comprehensive input validation - Error
	 * message management
	 */
	@FXML
	private void handleSearchBook() {
		String bookId = bookIdField.getText();
		String BookName = BookNameField.getText();
		String Book_Topic = BookTopic.getText();
		String bookauthor = book_author.getText();

		// Flag to track if any error is found
		boolean hasError = false;

		// Check if bookId is empty
		if (bookId.trim().isEmpty()) {
			bookIdField.getStyleClass().add("error");
			hasError = true;
		} else {
			// Check if bookId is a valid integer
			try {
				int id = Integer.parseInt(bookId); // Try to parse bookId as an integer
				bookIdField.getStyleClass().removeAll("error"); // Remove error styling if valid
			} catch (NumberFormatException e) {
				// If parsing fails, bookId is not a valid integer
				bookIdField.getStyleClass().add("error");
				hasError = true;
			}
		}

		// Check if BookName is empty
		if (BookName.trim().isEmpty()) {
			BookNameField.getStyleClass().add("error");
			hasError = true;
		} else {
			BookNameField.getStyleClass().removeAll("error");
		}

		// Check if Book_Topic is empty
		if (Book_Topic.trim().isEmpty()) {
			BookTopic.getStyleClass().add("error");
			hasError = true;
		} else {
			BookTopic.getStyleClass().removeAll("error");
		}

		// Check if bookauthor is empty
		if (bookauthor.trim().isEmpty()) {
			book_author.getStyleClass().add("error");
			hasError = true;
		} else {
			book_author.getStyleClass().removeAll("error");
		}

		// If any error is found, set the error message and stop further execution
		if (hasError) {
			if (bookId.trim().isEmpty() || BookName.trim().isEmpty() || Book_Topic.trim().isEmpty()
					|| bookauthor.trim().isEmpty()) {
				errorLabel.setText("Please fill all fields");
			} else {
				errorLabel.setText("Book ID must be a valid integer");
			}
			errorLabel.setTextFill(Color.RED); // Set text color to red
			return;
		}

		if (bookId != null && !bookId.isEmpty()) {
			Book book = new Book(Integer.parseInt(bookId), BookName, null, Book_Topic, "", bookauthor, 0, 0, null, "",
					0);
			ClientUI.chat.accept(new ResponseWrapper("checkBookForBorrow", book));
		} else {
			System.out.println("Error: bookId is null or empty");
		}
	}

	/**
	 * Handles navigation back to borrow method selection screen.
	 * 
	 * Actions: - Closes current window - Loads Borrow Method Selection FXML - Sets
	 * librarian name in destination controller - Configures new stage with
	 * application icon
	 * 
	 * Key Features: - Dynamic stage navigation - Preserves librarian context -
	 * Applies consistent application icon
	 * 
	 * @param event JavaFX action event triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void hendleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
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
