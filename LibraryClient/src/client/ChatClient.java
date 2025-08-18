// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import client.*;
import common.ChatIF;
import controllers.BarcodeBorrowBookController;
import controllers.BorrowBookController;
import controllers.BorrowedBooksHistoryController;
import controllers.BorrowingTimeReportController;
import controllers.CheckSubscriberStatusController;
import controllers.CompleteOrderBook;
import controllers.ConfirmBorrowController;
import controllers.EnterSubscriberIDFormController;
import controllers.ExtendBorrowingController;
import controllers.ExtendReturnDateController;
import controllers.ExtensionsReaderCardController;
import controllers.LibrarianLoginController;
import controllers.LibrarianMessagesController;
import controllers.LibrarianSignUpController;
import controllers.LoanExtensionHistoryController;
import controllers.LoansBookReaderCardController;
import controllers.NewPasswordToLibrarianController;
import controllers.OrderBookController;
import controllers.OrderedBooksHistoryController;
import controllers.OrderedBooksReaderCardController;
import controllers.ResetPasswordController;
import controllers.ResetPasswordSubscriberController;
import controllers.ReturnBookController;
import controllers.ReturnBookHistoryController;
import controllers.SetNewSubscriberPasswordController;
import controllers.SubscriberChangesHistoryController;
import controllers.SubscriberLoginController;
//import controllers.SubscriberPasswordHistoryController;
import controllers.SubscriberSignUpController;
import controllers.SubscriberStatusReportController;
import gui.BookSearchController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import logic.Book;
import logic.BorrowedBook;
import logic.BorrowedBooksHistory;
import logic.BorrowingReport;
import logic.LibrarianMessage;
import logic.LoanExtension;
import logic.OrderedBook;
import logic.OrderedBooksHistory;
import logic.ResponseWrapper;
import logic.ReturnBookHistory;
import logic.Subscriber;
import logic.SubscriberChangesHistory;
//import logic.SubscriberPasswordHistory;

