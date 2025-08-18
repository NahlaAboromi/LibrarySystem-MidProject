package controllers;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Controller for selecting and navigating library reports.
 * 
 * Responsibilities: - Manage report selection interface - Navigate to specific
 * report views - Preserve librarian context - Handle screen transitions
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class SelectReportController {
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private static Image icon;

	/**
	 * Handles navigation to borrowing time report screen.
	 * 
	 * Actions: - Close current window - Load borrowing time report FXML - Set
	 * librarian name - Configure stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleBorrowingTimeReport(ActionEvent event) throws IOException {
		// Handle viewing borrowing time report
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/BorrowingTimeReport.fxml"));
		Parent root = loader.load();
		BorrowingTimeReportController BorrowingTimeReportController = loader.getController();
		BorrowingTimeReportController.setLibrarianName(librarianName);
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Borrowing Time Report");

		// Load and set the icon
		loadIcon();
		if (icon != null) {
			stage.getIcons().add(icon);
		} else {
			System.err.println("Failed to load application icon.");
		}

		stage.show();
	}

	/**
	 * Handles navigation to subscriber status report screen.
	 * 
	 * Actions: - Close current window - Load subscriber status report FXML - Set
	 * librarian name - Configure stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleSubscriberStatusReport(ActionEvent event) throws IOException {
		// Handle viewing subscriber status report
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/SubscriberStatusReport.fxml"));
		Parent root = loader.load();
		SubscriberStatusReportController SubscriberStatusReportController = loader.getController();
		SubscriberStatusReportController.setLibrarianName(librarianName);
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Subscriber Status Report");

		// Load and set the icon
		loadIcon();
		if (icon != null) {
			stage.getIcons().add(icon);
		} else {
			System.err.println("Failed to load application icon.");
		}

		stage.show();
	}

	/**
	 * Handles return to librarian dashboard.
	 * 
	 * Actions: - Close current window - Load librarian dashboard FXML - Set
	 * librarian name - Configure stage - Apply application icon
	 * 
	 * @param event ActionEvent triggering navigation
	 * @throws IOException If FXML loading fails
	 */
	@FXML
	private void handleBack(ActionEvent event) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/LibrarianDashboardView.fxml"));
		Parent root = loader.load();
		LibrarianDashboardController librarianDashboard = loader.getController();
		librarianDashboard.setLibrarianName(librarianName);
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Librarian Dashboard");

		// Load and set the icon
		loadIcon();
		if (icon != null) {
			stage.getIcons().add(icon);
		} else {
			System.err.println("Failed to load application icon.");
		}

		stage.show();
	}

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides detailed error logging
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