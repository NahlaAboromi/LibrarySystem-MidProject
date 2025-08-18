package server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea; // Use JavaFX TextArea
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.Subscriber;
import common.ConsoleRedirector;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Vector;

import gui.ServerPortFrameController;


/**
 * The ServerUI class represents the user interface for the server application.
 * It extends JavaFX Application and provides a graphical interface
 * for controlling the server, displaying server console output, and managing
 * server settings such as the port number.
 */
public class ServerUI extends Application {
	/**
     * Default port number for the server.
     */
    final public static int DEFAULT_PORT = 5555;
    /**
     * A list of subscribers to the server.
     */
    public static Vector<Subscriber> subscribers = new Vector<Subscriber>(); 
    /**
     * The port on which the server will listen.
     */
    public static int port = 0;
    /**
     * Static variable to hold the application icon.
     */
    private static Image icon; // Static variable to hold the icon

    

    /**
     * The entry point for the JavaFX application.
     *
     * @param args Command-line arguments
     * @throws Exception If an error occurs during launching
     */
    public static void main(String args[]) throws Exception {
        launch(args);
    }

    
    
    /**
     * Starts the JavaFX application. Sets up the server port frame and console window.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs during startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ServerPortFrameController aFrame = new ServerPortFrameController();
            aFrame.start(primaryStage);

            // Create console window
            Stage consoleStage = new Stage();
            TextArea consoleTextArea = new TextArea(); // Correctly use JavaFX TextArea
            consoleTextArea.setEditable(false);
            consoleTextArea.setWrapText(true);

            VBox consoleRoot = new VBox(consoleTextArea);
            Scene consoleScene = new Scene(consoleRoot, 600, 400);
            consoleStage.setScene(consoleScene);
            consoleStage.setTitle("Server Console");
            consoleStage.show();
            // Load and set the icon for the console window
            loadIcon();
            if (icon != null) {
                consoleStage.getIcons().add(icon);
            } else {
                System.err.println("Failed to load application icon.");
            }
            // Redirect console output to the TextArea
            ConsoleRedirector.redirectToTextArea(consoleTextArea, false); // for System.out
            ConsoleRedirector.redirectToTextArea(consoleTextArea, true);  // for System.err

        } catch (Exception e) {
            System.out.println("ERROR - Failed to start the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    /**
     * Starts the server and listens on the specified port.
     *
     * @param p The port number for the server to listen on
     */
    public static void runServer(String p) {
        try {
            port = Integer.parseInt(p); 
        } catch (NumberFormatException e) {
            System.out.println("ERROR - Invalid port number format: " + e.getMessage());
            return;
        } catch (Throwable t) {
            System.out.println("ERROR - Could not connect! " + t.getMessage());
            return;
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.setBacklog(10); // Limit number of waiting connections
            sv.listen(); // Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Gets the current port number the server is using.
     *
     * @return The current port number
     */
    public int get_port() { return port; }
    
    
    /**
     * Loads the application icon from the resources folder.
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
