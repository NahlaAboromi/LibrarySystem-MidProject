package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import client.ClientUI;
import javafx.stage.Stage;
import logic.BorrowingReport;
import logic.ResponseWrapper;
import logic.YearMonth;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import java.io.ByteArrayInputStream;

/**
 * Controller for generating and displaying borrowing time reports.
 * 
 * Responsibilities: - Manage borrowing time report generation - Handle chart
 * visualization - Support year and month selection - Provide librarian context
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class BorrowingTimeReportController {
	private String librarianName; // Field to store librarian's name

	public void setLibrarianName(String name) {
		this.librarianName = name; // Method to set librarian's name
	}

	private static BorrowingTimeReportController instance;

	@FXML
	private ChoiceBox<Integer> yearChoiceBox;

	@FXML
	private ChoiceBox<String> monthChoiceBox;

	@FXML
	private BarChart<String, Number> barChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	@FXML
	private Label errorLabel;
	@FXML
	private StackedBarChart<String, Number> mostBorrowedChart;

	@FXML
	private StackedBarChart<String, Number> mostLateChart;

	public BorrowingTimeReportController() {
		instance = this;
	}

	public static BorrowingTimeReportController getInstance() {
		return instance;
	}

	/**
	 * Initializes report generation interface.
	 * 
	 * Actions: - Populate year and month choice boxes - Set default selections -
	 * Configure chart axis categories
	 */
	@FXML
	public void initialize() {
		yearChoiceBox.getItems().addAll(IntStream.rangeClosed(2024, 2030).boxed().toList());
		monthChoiceBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");

		yearChoiceBox.setValue(2025);
		monthChoiceBox.setValue("January");

		xAxis.setCategories(FXCollections.observableArrayList("Fiction", "Romance", "Science", "History","Biography","Mystery"));

	}

	/**
	 * Handles report generation request.
	 * 
	 * Actions: - Validate year and month selection - Request borrowing reports from
	 * server - Trigger chart and late return reports
	 * 
	 * @param event ActionEvent triggering report generation
	 */
	@FXML
	private void handleView(ActionEvent event) {
		String month = monthChoiceBox.getValue();
		Integer year = yearChoiceBox.getValue();

		if (month != null && year != null) {
			int monthNumber = monthChoiceBox.getSelectionModel().getSelectedIndex() + 1;
			int yearNumber = year;
			ClientUI.chat.accept(new ResponseWrapper("BorrowingReport", new YearMonth(yearNumber, monthNumber)));
			ClientUI.chat.accept(new ResponseWrapper("MostBorrowedReport", new YearMonth(yearNumber, monthNumber)));
			ClientUI.chat.accept(new ResponseWrapper("MostLateReport", new YearMonth(yearNumber, monthNumber)));
		}
	}

	/**
	 * Updates borrowing time chart with server data.
	 * 
	 * Features: - Dynamic chart population - Color-coded series - Error handling
	 * for empty data
	 * 
	 * @param arr Report data for chart visualization
	 */
	// פונקציה לעדכון הגרף עם נתונים חדשים
	public void updateChart(ArrayList<int[]> arr) {
	    Platform.runLater(() -> {
	        if (arr == null || arr.isEmpty()) {
	            errorLabel.setText("No data available");
	            barChart.getData().clear();
	            return;
	        }

	        // יצירת סדרות עבור כל קטגוריה
	        XYChart.Series<String, Number> regularBorrowingSeries = new XYChart.Series<>();
	        regularBorrowingSeries.setName("Regular Borrowing Time");

	        XYChart.Series<String, Number> lateBorrowingSeries = new XYChart.Series<>();
	        lateBorrowingSeries.setName("Late Return Time");

	        // שמות הקטגוריות
	        String[] categories = { "Fiction", "Romance", "Science", "History", "Biography", "Mystery" };

	        // הוספת נתונים לכל סדרה
	        for (int i = 0; i < Math.min(arr.size(), categories.length); i++) {
	            regularBorrowingSeries.getData().add(new XYChart.Data<>(categories[i], arr.get(i)[0]));
	            lateBorrowingSeries.getData().add(new XYChart.Data<>(categories[i], arr.get(i)[1]));
	        }

	        // הצגת הגרף
	        barChart.getData().clear();
	        barChart.getData().addAll(regularBorrowingSeries, lateBorrowingSeries);

	        // צבעים לכל סדרה
	        regularBorrowingSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #FF7043;")); // כתום
	        lateBorrowingSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #FFB74D;")); // צהוב בהיר

	        // הגדרת מקרא בצד ימין
	        barChart.setLegendSide(Side.RIGHT);

	        // הצגת הקטגוריות על ציר ה-X
	        xAxis.setCategories(FXCollections.observableArrayList(categories));

	        // ריקון שגיאות אם הכל תקין
	        errorLabel.setText("");
	    });
	}


	/**
	 * Displays error or informational messages.
	 * 
	 * @param message Message to display in error label
	 */
	public void updateMessage(String message) {
		Platform.runLater(() -> {
			errorLabel.setText(message);
		});
	}

	/**
	 * Handles navigation back to report selection screen.
	 * 
	 * Actions: - Close current window - Load report selection screen - Preserve
	 * librarian context
	 * 
	 * @param event ActionEvent triggering navigation
	 */
	@FXML
	private void handleBack(ActionEvent event) {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_librarian/SelectReport.fxml"));
			Parent root = loader.load();
			SelectReportController SelectReportController = loader.getController();
			SelectReportController.setLibrarianName(librarianName);
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			// Load and set the icon
			loadIcon();
			if (icon != null) {
				stage.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}
			stage.setTitle("Select Report");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Image icon; // Static variable to hold the icon

	/**
	 * Loads application icon from resources.
	 * 
	 * Key Characteristics: - Implements singleton pattern for icon loading -
	 * Ensures icon is loaded only once - Provides detailed error logging for
	 * missing icon
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

	/**
	 * Updates Most Borrowed Books chart with server data.
	 * 
	 * Features: - Dynamic chart population - Configurable Y-axis range -
	 * Color-coded bar representation - Handles empty data scenarios
	 * 
	 * @param borrowedData List of book borrowing statistics
	 */
	public void updateMostBorrowedChart(ArrayList<Object[]> borrowedData) {
		Platform.runLater(() -> {
			if (borrowedData == null || borrowedData.isEmpty()) {
				mostBorrowedChart.getData().clear();
				return;
			}
		     // מיון הנתונים לפי כמות ההשאלות בסדר יורד
	        Collections.sort(borrowedData, (a, b) -> Double.compare(
	            Double.parseDouble(String.valueOf(b[2])), 
	            Double.parseDouble(String.valueOf(a[2]))
	        ));

			// הגדרת הציר Y
			NumberAxis yAxis = (NumberAxis) mostBorrowedChart.getYAxis();
			yAxis.setAutoRanging(false);
			yAxis.setLowerBound(0);
			yAxis.setUpperBound(25);
			yAxis.setTickUnit(2.5);
			XYChart.Series<String, Number> regularBorrowingSeries = new XYChart.Series<>();
			regularBorrowingSeries.setName("Regular Borrowing");

			XYChart.Series<String, Number> lateBorrowingSeries = new XYChart.Series<>();
			lateBorrowingSeries.setName("Late Returns");

			for (Object[] bookData : borrowedData) {
				String bookName = String.valueOf(bookData[1]);
				double borrowCount = Double.parseDouble(String.valueOf(bookData[2]));

				regularBorrowingSeries.getData().add(new XYChart.Data<>(bookName, borrowCount));
				// כרגע אין לנו נתוני איחורים, אז נשים 0 או נתון אחר
				lateBorrowingSeries.getData().add(new XYChart.Data<>(bookName, 0));
			}

			mostBorrowedChart.getData().clear();
			mostBorrowedChart.getData().add(regularBorrowingSeries);

			// Apply styling directly to the chart and bars
			mostBorrowedChart.setStyle("-fx-bar-gap: 0; -fx-category-gap: 10;");

			mostBorrowedChart.setStyle("-fx-background-color: #e8d3cf; -fx-padding: 10;");

			regularBorrowingSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #FF7043;"));

			mostBorrowedChart.setLegendSide(Side.RIGHT);
		});
	}

	/**
	 * Updates Late Returns chart with server data.
	 * 
	 * Features: - Dynamic chart population - Configurable Y-axis range -
	 * Color-coded bar representation - Handles empty data scenarios
	 * 
	 * @param lateData List of late book return statistics
	 */
	public void updateLateReturnsChart(ArrayList<Object[]> lateData) {
		Platform.runLater(() -> {
			if (lateData == null || lateData.isEmpty()) {
				mostLateChart.getData().clear();
				return;
			}
			 // מיון הנתונים לפי כמות ההחזרות המאוחרות בסדר יורד
	        Collections.sort(lateData, (a, b) -> Double.compare(
	            Double.parseDouble(String.valueOf(b[2])), 
	            Double.parseDouble(String.valueOf(a[2]))
	        ));

			NumberAxis yAxis = (NumberAxis) mostLateChart.getYAxis();
			yAxis.setAutoRanging(false);
			yAxis.setLowerBound(0);
			yAxis.setUpperBound(25);
			yAxis.setTickUnit(2.5);

			XYChart.Series<String, Number> regularSeries = new XYChart.Series<>();
			regularSeries.setName("Late Returns");

			for (Object[] bookData : lateData) {
				String bookName = String.valueOf(bookData[1]);
				double lateCount = Double.parseDouble(String.valueOf(bookData[2]));
				regularSeries.getData().add(new XYChart.Data<>(bookName, lateCount));
			}

			mostLateChart.getData().clear();
			mostLateChart.getData().add(regularSeries);

			mostLateChart.setStyle("-fx-background-color: #e8d3cf; -fx-padding: 10;");

			regularSeries.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: #FFB74D;"));

			mostLateChart.setLegendSide(Side.RIGHT);
		});
	}
}
