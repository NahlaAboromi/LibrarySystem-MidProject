// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import java.io.*;
import common.ChatIF;

/**
 * This class constructs the UI for a chat client. It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author François Bélanger
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Laganière
 * @version July 2000
 */
public class ClientController implements ChatIF {
    // Class variables *************************************************

    /**
     * The default port to connect on.
     */
    public static int DEFAULT_PORT;

    // Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */
    private ChatClient client;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the ClientConsole UI.
     *
     * @param host The host to connect to.
     * @param port The port to connect on.
     */
    public ClientController(String host, int port) {
        try {
            client = new ChatClient(host, port, this);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection! Terminating client.");
            exception.printStackTrace();
            System.exit(1);
        }
    }

    // Instance methods ************************************************

    /**
     * This method waits for input from the console. Once it is
     * received, it sends it to the client's message handler.
     *
     * @param str The message to send to the client handler.
     */
    public void accept(Object str) {
        try {
            client.handleMessageFromClientUI(str);
        } catch (Exception e) {
            System.out.println("Error: Failed to send message to the client.");
            e.printStackTrace();
        }
    }

    /**
     * This method overrides the method in the ChatIF interface. It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {
        try {
            System.out.println("> " + message);
        } catch (Exception e) {
            System.out.println("Error: Failed to display message.");
            e.printStackTrace();
        }
    }
}
