package controllers;

import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;

import client.ChatClient;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.LibrarianMessage;
import logic.Subscriber;

/**
 * Controller for managing librarian messages view.
 * 
 * Responsibilities: - Display librarian messages - Fetch messages from server -
 * Manage table view configuration - Handle navigation between screens
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class LibrarianMessagesController {
	@FXML
	private TableView<LibrarianMessage> messagesTable;

	@FXML
	private TableColumn<LibrarianMessage, String> messageTextColumn;

	@FXML
	private TableColumn<LibrarianMessage, Date> messageDateColumn;

	@FXML
	private Label errorLabel;

	@FXML
	private Button backButton;

	private ObservableList<LibrarianMessage> messagesList = FXCollections.observableArrayList();

	private static LibrarianMessagesController instance;

	public LibrarianMessagesController() {
		instance = this;
	}

	public static LibrarianMessagesController getInstance() {
		return instance;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	/**
	 * Initializes table columns and fetches librarian messages.
	 * 
	 * Actions: - Configure table column bindings - Set cell value factories -
	 * Request messages from server
	 */
	@FXML
	public void initialize() {
		messageTextColumn.setCellValueFactory(new PropertyValueFactory<>("messageText"));
		messageDateColumn.setCellValueFactory(new PropertyValueFactory<>("messageDate"));

		messagesTable.setItems(messagesList);
		try {
			ClientUI.chat.accept("GET_LIBRARIAN_MESSAGES");
		} catch (Exception e) {
			errorLabel.setText("Failed to fetch messages");
			e.printStackTrace();
		}
	}

	/**
	 * Updates table with librarian messages.
	 * 
	 * @param messages List of librarian messages to display
	 */
	public void updateTable(ArrayList<LibrarianMessage> messages) {
		Platform.runLater(() -> {
			messagesList.clear();
			messagesList.addAll(messages);
		});
	}

	/**
	 * Handles navigation back to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard FXML - Preserve
	 * librarian context - Set application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
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

	private static Image icon; // Static variable to hold the application icon

	/**
	 * Loads application icon from resources.
	 * 
	 * Ensures: - Icon loaded only once - Fallback for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = LibrarianDashboardController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}
}