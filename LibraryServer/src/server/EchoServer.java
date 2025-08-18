// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import java.io.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import DBController.DBConnector;
import gui.ConnectionsController;
import javafx.application.Platform;
import logic.Book;
import logic.BorrowedBook;
import logic.BorrowedBooksHistory;
import logic.BorrowingReport;
import logic.ExtendLoanRequest;
import logic.ExtendReturnDateRequest;
import logic.Librarian;
import logic.LibrarianMessage;
import logic.LoanExtension;
import logic.OrderedBook;
import logic.OrderedBooksHistory;
import logic.ResponseWrapper;
import logic.ReturnBook;
import logic.ReturnBookHistory;
import logic.Subscriber;
import logic.SubscriberChangesHistory;
import logic.YearMonth;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
   // final public static int DEFAULT_PORT = 5555;

	// Constructors ****************************************************

	public static Subscriber[] subscribers;

	private static EchoServer instance;

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 * 
	 */
	public EchoServer(int port) {
		super(port);
		instance = this;
	}

	/**
	 * Returns the single instance of the EchoServer. If the instance is not already
	 * created, it initializes a new instance.
	 *
	 * @return The single instance of EchoServer.
	 */
	public static EchoServer getInstance() {

		return instance;
	}

	private ConnectionsController connectionsController;

	/**
	 * Sets the ConnectionsController for the EchoServer.
	 *
	 * @param controller The ConnectionsController to set.
	 */
	public void setConnectionsController(ConnectionsController controller) {
		this.connectionsController = controller;
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		if (msg instanceof ExtendReturnDateRequest) {
			extendReturnRequest(msg, client);
		}

		if (msg instanceof ReturnBookHistory) {
			returnBookHistory(msg, client);
		}
		if (msg instanceof ExtendLoanRequest) {
			extendLoanRequest(msg, client);
		}

		if (msg instanceof BorrowedBooksHistory) {
			borrowedBookHistory(msg, client);
		}
		if (msg instanceof OrderedBooksHistory) {
			OrderBookHistory(msg, client);
		}

		if (msg instanceof SubscriberChangesHistory) {
			subscriberChangesHistory(msg, client);
		}

		if (msg instanceof BorrowedBook) {
			borrowProcess(msg, client);
		}

		if (msg instanceof ReturnBook) {
			returnProcess(msg, client);
		}
		if (msg instanceof Subscriber) {
			subscriberMethod(msg, client);
		}
		if (msg instanceof Book) {
			checkBook(msg, client);
		}
		if (msg instanceof String) {
			String message = (String) msg;

			// Using switch-case for message handling
			switch (getMessageType(message)) {

			case "GET_LOAN_EXTENSION_HISTORY": {
				getLoanExtensionHistory(message, client);
				break;
			}


			case "GET_LIBRARIAN_MESSAGES": {
				getLibrarianMessages(message, client);
				break;
			}

			case "CATEGORY_SEARCH": {
				categorySearch(message, client);
				break;
			}

			case "GET_ALL_BOOKS": {
				getAllBooks(message, client);
				break;
			}

			case "CLOSE": {
				close(message, client);
				break;
			}

			case "GET_ALL_SUBSCRIBERS": {
				getAllSubscribers(message, client);
				break;
			}

			case "UpdateSubscriber": {
				handleUpdateSubscriber(message);
				break;
			}

			default: {
				try {
					Subscriber subscriber = DBConnector.getSubscriber(message);
					if (subscriber != null) {
						System.out.println("Server Found");
						this.sendToAllClients(subscriber.toString());
					} else {
						System.out.println("Not Found");
						this.sendToAllClients("Error");
					}
				} catch (SQLException e) {
					System.out.println("Database Error");
					this.sendToAllClients("Error");
					e.printStackTrace();
				}
				break;
			}
			}
		}

		if (msg instanceof ResponseWrapper) {
			ResponseWrapper response = (ResponseWrapper) msg;

			// בקשת אימות איפוס סיסמה לספרן
			switch (response.getType()) {
			case "LIBRARIAN_VERIFY_RESET_PASSWORD": {
				librarianVerifyResetPassword(response, client);
				break;
			}

			// בקשת איפוס סיסמה לספרן
			case "LIBRARIAN_RESET_PASSWORD": {
				librarianResetPassword(response, client);
				break;
			}

			// בקשת הרשמת ספרן חדש
			case "LIBRARIAN_SIGNUP": {
				librarianSignUp(response, client);
				break;
			}

			// בקשת התחברות ספרן
			case "LIBRARIAN_LOGIN": {
				librarianLogin(response, client);
				break;
			}
			// בקשת אימות איפוס סיסמה למנוי
			case "SUBSCRIBER_VERIFY_RESET_PASSWORD": {
				subscriberVerifyResetPassword(response, client);
				break;
			}

			// בקשת איפוס סיסמה למנוי
			case "SUBSCRIBER_SET_NEW_PASSWORD": {
				subscriberSetNewPassword(response, client);
				break;
			}

			// בקשת הרשמת מנוי חדש
			case "SUBSCRIBER_SIGNUP": {
				subscriberSignUp(response, client);
				break;
			}

			// טיפול בבקשה FetchBorrowedBooks
			case "FetchBorrowedBooks": {
				fetchBorrowedBooks(response, client);
				break;
			}
			case "FetchOrderedBooks": {
				fetchOrderedBooks(response, client);
				break;
			}

			case "FetchLoanExtensions": {
				fetchLoanExtensions(response, client);
				break;
			}
			case "CheckSubscriber": {
				checkSubscriber(response, client);
				break;
			}

			case "subscriberLogin": {
				subscriberLogin(response, client);
				break;
			}

			// Handle barcode search request
			case "SEARCH_BOOK_BY_BARCODE": {
				searchBookByBarcode(response, client);
				break;
			}
			case "SEARCH_BOOK_BY_NAME": {
				searchBookByName(response, client);
				break;
			}
			case "SEARCH_BOOK_BY_DESCRIPTION": {
				searchBookByDescription(response, client);
				break;
			}

			// check if the request to check status is to order or borrow
			case "Borrow": {
				checkSubscriberForBorrow(response, client);
				break;
			}
			// response type is "DoneOrder"
			case "DoneOrder": {
				DoneOrder(response, client);
				break;

			}
			case "CompleteOrder": {
				fullfiledOreder(response, client);
				break;
			}
			case "checkBookForBorrow":
			case "checkBookForOrder": {
				checkBookForBorrowOrOrder(response, client);
				break;
			}
			case "StatusReport": {
				statusReport(response, client);
				break;
			}
			case "BorrowingReport": {
				BorrowingReport(response, client);
				break;
			}
			case "MostLateReport": {
				MostLateReport(response, client);
				break;
			}
			case "MostBorrowedReport": {
				MostBorrowedReport(response, client);
				break;
			}
			default: {
				System.out.println("Unhandled request type: " + response.getType());
				try {
					client.sendToClient("Unhandled request type");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				break;
			}
			}
		} else {
			try {
				client.sendToClient("ERROR: Non-string message type received");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Searches for books by  description based on the given search term,
	 * and sends the search results to the client. In case of an error, an empty list is sent.
	 * 
	 * @param message The message containing the search term (after "SEARCH_BOOK_BY_DESCRIPTION").
	 * @param client The client to which the search results are sent.
	 */
	private void searchBookByDescription(ResponseWrapper response, ConnectionToClient client) {
		String bookDescription=(String)response.getData();
		try {
			ArrayList<Book> bookResults = DBConnector.searchBooksByDescription(bookDescription);
			// Sends a ResponseWrapper to the client with the type "book" and the
			// corresponding ArrayList of book results.
			client.sendToClient(new ResponseWrapper("book", bookResults));
			System.out.println("Sent book search results to client");
		} catch (SQLException e) {
			System.out.println("Error searching for books: " + e.getMessage());
			try {
				// In case of failure, sends a ResponseWrapper with an empty ArrayList to the
				// client.
				client.sendToClient(new ResponseWrapper("book", new ArrayList<>()));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Error sending results to client: " + e.getMessage());
		}		
	}

	/**
	 * Searches for books by name based on the given search term,
	 * and sends the search results to the client. In case of an error, an empty list is sent.
	 * 
	 * @param message The message containing the search term (after "SEARCH_BOOK_BY_NAME ").
	 * @param client The client to which the search results are sent.
	 */
	private void searchBookByName(ResponseWrapper response, ConnectionToClient client) {
		String bookName=(String)response.getData();
		try {
			ArrayList<Book> bookResults = DBConnector.searchBooksByName(bookName);
			// Sends a ResponseWrapper to the client with the type "book" and the
			// corresponding ArrayList of book results.
			client.sendToClient(new ResponseWrapper("book", bookResults));
			System.out.println("Sent book search results to client");
		} catch (SQLException e) {
			System.out.println("Error searching for books: " + e.getMessage());
			try {
				// In case of failure, sends a ResponseWrapper with an empty ArrayList to the
				// client.
				client.sendToClient(new ResponseWrapper("book", new ArrayList<>()));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Error sending results to client: " + e.getMessage());
		}		
	}
	
	

	/**
	 * Determines the message type based on the provided message string. The method
	 * checks if the message starts with or equals specific predefined strings to
	 * classify it into different message types. If no match is found, it returns
	 * "UNKNOWN".
	 *
	 * @param message The message string to be classified.
	 * @return A string representing the message type.
	 */
	private String getMessageType(String message) {
		if (message.startsWith("GET_LOAN_EXTENSION_HISTORY:"))
			return "GET_LOAN_EXTENSION_HISTORY";
		if (message.startsWith("SEARCH_BOOK"))
			return "SEARCH_BOOK";
		if (message.startsWith("CATEGORY_SEARCH"))
			return "CATEGORY_SEARCH";
		if (message.equals("GET_LIBRARIAN_MESSAGES"))
			return "GET_LIBRARIAN_MESSAGES";
		if (message.equals("GET_ALL_BOOKS"))
			return "GET_ALL_BOOKS";
		if (message.equals("CLOSE"))
			return "CLOSE";
		if (message.equals("GET_ALL_SUBSCRIBERS"))
			return "GET_ALL_SUBSCRIBERS";
		if (message.startsWith("UpdateSubscriber"))
			return "UpdateSubscriber";
		return "UNKNOWN";
	}

	/**
	 * Handles the request to generate and send a "Most Borrowed Report" to the client.
	 * This method retrieves the year and month from the response, fetches the report 
	 * from the database, and sends it to the client. If the report is unavailable or
	 * there is a database error, an appropriate error message is sent to the client.
	 *
	 * @param response The response wrapper containing the year and month data for the report.
	 * @param client The client connection to which the report or error message will be sent.
	 */
	private void MostBorrowedReport(ResponseWrapper response, ConnectionToClient client) {
		YearMonth yearMonth = (YearMonth) response.getData();
		int year = yearMonth.getYear();
		int month = yearMonth.getMonth();
		System.out.println("Received Year: " + year);
		System.out.println("Received Month: " + month);

		try {
			ArrayList<Object[]> borrowedArr = DBConnector.getMostBorrowedReport(year, month);
			if (borrowedArr == null || borrowedArr.isEmpty()) {
				client.sendToClient(new ResponseWrapper("MostBorrowedReport", "The report is currently unavailable"));
			} else {
				client.sendToClient(new ResponseWrapper("MostBorrowedReport", borrowedArr));
			}
		} catch (SQLException e) {
			System.err.println("Database error while fetching report: " + e.getMessage());
			e.printStackTrace();
			try {
				client.sendToClient(
						new ResponseWrapper(response.getType(), "An error occurred while generating the report"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("IO error while sending data to client: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * Generates and sends the "Most Late Report" for the specified year and month to the client.
	 * Sends an error message if the report is unavailable or if there is a database or I/O error.
	 *
	 * @param response The response containing year and month data.
	 * @param client The client to send the report or error message to.
	 */
	private void MostLateReport(ResponseWrapper response, ConnectionToClient client) {
		YearMonth yearMonth = (YearMonth) response.getData();
		int year = yearMonth.getYear();
		int month = yearMonth.getMonth();
		System.out.println("Received Year: " + year);
		System.out.println("Received Month: " + month);

		try {
			ArrayList<Object[]> lateArr = DBConnector.getMostLateReport(year, month);
			if (lateArr == null || lateArr.isEmpty()) {
				client.sendToClient(new ResponseWrapper("MostLateReport", "The report is currently unavailable"));
			} else {
				client.sendToClient(new ResponseWrapper("MostLateReport", lateArr));
			}
		} catch (SQLException e) {
			System.err.println("Database error while fetching report: " + e.getMessage());
			e.printStackTrace();
			try {
				client.sendToClient(
						new ResponseWrapper(response.getType(), "An error occurred while generating the report"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("IO error while sending data to client: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Generates and sends the "Borrowing Report" for the specified year and month to the client.
	 * Sends an error message if the report is unavailable or if there is a database or I/O error.
	 *
	 * @param response The response containing year and month data.
	 * @param client The client to send the report or error message to.
	 */
	private void BorrowingReport(ResponseWrapper response, ConnectionToClient client) {
		YearMonth yearMonth = (YearMonth) response.getData();
		int year = yearMonth.getYear();
		int month = yearMonth.getMonth();
		System.out.println("Received Year: " + year);
		System.out.println("Received Month: " + month);

		try {
			ArrayList<int[]> arr = DBConnector.getBorrowingReport(year, month);
			if (arr == null || arr.isEmpty()) {
				client.sendToClient(new ResponseWrapper("BorrowingReport", "The report is currently unavailable"));
			} else {
				client.sendToClient(new ResponseWrapper("BorrowingReport", arr));

			}
		} catch (SQLException e) {
			System.err.println("Database error while fetching report: " + e.getMessage());
			e.printStackTrace();
			try {
				client.sendToClient(
						new ResponseWrapper(response.getType(), "An error occurred while generating the report"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("IO error while sending data to client: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Generates and sends the "Status Report" for the specified year and month to the client.
	 * Sends an error message if the report is unavailable or if there is a database or I/O error.
	 *
	 * @param response The response containing year and month data.
	 * @param client The client to send the report or error message to.
	 */
	private void statusReport(ResponseWrapper response, ConnectionToClient client) {
		YearMonth yearMonth = (YearMonth) response.getData();
		int year = yearMonth.getYear();
		int month = yearMonth.getMonth();
		System.out.println("Received Year: " + year);
		System.out.println("Received Month: " + month);

		try {
			ArrayList<int[]> arr = DBConnector.getSubscriberStatusReport(year, month);
			if (arr == null || arr.isEmpty()) {
				client.sendToClient(new ResponseWrapper("StatusReport", "The report is currently unavailable"));
			} else {
				client.sendToClient(new ResponseWrapper("StatusReport", arr));

			}
		} catch (SQLException e) {
			System.err.println("Database error while fetching report: " + e.getMessage());
			e.printStackTrace();
			try {
				client.sendToClient(
						new ResponseWrapper(response.getType(), "An error occurred while generating the report"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("IO error while sending data to client: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all subscribers from the database and sends them to the client as a ResponseWrapper.
	 * Additionally, logs connection details of the client and updates the connections table if a controller is available.
	 * 
	 * @param message The message received from the client. It's not used in this method but could be used for logging or further processing.
	 * @param client The client to which the response is sent. Connection details are also logged.
	 */
	private void getAllSubscribers(String message, ConnectionToClient client) {
		try {
			ArrayList<Subscriber> subscribers = DBConnector.getAllSubscribers();
			// Sends a ResponseWrapper to the client with the type "subscriber" and the
			// corresponding ArrayList of subscribers.
			client.sendToClient(new ResponseWrapper("subscriber", subscribers));

			System.out.println("Client Connection Details:");
			System.out.println("IP Address: " + client.getInetAddress().getHostAddress());
			System.out.println("Host Name: " + client.getInetAddress().getHostName());
			System.out.println("Connection Status: Connected");

			if (connectionsController != null) {
				Platform.runLater(() -> {
					connectionsController.updateTable(client.getInetAddress().getHostAddress(),
							client.getInetAddress().getHostName(), "Connected");
				});
			}
		} catch (Exception e) {
			System.out.println("Error getting subscribers");
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Handles the disconnection process for a client. Logs the client's disconnection details,
	 * sends a success or failure message to the client, and updates the connections table.
	 * 
	 * @param message The message received from the client indicating the disconnection request.
	 * @param client The client that is disconnecting. The client's IP address and host name are logged.
	 */
	private void close(String message, ConnectionToClient client) {
		System.out.println("Client Disconnected:");
		System.out.println("IP Address: " + client.getInetAddress().getHostAddress());
		System.out.println("Host Name: " + client.getInetAddress().getHostName());
		try {
			client.sendToClient("DISCONNECT_SUCCESS");
		} catch (IOException e) {
			e.printStackTrace();
			try {
				client.sendToClient("DISCONNECT_FAILED");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		Platform.runLater(() -> {
			connectionsController.updateTable(client.getInetAddress().getHostAddress(),
					client.getInetAddress().getHostName(), "Disconnected");
		});
		return;
	}

	/**
	 * Retrieves a list of all books from the database and sends the list to the client.
	 * If an error occurs during the retrieval process, it prints the error details.
	 * 
	 * @param message The message received from the client requesting the list of books.
	 * @param client The client to which the list of books will be sent.
	 */
	private void getAllBooks(String message, ConnectionToClient client) {
		try {
			ArrayList<Book> books = DBConnector.getAllBooks();
			// Sends a ResponseWrapper to the client with the type "book" and the
			// corresponding ArrayList of book results.
			client.sendToClient(new ResponseWrapper("book", books));
			// return;
		} catch (Exception e) {
			System.out.println("Error getting subscribers");
			e.printStackTrace();
		}

		return;
	}

	/**
	 * Performs a category-based search for books and sends the search results to the client.
	 * 
	 * This method extracts the search term from the message, queries the database for books that
	 * match the category, and sends the results to the client. If an error occurs during the
	 * database search or while sending the results, appropriate error messages are printed.
	 * 
	 * @param message The message received from the client, which includes the category search term.
	 * @param client The client to which the search results will be sent.
	 */
	private void categorySearch(String message, ConnectionToClient client) {
		String searchTerm = message.substring("CATEGORY_SEARCH ".length());

		try {
			ArrayList<Book> bookResults = DBConnector.searchBooksByCategory(searchTerm);
			// Sends a ResponseWrapper to the client with the type "book" and the
			// corresponding ArrayList of book results.
			client.sendToClient(new ResponseWrapper("book", bookResults));
			System.out.println("Sent book search results to client");
		} catch (SQLException e) {
			System.out.println("Error searching for books: " + e.getMessage());
			try {
				// In case of failure, sends a ResponseWrapper with an empty ArrayList to the
				// client.
				client.sendToClient(new ResponseWrapper("book", new ArrayList<>()));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Error sending results to client: " + e.getMessage());
		}
		return;
	}

	
	/**
	 * Fetches librarian messages from the database and sends them to the client.
	 * In case of an error, an empty list is sent to the client.
	 * 
	 * @param message The message received from the client (not used).
	 * @param client The client to which the messages are sent.
	 */
	private void getLibrarianMessages(String message, ConnectionToClient client) {
		try {
			ArrayList<LibrarianMessage> messages = DBConnector.getLibrarianMessages();
			try {
				client.sendToClient(messages);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				client.sendToClient(new ArrayList<LibrarianMessage>());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	
	

	
	/**
	 * Retrieves the loan extension history for a specific subscriber based on the provided subscriber ID
	 * and sends the result back to the client. If an error occurs, the stack trace is printed.
	 * 
	 * @param message The message containing the subscriber ID after "GET_LOAN_EXTENSION_HISTORY:".
	 * @param client The client to which the loan extension history is sent.
	 */
	private void getLoanExtensionHistory(String message, ConnectionToClient client) {
		System.out.println("EXTENSION1");
		// Extract the subscriberId from the message
		String subscriberId = message.substring("GET_LOAN_EXTENSION_HISTORY:".length());

		try {
			// Fetch loan extension history from the database
			ArrayList<LoanExtension> loanExtensions = DBConnector.getLoanExtensionHistory(subscriberId);

			// Send the results back to the client
			// client.sendToClient(loanExtensions);
			client.sendToClient(new ResponseWrapper("LoanExtensionHistory", loanExtensions));
			System.out.println("EXTENSION1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return; // Exit after handling this request
	}

	
	/**
	 * Checks if a book exists in the library and its availability based on the provided book details.
	 * Validates the book's properties and sends appropriate responses to the client regarding its existence 
	 * and availability.
	 * 
	 * @param msg The message containing the book object to be checked.
	 * @param client The client to which the results are sent.
	 */
	private void checkBook(Object msg, ConnectionToClient client) {
		Book book = (Book) msg;

		if (book.getBookID() != 0 && !book.getBookName().isEmpty() && book.getBookImage() == null
				&& !book.getBookTopic().isEmpty() && book.getBookDescription().isEmpty()
				&& !book.getauthorName().isEmpty() && book.getNumberOfCopies() == 0
				&& book.getNumberOfAvailableCopies() == 0 && book.getFirstReturnDate() == null
				&& book.getBookPlace().isEmpty()) {

			try {
				// Check if the book exists in the database
				boolean doesExist = DBConnector.doesBookExist(book);
				boolean isAvailable = DBConnector.isBookAvailable(book);

				if (doesExist) {
					if (isAvailable) {
						client.sendToClient("book is available & there are copies");
					} else {
						client.sendToClient("There are no copies of the book");
					}
				} else {
					client.sendToClient("The book doesn't even exist in our library!");
				}
			} catch (Exception e) {
				System.out.println("Error during book validation: " + e.getMessage());
				// Optionally, you could send an error message here if needed
			}
		} else {
			// If the input data is invalid, you can choose to ignore it or handle it
			// differently
			System.out.println("Invalid book details provided.");
		}
	}

	/**
	 * Processes the completion of a book order by verifying the existence of the subscriber, 
	 * the book, and the order. It checks if the book is available for borrowing and updates the order status.
	 * Sends appropriate responses to the client based on the validation results.
	 *
	 * @param response The response wrapper containing the subscriber ID and book ID for the order.
	 * @param client The client to which the results are sent.
	 */
	private void fullfiledOreder(ResponseWrapper response, ConnectionToClient client) {
		System.out.printf("CompleteOrder");
		String subscriberIDBookName = (String) response.getData();
		String[] result = subscriberIDBookName.split(", ");
		int subscriberId = Integer.parseInt(result[0]);
		int bookId = Integer.parseInt(result[1]);
		try {
			Subscriber subscriberExists = DBConnector.getSubscriber(String.valueOf(subscriberId));
			boolean bookExists = DBConnector.doesBookExistById(bookId);

			if (subscriberExists == null) {
				System.out.println("subscriber Not Found");
				client.sendToClient("Subscriber Not Found For Order");
				return;
			}
			if (!bookExists) {
				System.out.println("book doesnt exists");
				client.sendToClient("Book Not Found For Order");
				return;

			}
			boolean OrderExists = DBConnector.checkExistanceOrder(subscriberId, bookId);
			System.out.printf("OrderExists:%b\n", OrderExists);

			if (OrderExists) {
				boolean doesTheBookReturnedAndSubscriberCanPick = DBConnector.doesSubscriberCanPickUPBook(subscriberId,
						bookId);
				if (!doesTheBookReturnedAndSubscriberCanPick) {
					client.sendToClient("The book doesn't returned yet");
					return;
				}
				boolean CompleteOrder = DBConnector.completeOrder(subscriberId, bookId);
				if (CompleteOrder == true) {
					client.sendToClient("Order Found And Status Changed Now Borrow Book");

				}
			} else {
				client.sendToClient("No Order Was Found");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Completes a book order by saving the order and updating the available copies of the book.
	 * Additionally, updates the first return date for the order. Sends a success response to the client.
	 *
	 * @param response The response wrapper containing the ordered book details.
	 * @param client The client to which the results are sent.
	 */
	private void DoneOrder(ResponseWrapper response, ConnectionToClient client) {
		OrderedBook book = (OrderedBook) response.getData();
		try {
			boolean Updated = DBConnector.saveOrderAndUpdateCopies(book);

			DBConnector.updateFirstReturnDateFromOrder(book);

			client.sendToClient("Book Order Done Successfully " + Updated);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if a subscriber exists and if their account is active for borrowing.
	 * Sends a response to the client indicating whether the subscriber exists, 
	 * and if their account is active or frozen.
	 *
	 * @param response The response wrapper containing the subscriber data.
	 * @param client The client to which the results are sent.
	 */
	private void checkSubscriberForBorrow(ResponseWrapper response, ConnectionToClient client) {
		Subscriber subscriber1 = (Subscriber) response.getData();
		try {
			System.out.println("nahla server 1");
			boolean exists = DBConnector.doesSubscriberExist(subscriber1.getSubscriberId());
			System.out.println("nahla server 2");
			System.out.println(exists);
			if (!exists) {
				System.out.println("subscriber does not exist");
				client.sendToClient("subscriber is not exist");
			} else {
				boolean isActive = DBConnector.validateSubscriberStatus(subscriber1.getSubscriberId());
				if (isActive) {
					System.out.println("subscriber  exist and not frozen");
					client.sendToClient("subscriber exist and not frozen");
				} else {
					System.out.println("subscriber is frozen ");
					client.sendToClient("subscriber is frozen");
				}
			}
		} catch (Exception e) {
			System.out.println("Error checking subscriber status: " + e.getMessage());
			try {
				client.sendToClient("Status Check Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

	}

	
	/**
	 * Searches for a book in the library using its barcode. 
	 * Checks if the book exists and if it is available for borrowing. 
	 * Sends the appropriate response to the client based on the book's status.
	 *
	 * @param response The response wrapper containing the book's data, including its barcode.
	 * @param client The client to which the results are sent.
	 */
	private void searchBookByBarcode(ResponseWrapper response, ConnectionToClient client) {
		// Extract the Book object from the response
		Book book = (Book) response.getData();
		String requestType = "Barcode";
		try {
			String barcode = book.getBarcode(); // Get the barcode from the Book object

			// Check if the book exists based on the barcode
			boolean doesExist = DBConnector.doesBookExistByBarcode(barcode);

			if (doesExist) {
				// Book exists, check its availability
				boolean isAvailable = DBConnector.isBookAvailableOrAllCopiesOrderedByBarcode(barcode);
				if (isAvailable) {
					// Book is available or all copies are ordered
					client.sendToClient("book is available & there are copies " + requestType);
				} else {
					// Book is not available
					client.sendToClient("There are no copies of the book " + requestType);
				}
			} else {
				// Book does not exist, notify the client
				client.sendToClient("The book doesn't even exist in our library! " + requestType);
			}
		} catch (Exception e) {
			System.err.println("Error during book validation: " + e.getMessage());
			try {
				client.sendToClient("An error occurred while processing your request.");
			} catch (IOException ioException) {
				System.err.println("Error sending error message to client: " + ioException.getMessage());
			}
		}
	}

	/**
	 * Validates a subscriber's login credentials (ID, username, and password).
	 * If the credentials are valid, a success message is sent to the client; otherwise, a failure message is sent.
	 *
	 * @param response The response wrapper containing the subscriber's data (ID, username, and password).
	 * @param client The client to which the login status is sent.
	 */
	private void subscriberLogin(ResponseWrapper response, ConnectionToClient client) {
		Subscriber newSubscriber = (Subscriber) response.getData();
		try {
			boolean isValid = DBConnector.validateSubscriber(newSubscriber.getSubscriberId(),
					newSubscriber.getUsername(), newSubscriber.getPassword());
			if (isValid) {
				System.out.println("Subscriber Login Successful");
				client.sendToClient("Subscriber Login Successful");
			} else {
				System.out.println("Login Failed, Invalid username or password");
				client.sendToClient("Subscriber Login Failed");
			}
		} catch (Exception e) {
			System.out.println("Error validating subscriber: " + e.getMessage());
			try {
				client.sendToClient("Subscriber Login Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * Checks whether a subscriber exists in the database based on their subscriber ID.
	 * Sends a response to the client indicating whether the subscriber exists or not.
	 *
	 * @param response The response wrapper containing the subscriber's data (subscriber ID).
	 * @param client The client to which the existence status of the subscriber is sent.
	 */
	private void checkSubscriber(ResponseWrapper response, ConnectionToClient client) {
		try {
			Subscriber subscriber = (Subscriber) response.getData();
			int subscriberId = subscriber.getSubscriberId();

			// בדוק אם המנוי קיים בבסיס הנתונים
			boolean exists = DBConnector.doesSubscriberExist(subscriberId);

			if (exists) {
				client.sendToClient("SubscriberIdExists");

			} else {
				// אם המנוי לא קיים, שלח הודעה מתאימה
				client.sendToClient("SubscriberIdNotExists");
			}
		} catch (Exception e) {
			System.out.println("Error connect to DB : " + e.getMessage());
		}
	}

	
	/**
	 * Fetches loan extension history for a subscriber from the database.
	 * Sends the retrieved loan extensions to the client.
	 *
	 * @param response The response wrapper containing the subscriber's ID.
	 * @param client The client to which the loan extension history is sent.
	 */
	private void fetchLoanExtensions(ResponseWrapper response, ConnectionToClient client) {
		int subscriberId = (int) response.getData(); // קבל את מזהה המנוי
		try {
			ArrayList<LoanExtension> loanExtensions = DBConnector.getLoanExtensionsBySubscriberId(subscriberId);
			// client.sendToClient(loanExtensions);
			client.sendToClient(new ResponseWrapper("LoanExtensions", loanExtensions));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Fetches the ordered books for a subscriber from the database.
	 * Sends the retrieved ordered books to the client.
	 *
	 * @param response The response wrapper containing the subscriber's ID.
	 * @param client The client to which the ordered books are sent.
	 */
	private void fetchOrderedBooks(ResponseWrapper response, ConnectionToClient client) {
		int subscriberId = (int) response.getData();
		System.out.println("Fetching ordered books for subscriber ID: " + subscriberId);

		try {
			ArrayList<OrderedBook> OrderedBooks = DBConnector.getOrderedBookBySubscriberIdToReaderCARD(subscriberId);
			System.out.println("Number of ordered books found: " + OrderedBooks.size());
			client.sendToClient(OrderedBooks);

		} catch (Exception e) {
			System.err.println("Error fetching ordered books: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * Fetches the borrowed books for a subscriber from the database.
	 * Sends the retrieved borrowed books to the client.
	 *
	 * @param response The response wrapper containing the subscriber's ID.
	 * @param client The client to which the borrowed books are sent.
	 */
	private void fetchBorrowedBooks(ResponseWrapper response, ConnectionToClient client) {
		int subscriberId = (int) response.getData(); // קבל את מזהה המנוי

		try {
			// קבל את הספרים המושאלים מה-DB
			ArrayList<BorrowedBook> borrowedBooks = DBConnector
					.getBorrowedBooksBySubscriberIdToReaderCARD(subscriberId);
			client.sendToClient(borrowedBooks);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Handles the subscriber sign-up process by adding the subscriber to the database.
	 * If the username already exists, an appropriate message is sent to the client.
	 *
	 * @param response The response wrapper containing the Subscriber object to be registered.
	 * @param client The client to send the result of the registration attempt.
	 */
	private void subscriberSignUp(ResponseWrapper response, ConnectionToClient client) {
		Subscriber subscriber = (Subscriber) response.getData();
		try {
			DBConnector.addSubscriberIfNotExists(subscriber);
			System.out.println("Registration Subscriber Successful");
			client.sendToClient("Registration Subscriber Successful");
		} catch (SQLException e) {
			System.out.println("Error adding Subscriber: " + e.getMessage());
			try {
				if (e.getMessage().contains("Username subscriber already exists")) {
					client.sendToClient("Username subscriber already exists");
				} else {
					System.out.println("Registration Subscriber Failed");
					client.sendToClient("Registration Subscriber Failed");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Error sending response to client: " + e.getMessage());
		}
	}

	/**
	 * Handles the process of setting a new password for a subscriber.
	 * It attempts to update the subscriber's password in the database and sends an appropriate message to the client.
	 *
	 * @param response The response wrapper containing the Subscriber object with the new password.
	 * @param client The client to send the result of the password update attempt.
	 */
	private void subscriberSetNewPassword(ResponseWrapper response, ConnectionToClient client) {
		Subscriber subscriber = (Subscriber) response.getData();
		try {
			boolean updateSuccess = DBConnector.updateSubscriberPassword(subscriber.getSubscriberId(),
					subscriber.getUsername(), subscriber.getPassword());

			if (updateSuccess) {
				client.sendToClient("Subscriber Password updated successfully");
			} else {
				client.sendToClient("Failed to update Subscriber password");
			}
		} catch (Exception e) {
			System.out.println("Error resetting password: " + e.getMessage());
			try {
				client.sendToClient("Reset Subscriber Password Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	
	/**
	 * Verifies the subscriber's identity for resetting the password by checking the subscriber's ID and username.
	 * If the ID and username match, the method confirms the validity of the request. 
	 * Otherwise, an error message is sent to the client.
	 *
	 * @param response The response wrapper containing the Subscriber object with the ID and username for verification.
	 * @param client The client to send the result of the verification attempt.
	 */
	private void subscriberVerifyResetPassword(ResponseWrapper response, ConnectionToClient client) {
		Subscriber subscriber = (Subscriber) response.getData();
		try {
			boolean isValid = DBConnector.validateSubscriberReset(subscriber.getSubscriberId(),
					subscriber.getUsername());

			if (isValid) {
				client.sendToClient("finded id and username for Subscriber");
			} else {
				client.sendToClient("Invalid ID or Username for Subscriber");
			}
		} catch (Exception e) {
			System.out.println("Error resetting password: " + e.getMessage());
			try {
				client.sendToClient("Reset Subscriber Password Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Verifies the librarian's credentials (username and password) for login. If the credentials are valid,
	 * the librarian's name is returned, and a success message is sent to the client. Otherwise, a failure message is sent.
	 *
	 * @param response The response wrapper containing the Librarian object with the username and password for login.
	 * @param client The client to send the result of the login attempt.
	 */
	private void librarianLogin(ResponseWrapper response, ConnectionToClient client) {
		Librarian librarian = (Librarian) response.getData();
		try {
			String librarianName = DBConnector.validateLibrarian(librarian.getUsername(), librarian.getPassword());

			if (librarianName != null) {
				client.sendToClient("Login Successful: " + librarianName);
			} else {
				client.sendToClient("Login Failed");
			}
		} catch (Exception e) {
			System.out.println("Error validating librarian: " + e.getMessage());
			try {
				client.sendToClient("Login Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Registers a new librarian by adding their information to the database. If the registration is successful, 
	 * a success message is sent to the client. If the username already exists or another error occurs, 
	 * an appropriate error message is sent to the client.
	 *
	 * @param response The response wrapper containing the Librarian object with the registration data.
	 * @param client The client to send the result of the registration attempt.
	 */
	private void librarianSignUp(ResponseWrapper response, ConnectionToClient client) {
		Librarian librarian = (Librarian) response.getData();
		try {
			DBConnector.addLibrarian(librarian);
			client.sendToClient("Registration Librarian Successful");
		} catch (SQLException e) {
			System.out.println("Error adding librarian: " + e.getMessage());
			try {
				if (e.getMessage().contains("Username already exists")) {
					client.sendToClient("Username already exists");
				} else {
					client.sendToClient("Registration Librarian Failed");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Error sending response to client: " + e.getMessage());
		}
	}

	/**
	 * Resets the password for a librarian. If the password update is successful, 
	 * a success message is sent to the client. In case of any error during the password reset process,
	 * an error message is sent to the client.
	 *
	 * @param response The response wrapper containing the Librarian object with the updated password.
	 * @param client The client to send the result of the password reset attempt.
	 */
	private void librarianResetPassword(ResponseWrapper response, ConnectionToClient client) {
		Librarian librarian = (Librarian) response.getData();
		try {
			DBConnector.updateLibrarianPassword(librarian.getUsername(), librarian.getPassword());
			client.sendToClient("Password updated successfully");
		} catch (Exception e) {
			System.out.println("Error resetting password: " + e.getMessage());
			try {
				client.sendToClient("Reset Password Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	
	/**
	 * Verifies if the librarian's ID and username are valid for the password reset process.
	 * If the credentials are valid, a confirmation message is sent to the client.
	 * If the credentials are invalid, an error message is sent to the client.
	 * In case of an exception, an error message is sent indicating a failure in the verification process.
	 *
	 * @param response The response wrapper containing the Librarian object with the ID and username to verify.
	 * @param client The client to send the result of the verification.
	 */
	private void librarianVerifyResetPassword(ResponseWrapper response, ConnectionToClient client) {
		Librarian librarian = (Librarian) response.getData();
		try {
			boolean isValid = DBConnector.validateLibrarianReset(librarian.getLibrarianId(), librarian.getUsername());

			if (isValid) {
				client.sendToClient("finded id and username for librarian");
			} else {
				client.sendToClient("Invalid ID or Username");
			}
		} catch (Exception e) {
			System.out.println("Error resetting password: " + e.getMessage());
			try {
				client.sendToClient("Reset Password Failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	
	/**
	 * Handles requests related to a subscriber, including fetching subscriber details
	 * and checking the status of a subscriber.
	 * 
	 * @param msg The message containing the subscriber information for processing.
	 * @param client The client to send the result of the request.
	 */
	private void subscriberMethod(Object msg, ConnectionToClient client) {
		Subscriber subscriber1 = (Subscriber) msg;

		// Check if this is a fetching details request
		if (subscriber1.getSubscriberEmail().isEmpty() && subscriber1.getSubscriberId() > 0
				&& subscriber1.getDetailedSubscriptionHistory() == 2 && subscriber1.getSubscriberName().isEmpty()
				&& subscriber1.getSubscriberPhoneNumber().isEmpty() && subscriber1.getUsername().isEmpty()
				&& subscriber1.getPassword().isEmpty()) {

			try {
				// כאן נוכל להוסיף לוגיקה לשליפת פרטי המנוי
				Subscriber subscriber = DBConnector.getSubscriberDetails(subscriber1.getSubscriberId());

				if (subscriber != null) {
					// אם המנוי נמצא, שלח אותו חזרה ללקוח
					client.sendToClient(new ResponseWrapper("success", subscriber));
				} else {
					// אם המנוי לא נמצא, שלח הודעת שגיאה
					client.sendToClient(new ResponseWrapper("failure", "Subscriber not found"));
				}
			} catch (SQLException e) {
				// טיפול בשגיאה של SQL
				System.out.println("SQL error while fetching subscriber details: " + e.getMessage());
				try {
					client.sendToClient(new ResponseWrapper("failure", "Database error: " + e.getMessage()));
				} catch (IOException ioException) {
					System.out.println("Error sending response to client: " + ioException.getMessage());
				}
			} catch (IOException e) {
				// טיפול בשגיאה של IO
				System.out.println("IO error while sending response to client: " + e.getMessage());
			} catch (Exception e) {
				// טיפול בשגיאות אחרות
				System.out.println("Error fetching subscriber details: " + e.getMessage());
				try {
					client.sendToClient(new ResponseWrapper("failure", "Error: " + e.getMessage()));
				} catch (IOException ioException) {
					System.out.println("Error sending response to client: " + ioException.getMessage());
				}
			}
		}

		// Check if this is a status check request
		if (subscriber1.getSubscriberEmail().isEmpty() && subscriber1.getSubscriberId() != 0
				&& subscriber1.getDetailedSubscriptionHistory() == 0 && subscriber1.getSubscriberName().isEmpty()
				&& subscriber1.getSubscriberPhoneNumber().isEmpty() && subscriber1.getUsername().isEmpty()
				&& subscriber1.getPassword().isEmpty()) {
			try {
				boolean exists = DBConnector.doesSubscriberExist(subscriber1.getSubscriberId());
				System.out.printf("exists:" + exists);
				if (!exists) {
					System.out.println("subscriber does not exist");
					client.sendToClient("subscriber is not exist");
				} else {
					boolean isActive = DBConnector.validateSubscriberStatus(subscriber1.getSubscriberId());
					if (isActive) {
						client.sendToClient("subscriber exist and not frozen");
					} else {
						System.out.println("subscriber is frozen ");
						client.sendToClient("subscriber is frozen");
					}
				}
			} catch (Exception e) {
				System.out.println("Error checking subscriber status: " + e.getMessage());
				try {
					client.sendToClient("Status Check Failed");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	
	/**
	 * Handles the process of returning a borrowed book, including checking for overdue returns,
	 * updating book records, and updating the number of available copies.
	 * 
	 * @param msg The message containing the details of the return process.
	 * @param client The client to send the result of the return process.
	 */
	private void returnProcess(Object msg, ConnectionToClient client) {
		ReturnBook returnBook = (ReturnBook) msg;

		try {
			// handle overdue(if return is late freeze)
			DBConnector.handleOverdue(returnBook);
			// Delete the borrowed book record into database and update book count and
			// update return date
			boolean isSuccess = DBConnector
					.deleteBorrowedBookAndUpdatenumberOfAvailableCopiesAndActiualReturnDate(returnBook);

			DBConnector.updateFirstReturnDate(returnBook);
			if (isSuccess) {
				client.sendToClient("Return book process completed successfully");
			} else {
				client.sendToClient("Error processing return request");
			}
		} catch (Exception e) {
			System.out.println("Error during return process: " + e.getMessage());
		}
	}

	/**
	 * Handles the process of borrowing a book, including saving the borrowed book record
	 * and updating the book's available count.
	 * 
	 * @param msg The message containing the details of the borrow process, including the book and subscriber information.
	 * @param client The client to send the result of the borrow process.
	 */
	private void borrowProcess(Object msg, ConnectionToClient client) {
		BorrowedBook borrowedBook = (BorrowedBook) msg;

		try {
			// Insert the borrowed book record into database and update book count
			boolean isSuccess = DBConnector.saveBorrowedBookAndUpdateCount(borrowedBook);

			if (isSuccess) {
				client.sendToClient("Borrow process completed successfully");
			} else {
				client.sendToClient(
						"This book is already borrowed by this subscriber->Error processing borrow request");
			}
		} catch (Exception e) {
			System.out.println("Error during borrow process: " + e.getMessage());
		}
	}

	/**
	 * Retrieves the change history of a subscriber based on their subscriber ID.
	 * 
	 * @param msg The message containing the subscriber ID for which the changes history is requested.
	 * @param client The client to send the changes history data or an error response.
	 */
	private void subscriberChangesHistory(Object msg, ConnectionToClient client) {
		SubscriberChangesHistory request = (SubscriberChangesHistory) msg;
		int subscriberId = request.getSubscriberId();
		try {
			ArrayList<SubscriberChangesHistory> changesHistories = DBConnector
					.getChangesHistoriesBySubscriberId(subscriberId);
			client.sendToClient(changesHistories);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the order history of books for a subscriber based on their subscriber ID.
	 * 
	 * @param msg The message containing the subscriber ID for which the order history is requested.
	 * @param client The client to send the ordered books history data or an error response.
	 */
	private void OrderBookHistory(Object msg, ConnectionToClient client) {
		OrderedBooksHistory request = (OrderedBooksHistory) msg;
		int subscriberId = request.getSubscriberId(); // קבל את מזהה המנוי
		try {
			ArrayList<OrderedBooksHistory> orderedBooks = DBConnector.getOrderedBooksBySubscriberId(subscriberId); // שיטה
			client.sendToClient(orderedBooks);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the borrowing history of books for a subscriber based on their subscriber ID.
	 * The method sends the borrowed books history to the client and logs the details of the first borrowed book.
	 * 
	 * @param msg The message containing the subscriber ID for which the borrowed books history is requested.
	 * @param client The client to send the borrowed books history data or an error response.
	 */
	private void borrowedBookHistory(Object msg, ConnectionToClient client) {
		System.out.println("server1");
		BorrowedBooksHistory request = (BorrowedBooksHistory) msg;
		int subscriberId = request.getSubscriberId(); // קבל את מזהה המנוי
		try {

			ArrayList<BorrowedBooksHistory> borrowedBooks = DBConnector.getBorrowedBooksBySubscriberId(subscriberId); // שיטה
																														// //
																														// מנוי
			System.out.println("server2");
			client.sendToClient(borrowedBooks);
			System.out.println("server3");
			System.out.println(borrowedBooks.size());
			// בדיקה אם הרשימה אינה ריקה לפני גישה לאלמנט הראשון
			if (!borrowedBooks.isEmpty()) {
				BorrowedBooksHistory book = borrowedBooks.get(0);
				System.out.println("First Borrowed Book Details:");
				System.out.println("History ID: " + book.getHistoryId());
				System.out.println("Subscriber ID: " + book.getSubscriberId());
				System.out.println("Book ID: " + book.getBookId());
				System.out.println("Book Name: " + book.getBookName());
				System.out.println("Author: " + book.getAuthor());
				System.out.println("Borrow Date: " + book.getBorrowDate());
				System.out.println("Return Date: " + book.getReturnDate());
				System.out.println("Actual Return Date: " + book.getActualReturnDate());
				System.out.println("Is Late: " + book.isLate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Processes a request to extend the return date of a borrowed book for a subscriber.
	 * Validates conditions such as the subscriber's status, whether the book is borrowed, 
	 * if the extension is within the allowed window, and if there are pending orders for the book.
	 * Responds with success or failure based on these conditions.
	 *
	 * @param msg The request object containing the book ID, subscriber ID, new return date, and librarian name.
	 * @param client The client to send the response to.
	 */
	private void extendReturnRequest(Object msg, ConnectionToClient client) {
		ExtendReturnDateRequest request = (ExtendReturnDateRequest) msg;
		int bookId = request.getBookId();
		int subscriberId = request.getSubscriberId();
		Date newReturnDate = request.getNewReturnDate();
		System.out.println("ExtendReturnDateRequest" + "   " + request.getLibrarianName());
		try {
			if (!DBConnector.validateSubscriberStatus(request.getSubscriberId())) {
				DBConnector.insertRejectedExtensionLibrarian(request, "Subscriber is frozen");
				client.sendToClient("Subscriber is frozen Librarian extension");
				return;
			}

			if (!DBConnector.isBookBorrowed(bookId, request.getSubscriberId())) {
				DBConnector.insertRejectedExtensionLibrarian(request, "Book is not currently borrowed");
				client.sendToClient("Book is not currently borrowed Librarian extension");
				return;
			}

			if (!DBConnector.isWithinExtensionWindow(bookId, request.getSubscriberId()))
			{  
				DBConnector.insertRejectedExtensionLibrarian(request,"Extensions are allowed only if it’s within 7 days before the return date");
				client.sendToClient("Extensions are allowed only if it’s within 7 days before the return date");
				return;
			}

			if (DBConnector.hasBookPendingOrders(bookId)) {
				DBConnector.insertRejectedExtensionLibrarian(request, "Extension denied - book has pending orders");
				client.sendToClient("Extension denied - book has pending orders Librarian extension");
				return;
			}
			boolean success = DBConnector.extendReturnDate(bookId, subscriberId, newReturnDate,
					request.getLibrarianName());
			if (success) {
				// Update notificationSent to 0
				DBConnector.updateNotificationSent(subscriberId, bookId, 0);
				client.sendToClient("Return date extended successfully.");
				return;
			} else {
				client.sendToClient("Failed to extend return date.");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.sendToClient("An error occurred while extending the return date.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
		}
		return;
	}

	/**
	 * Retrieves and sends the history of returned books for a subscriber.
	 * It fetches the returned books from the database based on the subscriber's ID.
	 * 
	 * @param msg The request object containing the subscriber's ID.
	 * @param client The client to send the returned books history to.
	 */
	private void returnBookHistory(Object msg, ConnectionToClient client) {
		ReturnBookHistory request = (ReturnBookHistory) msg;
		int subscriberId = request.getSubscriberId(); // Get the subscriber ID from the request

		try {
			ArrayList<ReturnBookHistory> returnedBooks = DBConnector.getReturnedBooksBySubscriberId(subscriberId);
			client.sendToClient(returnedBooks); // Send back the returned books to the client
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exceptions appropriately
		}
	}

	/**
	 * Processes an extension request for a book loan by a subscriber.
	 * It performs several validation checks including book existence, subscriber status,
	 * whether the book is currently borrowed, if the extension window is valid,
	 * and if there are pending orders for the book. If all checks pass, the loan is extended.
	 * 
	 * @param msg The request object containing the extension details.
	 * @param client The client to send the response to.
	 */
	private void extendLoanRequest(Object msg, ConnectionToClient client) 
	{
		ExtendLoanRequest extendRequest = (ExtendLoanRequest) msg;
		try {

			int bookId = DBConnector.getBookId(new Book(0, extendRequest.getBookName(), null, null, null,
					extendRequest.getAuthor(), 0, 0, null, null, 0));

			if (bookId == -1) 
			{
				extendRequest.setBookId(bookId);
				DBConnector.insertRejectedExtension(extendRequest, "Book does not exist in library");
				client.sendToClient("Book does not exist in library");
				return;
			}

			if (!DBConnector.validateSubscriberStatus(extendRequest.getSubscriberId())) {
				extendRequest.setBookId(bookId);
				DBConnector.insertRejectedExtension(extendRequest, "Subscriber is frozen");
				client.sendToClient("Subscriber is frozen");
				return;
			}

			if (!DBConnector.isBookBorrowed(bookId, extendRequest.getSubscriberId())) {
				extendRequest.setBookId(bookId);
				DBConnector.insertRejectedExtension(extendRequest, "Book is not currently borrowed");
				client.sendToClient("Book is not currently borrowed");
				return;
			}
///////////////here
			if (!DBConnector.isWithinExtensionWindow(bookId, extendRequest.getSubscriberId())) {
				extendRequest.setBookId(bookId);
				DBConnector.insertRejectedExtension(extendRequest,"Extensions are allowed only if it’s within 7 days before the return date");
				client.sendToClient("Extensions are allowed only if it’s within 7 days before the return date Subscriber");
				return;
			}

			if (DBConnector.hasBookPendingOrders(bookId)) {
				extendRequest.setBookId(bookId);
				DBConnector.insertRejectedExtension(extendRequest, "Extension denied - book has pending orders");
				client.sendToClient("Extension denied - book has pending orders");
				return;
			}

			extendRequest.setBookId(bookId);
			DBConnector.extendLoan(extendRequest);
			DBConnector.insertLibrarianMessage(extendRequest);
			client.sendToClient("Extension approved");

		} catch (Exception e) {
			try {
				client.sendToClient("Extension request failed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Validates the availability of a book for borrowing or ordering based on the request type.
	 * For borrowing, it checks if the book exists and is available. For ordering, it checks if the 
	 * book exists, if the subscriber's status is active, and if all copies are already ordered.
	 * Sends appropriate responses to the client based on the book availability and conditions.
	 *
	 * @param response The response object containing the request type and book details.
	 * @param client The client to send the validation result to.
	 */
	public void checkBookForBorrowOrOrder(ResponseWrapper response, ConnectionToClient client) {
		Book book = null;
		String subscriberID = null;
		if (response.getType().equals("checkBookForBorrow")) {
			book = (Book) response.getData();
		} else if (response.getType().equals("checkBookForOrder")) {
			Object[] data = (Object[]) response.getData();
			book = (Book) data[0];
			subscriberID = (String) data[1];
		}
		String requestType = response.getType().equals("checkBookForBorrow") ? "Borrow" : "Order";
		System.out.printf("The request is to :%s\n", requestType);
		try {
			boolean doesExist = requestType.equals("Borrow") ? DBConnector.doesBookExist(book)
					: DBConnector.doesBookWithNameAndAuthorExist(book);
			boolean isAvailable = requestType.equals("Borrow") ? DBConnector.isBookAvailable(book)
					: DBConnector.isBookAvailableForOrder(book);
			boolean isAllCopiesareOrdered = DBConnector.isAllCopiesareOrdered(book);
			int bookID = DBConnector.getBookId(book);
			// boolean isActive =
			// DBConnector.validateSubscriberStatus(Integer.parseInt(subscriberID));
			if (response.getType().equals("checkBookForOrder")) {
				boolean isActive = DBConnector.validateSubscriberStatus(Integer.parseInt(subscriberID));
				if (!isActive) {
					System.out.println("Order: Subscriber status is frozen");
					client.sendToClient("Order: Subscriber status is frozen");
					return;
				}
			}
			if (doesExist) {
				if (response.getType().equals("checkBookForOrder")) {
					boolean subscriberHasBorrowForBook = DBConnector
							.isSubscriberHasBorrowOfTheBook(Integer.parseInt(subscriberID), bookID);
					if (subscriberHasBorrowForBook) {
						System.out.println("You have a borrow for this book");
						client.sendToClient("You have a borrow for this book");
						return;
					}
				}
				if (isAvailable) {
					System.out.println("book is available & there are copies ");
					client.sendToClient("book is available & there are copies " + requestType);
				} else if (requestType.equals("Order") && isAllCopiesareOrdered) {
					System.out.println("All copies of the book are ordered ");
					client.sendToClient("All copies of the book are ordered");
				} else {
					if (requestType.equals("Borrow")) {
						client.sendToClient("There are no copies of the book " + requestType);
					} else {
						System.out.println("There are no copies of the book Order");
						System.out.printf("bookID:%d\n", bookID);
						client.sendToClient("There are no copies of the book Order " + bookID);
					}
				}
			} else {
				client.sendToClient("The book doesn't even exist in our library! " + requestType);
			}
		} catch (Exception e) {
			System.out.println("Error during book validation: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * Handles the update of a subscriber's information (phone and email).
	 * The method extracts the subscriber's ID, phone, and email from the provided message,
	 * creates a new `Subscriber` object with the updated details, and updates the database.
	 * It sends a success message to all clients if the update is successful, 
	 * or an error message if the update fails.
	 *
	 * @param msg The message containing the subscriber's details to be updated.
	 */
	private void handleUpdateSubscriber(String msg) {
		try {
			String st = msg.substring(14); // remove "UpdateSubscriber "
			String[] result = st.split(", ");

			// פרסור לפי הפורמט החדש
			int id = Integer.parseInt(result[0].split("=")[1]); // ID
			String phone = result[1].split("=")[1]; // Phone
			String email = result[2].split("=")[1]; // Email

			// יצירת אובייקט Subscriber עם הנתונים הרלוונטיים לעדכון
			Subscriber subscriberToUpdate = new Subscriber(id, // ID
					"", // Name (לא נדרש לעדכון)
					"", // Username (לא נדרש לעדכון)
					email, // Email
					phone, // Phone
					"", // Password (לא נדרש לעדכון)
					0 // detailed_subscription_history (לא נדרש לעדכון)
			);

			DBConnector.updateSubscriber(subscriberToUpdate);
			System.out.println("Subscriber updated successfully");
			this.sendToAllClients("Update Successful");

		} catch (Exception e) {
			System.out.println("Update failed: " + e.getMessage());
			this.sendToAllClients("Update Failed");
			e.printStackTrace();
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */

	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
		try {
			DBConnector.connect();
			DBConnector.startTimerTask();
		} catch (Exception e) {
			System.out.println("Error connecting to database");
			e.printStackTrace();
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
		DBConnector.stopTimerTask();
	}
}
//End of EchoServer class
