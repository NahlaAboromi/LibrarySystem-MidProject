package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
import logic.OrderedBook;
import logic.ResponseWrapper;

/**
 * Controller for book ordering process in library management system.
 * 
 * Responsibilities: - Manage book order request - Validate input fields -
 * Handle server communication - Provide user feedback
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class OrderBookController {

	@FXML
	private TextField subscriberIDField;

	@FXML
	private TextField BookNameField;

	@FXML
	private TextField bookAuthorField;

	@FXML
	private Label errorLabel;

	@FXML
	private Button OrderBook;

	private static OrderBookController instance;
	private static Image icon; // Static variable to hold the application icon

	public OrderBookController() {
		instance = this;
	}

	public static OrderBookController getInstance() {
		return instance;
	}

	private String subscriberID;

	public void setSubscriberId(String id) {
		this.subscriberID = id;
	}

	// Use the correct time zone (Asia/Jerusalem)
	ZoneId israelZone = ZoneId.of("Asia/Jerusalem");

	// Get the current date and time in the specified time zone
	ZonedDateTime zonedDateTime = ZonedDateTime.now(israelZone);

	// Convert ZonedDateTime to LocalDateTime
	LocalDateTime orderDateTime = zonedDateTime.toLocalDateTime();

	/**
	 * Handles navigation back to subscriber menu.
	 * 
	 * Actions: - Close current window - Load subscriber menu FXML - Preserve
	 * subscriber context - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = OrderBookController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Initializes controller with subscriber ID.
	 * 
	 * Actions: - Set subscriber ID field - Make subscriber ID non-editable
	 */
	@FXML
	public void initialize() {
		System.out.println("Initializing...");
		subscriberIDField.setText(subscriberID); // This ensures the ID is displayed
		subscriberIDField.setEditable(false);
	}

	/**
	 * Updates UI based on server response for book ordering.
	 * 
	 * Handles scenarios: - Book available - Subscriber account frozen - All copies
	 * ordered - Book already borrowed - Book not in library
	 * 
	 * @param message Server response message
	 */
	@FXML
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("book is available & there are copies")) {
				BookNameField.clear();
				bookAuthorField.clear();
				errorLabel.setText("There are available copies of the book");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("Order: Subscriber status is frozen")) {
				BookNameField.clear();
				bookAuthorField.clear();
				OrderBook.setVisible(false);
				errorLabel.setText("Your Status is Frozen, You can't order");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("All copies of the book are ordered")) {
				BookNameField.clear();
				bookAuthorField.clear();
				errorLabel.setText("All copies of the book are ordered");
				errorLabel.setTextFill(Color.RED);
			} else if (message.equals("You have a borrow for this book")) {
				BookNameField.clear();
				bookAuthorField.clear();
				errorLabel.setText("You already have a borrow for this book");
				errorLabel.setTextFill(Color.RED);
			} else if (message.startsWith("There are no copies of the book ")) {
				String bookToOrderID = message.substring("There are no copies of the book ".length());
				OrderedBook bookToOrder = new OrderedBook(Integer.parseInt(subscriberIDField.getText()),
						Integer.parseInt(bookToOrderID), BookNameField.getText(), orderDateTime, "Pending");
				ClientUI.chat.accept(new ResponseWrapper("DoneOrder", bookToOrder));
			} else if (message.equals("The book doesn't even exist in our library!")) {
				BookNameField.clear();
				bookAuthorField.clear();
				errorLabel.setText("The book doesn't even exist in our library!");
				errorLabel.setTextFill(Color.RED);
			} else if (message.startsWith("Order process completed successfully")) {
				String updatedOrNot = message.substring("Order process completed successfully ".length());
				System.out.println("updatedOrNot: " + updatedOrNot); // Debug print
				if (updatedOrNot.equals("false")) {
					BookNameField.clear();
					bookAuthorField.clear();
					errorLabel.setText("You already have an order of this book");
					errorLabel.setTextFill(Color.RED);
					return;
				}
				errorLabel.setText("Order process completed successfully");
				errorLabel.setTextFill(Color.GREEN);
			}
		});
	}

	/**
	 * Handles book order request submission.
	 * 
	 * Validation steps: - Check book name - Check book author - Validate input
	 * fields - Send order request to server
	 */
	@FXML
	private void handleOrderBook() {
		String bookName = BookNameField.getText();
		String bookAuthor = bookAuthorField.getText();

		BookNameField.getStyleClass().removeAll("error");
		bookAuthorField.getStyleClass().removeAll("error");
		errorLabel.setText("");

		// Check if both bookName and bookAuthor are empty
		if (bookName.trim().isEmpty() && bookAuthor.trim().isEmpty()) {
			BookNameField.getStyleClass().add("error");
			bookAuthorField.getStyleClass().add("error");
			errorLabel.setText("Please fill all fields");
			errorLabel.setTextFill(Color.RED);
			return;
		}

		// Check if bookName is empty
		if (bookName.trim().isEmpty()) {
			BookNameField.getStyleClass().add("error");
			bookAuthorField.getStyleClass().removeAll("error"); // Ensure the other field is not marked as error
			errorLabel.setText("Please fill the book name");
			errorLabel.setTextFill(Color.RED);
			return;
		}

		// Check if bookAuthor is empty
		if (bookAuthor.trim().isEmpty()) {
			bookAuthorField.getStyleClass().add("error");
			BookNameField.getStyleClass().removeAll("error"); // Ensure the other field is not marked as error
			errorLabel.setText("Please fill the book author");
			errorLabel.setTextFill(Color.RED);
			return;
		}
		Book bookToOrder = new Book(-1, bookName, null, null, null, bookAuthor, 0, 0, null, "", 0);
		ClientUI.chat.accept(
				new ResponseWrapper("checkBookForOrder", new Object[] { bookToOrder, subscriberIDField.getText() }));
	}

	/**
	 * Handles navigation back to subscriber menu.
	 * 
	 * Actions: - Close current window - Load subscriber menu FXML - Preserve
	 * subscriber context - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/TheSubscribersMenu.fxml"));
			Parent root = loader.load();

			// Get the controller of the new screen
			SubscriberMenuController subscriberMenuController = loader.getController();
			System.out.printf(subscriberIDField.getText());
			subscriberMenuController.setSubscriberId(subscriberIDField.getText());

			Stage home = new Stage();
			Scene scene = new Scene(root, 700, 600);
			home.setScene(scene);
			home.setTitle("Subscriber Menu");

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