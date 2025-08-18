package gui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import client.ClientUI;
import controllers.LibrarianDashboardController;
import controllers.SubscriberMenuController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.Book;
import logic.ResponseWrapper;

public class BookSearchController {

	@FXML
	private TextField searchFieldByName;
	
	@FXML
	private TextField searchFieldByDescription;

	
	@FXML
	private GridPane grid;

	@FXML
	private Label errorLabel;

	@FXML
	private ChoiceBox<String> searchCategory;

	@FXML
	private Button loginButton;
	@FXML
	private Button ButtongoHome;

	private boolean isSubscriber = false;
	private boolean isLibrarian = false;
	private boolean isReader = false;
	private String setSubscriberId;
	private static BookSearchController instance;
	private static Image icon;

	public void setIsSubscriber() {
		this.isSubscriber = true;
		updateUI(); // Update the UI after setting this property

	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
	}

	public void setIsLibrarian() {
		isLibrarian = true;
		updateUI(); // Update the UI after setting this property

	}

	public void setIsReader() {
		isReader = true;
		updateUI(); // Update the UI after setting this property

	}

	public BookSearchController() {
		instance = this;
	}

	public static BookSearchController getInstance() {
		return instance;
	}

	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = ClientConnectionController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Initializes book search interface.
	 * 
	 * Actions: - Populates search categories - Sets default category - Retrieves
	 * all books from server
	 */
	@FXML
	public void initialize() {
		try {
			searchCategory.getItems().addAll("Fiction", "Romance", "Science", "History","Biography","Mystery");
			searchCategory.getItems().add(0, "Select Category"); // Add it as the first option

			// Set "Select Category" as the default appearance
			searchCategory.setValue("Select Category");
			ClientUI.chat.accept("GET_ALL_BOOKS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("isSubscriber : " + isSubscriber);
		System.out.println("isLibrarian : " + isLibrarian);
	}

	/**
	 * Updates UI components based on user role.
	 * 
	 * Configures visibility of: - Home button - Login button
	 */
	private void updateUI() {
		System.out.println("isSubscriber: " + isSubscriber);
		if (isSubscriber) {
			ButtongoHome.setVisible(true);
			loginButton.setVisible(false);
		} else if (isReader) {
			loginButton.setVisible(true);
			ButtongoHome.setVisible(false);
		} else if (isLibrarian) {
			loginButton.setVisible(false);
			ButtongoHome.setVisible(true);
		}
	}

	/**
	 * Handles book search based on book name input.
	 * 
	 * Validates search query and sends request to server. Displays error messages
	 * for invalid inputs.
	 */
	@FXML
	private void handleSearchByName() {

		errorLabel.setText("");
		String query = searchFieldByName.getText();
		if (query.trim().isEmpty()) {
			errorLabel.setText("Please enter a book name");
			return;
		}

		try {
			ClientUI.chat.accept(new ResponseWrapper("SEARCH_BOOK_BY_NAME",query));
			searchFieldByName.clear();
		} catch (Exception e) {
			errorLabel.setText("An error occurred while searching for books.");
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Handles book search based on description of the book input.
	 * 
	 * Validates search query and sends request to server. Displays error messages
	 * for invalid inputs.
	 */
	@FXML
	private void handleSearchByDescription() {

		errorLabel.setText("");
		String query = searchFieldByDescription.getText();
		if (query.trim().isEmpty()) {
			errorLabel.setText("Please enter a description of the book");
			return;
		}

		try {
			ClientUI.chat.accept(new ResponseWrapper("SEARCH_BOOK_BY_DESCRIPTION",query));
			searchFieldByDescription.clear();
		} catch (Exception e) {
			errorLabel.setText("An error occurred while searching for books.");
			e.printStackTrace();
		}
	}
	/**
	 * Manages login process and navigation.
	 * 
	 * Actions: - Loads user selection screen - Handles stage transitions - Sets
	 * application icon
	 * 
	 * @param event JavaFX action event triggering login
	 */
	@FXML
	private void handleLogin(ActionEvent event) {
		try {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			System.out.println("Width: " + stage.getWidth());
			System.out.println("Height: " + stage.getHeight());

			// Load the UserSelectionController FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
			Parent root = loader.load();

			// Create a new stage for the UserSelectionController
			Stage userSelectionStage = new Stage();
			loadIcon();
			if (icon != null) {
				userSelectionStage.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}
			userSelectionStage.setScene(new Scene(root));
			userSelectionStage.setTitle("User Selection");

			// Set the application icon
			if (icon != null) {
				userSelectionStage.getIcons().add(icon);
			}

			// Hide the current window
			((Node) event.getSource()).getScene().getWindow().hide();

			// Show the UserSelectionController stage
			userSelectionStage.show();
		} catch (IOException e) {
			System.out.println("Error loading UserSelectionController: " + e.getMessage());
			errorLabel.setText("An error occurred while opening the login screen.");
			e.printStackTrace();
		}
	}

	/**
	 * Handles book search by category.
	 * 
	 * Actions: - Clears previous error messages - Retrieves selected category from
	 * ChoiceBox - Sends server request for: - All books if "Select Category" chosen
	 * - Books by specific category otherwise
	 * 
	 * @throws Exception If server communication fails
	 */
	@FXML
	private void searchBookByCategory() {
		errorLabel.setText("");
		String query = searchCategory.getValue();
		try {
			if (query.equals("Select Category")) {
				ClientUI.chat.accept("GET_ALL_BOOKS");

			} else {

				ClientUI.chat.accept("CATEGORY_SEARCH " + query);
			}
		} catch (Exception e) {
			errorLabel.setText("An error occurred while searching for books.");
			e.printStackTrace();
		}
	}

	/**
	 * Navigates user to home screen based on their role.
	 * 
	 * Supports navigation for: - Subscribers: Redirects to Subscriber Menu -
	 * Librarians: Redirects to Librarian Dashboard
	 * 
	 * @param event JavaFX action event triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		if (isSubscriber != false) {
			try {
				((Node) event.getSource()).getScene().getWindow().hide();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/TheSubscribersMenu.fxml"));
				Parent root = loader.load();

				SubscriberMenuController subscriberMenuController = loader.getController();
				subscriberMenuController.setSubscriberId(setSubscriberId);

				Stage home = new Stage();
				Scene scene = new Scene(root, 700, 600);
				home.setScene(scene);
				home.setTitle("The Subscribers Menu");
				// Load and set the icon
				loadIcon();
				if (icon != null) {
					home.getIcons().add(icon);
				} else {
					System.err.println("Failed to load application icon.");
				}
				home.show();

			} catch (IOException e) {
				errorLabel.setText("Error opening reset password screen");
				e.printStackTrace();
			}
		} else if (isLibrarian != false) {
			try {
				((Node) event.getSource()).getScene().getWindow().hide();
				FXMLLoader loader = new FXMLLoader(
						getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
				Parent root = loader.load();
				LibrarianDashboardController librarianDashboard = loader.getController();
				librarianDashboard.setLibrarianName(librarianName);
				Stage home = new Stage();
				Scene scene = new Scene(root);
				home.setScene(scene);
				home.setTitle("Librarian Dashboard");

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

	/**
	 * Resets book view to display all books.
	 * 
	 * Actions: - Clears previous error messages - Requests all books from server
	 * 
	 * @throws Exception If server communication fails
	 */
	@FXML
	private void handleReturn() {
		try {
			errorLabel.setText("");
			ClientUI.chat.accept("GET_ALL_BOOKS");
		} catch (Exception e) {
			errorLabel.setText("An error occurred while trying to view all books.");
			e.printStackTrace();
		}
	}

	/**
	 * Initializes and displays the Book Search view.
	 * 
	 * Actions: - Loads FXML and CSS - Sets stage title - Applies application icon
	 * 
	 * @param primaryStage Main application stage
	 */
	public void start(Stage primaryStage) {
		try {
			loadIcon(); // Load the icon
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BookSearchView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/BookSearchView.css").toExternalForm());
			primaryStage.setTitle("BookList");
			primaryStage.setScene(scene);
			if (icon != null) {
				primaryStage.getIcons().add(icon);
			}
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dynamically populates grid with book information.
	 * 
	 * Features: - Displays book image - Shows book details (name, author) -
	 * Indicates book availability - Handles empty book list scenario
	 * 
	 * @param books List of books to display
	 */
	public void loadBooks(ArrayList<Book> books) {
		Platform.runLater(() -> {
			int row = 0;
			int column = 0;

			if (books.isEmpty()) {
				errorLabel.setText("Book Not Found");
				return;
			}
			grid.getChildren().clear();

			for (Book book : books) {
				Image bookImage = new Image(new ByteArrayInputStream(book.getBookImage()));
				ImageView bookImageView = new ImageView(bookImage);
				bookImageView.setFitHeight(250);
				bookImageView.setFitWidth(250);
				bookImageView.getStyleClass().add("image-view");

				Label bookNameLabel = new Label(book.getBookName());
				Label authorNameLabel = new Label(book.getauthorName());
				bookNameLabel.getStyleClass().add("book-name-label");
				authorNameLabel.getStyleClass().add("author-availableCopies-label");

				Label availableCopiesLabel;
				if (book.getNumberOfAvailableCopies() > 0) {
					availableCopiesLabel = new Label("Available Copies: " + book.getNumberOfAvailableCopies()
							+ " ,Place: " + book.getBookPlace());
					availableCopiesLabel.getStyleClass().add("author-availableCopies-label");
				} else {
					Date firstReturnDate = book.getFirstReturnDate();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String formattedDate = sdf.format(firstReturnDate);
					availableCopiesLabel = new Label("UnAvailable, First Return Date: " + formattedDate);
					availableCopiesLabel.getStyleClass().add("unAvailableCopies-label");
				}

				VBox bookInfoVBox = new VBox(-3);
				bookInfoVBox.getChildren().addAll(bookNameLabel, authorNameLabel);
				bookInfoVBox.setStyle("-fx-alignment: center;");

				VBox vbox = new VBox(10);
				vbox.getChildren().addAll(bookImageView, bookInfoVBox, availableCopiesLabel);
				vbox.setStyle("-fx-alignment: center;");

				grid.add(vbox, column, row);

				column++;

				if (column > 2) {
					column = 0;
					row += 2;
				}
			}
		});
		grid.getStyleClass().add("grid-pane");
	}
}
