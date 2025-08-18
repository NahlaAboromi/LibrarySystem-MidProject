package common;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PrintStream;


/**
 * Utility class that redirects console output (System.out and System.err) to a JavaFX TextArea.
 * This allows the output to be displayed in a GUI, instead of the traditional console.
 */
public class ConsoleRedirector {
	
	/**
     * Redirects the standard output or error stream to a given TextArea.
     * 
     * @param textArea The TextArea to display the redirected output.
     * @param isError If true, redirects System.err (error output), otherwise redirects System.out (standard output).
     */
    public static void redirectToTextArea(TextArea textArea, boolean isError) {
        CustomOutputStream customOut = new CustomOutputStream(textArea);
        PrintStream printStream = new PrintStream(customOut, true);
        if (isError) {
            System.setErr(printStream); // Redirect System.err
        } else {
            System.setOut(printStream); // Redirect System.out
        }
    }

    
    /**
     * Custom OutputStream to redirect data to a JavaFX TextArea.
     * This class overrides the `write` method to append text to the TextArea.
     */
    private static class CustomOutputStream extends OutputStream {
        private final TextArea textArea;

        /**
         * Constructs a CustomOutputStream with the given TextArea.
         *
         * @param textArea The TextArea where the output will be displayed.
         */
        public CustomOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        
        /**
         * Writes a byte of data to the TextArea. 
         * This method is called by the PrintStream when data is written to the output stream.
         *
         * @param b The byte of data to write.
         */
        @Override
        public void write(int b) {
            Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
            // Do not write to the original output stream to suppress standard output
        }
    }
}
