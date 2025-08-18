package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Book;
import logic.ExtendLoanRequest;
import logic.OrderedBook;
import logic.ResponseWrapper;

/**
 * Controller for managing book loan extension process.
 * 
 * Responsibilities: - Handle loan extension request validation - Manage UI for
 * book extension - Communicate with server for loan extension - Provide
 * comprehensive error handling
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ExtendBorrowingController {
	@FXML
	private TextField subscriberIdField;

	@FXML
	private TextField currentDateField;

	@FXML
	private TextField bookNameField;
	@FXML
	private Label errorLabel; // Add this line
	@FXML
	private Label bookAuthor;
	@FXML
	private TextField bookAuthorField;

	private String subscriberId;
	private static Image icon; // Static variable to hold the application icon

	public void setSubscriberId(String id) {
		this.subscriberId = id;
	}

	private static ExtendBorrowingController instance;

	public ExtendBorrowingController() {
		instance = this;
	}

	public static ExtendBorrowingController getInstance() {
		return instance;
	}

	@FXML
	public void initialize() {
	}

	/**
	 * Initializes input fields with subscriber and current date. Sets fields as
	 * non-editable.
	 */
	public void initializeFields() {
		subscriberIdField.setText(subscriberId);
		subscriberIdField.setEditable(false);

		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		currentDateField.setText(currentDate.format(formatter));
		currentDateField.setEditable(false);
	}

	/**
	 * Handles loan extension request submission.
	 * 
	 * Validation steps: - Validate subscriber ID - Check book name and author -
	 * Prepare extension request - Send request to server
	 * 
	 * @param event ActionEvent triggering loan extension
	 */
	@FXML
	private void handleExtendLoan(ActionEvent event) {
		try {
			int subscriberId = Integer.parseInt(subscriberIdField.getText().trim());
			String bookName = bookNameField.getText().trim();
			String author = bookAuthorField.getText().trim();
			bookNameField.getStyleClass().removeAll("error");
			bookAuthorField.getStyleClass().removeAll("error");
			if ((bookName.isEmpty() && author.isEmpty())) {
				bookNameField.getStyleClass().add("error");
				bookAuthorField.getStyleClass().add("error");
				errorLabel.setText("Book name and author cannot be empty.");
				errorLabel.setTextFill(Color.RED);

				return;
			}
			if (bookName.isEmpty()) {
				bookNameField.getStyleClass().add("error");
				errorLabel.setText("Book name cannot be empty.");
				errorLabel.setTextFill(Color.RED);
				bookAuthorField.getStyleClass().removeAll("error");
				return;
			} else {
				if (author.isEmpty()) {
					bookAuthorField.getStyleClass().add("error");
					errorLabel.setText("Book author cannot be empty.");
					errorLabel.setTextFill(Color.RED);
					bookNameField.getStyleClass().removeAll("error");
					return;
				}

			}
			// Get current date from the field
			Date currentDate = Date.valueOf(currentDateField.getText().trim());

			ExtendLoanRequest extendRequest = new ExtendLoanRequest(subscriberId, 0, // bookId
					bookName, null, // originalReturnDate - יתמלא בשרת
					currentDate, // extensionDate
					null, // newReturnDate
					author);

			ClientUI.chat.accept(extendRequest);

		} catch (NumberFormatException e) {
			errorLabel.setText("Please enter valid ID numbers");
		}
	}

	/**
	 * Handles navigation back to subscriber menu.
	 * 
	 * Actions: - Load subscriber menu FXML - Pass subscriber ID to menu controller
	 * - Create new stage with menu scene - Set stage properties - Apply application
	 * icon - Close current stage
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/TheSubscribersMenu.fxml"));
			Parent root = loader.load();
			// Get the controller of the loaded FXML
			SubscriberMenuController Menu = loader.getController();

			// Pass data to the controller
			Menu.setSubscriberId(subscriberId);
			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("The Subscribers Menu");
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			}
			home.show();

			Stage currentStage = (Stage) subscriberIdField.getScene().getWindow();
			currentStage.close();

		} catch (IOException e) {
			errorLabel.setText("Error opening screen");
			e.printStackTrace();
		}
	}

	/**
	 * Updates UI based on server response for loan extension.
	 * 
	 * Handles scenarios: - Book not in library - Successful extension - Frozen
	 * subscriber account - Book not borrowed - Extension time constraints
	 * 
	 * @param message Server response message
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("Book does not exist in library")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the error message and color to red
				errorLabel.setText("Book does not exist in library");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			} else if (message.equals("Loan extension approved")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the success message and color to green
				errorLabel.setText("Extension approved");
				errorLabel.setTextFill(Color.GREEN);

				// Remove error styling from the fields
				bookAuthorField.getStyleClass().removeAll("error");
				bookNameField.getStyleClass().removeAll("error");
			} else if (message.equals("Cannot extend loan - subscriber account is frozen")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the error message and color to red
				errorLabel.setText("Cannot extend loan - your account is frozen");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			} else if (message.equals("Cannot extend loan - book is not borrowed by you")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the error message and color to red
				errorLabel.setText("This book is not borrowed by you");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			} else if (message.equals("Extensions are allowed only if it’s within 7 days before the return date")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the error message and color to red
				errorLabel.setText("Extensions are allowed only if it’s within 7 days before the return date");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			} else if (message.equals("Cannot extend loan - book has pending orders")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the error message and color to red
				errorLabel.setText("Extension denied - book has pending orders");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			} else if (message.equals("Extension request failed - please try again")) {
				// Clear input fields
				bookAuthorField.clear();
				bookNameField.clear();

				// Set the error message and color to red
				errorLabel.setText("Extension request failed - please try again");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			} else {
				// Handle unexpected messages
				errorLabel.setText("");
				errorLabel.setTextFill(Color.RED);

				// Add error styling to the fields
				bookAuthorField.getStyleClass().add("error");
				bookNameField.getStyleClass().add("error");
			}
		});
	}

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = ResetPasswordController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

}