package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import logic.OrderedBook; // Assuming you have this class
import logic.ResponseWrapper;

/**
 * Controller for managing subscriber's ordered books view.
 * 
 * Responsibilities: - Display ordered books records - Fetch ordered books data
 * from server - Manage table view configuration - Handle navigation between
 * screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class OrderedBooksReaderCardController {
	private static OrderedBooksReaderCardController instance;

	public OrderedBooksReaderCardController() {
		instance = this;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	public static OrderedBooksReaderCardController getInstance() {
		return instance;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("setSubscriberId called with: " + id);
	}

	@FXML
	private TableView<OrderedBook> orderedBooksTable;

	@FXML
	private TableColumn<OrderedBook, Integer> subscriberIdColumn;

	@FXML
	private TableColumn<OrderedBook, Integer> bookIdColumn;

	@FXML
	private TableColumn<OrderedBook, String> bookNameColumn; // עמודת שם הספר

	@FXML
	private TableColumn<OrderedBook, Date> orderDateColumn; // עמודת תאריך ההזמנה

	@FXML
	private TableColumn<OrderedBook, Date> firstReturnDateColumn; // עמודת תאריך החזרה הראשון

	@FXML
	private TableColumn<OrderedBook, Date> startOrderDateColumn; // עמודת תאריך התחלת ההזמנה

	@FXML
	private TableColumn<OrderedBook, Date> endOrderDateColumn; // עמודת תאריך סיום ההזמנה

	@FXML
	private Label errorLabel;
	@FXML
	private Button returnButton;

	@FXML
	private ObservableList<OrderedBook> orderedBooksList = FXCollections.observableArrayList();

	/**
	 * Initializes table columns and fetches ordered books.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * ordered books for subscriber - Hide sensitive columns
	 */
	@FXML
	public void initialize() {
		System.out.println("Subscriber ID in initialize: " + setSubscriberId);

		subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
		bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName")); // הגדרת עמודת שם הספר
		orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate")); // הגדרת עמודת תאריך ההזמנה
		firstReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("firstReturnDate")); // הגדרת עמודת תאריך
																									// החזרה הראשון
		startOrderDateColumn.setCellValueFactory(new PropertyValueFactory<>("startOrderDate")); // הגדרת עמודת תאריך
																								// התחלת ההזמנה
		endOrderDateColumn.setCellValueFactory(new PropertyValueFactory<>("endOrderDate")); // הגדרת עמודת תאריך סיום
																							// ההזמנה

		orderedBooksTable.setItems(orderedBooksList);

		fetchOrderedBooks();
		hideColumns();
	}

	/**
	 * Fetches ordered books for specific subscriber.
	 * 
	 * Features: - Asynchronous data retrieval - Thread-safe server communication -
	 * Error handling for data fetch
	 */
	public void fetchOrderedBooks() {
		System.out.println("Fetching ordered books... Subscriber ID: " + setSubscriberId);
		if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
			new Thread(() -> {
				try {
					ClientUI.chat.accept(new ResponseWrapper("FetchOrderedBooks", Integer.parseInt(setSubscriberId)));
					// Update the UI after fetching data
					Platform.runLater(() -> {
						// Update the UI here with the fetched data if necessary
					});
				} catch (Exception e) {
					Platform.runLater(() -> errorLabel.setText("Failed to fetch ordered books."));
					e.printStackTrace();
				}
			}).start();
		} else {
			errorLabel.setText("Subscriber ID is not set.");
		}
	}

	/**
	 * Updates table with ordered books records.
	 * 
	 * @param orderedBooks List of ordered books to display
	 */
	public void updateTable(ArrayList<OrderedBook> orderedBooks) {
		Platform.runLater(() -> {
			orderedBooksList.clear();
			orderedBooksList.addAll(orderedBooks);
		});
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Subscriber ID - First Return Date - Book ID
	 */
	@FXML
	public void hideColumns() {
		subscriberIdColumn.setVisible(false); // Hide the subscriber ID column if needed
		firstReturnDateColumn.setVisible(false);
		bookIdColumn.setVisible(false); // Hide the book ID column if needed
	}

	/**
	 * Handles navigation back to reader card view.
	 * 
	 * Actions: - Close current window - Load reader card FXML - Preserve subscriber
	 * context - Set librarian name - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ReaderCardView.fxml"));
			Parent root = loader.load();
			ReaderCardViewController readerCardController = loader.getController();
			readerCardController.loadSubscriber(ChatClient.s1);
			readerCardController.setSubscriberId(setSubscriberId);
			readerCardController.setLibrarianName(librarianName);
			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Reader Card");

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

	private static Image icon; // Static variable to hold the application icon

	/**
	 * Loads application icon from resources.
	 * 
	 * Ensures: - Icon loaded only once - Fallback for missing icon
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
