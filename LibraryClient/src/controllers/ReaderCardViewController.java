package controllers;

import java.io.IOException;
import java.net.URL;

import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.Subscriber;

/**
 * Controller for managing subscriber's reader card view.
 * 
 * Responsibilities: - Display subscriber details - Manage navigation to
 * different subscriber activities - Support librarian and subscriber contexts
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ReaderCardViewController {
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private boolean isLibrarian = false;

	public void setLibrarian(boolean librarian) {
		this.isLibrarian = librarian;
	}

	@FXML
	private Label subscriberIdLabel;
	@FXML
	private Label subscriberNameLabel;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label subscriberEmailLabel;
	@FXML
	private Label subscriberPhoneNumberLabel;
	@FXML
	private Label statusLabel;
	private static Image icon; // Static variable to hold the application icon

	private static ReaderCardViewController instance;

	public ReaderCardViewController() {
		instance = this;
	}

	public static ReaderCardViewController getInstance() {
		return instance;
	}

	private String setSubscriberId;

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

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
		System.out.println("Subscriber ID set to: " + id);
	}

	/**
	 * Loads and displays subscriber information.
	 * 
	 * Actions: - Populate labels with subscriber details - Show ID, name, username,
	 * email, phone, status
	 * 
	 * @param subscriber Subscriber object containing details
	 */
	public void loadSubscriber(Subscriber subscriber) {
		subscriberIdLabel.setText("Subscriber ID: " + subscriber.getSubscriberId());
		subscriberNameLabel.setText("Name: " + subscriber.getSubscriberName());
		usernameLabel.setText("Username: " + subscriber.getUsername());
		subscriberEmailLabel.setText("Email: " + subscriber.getSubscriberEmail());
		subscriberPhoneNumberLabel.setText("Phone: " + subscriber.getSubscriberPhoneNumber());
		statusLabel.setText("Status: " + subscriber.getStatus());
	}

	/**
	 * Handles navigation to subscriber activity history.
	 * 
	 * Actions: - Close current window - Load subscriber history menu - Preserve
	 * subscriber and librarian context - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleActivityHistoryButton(ActionEvent event) throws IOException {
		try {
			// סגור את החלון הנוכחי
			Stage currentStage = (Stage) statusLabel.getScene().getWindow();
			currentStage.close();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/MenuOfSubscriberHistory.fxml"));
			Parent root = loader.load();

			// קבל את הבקר לאחר טעינת ה-FXML
			MenuOfSubscriberHistoryController menuOfHistory = loader.getController();

			menuOfHistory.setSubscriberId(subscriberIdLabel.getText().replace("Subscriber ID: ", "").trim());
			menuOfHistory.setLibrarian(true);
			menuOfHistory.setLibrarianName(librarianName);

			Stage home = new Stage();
			Scene scene = new Scene(root, 540, 550);
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			}
			home.setScene(scene);
			home.setTitle("Activity History");

			home.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation to subscriber loan history.
	 * 
	 * Actions: - Close current window - Load loans book view - Set subscriber and
	 * librarian context - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleViewLoans(ActionEvent event) {
		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LoansBookReaderCard.fxml"));
			LoansBookReaderCardController controller = new LoansBookReaderCardController();
			loader.setController(controller);
			controller.setSubscriberId(setSubscriberId);
			controller.setLibrarianName(librarianName);
			Parent root = loader.load();
			System.out.println("lib name4:" + librarianName);

			Stage newStage = new Stage();
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("Loans Book");
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				newStage.getIcons().add(icon);
			}
			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation to subscriber loan extensions history.
	 * 
	 * Actions: - Close current window - Load extensions view - Set subscriber and
	 * librarian context - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleViewExtensions(ActionEvent event) {
		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ExtensionsReaderCard.fxml"));
			ExtensionsReaderCardController controller = new ExtensionsReaderCardController();
			loader.setController(controller);
			controller.setSubscriberId(setSubscriberId);
			controller.setLibrarianName(librarianName);
			Parent root = loader.load();

			Stage newStage = new Stage();
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("Extensions");
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				newStage.getIcons().add(icon);
			}
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation to subscriber's ordered books view.
	 * 
	 * Actions: - Close current window - Load ordered books view - Set subscriber
	 * and librarian context - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleViewOrders(ActionEvent event) {
		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/OrderedBooksReaderCard.fxml"));
			OrderedBooksReaderCardController controller = new OrderedBooksReaderCardController();
			loader.setController(controller);
			controller.setSubscriberId(setSubscriberId);
			controller.setLibrarianName(librarianName);

			Parent root = loader.load();

			Stage newStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle("Ordered Books");
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				newStage.getIcons().add(icon);
			}
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles return to subscriber ID entry form.
	 * 
	 * Actions: - Close current window - Load subscriber ID entry FXML - Set
	 * librarian name - Configure stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleReturn(ActionEvent event) {
		System.out.println("Returning to the previous GUI...");
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/EnterSubscriberIDForm.fxml")); // Replace
			Parent root = loader.load();
			EnterSubscriberIDFormController controller = loader.getController();
			controller.setLibrarianName(librarianName);
			// Set up the stage and scene
			Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("View Reader Card");
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				stage.getIcons().add(icon);
			}
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}