package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.OrderedBooksHistory; // Assuming you have this class

/**
 * Controller for managing subscriber's ordered books history view.
 * 
 * Responsibilities: - Display ordered books records - Fetch ordered books data
 * from server - Manage table view configuration - Handle navigation between
 * screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class OrderedBooksHistoryController {
	private boolean isLibrarian = false;

	public void setLibrarian(boolean librarian) {
		this.isLibrarian = librarian;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
	}

	private static OrderedBooksHistoryController instance;

	public OrderedBooksHistoryController() {
		instance = this;
	}

	public static OrderedBooksHistoryController getInstance() {
		return instance;
	}

	@FXML
	private TableView<OrderedBooksHistory> orderedBooksTable; // Assuming you have a model class for ordered books
	@FXML
	private TableColumn<OrderedBooksHistory, Integer> orderIdColumn;
	@FXML
	private TableColumn<OrderedBooksHistory, Integer> subscriberIdColumn;
	@FXML
	private TableColumn<OrderedBooksHistory, Integer> bookIdColumn; // Added Book ID column
	@FXML
	private TableColumn<OrderedBooksHistory, String> orderDateColumn;
	@FXML
	private TableColumn<OrderedBooksHistory, String> orderStatusColumn;
	@FXML
	private TableColumn<OrderedBooksHistory, String> bookNameColumn;
	@FXML
	private TableColumn<OrderedBooksHistory, String> authorColumn;

	@FXML
	private Label errorLabel;

	// ObservableList to hold the ordered books history
	private ObservableList<OrderedBooksHistory> orderedBookstable = FXCollections.observableArrayList();

	/**
	 * Handles navigation back to subscriber history menu.
	 * 
	 * Actions: - Close current window - Load subscriber history menu FXML -
	 * Preserve subscriber context - Support librarian view - Apply application icon
	 */
	@FXML
	public void handleBackButton() {
		try {
			// סגור את החלון הנוכחי
			Stage currentStage = (Stage) errorLabel.getScene().getWindow();
			currentStage.close();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/MenuOfSubscriberHistory.fxml"));
			Parent root = loader.load();

			// קבל את הבקר הקיים
			MenuOfSubscriberHistoryController controller = loader.getController();
			controller.setSubscriberId(setSubscriberId);
			// בדוק אם השדה isLibrarian הוא TRUE
			if (isLibrarian) {
				controller.setLibrarian(true);
			}
			Stage previousStage = new Stage();
			Scene scene = new Scene(root, 540, 400);
			previousStage.setScene(scene);
			previousStage.setTitle("Menu of Subscriber History");
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				previousStage.getIcons().add(icon);
			}

			previousStage.show();
		} catch (IOException e) {
			errorLabel.setText("Error returning to the previous screen");
			e.printStackTrace();
		}
	}

	/**
	 * Initializes table columns and fetches ordered books.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * ordered books for subscriber - Hide sensitive columns
	 */
	@FXML
	public void initialize() {
		System.out.println("Subscriber ID in initialize: " + setSubscriberId);
		orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
		orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
		bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

		orderedBooksTable.setItems(orderedBookstable);

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
					OrderedBooksHistory request = new OrderedBooksHistory(Integer.parseInt(setSubscriberId));
					ClientUI.chat.accept(request);
				} catch (NumberFormatException e) {
					Platform.runLater(() -> errorLabel.setText("Invalid subscriber ID format."));
					e.printStackTrace();
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
	public void updateTable(ArrayList<OrderedBooksHistory> orderedBooks) {
		Platform.runLater(() -> {
			orderedBookstable.clear(); // Clear existing items
			orderedBookstable.addAll(orderedBooks); // Add new items to the observable list
		});
	}

	public void setErrorLabelText(String text) {
		if (errorLabel != null) {
			errorLabel.setText(text);
		}
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Subscriber ID - Order ID - Book ID
	 */
	@FXML
	public void hideColumns() {
		subscriberIdColumn.setVisible(false); // להסתיר את עמודת ה-ID של המנוי
		// אם יש עמודות נוספות שתרצה להסתיר, תוכל להוסיף כאן
		orderIdColumn.setVisible(false);
		bookIdColumn.setVisible(false);
	}

	private static Image icon; // Static variable to hold the application icon

	/**
	 * Loads application icon from resources.
	 * 
	 * Ensures: - Icon loaded only once - Fallback for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = BorrowMethodSelectionController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}
}
