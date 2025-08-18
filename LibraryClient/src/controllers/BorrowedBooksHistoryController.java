package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import client.ClientUI;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
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
import logic.BorrowedBooksHistory; // Assuming you have this class
import logic.OrderedBooksHistory;

/**
 * Controller for managing borrowed books history view.
 * 
 * Responsibilities: - Display subscriber's borrowed books history - Manage
 * table columns and data - Handle navigation between screens - Support
 * librarian and subscriber contexts
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class BorrowedBooksHistoryController {
	private boolean isLibrarian = false;

	public void setLibrarian(boolean librarian) {
		this.isLibrarian = librarian;
	}

	private static BorrowedBooksHistoryController instance;

	public BorrowedBooksHistoryController() {
		instance = this;
	}

	public static BorrowedBooksHistoryController getInstance() {
		return instance;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("setSubscriberId called with: " + id);
	}

	@FXML
	private TableView<BorrowedBooksHistory> borrowedBooksTable;

	@FXML
	private TableColumn<BorrowedBooksHistory, Integer> historyIdColumn;

	@FXML
	private TableColumn<BorrowedBooksHistory, Integer> subscriberIdColumn;

	@FXML
	private TableColumn<BorrowedBooksHistory, Integer> bookIdColumn;

	@FXML
	private TableColumn<BorrowedBooksHistory, Date> borrowDateColumn;

	@FXML
	private TableColumn<BorrowedBooksHistory, Date> returnDateColumn;

	@FXML
	private TableColumn<BorrowedBooksHistory, Date> actualReturnDateColumn;

	@FXML
	private TableColumn<BorrowedBooksHistory, Boolean> isLateColumn;

	@FXML
	private Label errorLabel;
	@FXML
	TableColumn<BorrowedBooksHistory, String> bookNameColumn;
	@FXML
	TableColumn<BorrowedBooksHistory, String> authorColumn;

	@FXML
	private ObservableList<BorrowedBooksHistory> borrowedBookstable = FXCollections.observableArrayList();

	/**
	 * Initializes table columns and fetches borrowed books.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * borrowed books for subscriber - Hide sensitive columns
	 */
	@FXML
	public void initialize() {
		System.out.println("Subscriber ID in initialize: " + setSubscriberId);

		historyIdColumn.setCellValueFactory(new PropertyValueFactory<>("historyId"));
		subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));

		// Add these lines to bind the new properties

		bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));

		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

		borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
		returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

		actualReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
		isLateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().isLate()));

		borrowedBooksTable.setItems(borrowedBookstable);

		fetchBorrowedBooks();
		hideColumns();
	}

	/**
	 * Fetches borrowed books for specific subscriber.
	 * 
	 * Features: - Asynchronous book retrieval - Thread-safe UI updates - Error
	 * handling for invalid subscriber ID
	 */
	public void fetchBorrowedBooks() {
		System.out.println("Fetching borrowed books... Subscriber ID: " + setSubscriberId);
		if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
			new Thread(() -> {
				try {
					System.out.println("client1");
					BorrowedBooksHistory request = new BorrowedBooksHistory(Integer.parseInt(setSubscriberId));

					ClientUI.chat.accept(request);
					System.out.println("client2");
					// אם צריך לעדכן את ה-UI, השתמש ב-Platform.runLater() כאן
					Platform.runLater(() -> {
						// עדכן את ה-UI כאן
					});
				} catch (NumberFormatException e) {
					Platform.runLater(() -> errorLabel.setText("Invalid subscriber ID format."));
					e.printStackTrace();
				} catch (Exception e) {
					Platform.runLater(() -> errorLabel.setText("Failed to fetch borrowed books."));
					e.printStackTrace();
				}
			}).start();
		} else {
			errorLabel.setText("Subscriber ID is not set.");
		}
	}

	/**
	 * Updates table with borrowed books data.
	 * 
	 * @param borrowedBooks List of borrowed books to display
	 */
	public void updateTable(ArrayList<BorrowedBooksHistory> borrowedBooks) {
		System.out.println("client 6");
		Platform.runLater(() -> {
			borrowedBookstable.clear();
			borrowedBookstable.addAll(borrowedBooks);
		});
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Subscriber ID - History ID - Late status - Actual return
	 * date - Book ID
	 */
	@FXML
	public void hideColumns() {
		subscriberIdColumn.setVisible(false); // להסתיר את עמודת ה-ID של המנוי
		historyIdColumn.setVisible(false);
		isLateColumn.setVisible(false);
		actualReturnDateColumn.setVisible(false);
		bookIdColumn.setVisible(false);
	}

	/**
	 * Handles navigation back to subscriber history menu.
	 * 
	 * Actions: - Close current window - Load subscriber history menu - Preserve
	 * subscriber context - Support librarian view
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
