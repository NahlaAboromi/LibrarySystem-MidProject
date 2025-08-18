package controllers;

import java.io.IOException;
import java.net.URL;

import client.ChatClient;
import client.ClientUI;
import gui.BookSearchController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.Subscriber;

/**
 * Controller managing subscriber menu interactions
 * 
 * Handles navigation and actions for subscriber dashboard Implements Singleton
 * pattern for global access
 * 
 * @author [Developer Name]
 * @version 1.0
 * @since 2025-01-25
 */
public class SubscriberMenuController {

	private String subscriberID;
	private Label headerLabel; // Ensure this matches the fx:id in FXML

	private static Image icon; // Static variable to hold the icon

	public void setSubscriberId(String id) {
		this.subscriberID = id;
	}

	private static SubscriberMenuController instance;

	public SubscriberMenuController() {
		instance = this;
	}

	public static SubscriberMenuController getInstance() {
		return instance;
	}

	/**
	 * Handles navigation to subscriber activity history
	 * 
	 * Key Actions: - Closes current window - Loads history menu screen - Passes
	 * subscriber ID - Sets application icon
	 * 
	 * @param event Triggering navigation event
	 * @throws IOException if screen loading fails
	 */
	@FXML
	private void handleActivityHistoryButton(ActionEvent event) throws IOException {
		try {
			// Hide the current window
			((Node) event.getSource()).getScene().getWindow().hide();

			// Load FXML and initialize the controller
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/MenuOfSubscriberHistory.fxml"));
			Parent root = loader.load(); // Load the FXML file

			// Get the controller of the loaded FXML
			MenuOfSubscriberHistoryController menuOfHistory = loader.getController();

			// Pass data to the controller
			menuOfHistory.setSubscriberId(subscriberID);

			// Show the new window
			Stage home = new Stage();
			Scene scene = new Scene(root, 540, 400);
			home.setScene(scene);
			home.setTitle("History Menu");

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			home.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manages subscriber personal details update process
	 * 
	 * Workflow: - Validates subscriber ID - Fetches subscriber details from server
	 * - Loads subscriber details form - Sets application icon
	 * 
	 * @param event Triggering update event
	 * @throws IOException           if screen loading fails
	 * @throws NumberFormatException if ID parsing fails
	 */
	@FXML
	private void handleUpdatePersonalDetailsButton(ActionEvent event) throws IOException {
		subscriberID = subscriberID.trim();
		int id;
		try {
			id = Integer.parseInt(subscriberID);
		} catch (NumberFormatException e) {
			return;
		}
		Subscriber subscriberRequestFethDetails = new Subscriber(id, "", "", "", "", "", 2); // פרטים נוספים יכולים
																								// להיות ריקים
		ClientUI.chat.accept(subscriberRequestFethDetails);
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/SubscriberDetails.fxml"));
		Parent root = loader.load();
		// Pass the subscriber to the SubscriberFormController
		SubscriberFormController subscriberFormController = loader.getController();
		subscriberFormController.loadSubscriber(ChatClient.s1);
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		primaryStage.setTitle("Subscriber Details");

		// Load and set the icon
		loadIcon();
		if (icon != null) {
			primaryStage.getIcons().add(icon);
		} else {
			System.err.println("Failed to load application icon.");
		}

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Handles borrowing extension navigation
	 * 
	 * Responsibilities: - Close current window - Load borrowing extension screen -
	 * Initialize borrowing fields - Set application icon
	 * 
	 * @param event Triggering borrowing extension event
	 * @throws IOException if screen loading fails
	 */
	@FXML
	private void handleExtendBorrowingButton(ActionEvent event) throws IOException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/ExtendBorrowingView.fxml"));
			Parent root = loader.load();

			ExtendBorrowingController controller = loader.getController();
			controller.setSubscriberId(subscriberID);
			controller.initializeFields(); // יאתחל את התאריך הנוכחי ומספר המנוי

			Stage stage = new Stage();
			Scene scene = new Scene(root,500,470);
			stage.setScene(scene);
			stage.setTitle("Extend Borrowing");
			scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				stage.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles book search navigation for subscribers
	 * 
	 * Key Features: - Dynamic screen sizing - Maximizes window to screen dimensions
	 * - Passes subscriber context
	 * 
	 * @param event Triggering navigation event
	 * @throws IOException            if screen loading fails
	 * @throws ClassNotFoundException if controller not found
	 */
	@FXML
	private void handleSearchButton(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			// Hide the current window
			((Node) event.getSource()).getScene().getWindow().hide();

			// Load FXML and initialize the controller
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BookSearchView.fxml"));
			Parent root = loader.load(); // Load the FXML file

			// Get the controller of the loaded FXML
			BookSearchController bookSearchController = loader.getController();

			// Pass data to the controller
			bookSearchController.setIsSubscriber();
			bookSearchController.setSubscriberId(subscriberID);

			// Show the new window
			Stage home = new Stage();
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

			// Set the window size to fit the screen
			double screenWidth = screenBounds.getWidth();
			double screenHeight = screenBounds.getHeight();

			// Create a scene with the screen dimensions
			Scene scene = new Scene(root, screenWidth, screenHeight);
			home.setScene(scene);
			home.setTitle("Search Book");
			home.setMaximized(true); // Maximize the window to fit the screen

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			home.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manages book order process navigation
	 * 
	 * Workflow: - Closes current window - Loads book order form - Sets subscriber
	 * ID explicitly - Applies custom stylesheet - Sets application icon
	 * 
	 * @param event Triggering navigation event
	 * @throws IOException            if screen loading fails
	 * @throws ClassNotFoundException if controller not found
	 */
	@FXML
	private void handleOrderButton(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/OrderBookForm.fxml"));

			// Manually set the controller before loading the FXML
			OrderBookController orderBookController = new OrderBookController();
			orderBookController.setSubscriberId(subscriberID); // Set Subscriber ID before loading
			System.out.printf("subscriber ID is:%s\n", subscriberID);
			loader.setController(orderBookController); // Set the controller explicitly

			// Now load the FXML
			Parent root = loader.load();

			// Get the controller from the loaded FXML (should be the same as what we
			// manually set)
			OrderBookController orderBookController1 = loader.getController();
			System.out.println("Setting Subscriber ID: " + subscriberID);
			orderBookController1.setSubscriberId(subscriberID); // Ensure it's set here as well

			Stage home = new Stage();
			Scene scene = new Scene(root, 380, 400);
			home.setScene(scene);
			home.setTitle("Order Book Form");
			scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			home.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates back to user selection home screen
	 * 
	 * Responsibilities: - Close current subscriber menu - Load home screen - Set
	 * application icon
	 * 
	 * @param event Triggering navigation event
	 * @throws IOException            if screen loading fails
	 * @throws ClassNotFoundException if controller not found
	 */
	@FXML
	private void handleHomeButton(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
			Parent root = loader.load();

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle("Home");

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			home.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method for loading application icon
	 * 
	 * Implements lazy loading and singleton pattern for application icon
	 * 
	 * Key Responsibilities: - Load icon only once - Handle potential resource
	 * loading errors - Provide centralized icon management
	 * 
	 * @return Image application icon (can be null if loading fails)
	 */
	private void loadIcon() {
		if (icon == null) {
			// Adjust the path to point to the resources directory
			String iconPath = "/common/resources/icon.png";
			URL iconUrl = getClass().getResource(iconPath);
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.err.println("Icon file not found in resources! Expected path: " + iconPath);
			}
		}
	}
}