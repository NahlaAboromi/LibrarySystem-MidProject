package controllers;

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

import java.io.IOException;
import java.net.URL;

/**
 * Controller for managing book borrowing method selection.
 * 
 * Responsibilities: - Provide navigation between different borrowing methods -
 * Manage librarian context - Handle screen transitions - Apply consistent UI
 * branding
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class BorrowMethodSelectionController {

	private static Image icon; // Static variable to hold the application icon
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides fallback logging for missing icon
	 */
	private static void loadIcon() {
		if (icon == null) {
			URL iconUrl = BorrowMethodSelectionController.class.getResource("/common/resources/icon.png");
			if (iconUrl != null) {
				icon = new Image(iconUrl.toString());
			} else {
				System.out.println("Icon file not found in resources!");
			}
		}
	}

	/**
	 * Handles manual book borrowing method selection.
	 * 
	 * Actions: - Close current window - Load manual borrow screen - Preserve
	 * librarian context
	 * 
	 * @param event ActionEvent triggering manual borrow selection
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleManualBorrow(ActionEvent event) throws IOException {
		openBorrowScreen("/gui_librarian/BorrowBook.fxml", "Manual Borrow", event);
	}

	/**
	 * Handles barcode-based book borrowing method selection.
	 * 
	 * Actions: - Close current window - Load barcode borrow screen - Preserve
	 * librarian context
	 * 
	 * @param event ActionEvent triggering barcode borrow selection
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleBarcodeBorrow(ActionEvent event) throws IOException {
		openBorrowScreenBarcode("/gui_librarian/BarcodeBorrowBook.fxml", "Barcode Borrow", event);
	}

	/**
	 * Handles return to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard - Preserve
	 * librarian context
	 * 
	 * @param event ActionEvent triggering return to dashboard
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleReturn(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		// Load the previous screen (Librarian Dashboard)
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

		// Set the window size to fit the screen
		double screenWidth = screenBounds.getWidth();
		double screenHeight = screenBounds.getHeight();
		System.out.println("screen width"+screenWidth+"screen height:"+screenHeight);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
		Parent root = loader.load();
		LibrarianDashboardController librarianDashboard = loader.getController();
		librarianDashboard.setLibrarianName(librarianName);
		// Set up the stage and scene
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		stage.setTitle("Librarian Dashboard");

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			stage.getIcons().add(icon);
		}

		stage.show();
	}

	/**
	 * Opens barcode-based book borrowing screen.
	 * 
	 * Actions: - Close current window - Load barcode borrow FXML - Set librarian
	 * context - Configure stage with: - Error style sheet - Title - Application
	 * icon
	 * 
	 * @param fxmlPath Path to barcode borrow FXML
	 * @param title    Screen title
	 * @param event    Triggering action event
	 * @throws IOException If FXML loading fails
	 */
	private void openBorrowScreenBarcode(String fxmlPath, String title, ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		Parent root = loader.load();
		BarcodeBorrowBookController BarcodeBorrowBookController = loader.getController();
		BarcodeBorrowBookController.setLibrarianName(librarianName);
		Stage borrowStage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		borrowStage.setScene(scene);
		borrowStage.setTitle(title);

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			borrowStage.getIcons().add(icon);
		}

		borrowStage.show();
	}

	/**
	 * Opens manual book borrowing screen.
	 * 
	 * Actions: - Close current window - Load manual borrow FXML - Set librarian
	 * context - Configure stage with: - Specific scene dimensions - Error style
	 * sheet - Resizable window - Centered screen position - Application icon
	 * 
	 * @param fxmlPath Path to manual borrow FXML
	 * @param title    Screen title
	 * @param event    Triggering action event
	 * @throws IOException If FXML loading fails
	 */
	private void openBorrowScreen(String fxmlPath, String title, ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		Parent root = loader.load();
		BorrowBookController BorrowBookController = loader.getController();
		BorrowBookController.setLibrarianName(librarianName);
		Scene scene = new Scene(root, 600, 500);
		scene.getStylesheets().add(getClass().getResource("/gui/errorStyle.css").toExternalForm());

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(true); // Optional
		stage.centerOnScreen(); // Optional

		// Load and set the application icon
		loadIcon();
		if (icon != null) {
			stage.getIcons().add(icon);
		}

		stage.show();
	}
}