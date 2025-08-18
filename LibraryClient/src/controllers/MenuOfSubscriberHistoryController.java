package controllers;

import java.io.IOException;
import java.net.URL;

import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controller for managing subscriber history menu.
 * 
 * Responsibilities: - Provide navigation to different history views - Support
 * both subscriber and librarian contexts - Manage screen transitions - Preserve
 * user context
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class MenuOfSubscriberHistoryController {
	private boolean isLibrarian = false;

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarian(boolean librarian) {
		this.isLibrarian = librarian;
	}

	@FXML
	private Label headerLabel; // Header label

	@FXML
	private Button borrowingHistoryButton; // Button for Borrowing History

	@FXML
	private Button orderHistoryButton; // Button for Order History

	@FXML
	private Button passwordChangeHistoryButton; // Button for Password Change History

	@FXML
	private Button loanExtensionHistoryButton; // Button for Loan Extension History

	@FXML
	private Button returnBookHistoryButton; // Button for Return Book History

	@FXML
	private Button backToMenuButton; // Button to go back to the main menu

	private static Image icon; // Static variable to hold the application icon
	private String setSubscriberId;

	public void setSubscriberId(String id) {
		this.setSubscriberId = id;
	}

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = MenuOfSubscriberHistoryController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Helper method for screen navigation with consistent setup.
	 * 
	 * Features: - Load FXML from specified path - Configure new stage - Set window
	 * title - Apply application icon
	 * 
	 * @param fxmlPath   Path to FXML resource
	 * @param title      Window title
	 * @param controller Controller for the new screen
	 * @throws IOException If FXML loading fails
	 */
	private void navigateToScreen(String fxmlPath, String title, Object controller) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			loader.setController(controller);
			Parent root = loader.load();

			Stage newStage = new Stage();
			Scene scene = new Scene(root);
			newStage.setScene(scene);
			newStage.setTitle(title);

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				newStage.getIcons().add(icon);
			}

			newStage.show();
		} catch (IOException e) {
			headerLabel.setText("Failed to load the screen. Please try again.");
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation to return book history screen.
	 * 
	 * Actions: - Close current window - Set subscriber ID - Support librarian
	 * context - Navigate to return book history view
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleReturnBookHistory(ActionEvent event) {
		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		System.out.println("NAHLA ID: " + setSubscriberId);
		Stage currentStage = (Stage) headerLabel.getScene().getWindow();
		currentStage.close();
		System.out.println("nhlaid");
		ReturnBookHistoryController controller = new ReturnBookHistoryController();
		controller.setSubscriberId(setSubscriberId);
		if (this.isLibrarian) {
			controller.setLibrarian(true);
		}

		navigateToScreen("/gui_subscriber/ReturnBookHistory.fxml", "Return Book History", controller);
	}

	/**
	 * Handles navigation to borrowing history screen.
	 * 
	 * Actions: - Close current window - Set subscriber ID - Support librarian
	 * context - Navigate to borrowing history view
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleBorrowingHistory(ActionEvent event) {
		System.out.println("NAHLA ID: " + setSubscriberId);
		Stage currentStage = (Stage) headerLabel.getScene().getWindow();
		currentStage.close();
		System.out.println("NAHLA ID: " + setSubscriberId);
		BorrowedBooksHistoryController controller = new BorrowedBooksHistoryController();
		controller.setSubscriberId(setSubscriberId);
		if (this.isLibrarian) {
			controller.setLibrarian(true);
		}
		navigateToScreen("/gui_subscriber/BorrowedBooksHistory.fxml", "Borrowed Books History", controller);
	}

	/**
	 * Handles navigation to order history screen.
	 * 
	 * Actions: - Close current window - Set subscriber ID - Support librarian
	 * context - Navigate to order history view
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleOrderHistory(ActionEvent event) {
		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		Stage currentStage = (Stage) headerLabel.getScene().getWindow();
		currentStage.close();

		OrderedBooksHistoryController controller = new OrderedBooksHistoryController();
		controller.setSubscriberId(setSubscriberId);
		if (this.isLibrarian) {
			controller.setLibrarian(true);
		}
		navigateToScreen("/gui_subscriber/OrderedBooksHistory.fxml", "Ordered Books History", controller);
	}

	/**
	 * Handles navigation back to main menu.
	 * 
	 * Actions: - Determine navigation based on user role - Load appropriate FXML -
	 * Preserve subscriber context - Support librarian view - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	public void handleBackToMenu(ActionEvent event) {
		try {
			String fxmlPath;
			String windowTitle;

			// בדיקה האם זהו משתמש ספרנית
			if (isLibrarian) {
				fxmlPath = "/gui_librarian/ReaderCardView.fxml";
				windowTitle = "Reader Card";
			} else {
				fxmlPath = "/gui_subscriber/TheSubscribersMenu.fxml";
				windowTitle = "Subscribers Menu";
			}

			// בדיקת קיום הנתיב
			URL fxmlUrl = getClass().getResource(fxmlPath);
			if (fxmlUrl == null) {
				System.err.println("FXML file not found: " + fxmlPath);
				return;
			}

			FXMLLoader loader = new FXMLLoader(fxmlUrl);
			Parent root = loader.load();

			// קבל את הבקר לאחר טעינת ה-FXML
			Object controller = loader.getController();

			// העבר את מספר המנוי לבקר המתאים
			if (controller instanceof SubscriberMenuController) {
				((SubscriberMenuController) controller).setSubscriberId(setSubscriberId);
			} else if (controller instanceof ReaderCardViewController) {
				ReaderCardViewController readerCardController = (ReaderCardViewController) controller;
				readerCardController.setSubscriberId(setSubscriberId);
				readerCardController.setLibrarianName(librarianName);
				readerCardController.loadSubscriber(ChatClient.s1); // שימוש במתודה הקיימת במקום initialize

				if (this.isLibrarian) {
					readerCardController.setLibrarian(true);
				}
			}

			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.setTitle(windowTitle);

			// טעינת האייקון
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			}

			// סגירת החלון הנוכחי לפני פתיחת החלון החדש
			if (headerLabel != null && headerLabel.getScene() != null) {
				Stage currentStage = (Stage) headerLabel.getScene().getWindow();
				currentStage.close();
			}

			home.show();

		} catch (IOException e) {
			headerLabel.setText("Error opening menu.");
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation to subscriber changes history screen.
	 * 
	 * Actions: - Close current window - Set subscriber ID - Support librarian
	 * context - Navigate to subscriber changes history view
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handlePasswordChangeHistory(ActionEvent event) {
		Stage currentStage = (Stage) headerLabel.getScene().getWindow();
		currentStage.close();

		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		SubscriberChangesHistoryController controller = new SubscriberChangesHistoryController();
		controller.setSubscriberId(setSubscriberId);
		if (this.isLibrarian) {
			controller.setLibrarian(true);
		}
		navigateToScreen("/gui_subscriber/SubscriberChangesHistory.fxml", "Subscriber Change History", controller);
	}

	/**
	 * Handles navigation to loan extension history screen.
	 * 
	 * Actions: - Close current window - Set subscriber ID - Support librarian
	 * context - Navigate to loan extension history view
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleLoanExtensionHistory(ActionEvent event) {
		Stage currentStage = (Stage) headerLabel.getScene().getWindow();
		currentStage.close();

		System.out.println("Setting Subscriber ID: " + setSubscriberId);
		LoanExtensionHistoryController controller = new LoanExtensionHistoryController();
		controller.setSubscriberId(setSubscriberId);
		if (this.isLibrarian) {
			controller.setLibrarian(true);
		}
		navigateToScreen("/gui_subscriber/LoanExtensionHistory.fxml", "Loan Extension History", controller);
	}
}