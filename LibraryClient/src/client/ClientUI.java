package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea; // Use JavaFX TextArea
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.net.URL;

import common.ConsoleRedirector;
import gui.ClientConnectionController;
/**
 * Main JavaFX application class for initializing and managing the client-side interface.
 * 
 * Responsibilities:
 * - Launch JavaFX client application
 * - Create client connection interface
 * - Set up console window for logging
 * - Manage client controller initialization
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ClientUI extends Application {
    public static ClientController chat; // Only one instance

    private static Image icon; // Static variable to hold the icon
    /**
     * Application entry point. Launches the JavaFX application.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Error launching the application.");
            e.printStackTrace();
        }
    }
    /**
     * Primary method for initializing and displaying the client application.
     * 
     * Creates:
     * - Client connection interface
     * - Console window for system output
     * - Redirects console logs to TextArea
     * 
     * @param primaryStage The main application stage
     * @throws Exception If application initialization fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ClientConnectionController clientFrame = new ClientConnectionController();
            clientFrame.start(primaryStage);

            // Create console window
            Stage consoleStage = new Stage();
            TextArea consoleTextArea = new TextArea(); // Correctly use JavaFX TextArea
            consoleTextArea.setEditable(false);
            consoleTextArea.setWrapText(true);

            VBox consoleRoot = new VBox(consoleTextArea);
            Scene consoleScene = new Scene(consoleRoot, 600, 400);
            consoleStage.setScene(consoleScene);
            consoleStage.setTitle("Client Console");

            // Load and set the icon for the console window
            loadIcon();
            if (icon != null) {
                consoleStage.getIcons().add(icon);
            } else {
                System.err.println("Failed to load application icon.");
            }

            consoleStage.show();

            // Redirect console output to the TextArea
            ConsoleRedirector.redirectToTextArea(consoleTextArea, false); // for System.out
            ConsoleRedirector.redirectToTextArea(consoleTextArea, true);  // for System.err

        } catch (Exception e) {
            System.out.println("ERROR - Failed to start the client: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Initializes the chat client with specified host and port.
     * 
     * @param host Server hostname or IP address
     * @param port Server connection port number
     */
    public static void initChat(String host, int port) {
        try {
            chat = new ClientController(host, port);
        } catch (Exception e) {
            System.err.println("Error initializing chat client.");
            e.printStackTrace();
        }
    }
    /**
     * Utility method to load application icon.
     * 
     * Attempts to load icon from resources directory.
     * Provides error handling for missing icon file.
     */
    // Utility method to load the icon
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