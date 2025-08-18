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
import logic.LoanExtension;
import logic.ResponseWrapper;

/**
 * Controller for managing loan extensions history for a subscriber.
 * 
 * Responsibilities: - Display loan extension records - Fetch loan extension
 * data from server - Manage table view configuration - Handle navigation
 * between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ExtensionsReaderCardController {
	private static ExtensionsReaderCardController instance;

	public ExtensionsReaderCardController() {
		instance = this;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	public static ExtensionsReaderCardController getInstance() {
		return instance;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("setSubscriberId called with: " + id);
	}

	@FXML
	private TableView<LoanExtension> loanExtensionsTable;

	@FXML
	private TableColumn<LoanExtension, Integer> extensionIdColumn;

	@FXML
	private TableColumn<LoanExtension, Integer> subscriberIdColumn;

	@FXML
	private TableColumn<LoanExtension, Integer> bookIdColumn;

	@FXML
	private TableColumn<LoanExtension, String> bookNameColumn;

	@FXML
	private TableColumn<LoanExtension, String> authorColumn;

	@FXML
	private TableColumn<LoanExtension, Date> originalReturnDateColumn;

	@FXML
	private TableColumn<LoanExtension, Date> newReturnDateColumn;

	@FXML
	private TableColumn<LoanExtension, Date> extensionDateColumn;

	@FXML
	private TableColumn<LoanExtension, String> extensionStatusColumn;

	@FXML
	private TableColumn<LoanExtension, String> librarianNameColumn; // Added
	@FXML
	private TableColumn<LoanExtension, String> RejectionReason; // Added
	@FXML
	private Label errorLabel;
	@FXML
	private Button returnButton;

	@FXML
	private ObservableList<LoanExtension> loanExtensionsList = FXCollections.observableArrayList();

	/**
	 * Initializes table columns and fetches loan extensions.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * loan extensions for subscriber - Hide sensitive columns
	 */
	@FXML
	public void initialize() {
		System.out.println("Subscriber ID in initialize: " + setSubscriberId);

		extensionIdColumn.setCellValueFactory(new PropertyValueFactory<>("extensionId"));
		subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
		bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
		originalReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("originalReturnDate"));
		newReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("newReturnDate"));
		extensionDateColumn.setCellValueFactory(new PropertyValueFactory<>("extensionDate"));
		extensionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("extensionStatus"));

		librarianNameColumn.setCellValueFactory(new PropertyValueFactory<>("librarianName")); // Added this line
		RejectionReason.setCellValueFactory(new PropertyValueFactory<>("rejectionReason")); // Added this line

		loanExtensionsTable.setItems(loanExtensionsList);

		fetchLoanExtensions();
		hideColumns();
	}

	/**
	 * Fetches loan extensions for specific subscriber.
	 * 
	 * Features: - Asynchronous data retrieval - Thread-safe server communication -
	 * Error handling for data fetch
	 */
	public void fetchLoanExtensions() {
		System.out.println("Fetching loan extensions... Subscriber ID: " + setSubscriberId);
		if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
			new Thread(() -> {
				try {
					// Assuming you have a method to fetch loan extensions by subscriber ID
					ClientUI.chat.accept(new ResponseWrapper("FetchLoanExtensions", Integer.parseInt(setSubscriberId)));
					// Update the UI after fetching data
					Platform.runLater(() -> {
						// Update the UI here with the fetched data
					});
				} catch (Exception e) {
					Platform.runLater(() -> errorLabel.setText("Failed to fetch loan extensions."));
					e.printStackTrace();
				}
			}).start();
		} else {
			errorLabel.setText("Subscriber ID is not set.");
		}
	}

	/**
	 * Updates table with loan extension records.
	 * 
	 * @param loanExtensions List of loan extensions to display
	 */
	public void updateTable(ArrayList<LoanExtension> loanExtensions) {
		Platform.runLater(() -> {
			loanExtensionsList.clear();
			loanExtensionsList.addAll(loanExtensions);
		});
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Extension ID - Subscriber ID - Book ID
	 */
	@FXML
	public void hideColumns() {
		extensionIdColumn.setVisible(false); // Hide the extension ID column if needed
		subscriberIdColumn.setVisible(false);
		bookIdColumn.setVisible(false);
	}

	/**
	 * Handles navigation back to reader card view.
	 * 
	 * Actions: - Close current window - Load reader card FXML - Preserve subscriber
	 * context - Set application icon
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