import java.io.*;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static Subscriber s1 = new Subscriber(0, "", "", "", "", "", 0);

	public static boolean awaitResponse = false;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		// openConnection();
	}

	// Instance methods ************************************************

	/**
	 * Handles messages received from the server and routes them to appropriate processing methods
	 * based on their type (ArrayList, ResponseWrapper, or String).
	 * 
	 * @param msg The message object received from the server
	 */
	public void handleMessageFromServer(Object msg) {
		System.out.println("--> handleMessageFromServer");
		awaitResponse = false;

		if (msg instanceof ArrayList<?>) {
			handleArrayListMessage((ArrayList<?>) msg);
		} else if (msg instanceof ResponseWrapper) {
			handleResponseWrapperMessage((ResponseWrapper) msg);
		} else if (msg instanceof String) {
			handleStringMessage((String) msg);
		}
	}
	/**
	 * Handles incoming ArrayList messages by identifying the type of the first element
	 * and routing it to the appropriate update method for processing.
	 * 
	 * Supports processing of various list types including:
	 * - BorrowedBooksHistory
	 * - OrderedBook
	 * - BorrowedBook
	 * - LibrarianMessage
	 * - ReturnBookHistory
	 * - OrderedBooksHistory
	 * - SubscriberChangesHistory
	 * 
	 * @param list The ArrayList received from the server containing various data types
	 * @throws ClassCastException If list contains incompatible element types
	 */
	private void handleArrayListMessage(ArrayList<?> list) {
		System.out.println("Received ArrayList");
		System.out.println("ArrayList size: " + list.size());

		if (list.isEmpty()) {
			System.out.println("Received empty ArrayList");
			return;
		}

		Object firstElement = list.get(0);
		System.out.println("First element actual type: " + firstElement.getClass().getName());

		if (firstElement instanceof BorrowedBooksHistory) {
			updateBorrowedBooksHistoryTable(list);
		} else if (firstElement instanceof OrderedBook) {
			updateOrderedBooksTable(list);
		} else if (firstElement instanceof BorrowedBook) {
			updateLoansBookTable(list);
		} else if (firstElement instanceof LibrarianMessage) {
			updateLibrarianMessagesTable(list);
		} else if (firstElement instanceof ReturnBookHistory) {
			updateReturnBookHistoryTable(list);
		} else if (firstElement instanceof OrderedBooksHistory) {
			updateOrderedBooksHistoryTable(list);
		} else if (firstElement instanceof SubscriberChangesHistory) {
			updateSubscriberChangesHistoryTable(list);
		} else {
			handleUnknownArrayListType(firstElement);
		}
	}
	/**
	 * Updates the Borrowed Books History table with received data.
	 * Runs on JavaFX Platform thread to ensure thread-safety.
	 * 
	 * @param list ArrayList containing BorrowedBooksHistory objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateBorrowedBooksHistoryTable(ArrayList<?> list) {
		System.out.println("Processing BorrowedBooksHistory");
		ArrayList<BorrowedBooksHistory> borrowedBooks = (ArrayList<BorrowedBooksHistory>) list;
		BorrowedBooksHistoryController borrowedBooksController = BorrowedBooksHistoryController.getInstance();
		if (borrowedBooksController != null) {
			Platform.runLater(() -> borrowedBooksController.updateTable(borrowedBooks));
		}
	}
	/**
	 * Updates the Ordered Books table for reader card with received data.
	 * Runs on JavaFX Platform thread to ensure UI update safety.
	 * 
	 * @param list ArrayList containing OrderedBook objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateOrderedBooksTable(ArrayList<?> list) {
		System.out.println("Processing OrderedBook");
		ArrayList<OrderedBook> messages = (ArrayList<OrderedBook>) list;
		OrderedBooksReaderCardController controller = OrderedBooksReaderCardController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateTable(messages));
		}
	}
	/**
	 * Updates the Loans Book table for reader card with received data.
	 * Runs on JavaFX Platform thread to ensure UI update safety.
	 * 
	 * @param list ArrayList containing BorrowedBook objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateLoansBookTable(ArrayList<?> list) {
		System.out.println("Processing BorrowedBook");
		ArrayList<BorrowedBook> messages = (ArrayList<BorrowedBook>) list;
		LoansBookReaderCardController controller = LoansBookReaderCardController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateTable(messages));
		}
	}
	/**
	 * Updates the Librarian Messages table with received data.
	 * Runs on JavaFX Platform thread to ensure thread-safe UI update.
	 * 
	 * @param list ArrayList containing LibrarianMessage objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateLibrarianMessagesTable(ArrayList<?> list) {
		System.out.println("Processing LibrarianMessage");
		ArrayList<LibrarianMessage> messages = (ArrayList<LibrarianMessage>) list;
		LibrarianMessagesController controller = LibrarianMessagesController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateTable(messages));
		}
	}
	/**
	 * Updates the Return Book History table with received data.
	 * Runs on JavaFX Platform thread to ensure thread-safe UI update.
	 * 
	 * @param list ArrayList containing ReturnBookHistory objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateReturnBookHistoryTable(ArrayList<?> list) {
		System.out.println("Processing ReturnBookHistory");
		ArrayList<ReturnBookHistory> returnBookHistory = (ArrayList<ReturnBookHistory>) list;
		ReturnBookHistoryController returnBookController = ReturnBookHistoryController.getInstance();
		if (returnBookController != null) {
			Platform.runLater(() -> returnBookController.updateTable(returnBookHistory));
		}
	}
	/**
	 * Updates the Ordered Books History table with received data.
	 * Runs on JavaFX Platform thread to ensure thread-safe UI update.
	 * 
	 * @param list ArrayList containing OrderedBooksHistory objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateOrderedBooksHistoryTable(ArrayList<?> list) {
		System.out.println("Processing OrderedBooksHistory");
		ArrayList<OrderedBooksHistory> orderedBooks = (ArrayList<OrderedBooksHistory>) list;
		OrderedBooksHistoryController orderedBooksController = OrderedBooksHistoryController.getInstance();
		if (orderedBooksController != null) {
			Platform.runLater(() -> orderedBooksController.updateTable(orderedBooks));
		}
	}
	/**
	 * Updates the Subscriber Changes History table with received data.
	 * Runs on JavaFX Platform thread to ensure thread-safe UI update.
	 * 
	 * @param list ArrayList containing SubscriberChangesHistory objects
	 * @throws ClassCastException If list contains incompatible types
	 */
	private void updateSubscriberChangesHistoryTable(ArrayList<?> list) {
		System.out.println("Processing SubscriberChangesHistory");
		ArrayList<SubscriberChangesHistory> histories = (ArrayList<SubscriberChangesHistory>) list;
		SubscriberChangesHistoryController historyController = SubscriberChangesHistoryController.getInstance();
		if (historyController != null) {
			Platform.runLater(() -> historyController.updateTable(histories));
		}
	}
	/**
	 * Handles and logs details about an unrecognized ArrayList type.
	 * Prints the class name and all implemented interfaces for debugging.
	 * 
	 * @param firstElement The first element of an unhandled ArrayList type
	 */
	private void handleUnknownArrayListType(Object firstElement) {
		System.out.println("Unhandled type in ArrayList: " + firstElement.getClass().getName());
		System.out.println("Interfaces implemented by unhandled type:");
		for (Class<?> iface : firstElement.getClass().getInterfaces()) {
			System.out.println(" - " + iface.getName());
		}
	}
	/**
	 * Processes ResponseWrapper messages by routing to specific handler methods.
	 * 
	 * Supported response types include:
	 * - book
	 * - LoanExtensions
	 * - LoanExtensionHistory
	 * - StatusReport
	 * - BorrowingReport
	 * - MostLateReport
	 * - MostBorrowedReport
	 * - success
	 * 
	 * @param response The ResponseWrapper containing message data
	 * @throws Exception If an error occurs during response processing
	 */
	private void handleResponseWrapperMessage(ResponseWrapper response) {
		try {
			switch (response.getType()) {
			case "book":
				handleBookResponse(response);
				break;
			case "LoanExtensions":
				handleLoanExtensionsResponse(response);
				break;
			case "LoanExtensionHistory":
				handleLoanExtensionHistoryResponse(response);
				break;
			case "StatusReport":
				handleStatusReportResponse(response);
				break;
			case "BorrowingReport":
				handleBorrowingReportResponse(response);
				break;
			case "MostLateReport":
				handleMostLateReportResponse(response);
				break;
			case "MostBorrowedReport":
				handleMostBorrowedReportResponse(response);
				break;
			case "success":
				handleSubscriberSuccessResponse(response);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("Error updating table or handling results: " + e.getMessage());
		}
	}
	/**
	 * Handles book search response by loading books into BookSearchController.
	 * 
	 * @param response ResponseWrapper containing a list of Book objects
	 * @throws ClassCastException If response data is not a list of Books
	 */
	private void handleBookResponse(ResponseWrapper response) {
		ArrayList<Book> books = (ArrayList<Book>) response.getData();
		BookSearchController bookSearchController = BookSearchController.getInstance();
		if (bookSearchController != null) {
			bookSearchController.loadBooks(books);
		}
	}
	/**
	 * Processes loan extensions response and updates ExtensionsReaderCardController.
	 * Runs on JavaFX Platform thread to ensure thread-safe UI update.
	 * 
	 * @param response ResponseWrapper containing a list of LoanExtension objects
	 * @throws ClassCastException If response data is not a list of LoanExtensions
	 */
	private void handleLoanExtensionsResponse(ResponseWrapper response) {
		ArrayList<LoanExtension> loanExtensions = (ArrayList<LoanExtension>) response.getData();
		ExtensionsReaderCardController controller = ExtensionsReaderCardController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateTable(loanExtensions));
		}
	}
	/**
	 * Handles loan extension history response and updates LoanExtensionHistoryController.
	 * Runs on JavaFX Platform thread to ensure thread-safe UI update.
	 * 
	 * @param response ResponseWrapper containing a list of LoanExtension history objects
	 * @throws ClassCastException If response data is not a list of LoanExtensions
	 */
	private void handleLoanExtensionHistoryResponse(ResponseWrapper response) {
		ArrayList<LoanExtension> loanExtensionsHistory = (ArrayList<LoanExtension>) response.getData();
		LoanExtensionHistoryController historyController = LoanExtensionHistoryController.getInstance();
		if (historyController != null) {
			Platform.runLater(() -> historyController.updateTable(loanExtensionsHistory));
		}
	}
	/**
	 * Processes status report response with different data types.
	 * Supports both textual messages and numeric status data.
	 * Updates SubscriberStatusReportController accordingly.
	 * 
	 * @param response ResponseWrapper containing either a String message or ArrayList of int[]
	 * @throws ClassCastException If response data is neither String nor int[] list
	 */
	private void handleStatusReportResponse(ResponseWrapper response) {
		if (response.getData() instanceof String) {
			SubscriberStatusReportController statusController = SubscriberStatusReportController.getInstance();
			if (statusController != null) {
				Platform.runLater(() -> statusController.updateMessage((String) response.getData()));
			}
		} else {
			ArrayList<int[]> statusData = (ArrayList<int[]>) response.getData();
			SubscriberStatusReportController statusController = SubscriberStatusReportController.getInstance();
			if (statusController != null) {
				Platform.runLater(() -> statusController.updateLineChart(statusData));
			}
		}
	}
	/**
	 * Handles borrowing report response with different data types.
	 * Supports both textual messages and numeric borrowing data.
	 * Updates BorrowingTimeReportController accordingly.
	 * 
	 * @param response ResponseWrapper containing either a String message or ArrayList of int[]
	 * @throws ClassCastException If response data is neither String nor int[] list
	 */
	private void handleBorrowingReportResponse(ResponseWrapper response) {
		if (response.getData() instanceof String) {
			BorrowingTimeReportController controller1 = BorrowingTimeReportController.getInstance();
			if (controller1 != null) {
				Platform.runLater(() -> controller1.updateMessage((String) response.getData()));
			}
		} else {
			ArrayList<int[]> borrowingData = (ArrayList<int[]>) response.getData();
			BorrowingTimeReportController controller1 = BorrowingTimeReportController.getInstance();
			if (controller1 != null) {
				Platform.runLater(() -> controller1.updateChart(borrowingData));
			}
		}
	}
	/**
	 * Processes most late returns report response with different data types.
	 * Supports both textual messages and detailed late returns data.
	 * Updates BorrowingTimeReportController accordingly.
	 * 
	 * @param response ResponseWrapper containing either a String message or ArrayList of Object[]
	 * @throws ClassCastException If response data is neither String nor Object[] list
	 */
	private void handleMostLateReportResponse(ResponseWrapper response) {
		if (response.getData() instanceof String) {
			BorrowingTimeReportController controller2 = BorrowingTimeReportController.getInstance();
			if (controller2 != null) {
				Platform.runLater(() -> controller2.updateMessage((String) response.getData()));
			}
		} else {
			ArrayList<Object[]> lateData = (ArrayList<Object[]>) response.getData();
			BorrowingTimeReportController controller2 = BorrowingTimeReportController.getInstance();
			if (controller2 != null) {
				Platform.runLater(() -> controller2.updateLateReturnsChart(lateData));
			}
		}
	}
	/**
	 * Handles most borrowed books report response with different data types.
	 * Supports both textual messages and detailed borrowed books data.
	 * Updates BorrowingTimeReportController accordingly.
	 * 
	 * @param response ResponseWrapper containing either a String message or ArrayList of Object[]
	 * @throws ClassCastException If response data is neither String nor Object[] list
	 */
	private void handleMostBorrowedReportResponse(ResponseWrapper response) {
		if (response.getData() instanceof String) {
			BorrowingTimeReportController controller3 = BorrowingTimeReportController.getInstance();
			if (controller3 != null) {
				Platform.runLater(() -> controller3.updateMessage((String) response.getData()));
			}
		} else {
			ArrayList<Object[]> borrowedData = (ArrayList<Object[]>) response.getData();
			BorrowingTimeReportController controller3 = BorrowingTimeReportController.getInstance();
			if (controller3 != null) {
				Platform.runLater(() -> controller3.updateMostBorrowedChart(borrowedData));
			}
		}
	}
	/**
	 * Processes successful subscriber login/authentication response.
	 * Updates local subscriber (s1) with comprehensive subscriber details.
	 * 
	 * Populates subscriber information including:
	 * - Subscriber ID
	 * - Name
	 * - Username
	 * - Phone Number
	 * - Email
	 * - Password
	 * - Subscription History
	 * - Status
	 * 
	 * @param response ResponseWrapper containing Subscriber object
	 * @throws ClassCastException If response data is not a Subscriber object
	 */

	private void handleSubscriberSuccessResponse(ResponseWrapper response) {
		Subscriber subscriber = (Subscriber) response.getData();

		s1.setSubscriberId(subscriber.getSubscriberId());
		System.out.println("Subscriber ID: " + s1.getSubscriberId());

		s1.setSubscriberName(subscriber.getSubscriberName());
		System.out.println("Subscriber Name: " + s1.getSubscriberName());

		s1.setUsername(subscriber.getUsername());
		System.out.println("Username: " + s1.getUsername());

		s1.setSubscriberPhoneNumber(subscriber.getSubscriberPhoneNumber());
		System.out.println("Phone: " + s1.getSubscriberPhoneNumber());

		s1.setSubscriberEmail(subscriber.getSubscriberEmail());
		System.out.println("Email: " + s1.getSubscriberEmail());

		s1.setPassword(subscriber.getPassword());
		System.out.println("Password: " + s1.getPassword());

		s1.setDetailedSubscriptionHistory(subscriber.getDetailedSubscriptionHistory());
		System.out.println("Detailed Subscription History: " + s1.getDetailedSubscriptionHistory());

		s1.setStatus(subscriber.getStatus());
		System.out.println("Status: " + s1.getStatus());
	}
	/**
	 * Handles various string messages received from the server.
	 * Processes a wide range of scenarios including:
	 * - Book Extension Requests
	 * - Subscriber Management
	 * - Password Reset
	 * - Registration
	 * - Login
	 * - Book Borrowing/Returning
	 * - Order Management
	 * 
	 * Utilizes switch statement to route messages to specific handler methods.
	 * 
	 * @param message The string message received from the server
	 * @throws IllegalStateException If an unhandled message is received
	 */
	private void handleStringMessage(String message) {
		switch (message) {
		case "Return date extended successfully.": {
			handleReturnDateExtendedSuccessfully();
			break;
		}
		case "FailedToExtend": {
			handleFailedExtend();
			break;
		}
		case "Subscriber is frozen Librarian extension": {
			handleSubscriberFrozenExtension();
			break;
		}
		case "Book is not currently borrowed Librarian extension": {
			handleBookNotBorrowedExtension();
			break;
		}
		case "Extensions are allowed only if it’s within 7 days before the return date":
		{
			handleExtensionOutsideAllowedWindow();
			break;
		}
		case "Extension denied - book has pending orders Librarian extension": {
			handlePendingOrdersExtension();
			break;
		}
		// Subscriber ID Scenarios
		case "SubscriberIdExists": {
			handleSubscriberIdExists();
			break;
		}
		case "SubscriberIdNotExists": {
			handleSubscriberIdNotExists();
			break;
		}

		// Password Reset Scenarios
		case "finded id and username for Subscriber": {
			handleFoundSubscriberIdAndUsername();
			break;
		}
		case "Invalid ID or Username for Subscriber": {
			handleInvalidSubscriberIdOrUsername();
			break;
		}
		case "Reset  Subscriber Password Failed": {
			handleResetSubscriberPasswordFailed();
			break;
		}

		// Set New Password Scenarios
		case "Subscriber Password updated successfully": {
			handleSubscriberPasswordUpdatedSuccessfully();
			break;
		}
		case "Failed to update Subscriber password": {
			handleFailedToUpdateSubscriberPassword();
			break;
		}
		case "Reset Subscriber Password Failed": {
			handleResetSubscriberPasswordFailed();
			break;
		}

		// Registration Scenarios
		case "Registration Subscriber Successful": {
			handleRegistrationSubscriberSuccessful();
			break;
		}
		case "Registration Subscriber Failed": {
			handleRegistrationSubscriberFailed();
			break;
		}
		case "Username subscriber already exists": {
			handleUsernameSubscriberAlreadyExists();
			break;
		}

		// Login Scenarios
		case "Subscriber Login Successful": {
			handleSubscriberLoginSuccessful();
			break;
		}
		case "Subscriber Login Failed": {
			handleSubscriberLoginFailed();
			break;
		}

		// Librarian Password Reset Scenarios
		case "finded id and username for librarian": {
			handleFoundLibrarianIdAndUsername();
			break;
		}
		case "Invalid ID or Username": {
			handleInvalidLibrarianIdOrUsername();
			break;
		}

		case "Reset Password Failed": {
			handleResetPasswordFailed();
			break;
		}
		case "Login Failed": {
			handleLoginFailed();
			break;
		}
		case "Registration Librarian Successful": {
			handleRegistrationLibrarianSuccessful();
			break;
		}
		case "Username already exists": {
			handleUsernameAlreadyExists();
			break;
		}
		case "DISCONNECT_SUCCESS": {
			handleDisconnectSuccess();
			break;
		}
		case "DISCONNECT_FAILED": {
			handleDisconnectFailed();
			break;
		}
		case "Password updated successfully": {
			handlePasswordUpdatedSuccessfully();
			break;
		}
		case "Return book process completed successfully": {
			handleReturnBookProcessCompletedSuccessfully();
			break;
		}
		case "Error processing return request": {
			handleErrorProcessingReturnRequest();
			break;
		}
		case "All copies of the book are ordered": {
			handleAllCopiesOrdered();
			break;
		}
		case "You have a borrow for this book": {
			handleYouHaveBorrowForThisBook();
			break;
		}
		// Extend Borrowing Scenarios
		case "Book does not exist in library": {
			handleBookNotExistInLibraryForExtension();
			break;
		}
		case "Subscriber is frozen": {
			handleSubscriberFrozenForExtension();
			break;
		}
		case "Book is not currently borrowed": {
			handleBookNotCurrentlyBorrowed();
			break;
		}
		case "Extensions are allowed only if it’s within 7 days before the return date Subscriber": {
			handleExtensionNotAllowedOutsideWindow();
			break;
		}
		case "Extension denied - book has pending orders": {
			handleExtensionDeniedDueToPendingOrders();
			break;
		}
		case "Extension approved": {
			handleExtensionApproved();
			break;
		}
		case "Extension request failed": {
			handleExtensionRequestFailed();
			break;
		}

		// Complete Order Book Scenarios
		case "Order Found And Status Changed Now Borrow Book": {
			handleOrderFoundAndStatusChanged();
			break;
		}
		case "No Order Was Found": {
			handleNoOrderFound();
			break;
		}
		case "Book Not Found For Order": {
			handleBookNotFoundForOrder();
			break;
		}
		case "Subscriber Not Found For Order": {
			handleSubscriberNotFoundForOrder();
			break;
		}
		case "Order: Subscriber status is frozen": {
			handleOrderSubscriberStatusFrozen();
			break;
		}
		case "The book doesn't returned yet": {
			handleBookNotReturnedYet();
			break;
		}
		// הוספת Cases חדשים
		case "The book doesn't even exist in our library! Borrow": {
			handleBookNotExistInLibrary("The book doesn't even exist in our library! Borrow");
			break;
		}
		case "The book doesn't even exist in our library! Order": {
			handleBookNotExistInLibrary("The book doesn't even exist in our library! Order");
			break;
		}
		case "The book doesn't even exist in our library! Barcode": {
			handleBookNotExistInLibrary("The book doesn't even exist in our library! Barcode");
			break;
		}

		case "subscriber exist and not frozen": {
			handleSubscriberExistAndNotFrozen();
			break;
		}

		case "subscriber is frozen": {
			handleSubscriberFrozen();
			break;
		}

		case "subscriber is not exist": {
			handleSubscriberNotExist();
			break;
		}

		case "Borrow process completed successfully": {
			handleBorrowProcessCompletedSuccessfully();
			break;
		}

		case "This book is already borrowed by this subscriber->Error processing borrow request": {
			handleBookAlreadyBorrowedBySameSubscriber();
			break;
		}
		// Handle cases for books availability
		default:
			if (message.startsWith("There are no copies of the book ")) {
				handleNoCopiesOfTheBook(message);
				break;
			} else if (message.startsWith("book is available & there are copies")) {
				handleBookAvailable(message);
				break;
			} else if (message.startsWith("Book Order Done Successfully")) {
				handleBookOrderDoneSuccessfully(message);
				break;
			}
			  if (message.startsWith("Login Successful:")) {
	                handleLoginSuccessful(message);
	                break;
	            }
		}
		
	}
	/**
	 * Handles successful return date extension.
	 * Updates ExtendReturnDateController with success message.
	 */
	private void handleReturnDateExtendedSuccessfully() {
		ExtendReturnDateController controller = ExtendReturnDateController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Return date has been successfully extended."));
		}
