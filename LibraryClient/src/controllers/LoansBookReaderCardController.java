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
import logic.BorrowedBook; // Assuming you have this class
import logic.ResponseWrapper;

/**
 * Controller for managing subscriber's borrowed books view.
 * 
 * Responsibilities: - Display borrowed books for a specific subscriber - Fetch
 * borrowed books data from server - Manage table view configuration - Handle
 * navigation and context preservation
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class LoansBookReaderCardController {
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private static LoansBookReaderCardController instance;

	public LoansBookReaderCardController() {
		instance = this;
	}

	/**
	 * Controller for managing subscriber's borrowed books view.
	 * 
	 * Responsibilities: - Display borrowed books for a specific subscriber - Fetch
	 * borrowed books data from server - Manage table view configuration - Handle
	 * navigation and context preservation
	 * 
	 * @author Library Management System Team
	 * @version 1.0
	 */
	public static LoansBookReaderCardController getInstance() {
		return instance;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("setSubscriberId called with: " + id);
	}

	@FXML
	private TableView<BorrowedBook> borrowedBooksTable;

	@FXML
	private TableColumn<BorrowedBook, Integer> subscriberIdColumn;

	@FXML
	private TableColumn<BorrowedBook, Integer> bookIdColumn;

	@FXML
	private TableColumn<BorrowedBook, String> bookNameColumn; // עמודת שם הספר

	@FXML
	private TableColumn<BorrowedBook, String> authorColumn; // עמודת שם המחבר

	@FXML
	private TableColumn<BorrowedBook, Date> borrowDateColumn;

	@FXML
	private TableColumn<BorrowedBook, Date> returnDateColumn;

	@FXML
	private Label errorLabel;
	@FXML
	private Button returnButton;
	@FXML
	private ObservableList<BorrowedBook> borrowedBooksList = FXCollections.observableArrayList();
	private static Image icon; // Static variable to hold the application icon

	/**
	 * Initializes table columns and fetches borrowed books.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * borrowed books for subscriber - Hide sensitive columns
	 */
	@FXML
	public void initialize() {
		System.out.println("Subscriber ID in initialize: " + setSubscriberId);

		subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
		bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName")); // הגדרת עמודת שם הספר
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author")); // הגדרת עמודת שם המחבר
		borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
		returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

		borrowedBooksTable.setItems(borrowedBooksList);

		fetchBorrowedBooks();
		hideColumns();
	}

	/**
	 * Fetches borrowed books for specific subscriber.
	 * 
	 * Features: - Asynchronous data retrieval - Thread-safe server communication -
	 * Error handling for data fetch
	 */
	public void fetchBorrowedBooks() {
		System.out.println("Fetching borrowed books... Subscriber ID: " + setSubscriberId);
		if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
			new Thread(() -> {
				try {
					ClientUI.chat.accept(new ResponseWrapper("FetchBorrowedBooks", Integer.parseInt(setSubscriberId)));
					// Update the UI after fetching data
					Platform.runLater(() -> {
						// Update the UI here with the fetched data if necessary
					});
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
	 * Updates table with borrowed books records.
	 * 
	 * @param borrowedBooks List of borrowed books to display
	 */
	public void updateTable(ArrayList<BorrowedBook> borrowedBooks) {
		Platform.runLater(() -> {
			borrowedBooksList.clear();
			borrowedBooksList.addAll(borrowedBooks);
		});
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Subscriber ID - Book ID
	 */
	@FXML
	public void hideColumns() {
		subscriberIdColumn.setVisible(false);
		bookIdColumn.setVisible(false);
	}

	/**
	 * Handles book return date extension request.
	 * 
	 * Actions: - Validate book selection - Load ExtendReturnDate FXML - Pass book
	 * and subscriber details to extension controller - Configure new stage for
	 * extension
	 * 
	 * Features: - Dynamic book extension navigation - Context preservation - Error
	 * handling for invalid selection
	 * 
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleExtension() {
		BorrowedBook selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();

		if (selectedBook != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/ExtendReturnDate.fxml"));

				// Load the new GUI (ExtendReturnDate.fxml)
				Parent root = loader.load();

				// Get the controller for the new GUI
				ExtendReturnDateController controller = loader.getController();

				// Pass the necessary data (book ID, subscriber ID, current return date) to the
				// new controller
				controller.setBookDetails(selectedBook.getBookId(), // bookId
						setSubscriberId, // subscriberId
						selectedBook.getReturnDate().toLocalDate(), // originalReturnDate (converted to LocalDate)
						selectedBook.getBookName(), // bookName
						selectedBook.getAuthor() // author
				);
				controller.setLibrarianName(librarianName);

				// Pass the current stage to the new controller
				Stage currentStage = (Stage) borrowedBooksTable.getScene().getWindow();
				controller.setParentStage(currentStage);

				// Set up a new stage for the "Extend Return Date" window
				Stage stage = new Stage();
				stage.setTitle("Extend Return Date");

				// Set the scene with the loaded FXML root
				stage.setScene(new Scene(root));
				// Load and set the application icon
				loadIcon();
				if (icon != null) {
					stage.getIcons().add(icon);
				}
				// Show the new stage (window)
				stage.show();

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error loading ExtendReturnDate screen.");
			}
		} else {
			errorLabel.setText("Please select a book to extend.");
		}
	}

	/**
	 * Handles navigation back to reader card view.
	 * 
	 * Actions: - Close current window - Load reader card FXML - Preserve subscriber
	 * and librarian context - Configure new stage - Apply application icon
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
