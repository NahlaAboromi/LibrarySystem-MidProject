package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
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
import logic.OrderedBooksHistory;
import logic.ReturnBookHistory;

/**
 * Controller for managing subscriber's book return history view.
 * 
 * Responsibilities: - Display book return records - Fetch return book history
 * data from server - Manage table view configuration - Handle navigation
 * between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ReturnBookHistoryController {
	private boolean isLibrarian = false;

	public void setLibrarian(boolean librarian) {
		this.isLibrarian = librarian;
	}

	private static ReturnBookHistoryController instance;

	public ReturnBookHistoryController() {
		instance = this;
	}

	public static ReturnBookHistoryController getInstance() {
		return instance;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("Subscriber ID set to: " + id);
	}

	@FXML
	private TableView<ReturnBookHistory> returnBookTable;

	@FXML
	private TableColumn<ReturnBookHistory, Integer> historyIdColumn;

	@FXML
	private TableColumn<ReturnBookHistory, Integer> subscriberIdColumn;

	@FXML
	private TableColumn<ReturnBookHistory, Integer> bookIdColumn;

	@FXML
	private TableColumn<ReturnBookHistory, String> bookNameColumn; // Change to String

	@FXML
	private TableColumn<ReturnBookHistory, String> authorColumn; // Change to String

	@FXML
	private TableColumn<ReturnBookHistory, Date> borrowDateColumn;

	@FXML
	private TableColumn<ReturnBookHistory, Date> returnDateColumn;

	@FXML
	private TableColumn<ReturnBookHistory, Date> actualReturnDateColumn;

	@FXML
	private TableColumn<ReturnBookHistory, String> isLateColumn;

	@FXML
	private Label errorLabel;

	@FXML
	private ObservableList<ReturnBookHistory> returnBookList = FXCollections.observableArrayList();

	/**
	 * Initializes table columns and fetches return book history.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * return book history for subscriber - Hide sensitive columns
	 */
	@FXML
	public void initialize() {
		System.out.println("Initializing Return Book History Controller with Subscriber ID: " + setSubscriberId);

		historyIdColumn.setCellValueFactory(new PropertyValueFactory<>("historyId"));
		subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));

		bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

		borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
		returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
		actualReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));

		isLateColumn.setCellValueFactory(new PropertyValueFactory<>("isLate"));

		returnBookTable.setItems(returnBookList);

		fetchReturnBookHistory(); // Fetching data on initialization
		hideColumns();
	}

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
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				previousStage.getIcons().add(icon);
			}
			Scene scene = new Scene(root, 540, 400);
			previousStage.setScene(scene);
			previousStage.setTitle("Menu of Subscriber History");

			previousStage.show();
		} catch (IOException e) {
			errorLabel.setText("Error returning to the previous screen");
			e.printStackTrace();
		}
	}

	/**
	 * Fetches return book history for specific subscriber.
	 * 
	 * Features: - Asynchronous data retrieval - Thread-safe server communication -
	 * Error handling for data fetch
	 */
	public void fetchReturnBookHistory() {
		System.out.println("Fetching return book history for Subscriber ID: " + setSubscriberId);
		if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
			new Thread(() -> {
				try {
					// Create a request to fetch returned books for this subscriber ID
					ReturnBookHistory request = new ReturnBookHistory(Integer.parseInt(setSubscriberId));
					ClientUI.chat.accept(request); // Send request to server to fetch data
				} catch (NumberFormatException e) {
					Platform.runLater(() -> errorLabel.setText("Invalid subscriber ID format."));
					e.printStackTrace();
				} catch (Exception e) {
					Platform.runLater(() -> errorLabel.setText("Failed to fetch return book history."));
					e.printStackTrace();
				}
			}).start();
		} else {
			errorLabel.setText("Subscriber ID is not set.");
		}
	}

	/**
	 * Updates table with return book history records.
	 * 
	 * @param returnBookHistory List of return book history to display
	 */
	public void updateTable(ArrayList<ReturnBookHistory> returnBookHistory) {
		Platform.runLater(() -> {
			returnBookList.clear();
			returnBookList.addAll(returnBookHistory);
		});
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Subscriber ID - History ID - Return Date - Book ID
	 */
	@FXML
	public void hideColumns() {
		subscriberIdColumn.setVisible(false); // להסתיר את עמודת ה-ID של המנוי
		historyIdColumn.setVisible(false);
		returnDateColumn.setVisible(false);
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
			URL iconUrl = ResetPasswordController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

}
