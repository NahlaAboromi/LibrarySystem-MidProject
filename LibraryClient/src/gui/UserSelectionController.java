package gui;

import javafx.scene.image.Image;
import java.io.IOException;
import java.net.URL;

import controllers.LibrarianLoginController;
import controllers.SubscriberLoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UserSelectionController {

	@FXML
	private Button subscriberButton;

	@FXML
	private Button Search;

	@FXML
	private Button librarianButton;

	private Stage stage;
	private Image icon;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Initializes user selection interface.
	 * 
	 * Actions: - Sets button event handlers - Loads application icon
	 */
	@FXML
	private void initialize() {
		subscriberButton.setOnAction(event -> handleSubscriberSelection(event));
		librarianButton.setOnAction(event -> handleLibrarianSelection(event));
		Search.setOnAction(event -> handleGoToHome(event));

		// Load the icon from resources
		URL iconUrl = getClass().getResource("/common/resources/icon.png");
		if (iconUrl != null) {
			icon = new Image(iconUrl.toString());
		} else {
			System.out.println("Icon file not found in resources!");
		}
	}

	/**
	 * Handles subscriber login screen navigation.
	 * 
	 * Actions: - Closes current window - Loads subscriber login FXML - Sets up
	 * login stage - Applies application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	private void handleSubscriberSelection(ActionEvent event) {
		System.out.println("Subscriber selected");
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/gui_subscriber/SubscriberLoginController.fxml"));
			Parent root = loader.load();

			SubscriberLoginController loginController = loader.getController();
			Stage loginStage = new Stage();
			loginController.setStage(loginStage);

			Scene scene = new Scene(root, 600, 450);
			scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

			loginStage.setScene(scene);
			loginStage.setTitle("Subscriber Login");
			if (icon != null)
				loginStage.getIcons().add(icon);
			loginStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles librarian login screen navigation.
	 * 
	 * Actions: - Closes current window - Loads librarian login FXML - Sets up login
	 * stage - Applies application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	private void handleLibrarianSelection(ActionEvent event) {
		System.out.println("Librarian selected");
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianLoginView.fxml"));
			Parent root = loader.load();

			LibrarianLoginController loginController = loader.getController();
			Stage loginStage = new Stage();
			loginController.setStage(loginStage);

			Scene scene = new Scene(root, 600, 400);
			scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

			loginStage.setScene(scene);
			loginStage.setTitle("Librarian Login");
			if (icon != null)
				loginStage.getIcons().add(icon);
			loginStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates to book search view.
	 * 
	 * Actions: - Closes current window - Loads book search FXML - Sets full-screen
	 * book search stage - Applies application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	private void handleGoToHome(ActionEvent event) {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BookSearchView.fxml"));
			Parent root = loader.load();
			// Get the controller instance
			BookSearchController bookSearchController = loader.getController();
			bookSearchController.setIsReader();

			Stage home = new Stage();
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

			// Set the window size to fit the screen
			double screenWidth = screenBounds.getWidth();
			double screenHeight = screenBounds.getHeight();

			// Create a scene with the screen dimensions
			Scene scene = new Scene(root, screenWidth, screenHeight);
			home.setScene(scene);
			home.setTitle("BLIB");
			home.setMaximized(true); // Maximize the window to fit the screen

			if (icon != null) {
				home.getIcons().add(icon);
			}

			home.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes and displays user selection stage.
	 * 
	 * Actions: - Loads user selection FXML - Sets stage properties - Applies
	 * application icon
	 * 
	 * @param primaryStage Main application stage
	 * @throws Exception If stage initialization fails
	 */
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
		Parent root = loader.load();

		UserSelectionController controller = loader.getController();
		controller.setStage(primaryStage);

		Scene scene = new Scene(root, 400, 450);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Home");

		if (icon != null)
			primaryStage.getIcons().add(icon);
		primaryStage.show();
	}
}
