package controllers;

import client.ClientUI;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.image.Image;
import javafx.util.Callback;
import javafx.util.Duration;
import logic.ExtendReturnDateRequest;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

/**
 * Controller for managing the extension of book return dates.
 * 
 * This class handles the process of extending the return date for borrowed books,
 * including updating book and subscriber information, displaying relevant details,
 * and managing date picker constraints.
 * 
 * Key features:
 * - Allows librarians to extend return dates for borrowed books
 * - Validates extension requests based on library policies
 * - Updates book loan information in the system
 * - Manages UI interactions for the return date extension process
 * 
 * @author [Your Name Here]
 * @version 1.0
 * @since 2025-01-27
 */
public class ExtendReturnDateController {
	private String librarianName; // Field to store librarian's name
	private static Image icon; // Static variable to hold the application icon

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	@FXML
	private Label bookIdLabel;

	@FXML
	private Label subscriberIdLabel;

	@FXML
	private DatePicker newReturnDatePicker;

	@FXML
	private Label errorLabel;

	@FXML
	private Button confirmButton;

	private int bookId;
	private String subscriberId;
	private LocalDate originalReturnDate; // Store the original return date

	private static ExtendReturnDateController instance;
	  /**
     * Retrieves the singleton instance of ExtendReturnDateController.
     *
     * @return The singleton instance of ExtendReturnDateController.
     */
	public static ExtendReturnDateController getInstance() {
		return instance;
	}

	private Stage parentStage; // Variable to hold the parent stage (LoansBookReaderCard)
	private String bookName;
	private String author;

	/**
	 * Sets the parent stage for the return date extension controller.
	 * 
	 * Allows tracking and managing the parent window context.
	 * 
	 * @param stage The parent Stage object to be associated with the controller
	 */
	public void setParentStage(Stage stage) {
		this.parentStage = stage;
	}

