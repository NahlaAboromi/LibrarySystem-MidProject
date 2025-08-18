package common;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Utility class for redirecting console output to a JavaFX TextArea.
 * 
 * Provides mechanism to: - Redirect System.out and System.err streams - Display
 * console output in a TextArea - Ensure thread-safe UI updates
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class ConsoleRedirector {
	/**
	 * Redirects console output (standard or error) to specified TextArea.
	 * 
	 * @param textArea Target TextArea for displaying console output
	 * @param isError  Flag to determine whether to redirect System.err or
	 *                 System.out
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
	 * Custom OutputStream implementation for writing to JavaFX TextArea. Ensures
	 * thread-safe text appending using Platform.runLater().
	 */
	private static class CustomOutputStream extends OutputStream {
		private final TextArea textArea;

		/**
		 * Constructs CustomOutputStream with target TextArea.
		 * 
		 * @param textArea TextArea to receive console output
		 */
		public CustomOutputStream(TextArea textArea) {
			this.textArea = textArea;
		}

		/**
		 * Writes single character to TextArea. Uses Platform.runLater() to ensure UI
		 * thread safety.
		 * 
		 * @param b Integer representing character to write
		 */
		@Override
		public void write(int b) {
			Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
		}
	}
}
