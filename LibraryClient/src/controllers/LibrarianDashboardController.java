package controllers;

import java.io.IOException;
import java.net.URL;

import gui.BookSearchController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for librarian dashboard management.
 * 
 * Responsibilities: - Manage navigation between different librarian functions -
 * Preserve librarian context - Handle screen transitions - Apply consistent UI
 * branding
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class LibrarianDashboardController {
	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private static Image icon; // Static variable to hold the application icon

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
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

	/**
	 * Handles navigation back to user selection screen.
	 * 
	 * Actions: - Close current window - Load user selection FXML - Configure new
	 * stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleGoToHome(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserSelectionController.fxml"));
		Parent root = loader.load();

		Stage home = new Stage();
		Scene scene = new Scene(root);
		home.setScene(scene);
		home.setTitle("Home");

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			home.getIcons().add(icon);
		}

		home.show();
	}

	/**
	 * Handles navigation to book order completion screen.
	 * 
	 * Actions: - Close current window - Load order completion FXML - Set librarian
	 * name - Configure stage with: - Scene - Title - Error stylesheet - Application
	 * icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleCompleteOrder(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/CompleteOrderBook.fxml"));
		Parent root = loader.load();
		Stage searchStage = new Stage();
		Scene scene = new Scene(root);
		searchStage.setScene(scene);
		searchStage.setTitle("Fulfilled Order");
		CompleteOrderBook CompleteOrderBook = loader.getController();
		CompleteOrderBook.setLibrarianName(librarianName);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			searchStage.getIcons().add(icon);
		}

		searchStage.show();
	}

	/**
	 * Handles navigation to book borrowing method selection.
	 * 
	 * Actions: - Close current window - Load borrow method selection FXML - Set
	 * librarian name - Configure stage with: - Scene - Title - Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleBorrowBook(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/BorrowMethodSelection.fxml"));
		Parent root = loader.load();
		BorrowMethodSelectionController BorrowMethodSelectionController = loader.getController();
		BorrowMethodSelectionController.setLibrarianName(librarianName);
		Stage selectionStage = new Stage();
		Scene scene = new Scene(root,700,500);
		selectionStage.setScene(scene);
		selectionStage.setTitle("Select Borrow Method");

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			selectionStage.getIcons().add(icon);
		}

		selectionStage.show();
	}

	/**
	 * Handles navigation to subscriber sign-up screen.
	 * 
	 * Actions: - Close current window - Load subscriber sign-up FXML - Set
	 * librarian name - Configure stage with: - Scene dimensions - Title - Error
	 * stylesheet - Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void openSubscriberSignUp(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/SubscriberSignUpView.fxml"));
		Parent root = loader.load();
		SubscriberSignUpController SubscriberSignUpController = loader.getController();
		SubscriberSignUpController.setLibrarianName(librarianName);
		Stage signUpStage = new Stage();
		Scene scene = new Scene(root, 600,700);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		signUpStage.setScene(scene);
		  
		signUpStage.setTitle("Sign Up New Subscriber");

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			signUpStage.getIcons().add(icon);
		}

		signUpStage.show();
	}

	/**
	 * Handles navigation to librarian messages view.
	 * 
	 * Actions: - Close current window - Load librarian messages FXML - Set
	 * librarian name - Configure stage with: - Scene - Title - Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleViewMessages(ActionEvent event) {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianMessagesView.fxml"));
			Parent root = loader.load();
			LibrarianMessagesController LibrarianMessagesController = loader.getController();
			LibrarianMessagesController.setLibrarianName(librarianName);
			Stage messagesStage = new Stage();
			Scene scene = new Scene(root);
			messagesStage.setScene(scene);
			messagesStage.setTitle("Extension Messages");

			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				messagesStage.getIcons().add(icon);
			}

			messagesStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles navigation to book return form.
	 * 
	 * Actions: - Close current window - Load return book FXML - Set librarian name
	 * - Configure stage with: - Scene - Title - Error stylesheet - Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleReturnBook(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/ReturnBookForm.fxml"));
		Parent root = loader.load();
		ReturnBookController ReturnBookController = loader.getController();
		ReturnBookController.setLibrarianName(librarianName);
		Stage searchStage = new Stage();
		Scene scene = new Scene(root);
		searchStage.setScene(scene);
		searchStage.setTitle("Return Book");
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			searchStage.getIcons().add(icon);
		}

		searchStage.show();
	}

	/**
	 * Handles navigation to subscriber ID entry form.
	 * 
	 * Actions: - Close current window - Load subscriber ID form FXML - Set
	 * librarian name - Configure stage with: - Scene - Title - Error stylesheet -
	 * Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleViewReaderCard(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		System.out.println("lib name2:" + librarianName);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/EnterSubscriberIDForm.fxml"));

		Parent root = loader.load();

		EnterSubscriberIDFormController enterSubscriberIDForm = loader.getController();
		enterSubscriberIDForm.setLibrarianName(librarianName);

		Stage searchStage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		searchStage.setScene(scene);
		searchStage.setTitle("View Reader Card");

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			searchStage.getIcons().add(icon);
		}

		searchStage.show();
	}

	/**
	 * Handles navigation to report selection screen.
	 * 
	 * Actions: - Close current librarian dashboard window - Load report selection
	 * FXML - Set librarian name in destination controller - Configure new stage
	 * with: - Scene - Title - Application icon
	 * 
	 * Key Features: - Preserves librarian context - Applies consistent UI branding
	 * - Provides clean screen transition
	 * 
	 * @param event ActionEvent triggering report view navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleViewReports(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/SelectReport.fxml"));
		Parent root = loader.load();
		SelectReportController SelectReportController = loader.getController();
		SelectReportController.setLibrarianName(librarianName);
		Stage searchStage = new Stage();
		Scene scene = new Scene(root);
		searchStage.setScene(scene);
		searchStage.setTitle("Select Report");

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			searchStage.getIcons().add(icon);
		}

		searchStage.show();
	}

	/**
	 * Handles navigation to book search view.
	 * 
	 * Actions: - Close current window - Load book search FXML - Set librarian
	 * context - Configure stage with: - Full-screen dimensions - Title - Maximized
	 * view - Application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException            If FXML loading fails
	 * @throws ClassNotFoundException If controller class not found
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
			bookSearchController.setIsLibrarian();
			bookSearchController.setLibrarianName(librarianName);

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
}