package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.SubscriberChangesHistory;
//import logic.SubscriberPasswordHistory;
/**
 * Controller for managing and displaying subscriber changes history
 * 
 * Handles retrieval, display, and navigation of subscriber modification records
 * 
 * @author [Developer Name]
 * @version 1.0
 * @since 2025-01-25
 */
public class SubscriberChangesHistoryController {
	private boolean isLibrarian = false;

	public void setLibrarian(boolean librarian) {
	    this.isLibrarian = librarian;
	}

    private String setSubscriberId;

    public void setSubscriberId(String id) {
        this.setSubscriberId = id;
    }
    private static SubscriberChangesHistoryController instance;

    public SubscriberChangesHistoryController() {
        instance = this;
    }

    public static SubscriberChangesHistoryController getInstance() {
        return instance;
    }
    
    @FXML
    private TableView<SubscriberChangesHistory> changesHistoryTable;
    @FXML
    private TableColumn<SubscriberChangesHistory, Integer> changeHistoryIdColumn;
    @FXML
    private TableColumn<SubscriberChangesHistory, Integer> subscriberIdColumn;
    @FXML
    private TableColumn<SubscriberChangesHistory, String> changeTypeColumn;
    @FXML
    private TableColumn<SubscriberChangesHistory, String> oldValueColumn;
    @FXML
    private TableColumn<SubscriberChangesHistory, String> newValueColumn;
    @FXML
    private TableColumn<SubscriberChangesHistory, Date> changeDateColumn;

    private ObservableList<SubscriberChangesHistory> changesHistoryList = FXCollections.observableArrayList();

    @FXML
    private Label errorLabel;
    @FXML
    public void initialize() {
        changeHistoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("changeHistoryId"));
        subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        changeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("changeType"));
        oldValueColumn.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        newValueColumn.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        changeDateColumn.setCellValueFactory(new PropertyValueFactory<>("changeDate"));

        changesHistoryTable.setItems(changesHistoryList);
        
        fetchChangesHistory();
        hideColumns();
    }
    /**
     * Retrieves changes history for the specified subscriber.
     * 
     * This method executes an asynchronous database query to fetch modification records.
     */
    public void fetchChangesHistory() {
        System.out.println("Fetching changes history... Subscriber ID: " + setSubscriberId);
        if (setSubscriberId != null && !setSubscriberId.isEmpty()) {
            new Thread(() -> {
                try {
                    SubscriberChangesHistory request = new SubscriberChangesHistory(
                        0, // changeHistoryId (לא רלוונטי לבקשה)
                        Integer.parseInt(setSubscriberId),
                        "", // changeType (ריק כי אנחנו רוצים את כל סוגי השינויים)
                        "", // oldValue (לא רלוונטי לבקשה)
                        "", // newValue (לא רלוונטי לבקשה)
                        null // changeDate (לא רלוונטי לבקשה)
                    );
                    ClientUI.chat.accept(request);
                } catch (NumberFormatException e) {
                    Platform.runLater(() -> errorLabel.setText("Invalid subscriber ID format."));
                    e.printStackTrace();
                } catch (Exception e) {
                    Platform.runLater(() -> errorLabel.setText("Failed to fetch changes history."));
                    e.printStackTrace();
                }
            }).start();
        } else {
            errorLabel.setText("Subscriber ID is not set.");
        }
    }

    /**
     * Updates table with retrieved changes history
     * 
     * Runs on JavaFX Application Thread to ensure thread-safety
     * 
     * @param changesHistories List of subscriber change records
     */
    public void updateTable(ArrayList<SubscriberChangesHistory> changesHistories) {
        Platform.runLater(() -> {
            changesHistoryList.clear();
            changesHistoryList.addAll(changesHistories);
        });
    }

    @FXML
    public void hideColumns() {
        subscriberIdColumn.setVisible(false);
        changeHistoryIdColumn.setVisible(false);
    }
    /**
     * Handles navigation back to the subscriber history menu.
     * 
     * This method closes the current window and opens the previous screen,
     * preserving subscriber context and librarian status.
     */
    @FXML
    public void handleBackButton() {
        try {
            // סגור את החלון הנוכחי
            Stage currentStage = (Stage) errorLabel.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/MenuOfSubscriberHistory.fxml"));
            Parent root = loader.load();
            
            // קבל את הבקר הקיים
            MenuOfSubscriberHistoryController controller = loader.getController();
            controller.setSubscriberId(setSubscriberId);
         // בדוק אם השדה isLibrarian הוא TRUE
            if (isLibrarian) {
                controller.setLibrarian(true);
            }
            Stage previousStage = new Stage();
			// Load and set the application icon
			loadIcon();
			if (icon != null) {
				previousStage.getIcons().add(icon);
			}
			Scene scene = new Scene(root, 540, 400);
            previousStage.setScene(scene);
            previousStage.setTitle("Menu of Subscriber History");

            previousStage.show();
        } catch (IOException e) {
            errorLabel.setText("Error returning to the previous screen");
            e.printStackTrace();
        }
    }
    private static Image icon; // Static variable to hold the application icon
    /**
     * Loads application icon
     * 
     * Ensures icon is loaded only once
     * Handles resource loading gracefully
     */
    private static void loadIcon() 
    {
        if (icon == null) {
            URL iconUrl = ResetPasswordController.class.getResource("/common/resources/icon.png");
            if (iconUrl != null) {
                icon = new Image(iconUrl.toString());
            } else {
                System.out.println("Icon file not found in resources!");
            }
        }
    }

}
