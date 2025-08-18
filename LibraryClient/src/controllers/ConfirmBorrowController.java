package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;

import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.BorrowedBook;

/**
 * Controller for confirming book borrowing process.
 * 
 * Responsibilities: - Manage book borrowing confirmation interface - Validate
 * borrowing parameters - Handle date restrictions - Process borrowing request
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ConfirmBorrowController {
	@FXML
	private TextField Book_Id;

	@FXML
	private TextField Subscriber_Id;

	@FXML
	private Button ButtonGoToHome;
	
	@FXML
	private DatePicker Current_DatePicker;

	@FXML
	private DatePicker Return_DatePicker;

	@FXML
	private Label errorLabel;
	@FXML
	private Label bookIdLabel; // Add a Label for the Book ID/Barcode field

	private int userID;
	private int bookID;
	private boolean isToCompleteOrder = false;
	private static ConfirmBorrowController instance;
	private static Image icon; // Static variable to hold the application icon

	public void setSource(String source) {
		this.source = source;
	}

	private String source; // Field to track the source of the request

	public ConfirmBorrowController() {
		instance = this;
	}

	public static ConfirmBorrowController getInstance() {
		return instance;
	}

	public void isToCompleteOrder() {
		this.isToCompleteOrder = true;
	}
	
	
	
	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = ConfirmBorrowController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Sets book and subscriber IDs for borrowing process.
	 * 
	 * Actions: - Update bookID and userID fields - Log source of request - Trigger
	 * display data method
	 * 
	 * @param bookId Unique identifier for the book
	 * @param userId Unique identifier for the subscriber
	 */
	public void setSubscriberIDandBookId(int bookId, int userId) {
		this.bookID = bookId;
		this.userID = userId;
		System.out.println("Sorce2" + source);
		displayData();
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	/**
	 * Displays initial borrowing data and sets UI constraints.
	 * 
	 * Actions: - Populate book and subscriber IDs - Set current and return dates -
	 * Apply date selection restrictions - Configure label based on source
	 */
	private void displayData() {
		System.out.println("isToCompleteOrder: "+isToCompleteOrder );
		if(isToCompleteOrder)
		{
			ButtonGoToHome.setVisible(false);
		}
		if (source.equals("BOOK_ID")) {
			bookIdLabel.setText("Book ID:"); // Change the label text to "Book ID"
		} else if (source.equals("BARCODE")) {
			bookIdLabel.setText("Barcode:"); // Change the label text to "Barcode"
		} else {
			bookIdLabel.setText("Book ID:"); // Change the label text to "Book ID"

		}

		// Populate text fields and date pickers
		Book_Id.setText(String.valueOf(bookID));
		Subscriber_Id.setText(String.valueOf(userID));
		// Disable text fields for IDs
		Book_Id.setEditable(false);
		Subscriber_Id.setEditable(false);

		// Set current date
		LocalDate currentDate = LocalDate.now();
		Current_DatePicker.setValue(currentDate);
		Current_DatePicker.setDisable(true); // Completely disable the Current_DatePicker

		// Restrict the Return_DatePicker to allow a maximum of 2 weeks from the current
		// date
		LocalDate maxReturnDate = currentDate.plusWeeks(2);
		Return_DatePicker.setValue(maxReturnDate);

		Return_DatePicker.setDayCellFactory(datePicker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);

				// Disable dates beyond 2 weeks from the current date
				if (date.isAfter(maxReturnDate) || date.isBefore(currentDate.plusDays(1))) {
					setDisable(true);
					setStyle("-fx-background-color: #ffc0cb;"); // Optional: Highlight invalid dates
				}
			}
		});
		// Disable typing in the Return_DatePicker editor
	    Return_DatePicker.getEditor().setEditable(false);

	    // Prevent typing by consuming key events
	    Return_DatePicker.getEditor().addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> event.consume());
	}

	/**
	 * Handles book borrowing confirmation request.
	 * 
	 * Validation steps: - Parse book and subscriber IDs - Convert dates to SQL
	 * format - Create BorrowedBook object - Send borrowing request to server
	 * 
	 * @param event ActionEvent triggering confirmation
	 */
	@FXML
	private void handleConfirm(ActionEvent event) {
		try {
			int bookId = Integer.parseInt(Book_Id.getText().trim());
			int subscriberId = Integer.parseInt(Subscriber_Id.getText().trim());

			LocalDate currentDate = Current_DatePicker.getValue();
			LocalDate returnDate = Return_DatePicker.getValue();

			// Convert LocalDate to SQL Date
			Date sqlCurrentDate = Date.valueOf(currentDate);
			Date sqlReturnDate = Date.valueOf(returnDate);

			BorrowedBook borrowedBook = new BorrowedBook(subscriberId, bookId, source, sqlCurrentDate, sqlReturnDate);
			System.out.println("ConfirmBokkID:" + bookId);
			ClientUI.chat.accept(borrowedBook);

			errorLabel.setText("Borrow process completed successfully!");
		} catch (NumberFormatException e) {
			errorLabel.setText("Invalid input data. Please check your fields.");
			errorLabel.setTextFill(Color.RED);
		} catch (Exception e) {
			errorLabel.setText("Error processing borrow request.");
			errorLabel.setTextFill(Color.RED);
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation back to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard FXML - Set
	 * librarian name - Configure stage with: - Scene - Title - Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) {
		try {
			// Close the current window
			((Node) event.getSource()).getScene().getWindow().hide();

			// Load the Librarian Dashboard
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
			Parent root = loader.load();
			LibrarianDashboardController librarianDashboard = loader.getController();
			librarianDashboard.setLibrarianName(librarianName);
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Librarian Dashboard");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				stage.getIcons().add(icon);
			}

			stage.show();
		} catch (IOException e) {
			errorLabel.setText("Error navigating to home.");
			e.printStackTrace();
		}
	}

	/**
	 * Updates UI based on server response for borrowing process.
	 * 
	 * Handles scenarios: - Successful borrowing - Book already borrowed by
	 * subscriber
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Borrow process completed successfully")) {

				// Set success message and color to green
				errorLabel.setText("Borrow process completed successfully");
				errorLabel.setTextFill(Color.GREEN);
				ButtonGoToHome.setVisible(true);

			} else if (message
					.equals("This book is already borrowed by this subscriber->Error processing borrow request")) {
				// Clear input fields

				Return_DatePicker.setValue(null); // Clear only the return date
				ButtonGoToHome.setVisible(true);

				// Set error message and color to red
				errorLabel.setText("This book is already borrowed by this subscriber");
				errorLabel.setTextFill(Color.RED);

			}
		});
	}
}