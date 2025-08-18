package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.ClientConnection;
import ocsf.server.ConnectionToClient;
import server.EchoServer;
import server.ServerUI;

/**
 * Controller class for managing the Connections tab of the server UI.
 * It handles the display and updating of active client connections in a table.
 */
public class ConnectionsController  {
    @FXML
    private TableView<ClientConnection> connectionsTable;

    @FXML
    private TableColumn<ClientConnection, String> ipColumn;

    @FXML
    private TableColumn<ClientConnection, String> hostColumn;

    @FXML
    private TableColumn<ClientConnection, String> statusColumn;

    private ObservableList<ClientConnection> connectionsList = FXCollections.observableArrayList();

    
    /**
     * Initializes the table view by setting up the columns and their corresponding properties.
     * This method is automatically called when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        try {
            ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
            hostColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            connectionsTable.setItems(connectionsList);
        } catch (Exception e) {
            System.err.println("Error initializing table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    /**
     * Updates the connections table with a new or updated connection.
     * 
     * @param ip The IP address of the connection.
     * @param hostName The host name of the connection.
     * @param status The current status of the connection.
     */
    public void updateTable(String ip, String hostName, String status) {
        Platform.runLater(() -> {
            try {
                boolean updated = false;
                for (ClientConnection connection : connectionsList) {
                    if (connection.getIp().equals(ip) && connection.getHostName().equals(hostName)) {
                        connection.setStatus(status); // Update the status
                        connectionsTable.refresh(); // Refresh the table to show the updated status
                        updated = true;
                        break;
                    }
                }

                // If not found, add a new connection
                if (!updated) {
                    ClientConnection connection = new ClientConnection();
                    connection.setIp(ip);
                    connection.setHostName(hostName);
                    connection.setStatus(status);
                    connectionsList.add(connection);
                }
            } catch (Exception e) {
                System.err.println("Error updating connection table: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    
    /**
     * Handles the exit action for the application.
     * It ensures that the application exits completely by calling `Platform.exit()`
     * and `System.exit(0)` to terminate the program.
     */
    @FXML
    public void handleExit() {
        // Close the application or perform any cleanup necessary before exiting
        Platform.exit(); // This will close the application
        System.exit(0); // Ensure that the application exits completely
    }
}