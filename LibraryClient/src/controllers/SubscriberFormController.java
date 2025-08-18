package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Subscriber;

/**
 * Controller for managing subscriber profile information and updates.
 * 
 * This class handles:
 * - Displaying subscriber details
 * - Validating and updating contact information
 * - Navigation between screens
 */
public class SubscriberFormController implements Initializable {
	private Subscriber subscriber;

	// Text Fields
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtPhone;

	@FXML
	private TextField txtEmail;

	@FXML
	private TextField txtPassword;

	// Labels
	@FXML
	private Label lblId;

	@FXML
	private Label lblName;

	@FXML
	private Label lblUsername;

	@FXML
	private Label lblPhone;

	@FXML
	private Label lblEmail;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblStatusValue;

	@FXML
	private Label lblPassword;

	@FXML
	private Label errorLabel;

	// Buttons
	@FXML
	private Button btnSave;

	@FXML
	private Button btnClose;

	@FXML
	private Button btnGoHome;

	private static Image icon; // Static variable to hold the icon

	/**
	 * Loads subscriber details into form fields
	 * 
	 * Populates text fields with subscriber information Disables editing of
	 * critical fields
	 * 
	 * @param subscriber Subscriber object containing profile details
	 */
	public void loadSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
		this.txtId.setText(String.valueOf(subscriber.getSubscriberId()));
		this.txtName.setText(subscriber.getSubscriberName());
		this.txtUsername.setText(subscriber.getUsername()); // Set username
		this.txtPhone.setText(subscriber.getSubscriberPhoneNumber());
		this.txtEmail.setText(subscriber.getSubscriberEmail());
		this.txtPassword.setText(subscriber.getPassword()); // Set password
		this.lblStatus.setText(subscriber.getStatus()); // Set status

		// Update status
		this.lblStatusValue.setText(subscriber.getStatus()); // Set status

		// Disable editing for ID, Name, Username, and Password fields
		this.txtId.setEditable(false);
		this.txtName.setEditable(false);
		this.txtUsername.setEditable(false); // Disable username editing
		this.txtPassword.setEditable(false); // Disable password editing
		this.lblStatus.setVisible(true); // Show status
	}

	/**
	 * Handles saving subscriber contact information.
	 * 
	 * Performs validation on:
	 * - Phone number format (10 digits)
	 * - Email format (@gmail.com)
	 * 
	 * Sends update request to server if validation passes.
	 * 
	 * @param event Action event triggering save operation
	 */
	@FXML
	public void handleSave(ActionEvent event) {
		String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		if (txtPhone.getText().length() != 10 || !(txtPhone.getText().matches("\\d{10}"))) {
			System.out.println("asfsd");
			txtPhone.getStyleClass().add("error");
			errorLabel.setText("Illegal Input");
			errorLabel.setTextFill(Color.RED);

		} else {
			txtPhone.getStyleClass().removeAll("error");
		}
		String email = txtEmail.getText().trim();

		if (!email.endsWith("@gmail.com") || !email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
			txtEmail.getStyleClass().add("error");
			errorLabel.setText("Email Must End With @gmail.com and valid");
			errorLabel.setTextFill(Color.RED);

		} else {
			txtEmail.getStyleClass().removeAll("error");

			// Reset to default border color
		}
		if (txtPhone.getText().length() == 10 && txtPhone.getText().matches("\\d{10}")
				&& txtEmail.getText().matches(emailRegex)) {
			subscriber.setSubscriberPhoneNumber(txtPhone.getText());
			subscriber.setSubscriberEmail(txtEmail.getText());
			errorLabel.setText("Saved Successfully!");
			errorLabel.setTextFill(Color.GREEN);
			// Send update request to the server with relevant details only
			String updateMessage = String.format("UpdateSubscriber ID=%d, Phone=%s, Email=%s",
					subscriber.getSubscriberId(), subscriber.getSubscriberPhoneNumber(),
					subscriber.getSubscriberEmail());

			ClientUI.chat.accept(updateMessage);
		}
	}

	/**
	 * Closes client connection
	 * 
	 * Sends close request to server Waits for server response
	 * 
	 * @param event Action event triggering connection closure
	 * @throws Exception for connection-related errors
	 */
	@FXML
	public void getExitBtn(ActionEvent event) throws Exception {
		ClientUI.chat.accept("CLOSE");
		// Wait for a response from the server
		while (client.ChatClient.awaitResponse) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Navigates back to subscriber main menu.
	 * 
	 * Loads subscribers menu screen and passes current subscriber ID.
	 * 
	 * @param event Action event triggering navigation
	 * @throws IOException if screen loading fails
	 */
	@FXML
	public void handleGoHome(ActionEvent event) throws IOException, ClassNotFoundException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui_subscriber/TheSubscribersMenu.fxml"));
			Parent root = loader.load();
			// Get the controller of the loaded FXML
			SubscriberMenuController menu = loader.getController();

			// Pass data to the controller
			menu.setSubscriberId(String.valueOf(subscriber.getSubscriberId()));

			Stage home = new Stage();
			Scene scene = new Scene(root, 700, 600);
			home.setScene(scene);
			home.setTitle("TheSubscribersMenu");

			// Load and set the icon
			loadIcon();
			if (icon != null) {
				home.getIcons().add(icon);
			} else {
				System.err.println("Failed to load application icon.");
			}

			home.show();

			// Close the current window
			Stage currentStage = (Stage) lblId.getScene().getWindow();
			currentStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads application icon
	 * 
	 * Ensures icon is loaded only once Handles resource loading gracefully
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