//		ArrayList<LoanExtension> messages = (ArrayList<LoanExtension>) msg;
//		ExtensionsReaderCardController controller2 = ExtensionsReaderCardController.getInstance();
//		if (controller2 != null) {
//			Platform.runLater(() -> controller2.updateTable(messages));
//		}
	}
	/**
	 * Handles failed return date extension attempt.
	 * Updates ExtendReturnDateController with failure message.
	 */
	private void handleFailedExtend() {
		ExtendReturnDateController controller = ExtendReturnDateController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Failed to extend return date. Please try again."));
		}
	}
	/**
	 * Handles scenarios of subscriber account being frozen during extension.
	 * Updates ExtendReturnDateController with account status message.
	 */
	private void handleSubscriberFrozenExtension() {
		ExtendReturnDateController controller = ExtendReturnDateController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Cannot extend loan - your account is frozen."));
		}
	}
	/**
	 * Handles cases where book is not currently borrowed by subscriber.
	 * Updates ExtendReturnDateController with specific error message.
	 */
	private void handleBookNotBorrowedExtension() {
		ExtendReturnDateController controller = ExtendReturnDateController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("This book is not borrowed by you."));
		}
	}
	/**
	 * Handles extension requests outside the allowed time window.
	 * Updates ExtendReturnDateController with time constraint message.
	 */
	private void handleExtensionOutsideAllowedWindow()
	{
		ExtendReturnDateController controller = ExtendReturnDateController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Extensions are allowed only if it’s within 7 days before the return date"));
		}
	}
	/**
	 * Handles extension denial due to pending book orders.
	 * Updates ExtendReturnDateController with order-related message.
	 */
	private void handlePendingOrdersExtension() {
		ExtendReturnDateController controller = ExtendReturnDateController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Cannot extend - book has pending orders."));
		}
	}

	/**
	 * Handles scenario when subscriber ID exists in the system.
	 * Updates EnterSubscriberIDFormController with appropriate message.
	 */
	private void handleSubscriberIdExists() {
		EnterSubscriberIDFormController controller = EnterSubscriberIDFormController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("SubscriberIdExists"));
		}
	}
	/**
	 * Handles scenario when subscriber ID does not exist in the system.
	 * Updates EnterSubscriberIDFormController with appropriate message.
	 */
	private void handleSubscriberIdNotExists() {
		EnterSubscriberIDFormController controller = EnterSubscriberIDFormController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("SubscriberIdNotExists"));
		}
	}
	/**
	 * Handles successful identification of subscriber ID and username.
	 * Updates ResetPasswordSubscriberController with confirmation message.
	 */
	private void handleFoundSubscriberIdAndUsername() {
		ResetPasswordSubscriberController controller = ResetPasswordSubscriberController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("finded id and username for Subscriber"));
		}
	}
	/**
	 * Handles invalid subscriber ID or username during password reset process.
	 * Updates ResetPasswordSubscriberController with error message.
	 */
	private void handleInvalidSubscriberIdOrUsername() {
		ResetPasswordSubscriberController controller = ResetPasswordSubscriberController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Invalid ID or Username for Subscriber"));
		}
	}
	/**
	 * Handles failure in subscriber password reset attempt.
	 * Updates ResetPasswordSubscriberController with error message.
	 */
	private void handleResetSubscriberPasswordFailed() {
		ResetPasswordSubscriberController controller = ResetPasswordSubscriberController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Invalid ID or Username for Subscriber"));
		}
	}
	/**
	 * Handles successful subscriber password update.
	 * Updates SetNewSubscriberPasswordController with success message.
	 */
	private void handleSubscriberPasswordUpdatedSuccessfully() {
		SetNewSubscriberPasswordController controller = SetNewSubscriberPasswordController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Subscriber Password updated successfully"));
		}
	}
	/**
	 * Handles failure in updating subscriber password.
	 * Updates SetNewSubscriberPasswordController with error message.
	 */
	private void handleFailedToUpdateSubscriberPassword() {
		SetNewSubscriberPasswordController controller = SetNewSubscriberPasswordController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Failed to update Subscriber password"));
		}
	}

	/**
	 * Handles successful subscriber registration.
	 * Updates SubscriberSignUpController with success message.
	 */
	private void handleRegistrationSubscriberSuccessful() {
		SubscriberSignUpController controller = SubscriberSignUpController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Registration Subscriber Successful"));
		}
	}
	/**
	 * Handles failed subscriber registration attempt.
	 * Updates SubscriberSignUpController with error message.
	 */
	private void handleRegistrationSubscriberFailed() {
		SubscriberSignUpController controller = SubscriberSignUpController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Registration Subscriber Failed"));
		}
	}
	/**
	 * Handles scenario when subscriber username already exists.
	 * Updates SubscriberSignUpController with duplicate username message.
	 */
	private void handleUsernameSubscriberAlreadyExists() {
		SubscriberSignUpController controller = SubscriberSignUpController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Username subscriber already exists"));
		}
	}

	/**
	 * Handles successful subscriber login process.
	 * Updates SubscriberLoginController with success message.
	 */
	private void handleSubscriberLoginSuccessful() {
		SubscriberLoginController controller = SubscriberLoginController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Subscriber Login Successful"));
		}
	}
	/**
	 * Handles failed subscriber login attempt.
	 * Updates SubscriberLoginController with failure message.
	 */
	private void handleSubscriberLoginFailed() {
		SubscriberLoginController controller = SubscriberLoginController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Subscriber Login Failed"));
		}
	}
	/**
	 * Handles successful identification of librarian ID and username.
	 * Updates ResetPasswordController with confirmation message.
	 */
	private void handleFoundLibrarianIdAndUsername() {
		ResetPasswordController controller = ResetPasswordController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.showError("finded id and username for librarian"));
		}
	}
	/**
	 * Handles invalid librarian ID or username during password reset.
	 * Updates ResetPasswordController with error message.
	 */
	private void handleInvalidLibrarianIdOrUsername() {
		ResetPasswordController controller = ResetPasswordController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.showError("Invalid ID or Username"));
		}
	}
	/**
	 * Handles failure in librarian password reset attempt.
	 * Updates ResetPasswordController with error message.
	 */
	private void handleResetPasswordFailed() {
		ResetPasswordController controller = ResetPasswordController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.showError("Reset Password Failed"));
		}
	}
	/**
	 * Handles successful librarian login with name extraction.
	 * Sets librarian name in LibrarianLoginController and updates message.
	 * 
	 * @param message Login success message containing librarian name
	 */
	private void handleLoginSuccessful(String message) {
		// Extract the librarian's name from the message
		String librarianName = message.substring("Login Successful: ".length());

		LibrarianLoginController controller = LibrarianLoginController.getInstance();
		if (controller != null) {
			controller.setLibrarianName(librarianName); // Set the librarian's name in the controller

			Platform.runLater(() -> controller.updateMessage("Login Successful"));
		}
	}
	/**
	 * Handles failed librarian login attempt.
	 * Updates LibrarianLoginController with failure message.
	 */
	private void handleLoginFailed() {
		LibrarianLoginController controller = LibrarianLoginController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Login Failed"));
		}
	}
	/**
	 * Handles successful librarian registration.
	 * Updates LibrarianSignUpController with success message.
	 */
	private void handleRegistrationLibrarianSuccessful() {
		LibrarianSignUpController controller = LibrarianSignUpController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Registration Successful"));
		}
	}
	/**
	 * Handles scenario when librarian username already exists.
	 * Updates LibrarianSignUpController with duplicate username message.
	 */
	private void handleUsernameAlreadyExists() {
		LibrarianSignUpController controller = LibrarianSignUpController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Username already exists"));
		}
	}
	/**
	 * Handles successful disconnection from server.
	 * Terminates application gracefully.
	 */
	private void handleDisconnectSuccess() {
		System.out.println("Disconnection confirmed by server.");
		System.exit(0);
	}
	/**
	 * Handles failed disconnection attempt.
	 * Logs error message.
	 */
	private void handleDisconnectFailed() {
		System.out.println("Disconnection failed. Please try again.");
	}
	/**
	 * Handles successful password update for librarian.
	 * Updates NewPasswordToLibrarianController with success message.
	 */
	private void handlePasswordUpdatedSuccessfully() {
		NewPasswordToLibrarianController controller = NewPasswordToLibrarianController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Password updated successfully"));
		}
	}
	/**
	 * Handles successful book return process.
	 * Updates ReturnBookController with success message.
	 */
	private void handleReturnBookProcessCompletedSuccessfully() {
		ReturnBookController controller = ReturnBookController.getReturnBookController();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Return Book Successfully"));
		}
	}
	/**
	 * Handles error in book return process.
	 * Updates ReturnBookController with error message when no matching borrow record exists.
	 */
	private void handleErrorProcessingReturnRequest() {
		ReturnBookController controller = ReturnBookController.getReturnBookController();
		if (controller != null) {
			Platform.runLater(() -> controller
					.updateMessage("Return Book Failed -> No existing borrow record matches the provided details."));
		}
	}
	/**
	 * Handles scenarios with no available book copies.
	 * Routes message to appropriate controller based on context:
	 * - Borrow
	 * - Order
	 * - Barcode
	 * 
	 * @param message Detailed message about book availability
	 */
	private void handleNoCopiesOfTheBook(String message) {
		String borrowOrOrderOrBarcode = message.substring("There are no copies of the book ".length());

		if (borrowOrOrderOrBarcode.startsWith("Borrow")) {
			BorrowBookController controller = BorrowBookController.getInstance();
			if (controller != null) {
				Platform.runLater(() -> controller.updateMessage("There are no copies of the book"));
			}
		} else if (borrowOrOrderOrBarcode.startsWith("Order")) {
			String bookToOrderID = borrowOrOrderOrBarcode.substring("Order ".length());
			OrderBookController controller = OrderBookController.getInstance();
			if (controller != null) {
				Platform.runLater(() -> controller.updateMessage("There are no copies of the book " + bookToOrderID));
			}
		} else if (borrowOrOrderOrBarcode.equals("Barcode")) {
			BarcodeBorrowBookController controller = BarcodeBorrowBookController.getInstance();
			if (controller != null) {
				Platform.runLater(() -> controller.updateMessage("There are no copies of the book"));
			}
		}
	}
	/**
	 * Handles scenario when all copies of a book are already ordered.
	 * Updates OrderBookController with appropriate message.
	 */
	private void handleAllCopiesOrdered() {
		OrderBookController controller = OrderBookController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("All copies of the book are ordered"));
		}
	}
	/**
	 * Handles scenario when subscriber has already borrowed the book.
	 * Updates OrderBookController with appropriate message.
	 */
	private void handleYouHaveBorrowForThisBook() {
		OrderBookController controller = OrderBookController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("You have a borrow for this book"));
		}
	}
	/**
	 * Handles scenario when book is available with copies.
	 * Routes message to appropriate controller based on context:
	 * - Borrow
	 * - Order
	 * - Barcode
	 * 
	 * @param message Detailed message about book availability
	 */
	private void handleBookAvailable(String message) {
		String borrowOrOrderOrBarcode = message.substring("book is available & there are copies ".length());

		if (borrowOrOrderOrBarcode.equals("Borrow")) {
			BorrowBookController controller = BorrowBookController.getInstance();
			if (controller != null) {
				Platform.runLater(() -> controller.updateMessage("book is available & there are copies"));
			}
		} else if (borrowOrOrderOrBarcode.equals("Order")) {
			OrderBookController controller1 = OrderBookController.getInstance();
			if (controller1 != null) {
				Platform.runLater(() -> controller1.updateMessage("book is available & there are copies"));
			}
		} else if (borrowOrOrderOrBarcode.equals("Barcode")) {
			BarcodeBorrowBookController controller1 = BarcodeBorrowBookController.getInstance();
			if (controller1 != null) {
				Platform.runLater(() -> controller1.updateMessage("book is available & there are copies"));
			}
		}
	}
	/**
	 * Handles scenario when book does not exist in library.
	 * Routes message to appropriate controller based on context:
	 * - Borrow
	 * - Order
	 * - Barcode
	 * 
	 * @param message Detailed message about book non-existence
	 */
	private void handleBookNotExistInLibrary(String message) {
		String borrowOrOrderOrBarcode = message.substring("The book doesn't even exist in our library! ".length());

		if (borrowOrOrderOrBarcode.equals("Borrow")) {
			BorrowBookController controller = BorrowBookController.getInstance();
			if (controller != null) {
				Platform.runLater(() -> controller.updateMessage("The book doesn't even exist in our library!"));
			}
		} else if (borrowOrOrderOrBarcode.equals("Order")) {
			OrderBookController controller = OrderBookController.getInstance();
			if (controller != null) {
				Platform.runLater(() -> controller.updateMessage("The book doesn't even exist in our library!"));
			}
		} else if (borrowOrOrderOrBarcode.equals("Barcode")) {
			BarcodeBorrowBookController controller1 = BarcodeBorrowBookController.getInstance();
			if (controller1 != null) {
				Platform.runLater(() -> controller1.updateMessage("The book doesn't even exist in our library!"));
			}
		}
	}
	/**
	 * Handles scenario when subscriber exists and is not frozen.
	 * Updates CheckSubscriberStatusController with appropriate message.
	 */
	private void handleSubscriberExistAndNotFrozen() {
		CheckSubscriberStatusController controller = CheckSubscriberStatusController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("subscriber exist and not frozen"));
		}
	}
	/**
	 * Handles scenario when subscriber account is frozen.
	 * Updates CheckSubscriberStatusController with appropriate message.
	 */
	private void handleSubscriberFrozen() {
		CheckSubscriberStatusController controller = CheckSubscriberStatusController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("subscriber is frozen"));
		}
	}
	/**
	 * Handles scenario when subscriber does not exist.
	 * Updates CheckSubscriberStatusController with appropriate message.
	 */
	private void handleSubscriberNotExist() {
		CheckSubscriberStatusController controller = CheckSubscriberStatusController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("subscriber is not exist"));
		}
	}
	/**
	 * Handles successful book borrowing process.
	 * Updates ConfirmBorrowController with success message.
	 */
	private void handleBorrowProcessCompletedSuccessfully() {
		ConfirmBorrowController controller = ConfirmBorrowController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Borrow process completed successfully"));
		}
	}
	/**
	 * Handles scenario when book is already borrowed by the same subscriber.
	 * Updates ConfirmBorrowController with error message.
	 */
	private void handleBookAlreadyBorrowedBySameSubscriber() {
		ConfirmBorrowController controller = ConfirmBorrowController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage(
					"This book is already borrowed by this subscriber->Error processing borrow request"));
		}
	}
	/**
	 * Handles successful book order process.
	 * Updates OrderBookController with success message.
	 * 
	 * @param message Detailed message about order completion
	 */
	private void handleBookOrderDoneSuccessfully(String message) {
		String updatedOrNot = message.substring("Book Order Done Successfully ".length());
		OrderBookController controller = OrderBookController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Order process completed successfully " + updatedOrNot));
		}
	}
	/**
	 * Handles scenario when book does not exist in library during loan extension request.
	 * Updates ExtendBorrowingController with appropriate error message.
	 */
	private void handleBookNotExistInLibraryForExtension() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Book does not exist in library"));
		}
	}
	/**
	 * Handles scenario when subscriber account is frozen during loan extension request.
	 * Updates ExtendBorrowingController with account status error message.
	 */
	private void handleSubscriberFrozenForExtension() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Cannot extend loan - subscriber account is frozen"));
		}
	}
	/**
	 * Handles scenario when book is not currently borrowed by subscriber during extension request.
	 * Updates ExtendBorrowingController with borrowing status error message.
	 */
	private void handleBookNotCurrentlyBorrowed() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Cannot extend loan - book is not borrowed by you"));
		}
	}
	/**
	 * Handles scenario when loan extension is requested outside the permitted 7-day window.
	 * Updates ExtendBorrowingController with time constraint error message.
	 */
	private void handleExtensionNotAllowedOutsideWindow() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Extensions are allowed only if it’s within 7 days before the return date"));
		}
	}
	/**
	 * Handles scenario when loan extension is denied due to existing pending book orders.
	 * Updates ExtendBorrowingController with order-related error message.
	 */
	private void handleExtensionDeniedDueToPendingOrders() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Cannot extend loan - book has pending orders"));
		}
	}
	/**
	 * Handles successful loan extension approval.
	 * Updates ExtendBorrowingController with success message.
	 */
	private void handleExtensionApproved() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Loan extension approved"));
		}
	}
	/**
	 * Handles failed loan extension request.
	 * Updates ExtendBorrowingController with failure message.
	 */
	private void handleExtensionRequestFailed() {
		ExtendBorrowingController controller = ExtendBorrowingController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Extension request failed - please try again"));
		}
	}
	/**
	 * Handles scenario when order is found and status is changed to book borrowing.
	 * Updates CompleteOrderBook controller with status change message.
	 */
	private void handleOrderFoundAndStatusChanged() {
		CompleteOrderBook controller = CompleteOrderBook.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Order Found And Status Changed Now Borrow Book"));
		}
	}
	/**
	 * Handles scenario when no order is found.
	 * Updates CompleteOrderBook controller with appropriate message.
	 */
	private void handleNoOrderFound() {
		CompleteOrderBook controller = CompleteOrderBook.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("No Order Was Found"));
		}
	}
	/**
	 * Handles scenario when book is not found for a specific order.
	 * Updates CompleteOrderBook controller with book not found message.
	 */
	private void handleBookNotFoundForOrder() {
		CompleteOrderBook controller = CompleteOrderBook.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Book Not Found For Order"));
		}
	}
	/**
	 * Handles scenario when subscriber is not found for a specific order.
	 * Updates CompleteOrderBook controller with subscriber not found message.
	 */
	private void handleSubscriberNotFoundForOrder() {
		CompleteOrderBook controller = CompleteOrderBook.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Subscriber Not Found For Order"));
		}
	}
	/**
	 * Handles scenario when subscriber's account status prevents order processing.
	 * Updates OrderBookController with frozen account message.
	 */
	private void handleOrderSubscriberStatusFrozen() {
		OrderBookController controller = OrderBookController.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("Order: Subscriber status is frozen"));
		}
	}
	/**
	 * Handles scenario when book is not yet returned for order completion.
	 * Updates CompleteOrderBook controller with book return status message.
	 */
	private void handleBookNotReturnedYet() {
		CompleteOrderBook controller = CompleteOrderBook.getInstance();
		if (controller != null) {
			Platform.runLater(() -> controller.updateMessage("The book doesn't returned yet"));
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(Object message) {
		try {
			openConnection(); // in order to send more than one message
			awaitResponse = true;

			if (message instanceof String) {
				sendToServer(message);
			} else if (message instanceof Serializable) {
				sendToServer(message);
			} else {
				throw new IllegalArgumentException("Message must be either a String or a Serializable object");
			}

			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			showErrorAlert("Connection Error", "Could not send message to server: undetected port");
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			showErrorAlert("Invalid Message", e.getMessage());
			clientUI.display("Invalid message type: " + e.getMessage());
		}
	}
	/**
	 * Displays a JavaFX error alert dialog with specified title and content.
	 * 
	 * Key characteristics:
	 * - Uses JavaFX Alert with ERROR type
	 * - Sets custom title
	 * - Removes default header text
	 * - Displays custom error content
	 * - Blocks UI until alert is dismissed
	 * 
	 * @param title The title of the error dialog window
	 * @param content The detailed error message to be displayed in the alert
	 */
	private void showErrorAlert(String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}

//End of ChatClient class