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
import logic.LoanExtension;

/**
 * Controller for managing loan extension history view.
 * 
 * Responsibilities: - Display loan extension records - Fetch loan extension
 * data from server - Manage table view configuration - Handle navigation
 * between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class LoanExtensionHistoryController {
	private boolean isLibrarian = false;

	public void setLibrarian(boolean librarian) {
		this.isLibrarian = librarian;
	}

	private static LoanExtensionHistoryController instance;

	public LoanExtensionHistoryController() {
		instance = this;
	}

	public static LoanExtensionHistoryController getInstance() {
		return instance;
	}

	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("setSubscriberId called with: " + id);
	}

	@FXML
	private TableView<LoanExtension> loanExtensionTable;

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
	private TableColumn<LoanExtension, String> librarianNameColumn; // Added this line
	@FXML
	private TableColumn<LoanExtension, String> rejectionReasonColumn;

	@FXML
	private Label errorLabel;

	@FXML
	private ObservableList<LoanExtension> loanExtensionList = FXCollections.observableArrayList();

	/**
	 * Initializes table columns and fetches loan extension history.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories - Fetch
	 * loan extension history for subscriber - Hide sensitive columns
	 */

	@FXML
	public void initialize() {
		System.out.println("Subscriber ID in initialize: " + setSubscriberId); // Check if this is not null

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
		rejectionReasonColumn.setCellValueFactory(new PropertyValueFactory<>("rejectionReason"));

		// Set the table data
		loanExtensionTable.setItems(loanExtensionList);

		// Call fetchLoanExtensionHistory to request data from the server
		fetchLoanExtensionHistory();
		hideColumns();
	}


    /**
     * Fetches loan extension history for specific subscriber.
     */
	public void fetchLoanExtensionHistory() {
		System.out.println("Fetching loan extension history... Subscriber ID: " + setSubscriberId);
		if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
			new Thread(() -> {
				try {
					ClientUI.chat.accept("GET_LOAN_EXTENSION_HISTORY:" + setSubscriberId);
				} catch (NumberFormatException e) {
					Platform.runLater(() -> errorLabel.setText("Invalid subscriber ID format."));
					e.printStackTrace();
				} catch (Exception e) {
					Platform.runLater(() -> errorLabel.setText("Failed to fetch loan extension history."));
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
			loanExtensionList.clear();
			loanExtensionList.addAll(loanExtensions);
		});
	}

	/**
	 * Hides sensitive table columns.
	 * 
	 * Columns hidden: - Subscriber ID - Book ID - Extension ID
	 */
	@FXML
	public void hideColumns() {
		subscriberIdColumn.setVisible(false); // Hide the subscriber ID column
		bookIdColumn.setVisible(false);
		extensionIdColumn.setVisible(false); // Hide the extension ID column
	}

	  /**
     * Handles navigation back to subscriber history menu.
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