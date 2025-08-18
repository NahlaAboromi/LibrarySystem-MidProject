package gui;

import java.net.SocketException;
import java.net.URL;
import java.io.IOException;

import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ClientConnectionController {
	@FXML
	private TextField id_IP;
	@FXML
	private TextField portField;
	@FXML
	private Label errorLabel;
	@FXML
	private Button SENDID;

	private static Image icon;

	/**
	 * Loads application icon from resources. Ensures icon is loaded only once.
	 */
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
	 * Establishes connection to server and navigates to book search view.
	 * 
	 * Validation steps: - Checks IP address input - Validates port number -
	 * Initializes client connection - Loads book search interface
	 * 
	 * @param event ActionEvent triggering connection attempt
	 */
	public void Connect(ActionEvent event) {
		String host = id_IP.getText();
		String portText = portField.getText();

		if (host.trim().isEmpty()) {
			errorLabel.setText("Please enter IP address");
			return;
		}

		if (portText.trim().isEmpty()) {
			errorLabel.setText("Please enter Port number");
			return;
		}

		try {
			int port = Integer.parseInt(portText);

			try {
				// Initialize the chat connection
				ClientUI.initChat(host, port);

				// Load the BookSearchController
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BookSearchView.fxml"));
				Parent root = loader.load();

				// Get the controller instance
				BookSearchController bookSearchController = loader.getController();
				bookSearchController.setIsReader();

				// Create a new stage for the BookSearchController
				Stage bookSearchStage = new Stage();
				Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

				// Set the window size to fit the screen
				double screenWidth = screenBounds.getWidth();
				double screenHeight = screenBounds.getHeight();

				// Create a scene with the screen dimensions
				Scene scene = new Scene(root, screenWidth, screenHeight);
				bookSearchStage.setScene(scene);
				bookSearchStage.setTitle("Book Search");

				bookSearchStage.setMaximized(true); // Maximize the window to fit the screen
				// Set the application icon
				if (icon != null) {
					bookSearchStage.getIcons().add(icon);
				}

				// Hide the current window
				((Node) event.getSource()).getScene().getWindow().hide();

				// Show the BookSearchController stage
				bookSearchStage.show();

				// Fetch all books (optional, depending on your logic)
				ClientUI.chat.accept("GET_ALL_SUBSCRIBERS");

			} catch (Exception e) {
				System.out.println(e.getMessage());
				errorLabel.setText("An error occurred: " + e.getMessage());
			}
		} catch (NumberFormatException e) {
			errorLabel.setText("Port must be a valid number");
		}
	}

	/**
	 * Navigates back to user selection screen.
	 * 
	 * Actions: - Closes current window - Loads user selection FXML - Sets
	 * application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
	 */
	@FXML
	private void hendleGoToHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
			Parent root = loader.load();

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("home");

			if (icon != null) {
				home.getIcons().add(icon);
			}

			home.show();
		} catch (IOException e) {
			errorLabel.setText("Error opening reset password screen");
			e.printStackTrace();
		}
	}

	/**
	 * Initializes and displays connection setup stage.
	 * 
	 * Actions: - Loads connection setup FXML - Sets stage properties - Applies
	 * application icon
	 * 
	 * @param primaryStage Main application stage
	 * @throws Exception If stage initialization fails
	 */
	public void start(Stage primaryStage) throws Exception {
		try {
			loadIcon(); // Load the icon
			Parent root = FXMLLoader.load(getClass().getResource("/gui/ConnectionWithIPandPortFrame.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Connection Setup");
			primaryStage.setScene(scene);

			primaryStage.setWidth(400);
			primaryStage.setHeight(300);

			if (icon != null) {
				primaryStage.getIcons().add(icon);
			}

			primaryStage.show();
		} catch (IOException e) {
			System.err.println("Failed to load the connection frame: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
