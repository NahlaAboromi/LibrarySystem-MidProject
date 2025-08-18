package gui;

import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import server.EchoServer;
import server.ServerUI;

/**
 * Controller class for managing the Server Port Frame, which allows the user to specify the port 
 * for the server to run on. It handles starting the server and transitioning to the Connections window.
 */
public class ServerPortFrameController {

    String temp = "";

    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnDone = null;

    @FXML
    private Label lbllist;

    @FXML
    private TextField portxt;

    private static Image icon; // Static variable to hold the icon

    
    /**
     * Returns the port entered by the user in the text field.
     * 
     * @return The port number as a String.
     */
    private String getport() {
        return portxt.getText();
    }

    
    /**
     * Handles the 'Done' button click event. It validates the port input and starts the server 
     * if valid. It then transitions to the Connections window and hides the current window.
     * 
     * @param event The ActionEvent triggered by the button click.
     */
    public void Done(ActionEvent event) {
        String p = getport();

        // Check if the port text field is empty
        if (p.trim().isEmpty()) {
            System.out.println("You must enter a port number");
            return; // Return early to prevent further actions
        }

        try {
            // Try to run the server with the given port
            ServerUI.runServer(p);

            // Hide current window
            ((Node) event.getSource()).getScene().getWindow().hide();

            // Open the Connections window
            Stage connectionsStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Connections.fxml"));
            Parent root = loader.load();
            ConnectionsController controller = loader.getController();

            // Set the ConnectionsController in EchoServer
            EchoServer.getInstance().setConnectionsController(controller);

            Scene scene = new Scene(root);
            connectionsStage.setTitle("Connected Clients");

            // Load and set the icon
            loadIcon();
            if (icon != null) {
                connectionsStage.getIcons().add(icon);
            } else {
                System.err.println("Failed to load application icon.");
            }

            connectionsStage.setScene(scene);
            connectionsStage.show();

        } catch (Exception e) {
            // Print the error message if an exception occurs
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace(); // Print the full stack trace for debugging
        }
    }

    
    /**
     * Initializes the primary stage and shows the ServerPort window. It also loads the 
     * FXML and applies any necessary styles.
     * 
     * @param primaryStage The main stage of the application.
     */
    public void start(Stage primaryStage) {
        try {
            // Try loading the FXML for the ServerPort frame
            Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/ServerPort.css").toExternalForm());
            primaryStage.setTitle("Server");

            // Load and set the icon
            loadIcon();
            if (icon != null) {
                primaryStage.getIcons().add(icon);
            } else {
                System.err.println("Failed to load application icon.");
            }

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            // Handle any error in loading the FXML or showing the scene
            System.out.println("Error loading the ServerPort window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    /**
     * Handles the 'Exit' button click event. This will exit the application when called.
     * 
     * @param event The ActionEvent triggered by the button click.
     */
    public void getExitBtn(ActionEvent event) {
        try {
            // Handle server exit
            System.out.println("Exit Server");
            System.exit(0);
        } catch (Exception e) {
            // Catch any unexpected errors during the exit process
            System.out.println("Error during exit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility method to load the application icon. This method attempts to load the icon from
     * the specified resources path and sets it for the application stage.
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