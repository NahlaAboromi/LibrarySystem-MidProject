package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.ResponseWrapper;
import logic.YearMonth;
import javafx.scene.control.ChoiceBox;

import java.util.stream.IntStream;

/**
 * Controller for generating and displaying subscriber status reports
 * 
 * Manages: - Year and month selection - Line chart visualization of subscriber
 * status - Server communication for report generation
 * 
 * @author [Developer Name]
 * @version 1.0
 * @since 2025-01-25
 */
public class SubscriberStatusReportController {
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private static SubscriberStatusReportController instance;

	public SubscriberStatusReportController() {
		instance = this;
	}

	public static SubscriberStatusReportController getInstance() {
		return instance;
	}

	@FXML
	private ChoiceBox<Integer> yearChoiceBox;

	@FXML
	private ChoiceBox<String> monthChoiceBox;

	@FXML
	private LineChart<Number, Number> lineChart; // Changed to LineChart

	@FXML
	private NumberAxis xAxis; // Use NumberAxis for both axes

	@FXML
	private NumberAxis yAxis;

	@FXML
	private Label errorLabel;

	/**
	 * Initializes controller components
	 * 
	 * Configures: - Year choice box (2024-2030) - Month choice box - Default
	 * selections
	 */
	@FXML
	public void initialize() {

		// Initialize the yearChoiceBox with a range of years (e.g., 2000 to 2030)
		yearChoiceBox.getItems().addAll(IntStream.rangeClosed(2024, 2030).boxed().toList());

		// Initialize the monthChoiceBox with month names
		monthChoiceBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");

		// Set default selections
		yearChoiceBox.setValue(2025); // Default to the current year
		monthChoiceBox.setValue("January"); // Default to the first month

		// updateLineChart1();
	}

	/**
	 * Handles report generation request
	 * 
	 * Workflow: - Validates year and month selection - Converts month name to
	 * number - Sends report request to server
	 * 
	 * @param event Triggering action event
	 */
	@FXML
	private void handleView(ActionEvent event) {
		String month = monthChoiceBox.getValue();
		Integer year = yearChoiceBox.getValue();
		int monthNumber = 0;
		int yearNumber = 0;
		if (month != null && year != null) {
			// Convert month name to number
			monthNumber = monthChoiceBox.getSelectionModel().getSelectedIndex() + 1;
			yearNumber = year;
		}
		YearMonth yearmonth = new YearMonth(yearNumber, monthNumber);
//        ClientUI.chat.accept(yearmonth);
		ResponseWrapper request = new ResponseWrapper("StatusReport", yearmonth);
		ClientUI.chat.accept(request);

	}

	/**
	 * Updates line chart with subscriber status data
	 * 
	 * Key Features: - Thread-safe UI update - Dynamic chart population - Error
	 * handling for invalid data
	 * 
	 * @param arr ArrayList containing subscriber status data
	 */
	public void updateLineChart(ArrayList<int[]> arr) {
		Platform.runLater(() -> {
			// Ensure xAxis and yAxis are correctly configured
			xAxis.setLabel("Day of the Month");
			yAxis.setLabel("Subscribers Count");

			// Validate input data
			if (arr == null || arr.isEmpty()) {
				errorLabel.setText("No data available to update the chart.");
				lineChart.getData().clear(); // Clear the chart if no data
				return;
			}

			// Initialize data arrays
			int[] activeSubscribers = new int[31];
			int[] frozenSubscribers = new int[31];

			// Observable lists for active and frozen subscribers
			ObservableList<XYChart.Data<Number, Number>> activeSubscribersData = FXCollections.observableArrayList();
			ObservableList<XYChart.Data<Number, Number>> frozenSubscribersData = FXCollections.observableArrayList();

			// Loop through the input ArrayList<int[]> to extract data
			for (int day = 0; day < arr.size(); day++) { // Ensure day doesn't exceed 31
				int[] data = arr.get(day);

				// Validate that the array has exactly 2 elements
				if (data.length != 2) {
					System.err
							.println("Invalid data at day " + (day + 1) + ": Expected 2 elements, got " + data.length);
					continue; // Skip invalid data
				}

				activeSubscribers[day] = data[0];
				frozenSubscribers[day] = data[1];
			}

			// Populate observable lists
			for (int day = 1; day <= arr.size(); day++) {
				activeSubscribersData.add(new XYChart.Data<>(day, activeSubscribers[day - 1]));
				frozenSubscribersData.add(new XYChart.Data<>(day, frozenSubscribers[day - 1]));
			}

			// Create and configure series for active and frozen subscribers
			XYChart.Series<Number, Number> activeSeries = new XYChart.Series<>();
			activeSeries.setName("Active Subscribers");
			activeSeries.getData().addAll(activeSubscribersData);

			XYChart.Series<Number, Number> frozenSeries = new XYChart.Series<>();
			frozenSeries.setName("Frozen Subscribers");
			frozenSeries.getData().addAll(frozenSubscribersData);

			// Update the chart
			lineChart.getData().clear(); // Clear previous data
			lineChart.getData().addAll(activeSeries, frozenSeries);

			// Clear any previous error messages
			errorLabel.setText("");
		});
	}

	public void updateMessage(String message) {
		Platform.runLater(() -> {
			if (message.equals("The report is currently unavailable")) {
				errorLabel.setText("The report is currently unavailable");
			}
		});
	}

	/**
	 * Handles navigation back to report selection screen
	 * 
	 * Key Responsibilities: - Close current window - Load report selection FXML -
	 * Pass librarian name - Set application icon
	 * 
	 * @param event Navigation trigger event
	 * @throws IOException if screen loading fails
	 */
	@FXML
	private void handleBack(ActionEvent event) throws IOException {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/SelectReport.fxml"));
			Parent root = loader.load();
			SelectReportController SelectReportController = loader.getController();
			SelectReportController.setLibrarianName(librarianName);
			Stage home = new Stage();
			Scene scene = new Scene(root);
			home.setScene(scene);
			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}
			home.setTitle("HOME");
			home.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Image icon; // Static variable to hold the icon

	/**
	 * Loads application icon with lazy initialization
	 * 
	 * Ensures: - Icon loaded only once - Graceful error handling - Resource path
	 * flexibility
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