	/**
	 * Loads application icon from resources directory.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = ExtendReturnDateController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Initializes the controller after FXML loading.
	 * 
	 * Actions: - Sets singleton instance - Configures DatePicker with custom cell
	 * factory
	 */
	@FXML
    public void initialize() {
        instance = this;

        // Ensure typing is disabled in the DatePicker
        newReturnDatePicker.getEditor().setEditable(false);

        // Add a listener to enforce non-editable behavior
        newReturnDatePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                newReturnDatePicker.getEditor().setEditable(false);
            }
        });

        // Set the day cell factory for the DatePicker
        newReturnDatePicker.setDayCellFactory(createDayCellFactory());
    }

	/**
	 * Updates error label with server response messages.
	 * 
	 * Handles various scenarios: - Successful return date extension - Failed
	 * extension attempts - Account status issues - Borrowing constraints
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Return date has been successfully extended.")) {
				System.out.println("succed");
				errorLabel.setText("Return date has been successfully extended.");
				errorLabel.setTextFill(Color.GREEN); // Set text color to green for success

			} else if (message.equals("Failed to extend return date.")) {
				errorLabel.setText("Failed to extend return date. Please try again.");
				errorLabel.setTextFill(Color.RED); // Set text color to red for failure
			} else if (message.equals("Subscriber is frozen")) {
				errorLabel.setText("Cannot extend loan - your account is frozen.");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("Book is not currently borrowed")) {
				errorLabel.setText("This book is not borrowed by you.");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("Extensions are allowed only if it’s within 7 days before the return date")) {
				errorLabel.setText("Extensions are allowed only if it’s within 7 days before the return date");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("Extension denied - book has pending orders")) {
				errorLabel.setText("Cannot extend - book has pending orders.");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("Extension request failed")) {
				errorLabel.setText("Extension request failed - please try again.");
				errorLabel.setTextFill(Color.RED);
			} else {
				errorLabel.setText(message); // Handle any unexpected messages
				errorLabel.setTextFill(Color.RED);
			}
		});
	}


	/**
	 * Sets book and subscriber details for return date extension.
	 * 
	 * Actions: - Update book and subscriber information - Display book and
	 * subscriber IDs - Configure date picker constraints
	 * 
	 * @param bookId             Unique book identifier
	 * @param subscriberId       Subscriber's unique identifier
	 * @param originalReturnDate Initial book return date
	 * @param bookName           Name of the book
	 * @param author             Book's author
	 */
	public void setBookDetails(int bookId, String subscriberId, LocalDate originalReturnDate, String bookName,
			String author) {
		this.bookId = bookId;
		this.subscriberId = subscriberId;
		this.originalReturnDate = originalReturnDate;
		this.bookName = bookName; // Set the book name
		this.author = author; // Set the author

		// Display book and subscriber details
		bookIdLabel.setText("Book ID: " + bookId);
		subscriberIdLabel.setText("Subscriber ID: " + subscriberId);

		// Print the original return date and the new return date (1 day after original)
		System.out.println("Original Return Date: " + originalReturnDate);
		System.out.println("New Return Date (1 day after original): " + originalReturnDate.plusDays(1));

		// Set DatePicker constraints based on the original return date
		newReturnDatePicker.setDayCellFactory(createDayCellFactory());
		newReturnDatePicker.setValue(originalReturnDate.plusDays(1)); // Default to 1 day after the original date
	}

	private Callback<DatePicker, DateCell> createDayCellFactory() {
		return datePicker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);

				if (date.isBefore(originalReturnDate.plusDays(1)) || date.isAfter(originalReturnDate.plusDays(14))) {
					setDisable(true); // Disable dates outside the valid range
					setStyle("-fx-background-color: #d3d3d3;"); // Optional: Gray out invalid dates
				}
			}
		};
	}

	/**
	 * Handles confirmation of book return date extension.
	 * 
	 * Actions: - Validate selected return date - Create extension request object -
	 * Send request to server - Manage UI transition after request
	 * 
	 * Validation Constraints: - Return date must be 1-14 days after original return
	 * date
	 * 
	 * @throws Exception If request processing fails
	 */
	@FXML
	private void handleConfirm() {
		LocalDate selectedDate = newReturnDatePicker.getValue();

		// Validate the selected date
		if (!isValidReturnDate(selectedDate)) {
			updateMessage("Please select a valid return date (1-14 days after the original return date).");
			return;
		}

		System.out.println("Extending return date for Book ID: " + bookId + ", Subscriber ID: " + subscriberId
				+ ", New Return Date: " + selectedDate);

		try {
			// Create a request object with all required fields
			ExtendReturnDateRequest request = new ExtendReturnDateRequest(bookId, // bookId
					Integer.parseInt(subscriberId), // subscriberId
					java.sql.Date.valueOf(selectedDate), // newReturnDate
					librarianName, // librarianName
					bookName, // bookName
					author, // author
					java.sql.Date.valueOf(originalReturnDate) // originalReturnDate
			);
			// Send the request to the server
			ClientUI.chat.accept(request);

			// Create a pause transition for a 1 second delay
			PauseTransition pause = new PauseTransition(Duration.seconds(5));

			// Set an action to perform after the delay
			pause.setOnFinished(event -> {
				System.out.println("5 second delay is over!");
				// Close the current stage and reopen the LoansBookReaderCard GUI
				Stage currentStage = (Stage) confirmButton.getScene().getWindow();
				if (currentStage != null) {
					currentStage.close(); // Close the current stage
				}
				reopenGUI();
			});

			// Start the pause
			pause.play();

		} catch (Exception e) {
			e.printStackTrace();
			updateMessage("Error extending return date. Please try again.");
		}
	}

	/**
	 * Validates selected return date for loan extension.
	 * 
	 * Validation Criteria: - Date must not be null - Date must be at least 1 day
	 * after original return date - Date must not exceed 14 days after original
	 * return date
	 * 
	 * @param selectedDate Date selected for return extension
	 * @return Boolean indicating date validity
	 */
	private boolean isValidReturnDate(LocalDate selectedDate) {
		return selectedDate != null && !selectedDate.isBefore(originalReturnDate.plusDays(1))
				&& !selectedDate.isAfter(originalReturnDate.plusDays(14));
	}

	/**
	 * Reopens Loans Book Reader Card after return date extension.
	 * 
	 * Actions: - Close parent stage - Load LoansBookReaderCard FXML - Set
	 * subscriber and librarian context - Configure new stage - Apply application
	 * icon
	 * 
	 * @throws IOException If FXML loading fails
	 */
	private void reopenGUI() {
		System.out.println("Setting Subscriber ID: " + subscriberId);
		try {
			parentStage.close();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LoansBookReaderCard.fxml"));

			// Create a new instance of the controller
			LoansBookReaderCardController controller = new LoansBookReaderCardController();

			// Set the controller for the loader
			loader.setController(controller);

			controller.setSubscriberId(subscriberId);
			controller.setLibrarianName(librarianName);
			Parent root = loader.load();

			Stage newStage = new Stage();
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("Loans Book");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				newStage.getIcons().add(icon);
			}

			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}