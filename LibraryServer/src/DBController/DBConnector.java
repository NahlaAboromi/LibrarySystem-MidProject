package DBController;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
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
import logic.ReturnBook;
import logic.ReturnBookHistory;
import logic.Subscriber;
import logic.SubscriberChangesHistory;
//import logic.SubscriberPasswordHistory;
import server.LibraryNotificationService;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * The DBConnector class is responsible for managing a connection to a MySQL
 * database. It ensures that only one instance of the connection is created
 * (singleton pattern). The class also uses a Timer for periodic operations (if
 * required).
 * 
 * Instance variables: connection - A static Connection object to manage the
 * database connection. timer - A static Timer object for scheduling tasks.
 * dbConnector - A static instance of DBConnector to implement the singleton
 * pattern.
 */
public class DBConnector {

	private static Connection connection = null;
	private static Timer timer;
	private static DBConnector dbConnector;

	/**
	 * Establishes a connection to the MySQL database if it has not been established
	 * yet.
	 * 
	 * @throws Exception if there is an error loading the database driver or
	 *                   connecting to the database.
	 */
	public static void connect() throws Exception {
		if (connection == null) {
			try {
				// טוען את הדרייבר
				Class.forName("com.mysql.cj.jdbc.Driver");

				// יוצר חיבור למסד הנתונים
				connection = DriverManager.getConnection("jdbc:mysql://localhost/library?serverTimezone=IST", "root",
					//	"Roba1234");
			 "Aa123456");
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	/**
	 * Starts a periodic timer task that performs various database operations every
	 * 30 seconds.
	 * 
	 * Tasks include: - Sending notifications for arrived books. - Deleting expired
	 * orders. - Unfreezing user accounts. - Sending return reminders. - Generating
	 * borrowing reports and status data at the end of each month.
	 * 
	 * This method ensures the timer starts only once.
	 */
	public static void startTimerTask() {
		if (timer == null) {
			System.out.println("Timer Task started....");
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						updateIsLate(connection);
						sendNotificationArrivedBook(connection);
						deleteOrder(connection);
						unFreezeAccount(connection); // Call the unFreezeAccount method
						sendReturnReminders(connection); // Check for return reminders


	                    if (isEndOfMonth()) {
	                        //  - זמני השאלה
	                        generateBorrowingReport(connection, LocalDateTime.now().getYear(),
	                                LocalDateTime.now().getMonthValue());
	                        
	                        // דוח סטטוס מנויים
	                        generateStatusData(connection, LocalDateTime.now().getYear(),
	                                LocalDateTime.now().getMonthValue());
	                        
	                        // דוח ספרים עם הכי הרבה איחורים
	                        generateMostLateReturnsReport(connection, LocalDateTime.now().getYear(),
	                                LocalDateTime.now().getMonthValue());
	                        
	                        // דוח ספרים המושאלים ביותר
	                        generateMostBorrowedReport(connection, LocalDateTime.now().getYear(),
	                                LocalDateTime.now().getMonthValue());
	                    }
						///////////////// Fatma 17/1
						// group member do this to save report data in your table
						///////////////// :)/////////////////////////////////////////

					//	 for (int month = 1; month <= 12; month++)
						// { //Call generateStatusData for each year and month
						// generateStatusData(connection,2024,1); 
						 //}
						 //for (int month = 1; month <= 12; month++)
						// { //Call generateMostLateReturnsReport for each year and month
							// generateMostLateReturnsReport(connection,2024,1);
							// generateBorrowingReport(connection,2024,1);
							 //}
					//	 for (int month = 1; month <= 12; month++)
					//	 { //Call generateMostBorrowedReport for each year and month
						// generateMostBorrowedReport(connection,2024,1);
							 //}
						 
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}, 0, 30000); // משימה שרצה כל חצי דקה
		}
	}

	/**
	 * Stops the currently running timer task, if active.
	 * 
	 * This method cancels all scheduled tasks of the timer and clears the timer
	 * object from memory by setting it to null.
	 */
	public static void stopTimerTask() {
		if (timer != null) {
			timer.cancel(); // מבטל את פעולות הטיימר
			timer = null; // מנקה את האובייקט מהזיכרון
		}
	}
	
	
	/**
	 * Updates the `is_late` status for all overdue books in the `borrowedbookshistory` table.
	 * 
	 * This method checks for books where the current date is past the `return_date` and the `actual_return_date`
	 * is still `NULL`, indicating that the book has not been returned yet. It sets the `is_late` field to 1 for these books.
	 *
	 * @param connection The connection to the database.
	 * @throws SQLException If a database access error occurs.
	 */
	public static void updateIsLate(Connection connection) throws SQLException {/////////////FATMA 24/1
	    // SQL query to update the is_late status for overdue books
	    String query = "UPDATE borrowedbookshistory SET is_late = 1 WHERE NOW() > DATE_ADD(return_date, INTERVAL 1 DAY) AND actual_return_date IS NULL";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.executeUpdate(); // Execute the update query
	    } catch (SQLException e) {
	        // Handle the SQLException
	        System.err.println("Error updating is_late status: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	/**
	 * Unfreezes subscriber accounts whose freeze period has ended.
	 * 
	 * - Updates the status of eligible subscribers to 'active'. - Deletes
	 * corresponding entries from the `frozenaccounts` table.
	 * 
	 * @param connection the database connection.
	 * @throws SQLException if a database access error occurs.
	 */
	public static void unFreezeAccount(Connection connection) throws SQLException {
		// Query to fetch all subscriber IDs whose frozen_end_date has passed
		String getIdQuery = "SELECT subscriber_id FROM frozenaccounts WHERE frozen_end_date <= NOW()";

		// Query to update subscriber status
		String unfreezeSubscriberQuery = "UPDATE subscriber SET status = 'active' WHERE subscriber_id = ?";

		// Query to delete rows from frozenaccounts
		String deleteQuery = "DELETE FROM frozenaccounts WHERE frozen_end_date <= NOW()";

		try (PreparedStatement getIdStmt = connection.prepareStatement(getIdQuery);
				PreparedStatement unfreezeStmt = connection.prepareStatement(unfreezeSubscriberQuery);
				PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

			// Fetch subscriber IDs
			ResultSet resultSet = getIdStmt.executeQuery();

			while (resultSet.next()) {
				int subscriberId = resultSet.getInt("subscriber_id");

				// Update subscriber status
				unfreezeStmt.setInt(1, subscriberId);
				unfreezeStmt.executeUpdate();
			}

			// Delete rows from frozenaccounts
			deleteStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the database connection if it is open.
	 * 
	 * - Ensures the connection is safely closed. - Logs any errors that occur
	 * during the closing process.
	 * 
	 * @throws SQLException if an error occurs while closing the connection.
	 */
	public static void closeConnection() throws SQLException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// Log and handle the error during closing the connection
				System.out.println("Error while closing the database connection: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retrieves a list of librarian messages ordered by the most recent message date.
	 *
	 * This method fetches all messages from the `LibrarianMessages` table, sorting them by the `message_date` in descending order.
	 * Each message contains the message text and the date it was sent.
	 *
	 * @return an `ArrayList` of `LibrarianMessage` objects representing the librarian messages retrieved from the database.
	 * @throws SQLException if a database access error occurs, or if the query fails to execute.
	 */
	public static ArrayList<LibrarianMessage> getLibrarianMessages() throws SQLException 
	{

		ArrayList<LibrarianMessage> messages = new ArrayList<>();
		String query = "SELECT * FROM LibrarianMessages ORDER BY message_date DESC";

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next())
		{
	    
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(rs.getDate("message_date"));
	        cal.add(Calendar.DATE, 1);
	        Date correctedDate = new Date(cal.getTimeInMillis());
	        messages.add(new LibrarianMessage(rs.getString("message_text"), correctedDate));
	    }

		rs.close();
		stmt.close();
		return messages;
	}

	
	/**
	 * Retrieves a list of borrowed books for a specific subscriber by their ID.
	 * 
	 * - Fetches book borrowing history, including book details, borrowing, and return dates.
	 * - Adjusts SQL dates to account for timezone or storage discrepancies.
	 * - Converts `is_late` from integer to boolean.
	 * 
	 * @param subscriberId the ID of the subscriber whose borrowing history is retrieved.
	 * @return an `ArrayList` of `BorrowedBooksHistory` objects containing borrowing details.
	 * @throws SQLException if a database access error occurs.
	 */
	public static ArrayList<BorrowedBooksHistory> getBorrowedBooksBySubscriberId(int subscriberId) throws SQLException {
		ArrayList<BorrowedBooksHistory> borrowedBooks = new ArrayList<>();
		String query = "SELECT b.history_id, b.subscriber_id, b.book_id, bk.book_name AS book_name, bk.book_author AS author, "
				+ "b.borrow_date, b.return_date, b.actual_return_date, b.is_late " + "FROM borrowedbookshistory b "
				+ "JOIN books bk ON b.book_id = bk.book_id " + "WHERE b.subscriber_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
////////////////////////////////////21.1.25start
					Calendar cal = Calendar.getInstance();
					java.sql.Date borrowDate = rs.getDate("borrow_date");
					if (borrowDate != null) {
						cal.setTime(borrowDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						borrowDate = new java.sql.Date(cal.getTimeInMillis());
					}
					java.sql.Date returnDate = rs.getDate("return_date");
					if (returnDate != null) {
						cal.setTime(returnDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						returnDate = new java.sql.Date(cal.getTimeInMillis());
					}

					java.sql.Date actualReturnDate = rs.getDate("actual_return_date");
					if (actualReturnDate != null) {
						cal.setTime(actualReturnDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						actualReturnDate = new java.sql.Date(cal.getTimeInMillis());
					}
///////////////////////////////////////////21.1.25
					BorrowedBooksHistory book = new BorrowedBooksHistory(rs.getInt("history_id"),
							rs.getInt("subscriber_id"), rs.getInt("book_id"), rs.getString("book_name"), // Retrieve //
																											// book name
							rs.getString("author"), // Retrieve author name
							borrowDate, returnDate, actualReturnDate, rs.getInt("is_late") == 1 // Convert TINYINT to
																								// boolean
					);
					borrowedBooks.add(book);
				}
			}
		}

		return borrowedBooks;
	}

	
	
	/**
	 * Retrieves the changes history for a specific subscriber by their ID.
	 * 
	 * - Fetches change records, including the type of change, old and new values, and the timestamp of each change.
	 * - Each change is stored as a `SubscriberChangesHistory` object.
	 * 
	 * @param subscriberId the ID of the subscriber whose changes history is retrieved.
	 * @return an `ArrayList` of `SubscriberChangesHistory` objects containing change records.
	 */
	public static ArrayList<SubscriberChangesHistory> getChangesHistoriesBySubscriberId(int subscriberId) {
		ArrayList<SubscriberChangesHistory> changesHistories = new ArrayList<>();
		String query = "SELECT * FROM subscriber_changes_history WHERE subscriber_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int changeHistoryId = rs.getInt("change_history_id");
				String changeType = rs.getString("change_type");
				String oldValue = rs.getString("old_value");
				String newValue = rs.getString("new_value");
				Timestamp timestamp = rs.getTimestamp("change_date");
				Date changeDate = timestamp != null ? new Date(timestamp.getTime()) : null;

				SubscriberChangesHistory changesHistory = new SubscriberChangesHistory(changeHistoryId, subscriberId,
						changeType, oldValue, newValue, changeDate);
				changesHistories.add(changesHistory);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return changesHistories;
	}

	
	
	/**
	 * Retrieves the loan extension history for a specific subscriber, including details such as the original return date,
	 * new return date, extension date, status, librarian information, and any rejection reasons.
	 *
	 * @param subscriberId the ID of the subscriber whose loan extension history is to be retrieved.
	 * @return an `ArrayList` of `LoanExtension` objects representing the loan extension history for the specified subscriber.
	 * @throws SQLException if a database access error occurs, or if the query fails to execute.
	 */
	public static ArrayList<LoanExtension> getLoanExtensionHistory(String subscriberId) throws SQLException {
		ArrayList<LoanExtension> loanExtensions = new ArrayList<>();
		String query = "SELECT extension_id, subscriber_id, book_id, book_name, author, "
				+ "original_return_date, new_return_date, extension_date, extension_status, librarian_name, rejection_reason "
				+ "FROM loan_extension_history WHERE subscriber_id = ? ORDER BY extension_date DESC";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, Integer.parseInt(subscriberId));
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				// בדיקה והמרה של כל תאריך בנפרד
				java.sql.Date originalReturnDate = rs.getDate("original_return_date");
				java.sql.Date newReturnDate = rs.getDate("new_return_date");
				java.sql.Date extensionDate = rs.getDate("extension_date");

				// המרה רק אם התאריך אינו NULL
				if (originalReturnDate != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(originalReturnDate);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					originalReturnDate = new java.sql.Date(cal.getTimeInMillis());
				}

				if (newReturnDate != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(newReturnDate);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					newReturnDate = new java.sql.Date(cal.getTimeInMillis());
				}

				if (extensionDate != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(extensionDate);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					extensionDate = new java.sql.Date(cal.getTimeInMillis());
				}

				// המשך טיפול ברשומה
				String librarianName = rs.getString("librarian_name");
				if (librarianName == null) {
					librarianName = "The extension was performed by the subscriber";
				}

				LoanExtension extension = new LoanExtension(rs.getInt("extension_id"), rs.getInt("subscriber_id"),
						rs.getInt("book_id"), rs.getString("book_name"), rs.getString("author"), originalReturnDate, // יכול
																														// להיות
																														// NULL
						newReturnDate, // יכול להיות NULL
						extensionDate, // יכול להיות NULL
						rs.getString("extension_status"), librarianName, rs.getString("rejection_reason"));

				loanExtensions.add(extension);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return loanExtensions;
	}

/////////////////////////////////////////////end 21/1/25
	
	/**
	 * Retrieves the history of returned books for a specific subscriber. The returned books include details such as 
	 * the borrow date, return date, actual return date, book name, author, and whether the book was returned late.
	 *
	 * @param subscriberId the ID of the subscriber whose returned book history is to be retrieved.
	 * @return an `ArrayList` of `ReturnBookHistory` objects representing the returned books for the specified subscriber.
	 * @throws SQLException if a database access error occurs, or if the query fails to execute.
	 */
	public static ArrayList<ReturnBookHistory> getReturnedBooksBySubscriberId(int subscriberId) throws SQLException {
		ArrayList<ReturnBookHistory> returnedBooks = new ArrayList<>();
		String query = "SELECT b.history_id, b.subscriber_id, b.book_id, bk.book_name AS book_name, bk.book_author AS author, "
				+ "b.borrow_date, b.return_date, b.actual_return_date, b.is_late " + "FROM borrowedbookshistory b "
				+ "JOIN books bk ON b.book_id = bk.book_id "
				+ "WHERE b.subscriber_id = ? AND b.actual_return_date IS NOT NULL";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {

					Calendar cal = Calendar.getInstance();
					java.sql.Date borrowDate = rs.getDate("borrow_date");
					if (borrowDate != null) {
						cal.setTime(borrowDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						borrowDate = new java.sql.Date(cal.getTimeInMillis());
					}

					java.sql.Date returnDate = rs.getDate("return_date");
					if (returnDate != null) {
						cal.setTime(returnDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						returnDate = new java.sql.Date(cal.getTimeInMillis());
					}

					java.sql.Date actualReturnDate = rs.getDate("actual_return_date");
					if (actualReturnDate != null) {
						cal.setTime(actualReturnDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						actualReturnDate = new java.sql.Date(cal.getTimeInMillis());
					}
					ReturnBookHistory book = new ReturnBookHistory(rs.getInt("history_id"), rs.getInt("subscriber_id"),
							rs.getInt("book_id"), rs.getString("book_name"), rs.getString("author"), borrowDate,
							returnDate, actualReturnDate, rs.getInt("is_late") == 1 // Convert TINYINT to boolean (1 for
																					// true, 0 for false)
					);
					returnedBooks.add(book);
				}
			}
		}

		return returnedBooks;
	}
	
	
	
	/**
	 * Retrieves the loan extension history for a specific subscriber. The loan extensions include details such as 
	 * the original return date, new return date, extension date, extension status, librarian's name, and any rejection reason.
	 *
	 * @param subscriberId the ID of the subscriber whose loan extension history is to be retrieved.
	 * @return an `ArrayList` of `LoanExtension` objects representing the loan extensions for the specified subscriber.
	 * @throws SQLException if a database access error occurs, or if the query fails to execute.
	 */
	public static ArrayList<LoanExtension> getLoanExtensionsBySubscriberId(int subscriberId) throws SQLException {
		ArrayList<LoanExtension> loanExtensions = new ArrayList<>();
		String query = "SELECT extension_id, subscriber_id, book_id, book_name, author, "
				+ "original_return_date, new_return_date, extension_date, extension_status, librarian_name, rejection_reason "
				+ "FROM loanextensions " + "WHERE subscriber_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String librarianName = rs.getString("librarian_name");
					String rejectionReason = rs.getString("rejection_reason");

					if (librarianName == null) {
						librarianName = "The extension was performed by the subscriber";
					}
					if (rejectionReason == null) {
						rejectionReason = "No rejection reason provided";
					}

					// בדיקת NULL לכל תאריך לפני ההמרה
					java.sql.Date originalReturnDate = rs.getDate("original_return_date");
					if (originalReturnDate != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(originalReturnDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						originalReturnDate = new java.sql.Date(cal.getTimeInMillis());
					}

					java.sql.Date newReturnDate = rs.getDate("new_return_date");
					if (newReturnDate != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(newReturnDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						newReturnDate = new java.sql.Date(cal.getTimeInMillis());
					}

					java.sql.Date extensionDate = rs.getDate("extension_date");
					if (extensionDate != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(extensionDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						extensionDate = new java.sql.Date(cal.getTimeInMillis());
					}

					LoanExtension extension = new LoanExtension(rs.getInt("extension_id"), rs.getInt("subscriber_id"),
							rs.getInt("book_id"), rs.getString("book_name"), rs.getString("author"), originalReturnDate,
							newReturnDate, extensionDate, rs.getString("extension_status"), librarianName,
							rejectionReason);
					loanExtensions.add(extension);
				}
			}
		}
		return loanExtensions;
	}

	
	/**
	 * Retrieves a list of borrowed books for a specific subscriber. The returned list contains details 
	 * such as the subscriber's ID, book ID, book name, author, borrow date, and return date. The method also 
	 * adjusts the return date by adding one day.
	 *
	 * @param subscriberId the ID of the subscriber whose borrowed books are to be retrieved.
	 * @return an `ArrayList` of `BorrowedBook` objects representing the borrowed books for the specified subscriber.
	 * @throws SQLException if a database access error occurs, or if the query fails to execute.
	 */
	public static ArrayList<BorrowedBook> getBorrowedBooksBySubscriberIdToReaderCARD(int subscriberId)
			throws SQLException {
		ArrayList<BorrowedBook> borrowedBooks = new ArrayList<>();
		String query = "SELECT b.subscriber_id, b.book_id, bk.book_name AS book_name, bk.book_author AS author, "
				+ "b.borrow_date, b.return_date " + "FROM borrowedbooks b " + "JOIN books bk ON b.book_id = bk.book_id "
				+ "WHERE b.subscriber_id = ?";

		System.out.println("Executing query for subscriber_id: " + subscriberId); // Print the subscriber ID being
																					// queried

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);

			try (ResultSet rs = pstmt.executeQuery()) {
				System.out.println("Query executed successfully. Processing result set...");
				while (rs.next()) {
					int id = rs.getInt("subscriber_id");
					int bookId = rs.getInt("book_id");
					String bookName = rs.getString("book_name");
					String author = rs.getString("author");
					Date borrowDate = rs.getDate("borrow_date");
					Date returnDate = rs.getDate("return_date");

					// Print the details of each book being added to the list
					System.out.println("Found Borrowed Book: ");
					System.out.println("Subscriber ID: " + id);
					System.out.println("Book ID: " + bookId);
					System.out.println("Book Name: " + bookName);
					System.out.println("Author: " + author);
					System.out.println("Borrow Date: " + borrowDate);
					System.out.println("Return Date: " + returnDate);

					// Add one day to the return date using Calendar
					if (returnDate != null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(returnDate);
						calendar.add(Calendar.DATE, 1); // Add one day

						// Convert the new date back to java.sql.Date
						returnDate = new Date(calendar.getTimeInMillis());
						calendar.setTime(borrowDate);
						calendar.add(Calendar.DAY_OF_MONTH, 1);
						borrowDate = new Date(calendar.getTimeInMillis());
					}

					// Create a BorrowedBook object with the modified return date
					BorrowedBook book = new BorrowedBook(id, bookId, bookName, author, borrowDate, returnDate);
					borrowedBooks.add(book);
				}
			}
		}

		// Print the total number of books fetched
		System.out.println("Total Borrowed Books for Subscriber ID " + subscriberId + ": " + borrowedBooks.size());

		return borrowedBooks;
	}
	
//////////REPORT////////////////////////REPORT//////////////////////////REPORT///////////////////////////////REPORT///////////////////
//////////REPORT////////////////////////REPORT//////////////////////////REPORT///////////////////////////////REPORT///////////////////
//////////REPORT////////////////////////REPORT//////////////////////////REPORT///////////////////////////////REPORT///////////////////

	/**
	 * Generates a report of the most late book returns for a specific year and month.
	 * The report includes the book ID, book name, the count of late returns, and 
	 * the average number of days the book was returned late. The report data is then
	 * converted into JSON format and saved into the database.
	 *
	 * @param connection the database connection to be used for executing the SQL queries
	 * @param year the year to filter the late returns by
	 * @param month the month to filter the late returns by
	 * @throws SQLException if any database-related error occurs during the query execution or data insertion
	 */
	private static void generateMostLateReturnsReport(Connection connection, int year, int month) throws SQLException {
	    ArrayList<Object[]> lateReturnData = new ArrayList<>();

	    String query = "SELECT b.book_id, b.book_name, " +
	                  "COUNT(*) as late_count, " +
	                  "AVG(DATEDIFF(actual_return_date, return_date)) as avg_late_days " +
	                  "FROM borrowedbookshistory bbh " +
	                  "JOIN books b ON bbh.book_id = b.book_id " +
	                  "WHERE YEAR(return_date) = ? " +
	                  "AND MONTH(return_date) = ? " +
	                  "AND bbh.is_late = 1 " +
	                  "GROUP BY b.book_id, b.book_name " +
	                  "ORDER BY late_count DESC " +
	                  "LIMIT 10";

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, year);
	        stmt.setInt(2, month);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Object[] bookData = {
	                    rs.getInt("book_id"),
	                    rs.getString("book_name"),
	                    rs.getInt("late_count"),
	                    rs.getDouble("avg_late_days")
	                };
	                lateReturnData.add(bookData);
	            }
	        }
	    }

	    // המרה ל-JSON ושמירה בבסיס הנתונים
	    Gson gson = new Gson();
	    String reportDataJson = gson.toJson(lateReturnData);

	    String saveQuery = "INSERT INTO most_late_returns_report_data (year, month, report_data) VALUES (?, ?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(saveQuery)) {
	        stmt.setInt(1, year);
	        stmt.setInt(2, month);
	        stmt.setString(3, reportDataJson);
	        stmt.executeUpdate();
	    }
	}

	
	/**
	 * Generates a report of the most borrowed books for a specific year and month.
	 * The report includes the book ID, book name, and the count of times each book
	 * was borrowed. The report data is then converted into JSON format and saved into the database.
	 *
	 * @param connection the database connection to be used for executing the SQL queries
	 * @param year the year to filter the borrow records by
	 * @param month the month to filter the borrow records by
	 * @throws SQLException if any database-related error occurs during the query execution or data insertion
	 */
	private static void generateMostBorrowedReport(Connection connection, int year, int month) throws SQLException {
	    ArrayList<Object[]> borrowData = new ArrayList<>();

	    String query = "SELECT b.book_id, b.book_name, " +
	                  "COUNT(*) as borrow_count " +
	                  "FROM borrowedbookshistory bbh " +
	                  "JOIN books b ON bbh.book_id = b.book_id " +
	                  "WHERE YEAR(borrow_date) = ? " +
	                  "AND MONTH(borrow_date) = ? " +
	                  "GROUP BY b.book_id, b.book_name " +
	                  "ORDER BY borrow_count DESC " +
	                  "LIMIT 10";

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, year);
	        stmt.setInt(2, month);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Object[] bookData = {
	                    rs.getInt("book_id"),
	                    rs.getString("book_name"),
	                    rs.getInt("borrow_count")
	                };
	                borrowData.add(bookData);
	            }
	        }
	    }

	    // המרה ל-JSON ושמירה בבסיס הנתונים
	    Gson gson = new Gson();
	    String reportDataJson = gson.toJson(borrowData);

	    String saveQuery = "INSERT INTO most_borrowed_report_data (year, month, report_data) VALUES (?, ?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(saveQuery)) {
	        stmt.setInt(1, year);
	        stmt.setInt(2, month);
	        stmt.setString(3, reportDataJson);
	        stmt.executeUpdate();
	    }
	}

	/**
	 * Checks if the current date and time is the end of the current month (i.e., the last minute of the last day of the month).
	 * 
	 * @return true if the current date and time is the last minute of the last day of the current month, otherwise false.
	 */
	private static boolean isEndOfMonth() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59);

		return now.isEqual(endOfMonth);
	}



	/**
	 * Generates a borrowing report for a given year and month, including both regular and late borrowing times for different book categories.
	 * The report data is saved as a JSON string in the database.
	 * 
	 * @param connection the database connection to use for executing SQL queries.
	 * @param year the year for which the borrowing report should be generated.
	 * @param month the month for which the borrowing report should be generated.
	 * @throws SQLException if there is an error executing any of the SQL queries.
	 */
	private static void generateBorrowingReport(Connection connection, int year, int month) throws SQLException {
		// מערך לשמירת הנתונים עבור כל קטגוריה
		ArrayList<int[]> borrowingData = new ArrayList<>();

		// רשימת הקטגוריות הייחודיות
		String[] categories = { "Fiction", "Romance", "Science", "History","Biography","Mystery"};

		for (String category : categories) {
			int[] categoryData = { 0, 0 }; // [0] - זמן השאלה רגיל, [1] - זמן איחור

			// שאילתה לחישוב זמני השאלה רגילים
			String regularBorrowQuery = "SELECT SUM(DATEDIFF(return_date, borrow_date)) "
					+ "FROM borrowedbookshistory b " + "JOIN books bk ON b.book_id = bk.book_id "
					+ "WHERE bk.book_topic = ? " + "AND YEAR(b.borrow_date) = ? " + "AND MONTH(b.borrow_date) = ? "
					+ "AND b.is_late = 0";

			// שאילתה לחישוב זמני איחור
			String lateReturnQuery = "SELECT SUM(DATEDIFF(actual_return_date, return_date)) "
					+ "FROM borrowedbookshistory b " + "JOIN books bk ON b.book_id = bk.book_id "
					+ "WHERE bk.book_topic = ? " + "AND YEAR(b.borrow_date) = ? " + "AND MONTH(b.borrow_date) = ? "
					+ "AND b.is_late = 1";

			// חישוב זמני השאלה רגילים
			try (PreparedStatement stmt = connection.prepareStatement(regularBorrowQuery)) {
				stmt.setString(1, category);
				stmt.setInt(2, year);
				stmt.setInt(3, month);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					categoryData[0] = rs.getInt(1);
				}
			}

			// חישוב זמני איחור
			try (PreparedStatement stmt = connection.prepareStatement(lateReturnQuery)) {
				stmt.setString(1, category);
				stmt.setInt(2, year);
				stmt.setInt(3, month);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					categoryData[1] = rs.getInt(1);
				}
			}

			borrowingData.add(categoryData);
		}

		// המרה ל-JSON ושמירה בבסיס הנתונים
		Gson gson = new Gson();
		String reportDataJson = gson.toJson(borrowingData);

		String saveQuery = "INSERT INTO borrowingtimereportdata (year, month, report_data) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(saveQuery)) {
			stmt.setInt(1, year);
			stmt.setInt(2, month);
			stmt.setString(3, reportDataJson);
			stmt.executeUpdate();
		}
	}

	
	/**
	 * Generates a status report for subscribers by tracking the active and frozen subscribers for each day in a given month and year.
	 * The result is serialized as a JSON string and saved into the database.
	 * 
	 * @param connection the database connection used to execute SQL queries.
	 * @param year the year for which the status report is generated.
	 * @param month the month for which the status report is generated.
	 * @throws SQLException if there is an error executing any of the SQL queries.
	 */
	private static void generateStatusData(Connection connection, int year, int month) throws SQLException {

		ArrayList<int[]> arr = new ArrayList<>();
		// YearMonth from the Timer not logic
		// Create a YearMonth instance
		YearMonth yearMonth = YearMonth.of(year, month);

		// Get the number of days in the month
		int numOfDays = yearMonth.lengthOfMonth();

		for (int i = 1; i <= numOfDays; i++) {
			int arr2[] = { 0, 0 };
			// Create the date using YearMonth and the day of the month
			LocalDate date = yearMonth.atDay(i);
			// Format the date to a string in 'yyyy-MM-dd' format
			String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

			String ActiveSubscriberStatus = "SELECT COUNT(*) FROM subscriber WHERE registerDate < ?";
			String FrozenSubscriberStatus = "SELECT COUNT(*) FROM frozenaccountshistory WHERE frozen_start_date <= ? AND frozen_end_date >= ?";

			// Prepare the statement for Active Subscriber Status
			PreparedStatement stmt1 = connection.prepareStatement(ActiveSubscriberStatus);

			// Set the parameter for the query
			stmt1.setString(1, formattedDate); // Set the first parameter (index 1) to the constructed date

			// Execute the query for active subscribers
			ResultSet resultSet = stmt1.executeQuery();

			if (resultSet.next()) {
				// Retrieve the active subscriber count
				int activeSubscribers = resultSet.getInt(1); // Get the count from the first column of the result set
				arr2[0] = activeSubscribers; // Store the count in arr2[0]
			}

			// Prepare the statement
			PreparedStatement stmt2 = connection.prepareStatement(FrozenSubscriberStatus);

			// Set the date values for both parameters (the same formatted date)
			stmt2.setString(1, formattedDate);
			stmt2.setString(2, formattedDate);

			// Execute the query and retrieve the result
			ResultSet resultSet2 = stmt2.executeQuery();
			if (resultSet2.next()) {
				int frozenSubscriber = resultSet2.getInt(1); // Get the count from the first column
				arr2[1] = frozenSubscriber;
				arr2[0] -= frozenSubscriber;

			}
			arr.add(i - 1, arr2);
		}
		// Serialize the ArrayList<int[]> to JSON format
		Gson gson = new Gson();
		String reportDataJson = gson.toJson(arr);

		// SQL query to insert the data into the table
		String saveData = "INSERT INTO subscriberstatusreportdata (year, month, report_data) VALUES (?, ?, ?)";

		try (PreparedStatement stmt = connection.prepareStatement(saveData)) {
			// Set parameters for year, month, and the serialized JSON data
			stmt.setInt(1, year);
			stmt.setInt(2, month);
			stmt.setString(3, reportDataJson);

			// Execute the insert statement
			stmt.executeUpdate();
		}
	}

	
	/**
	 * Retrieves the subscriber status report for a specific year and month from the database. 
	 * The report includes the number of active and frozen subscribers for each day of the month. 
	 * If the last day of the month is after the current date, the method returns null.
	 *
	 * @param year the year for which the subscriber status report is requested.
	 * @param month the month for which the subscriber status report is requested.
	 * @return an ArrayList of int arrays, each containing two values: active subscribers and frozen subscribers for a specific day.
	 *         Returns null if the last day of the month is after the current date.
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public static ArrayList<int[]> getSubscriberStatusReport(int year, int month) throws SQLException {
		ArrayList<int[]> arr = new ArrayList<>();

		// YearMonth from the Timer (not logic)
		YearMonth yearMonth = YearMonth.of(year, month);

		// Get the number of days in the month
		int numOfDays = yearMonth.lengthOfMonth();
		LocalDate lastDay = yearMonth.atDay(numOfDays);

		// If the last day of the month is after the current date, return null
		if (lastDay.isAfter(LocalDate.now())) {
			return null;
		} else {
			boolean flag = true;
			// SQL query to get data for the specific year and month
			String query = "SELECT report_data FROM subscriberstatusreportdata WHERE year = ? AND month = ?";

			// Prepare the statement
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, year); // Set year in the query
			stmt.setInt(2, month); // Set month in the query

			// Execute the query and retrieve the results
			ResultSet resultSet = stmt.executeQuery();

			// Initialize arr2 to store pairs of integers
			int[] arr2 = new int[2];

			while (resultSet.next()) {
				// Assuming that the report_data is stored as a string representation of an int
				// array
				// like "[8, 0]"
				String reportData = resultSet.getString("report_data");

				// Remove brackets and split by comma to get individual numbers
				reportData = reportData.replace("[", "").replace("]", "");
				String[] data = reportData.split(",");

				if (flag == true) {

					// Process the data assuming there are at least two numbers for each report
					for (int i = 0; i < data.length - 2; i = i + 2) {
						arr2[0] = Integer.parseInt(data[i].trim()); // Store the first value
						arr2[1] = Integer.parseInt(data[i + 1].trim()); // Store the second value
						arr.add(arr2.clone()); // Add the completed array to the list (using clone to avoid overwriting)
						flag = false;
					}
				}
			}
			return arr;
		}
	}

	
	/**
	 * Retrieves the borrowing report for a specific year and month from the database. 
	 * The report contains borrowing times for different book categories. 
	 * The data is stored in JSON format and is parsed into an ArrayList of int arrays.
	 *
	 * @param year the year for which the borrowing report is requested.
	 * @param month the month for which the borrowing report is requested.
	 * @return an ArrayList of int arrays, where each array contains borrowing times for a specific category.
	 *         Returns null if no report data exists for the specified year and month.
	 * @throws SQLException if there is an error executing the SQL query.
	 */
	public static ArrayList<int[]> getBorrowingReport(int year, int month) throws SQLException {
		ArrayList<int[]> arr = new ArrayList<>();

		String query = "SELECT report_data FROM borrowingtimereportdata WHERE year = ? AND month = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, year);
			stmt.setInt(2, month);
			ResultSet resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				// המרת JSON לאובייקט
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<int[]>>() {
				}.getType();
				arr = gson.fromJson(resultSet.getString("report_data"), type);
				return arr;
			}
		}
		return null; // אם אין דוח לחודש המבוקש
	}
	public static ArrayList<Object[]> getMostLateReport(int year, int month) throws SQLException {
	    ArrayList<Object[]> arr = new ArrayList<>();

	    String query = "SELECT report_data FROM most_late_returns_report_data WHERE year = ? AND month = ?";

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, year);
	        stmt.setInt(2, month);
	        ResultSet resultSet = stmt.executeQuery();

	        if (resultSet.next()) {
	            // המרת JSON לאובייקט
	            Gson gson = new Gson();
	            Type type = new TypeToken<ArrayList<Object[]>>() {
	            }.getType();
	            arr = gson.fromJson(resultSet.getString("report_data"), type);
	            return arr;
	        }
	    }
	    return null; // אם אין דוח לחודש המבוקש
	}

	public static ArrayList<Object[]> getMostBorrowedReport(int year, int month) throws SQLException {
	    ArrayList<Object[]> arr = new ArrayList<>();

	    String query = "SELECT report_data FROM most_borrowed_report_data WHERE year = ? AND month = ?";

	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, year);
	        stmt.setInt(2, month);
	        ResultSet resultSet = stmt.executeQuery();

	        if (resultSet.next()) {
	            // המרת JSON לאובייקט
	            Gson gson = new Gson();
	            Type type = new TypeToken<ArrayList<Object[]>>() {
	            }.getType();
	            arr = gson.fromJson(resultSet.getString("report_data"), type);
	            return arr;
	        }
	    }
	    return null; // אם אין דוח לחודש המבוקש
	}

//////////REPORT////////////////////////REPORT//////////////////////////REPORT///////////////////////////////REPORT///////////////////
//////////REPORT////////////////////////REPORT//////////////////////////REPORT///////////////////////////////REPORT///////////////////
//////////REPORT////////////////////////REPORT//////////////////////////REPORT///////////////////////////////REPORT///////////////////

	/**
	 * Sends email notifications to subscribers whose ordered books have arrived.
	 * The method retrieves orders that have reached their start order date and 
	 * have not yet sent notifications. For each of these orders, it sends an 
	 * email to the subscriber and updates the order to indicate that the 
	 * notification has been sent.
	 * 
	 * @param connection the database connection to be used for retrieving the orders and updating the database.
	 */
	public static void sendNotificationArrivedBook(Connection connection) {
		int bookId = -1, subscriberId = -1;
		String subscriberName = null;
		String query = "SELECT o.subscriber_id, o.book_name, s.subscriber_email,o.book_id, s.subscriber_name "
				+ "FROM orderedbooks o " + "JOIN subscriber s ON o.subscriber_id = s.subscriber_id "
				+ "WHERE o.start_order_date <= NOW() AND o.notificationSent=0";

		try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

			// Iterate through the result set and send notifications
			while (rs.next()) {
				subscriberId = rs.getInt("subscriber_id");
				String bookName = rs.getString("book_name");
				String email = rs.getString("subscriber_email");
				bookId = rs.getInt("book_id");
				subscriberName = rs.getString("subscriber_name");
				System.out.println("Sending email to: " + email);
				System.out.println("Sending to: " + subscriberName);
				System.out.println("Sending  to: " + bookName);

				// Send email notification
				sendEmailArrivedBookNotification(email, subscriberName, bookName);
			}
			String updateQuery = "UPDATE orderedbooks SET notificationSent = 1 WHERE subscriber_id = ? AND book_id=? AND notificationSent = 0";
			try (PreparedStatement stmt1 = connection.prepareStatement(updateQuery)) {
				stmt1.setInt(1, subscriberId);
				stmt1.setInt(2, bookId);
				stmt1.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Sends an email notification to the subscriber informing them that their 
	 * ordered book has arrived.
	 * 
	 * This method creates a `LibraryNotificationService` object with an SMTP server 
	 * and uses it to send an email to the subscriber. It then prints a message 
	 * confirming the notification has been sent.
	 * 
	 * @param email the email address of the subscriber to whom the notification will be sent.
	 * @param subscriberName the name of the subscriber receiving the notification.
	 * @param bookName the name of the book that has arrived.
	 */
	private static void sendEmailArrivedBookNotification(String email, String subscriberName, String bookName) {
		String smtpHostServer = "smtp.gmail.com"; // Replace with actual SMTP server
		LibraryNotificationService notificationService = new LibraryNotificationService(smtpHostServer);

		notificationService.sendBookArrivalNotification(subscriberName, bookName, email);
		System.out.println("Notification sent to " + email + " for book: " + bookName);
	}

	
	/**
	 * Sends email reminders to subscribers whose books are due for return the next day.
	 * 
	 * This method retrieves data from the `borrowedbooks` table to identify subscribers 
	 * whose books are due for return the next day. It sends a reminder email to each 
	 * subscriber using the `sendEmailReturnReminder` method. After sending the reminder, 
	 * it updates the `notificationSent` field in the `borrowedbooks` table to indicate 
	 * that the reminder has been sent.
	 * 
	 * @param connection the database connection used to retrieve the data and update the records.
	 * @throws SQLException if a database access error occurs.
	 */
	private static void sendReturnReminders(Connection connection) throws SQLException {
		int subscriberId = -1, bookId = -1;
		String query = "SELECT b.subscriber_id, b.book_id, b.return_date, b.notificationSent, s.subscriber_email, s.subscriber_name, bb.book_name "
				+ "FROM borrowedbooks b " + "JOIN subscriber s ON b.subscriber_id = s.subscriber_id "
				+ "JOIN books bb ON b.book_id = bb.book_id "
				+ "WHERE b.return_date = CURDATE() + INTERVAL 1 DAY AND b.notificationSent = 0";

		try (PreparedStatement pstmt1 = connection.prepareStatement(query); ResultSet rs = pstmt1.executeQuery()) {

			while (rs.next()) {
				subscriberId = rs.getInt("subscriber_id");
				bookId = rs.getInt("book_id");
				String email = rs.getString("subscriber_email");
				String subscriberName = rs.getString("subscriber_name");
				String bookName = rs.getString("book_name");

				// Get the book name using the book ID
				System.out.println("Sending email to: " + email);
				System.out.println("Sending to: " + subscriberName);
				System.out.println("Sending  to: " + bookName);
				sendEmailReturnReminder(email, subscriberName, bookName); // Use the retrieved book name

				// Update notificationSent flag in the database
			}
			String updateQuery = "UPDATE borrowedbooks SET notificationSent = 1 WHERE subscriber_id = ? AND book_id = ?";
			try (PreparedStatement pstmt2 = connection.prepareStatement(updateQuery)) {
				pstmt2.setInt(1, subscriberId);
				pstmt2.setInt(2, bookId);
				pstmt2.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Sends a return reminder email to a subscriber.
	 * 
	 * This method constructs and sends an email to a subscriber reminding them to return
	 * a borrowed book by using the `LibraryNotificationService` class to send the notification.
	 * It prints a confirmation message to the console once the email is sent.
	 * 
	 * @param email the email address of the subscriber to receive the return reminder.
	 * @param subscriberName the name of the subscriber to personalize the email.
	 * @param bookName the name of the book for which the return reminder is being sent.
	 */
	private static void sendEmailReturnReminder(String email, String subscriberName, String bookName) {
		String smtpHostServer = "smtp.gmail.com"; // Replace with actual SMTP server
		LibraryNotificationService notificationService = new LibraryNotificationService(smtpHostServer);

		// Call the method to send the return reminder notification
		notificationService.sendReturnReminderNotificationForBorrow(subscriberName, bookName, email);

		System.out.println("Notification sent to " + email + " for book: " + bookName);
	}

	//////// NOTIFICATION////////////NOTIFICATION///////////NOTIFICATION/////////////NOTIFICATION/////////
	//////// NOTIFICATION////////////NOTIFICATION///////////NOTIFICATION/////////////NOTIFICATION/////////

	/////////////// BOOK////////////BOOK/////////////BOOK/////////BOOK//////////////////////////////////
	/////////////// BOOK////////////BOOK/////////////BOOK/////////BOOK//////////////////////////////////
	/////////////// BOOK////////////BOOK/////////////BOOK/////////BOOK//////////////////////////////////

	
	/**
	 * Retrieves the ID of a book based on its name and author.
	 * 
	 * This method queries the database to find a book with the exact name and author specified in the
	 * given `Book` object. It returns the book's ID if found, or -1 if the book is not found in the database.
	 * 
	 * @param book the `Book` object containing the name and author to search for.
	 * @return the ID of the book if found, or -1 if the book is not found.
	 * @throws SQLException if a database access error occurs.
	 */
	public static int getBookId(Book book) throws SQLException {
		String query = "SELECT * FROM books WHERE  book_name = ? AND  book_author = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, book.getBookName());
			pstmt.setString(2, book.getauthorName());

			System.out.println("Executing query: " + query);
			System.out.println("Book name: " + book.getBookName());
			System.out.println("Author name: " + book.getauthorName());

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) { // Only proceed if the book is found
					int bookId = rs.getInt("book_id");
					System.out.printf("bookId: %d\n", bookId);
					return bookId;
				} else {
					return -1;
				}
			}
		}
	}

	
	/**
	 * Checks whether a specific book is borrowed by a specific subscriber.
	 * 
	 * This method queries the `BorrowedBooks` table in the database to determine if the specified book 
	 * (identified by its `bookId`) has been borrowed by the specified subscriber (identified by their 
	 * `subscriberId`). It returns `true` if the book is borrowed by the subscriber, and `false` otherwise.
	 * 
	 * @param bookId the ID of the book to check.
	 * @param subscriberId the ID of the subscriber to check.
	 * @return `true` if the book is borrowed by the subscriber, `false` otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public static boolean isBookBorrowed(int bookId, int subscriberId) throws SQLException {
		String query = "SELECT COUNT(*) FROM BorrowedBooks WHERE book_id = ? AND subscriber_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, bookId);
		pstmt.setInt(2, subscriberId);

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		rs.close();
		pstmt.close();

		return count > 0;
	}

	/**
	 * Searches for books by name or description based on a given search term.
	 * 
	 * This method performs a case-insensitive search in the `books` table for books whose name or 
	 * contains the specified search term. The method returns a list of `Book` objects 
	 * that match the search criteria.
	 * 
	 * @param searchTerm the term to search for in the book name.
	 * @return an `ArrayList` of `Book` objects that match the search term.
	 * @throws SQLException if a database access error occurs.
	 */
	public static ArrayList<Book> searchBooksByName(String searchTerm) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();
		String query = "SELECT * FROM books WHERE LOWER(book_name) LIKE ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, searchTerm);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					books.add(new Book(rs.getInt("book_id"), rs.getString("book_name"), rs.getBytes("book_image"),
							rs.getString("book_topic"), rs.getString("book_description"), rs.getString("book_author"),
							rs.getInt("numberOfCopies"), rs.getInt("numberOfAvailableCopies"),
							rs.getDate("firstReturnDate"), rs.getString("book_place"),
							rs.getInt("numberOfOrderedCopies")));
				}
			}
		}

		return books;
	}
	
	
	
	/**
	 * Searches for books description based on a given search term.
	 * 
	 * This method performs a case-insensitive search in the `books` table for books whose 
	 * description contains the specified search term. The method returns a list of `Book` objects 
	 * that match the search criteria.
	 * 
	 * @param searchTerm the term to search for in the book description.
	 * @return an `ArrayList` of `Book` objects that match the search term.
	 * @throws SQLException if a database access error occurs.
	 */
	public static ArrayList<Book> searchBooksByDescription(String searchTerm) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();
		String query = "SELECT * FROM books WHERE LOWER(book_description) LIKE ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String likeSearchTerm = "%" + searchTerm.toLowerCase() + "%";
			pstmt.setString(1, likeSearchTerm);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					books.add(new Book(rs.getInt("book_id"), rs.getString("book_name"), rs.getBytes("book_image"),
							rs.getString("book_topic"), rs.getString("book_description"), rs.getString("book_author"),
							rs.getInt("numberOfCopies"), rs.getInt("numberOfAvailableCopies"),
							rs.getDate("firstReturnDate"), rs.getString("book_place"),
							rs.getInt("numberOfOrderedCopies")));
				}
			}
		}

		return books;
	}

	/**
	 * Searches for books by category (topic) based on a given search term.
	 * 
	 * This method performs a case-sensitive search in the `books` table for books whose `book_topic` 
	 * matches the specified search term. It returns a list of `Book` objects that belong to the given category.
	 * 
	 * @param searchTerm the category (topic) to search for in the books' topics.
	 * @return an `ArrayList` of `Book` objects that match the given category.
	 * @throws SQLException if a database access error occurs.
	 */
	public static ArrayList<Book> searchBooksByCategory(String searchTerm) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();
		String query = "SELECT * FROM books WHERE book_topic= ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, searchTerm);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					books.add(new Book(rs.getInt("book_id"), rs.getString("book_name"), rs.getBytes("book_image"),
							rs.getString("book_topic"), rs.getString("book_description"), rs.getString("book_author"),
							rs.getInt("numberOfCopies"), rs.getInt("numberOfAvailableCopies"),
							rs.getDate("firstReturnDate"), rs.getString("book_place"),
							rs.getInt("numberOfOrderedCopies")));
				}
			}
		}
		return books;
	}

	
	/**
	 * Retrieves all books from the database.
	 * 
	 * This method executes a SQL query to retrieve all records from the `books` table 
	 * and returns them as a list of {@link Book} objects.
	 * 
	 * @return an {@link Book} ArrayList containing all books in the database.
	 * @throws SQLException if a database access error occurs.
	 */
	public static ArrayList<Book> getAllBooks() throws SQLException {
		ArrayList<Book> books = new ArrayList<>();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM books");

		while (rs.next()) {
			books.add(new Book(rs.getInt("book_id"), rs.getString("book_name"), rs.getBytes("book_image"),
					rs.getString("book_topic"), rs.getString("book_description"), rs.getString("book_author"),
					rs.getInt("numberOfCopies"), rs.getInt("numberOfAvailableCopies"), rs.getDate("firstReturnDate"),
					rs.getString("book_place"), rs.getInt("numberOfOrderedCopies")));
		}
		return books;
	}

	
	/**
	 * Checks if a book is available for order.
	 * 
	 * This method checks whether a book has available copies and whether any copies have been ordered.
	 * It returns `true` if the book has at least one available copy and no copies have been ordered yet.
	 * 
	 * @param book The `Book` object representing the book to check.
	 * @return `true` if the book has available copies and no copies have been ordered, `false` otherwise.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 */
	public static boolean isBookAvailableForOrder(Book book) throws SQLException {
		String query = "SELECT numberOfAvailableCopies,numberOfOrderedCopies  FROM books WHERE BINARY book_name = ? AND book_author = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, book.getBookName());
		pstmt.setString(2, book.getauthorName());

		ResultSet rs = pstmt.executeQuery();
		boolean b = false;
		if (rs.next()) {
			int availableCopies = rs.getInt("numberOfAvailableCopies");
			int OrderedCopies = rs.getInt("numberOfOrderedCopies");

			b = availableCopies > 0 && OrderedCopies == 0;
			rs.close();
			pstmt.close();
			System.out.printf("book available: %b%n", b);
		}
		return b; // Returns true if there are available copies

	}

	
	/**
	 * Checks if all copies of a book have been ordered.
	 * 
	 * This method compares the total number of copies of a book with the number of copies that have been ordered.
	 * It returns `true` if all copies of the book have been ordered, otherwise it returns `false`.
	 * 
	 * @param book The `Book` object representing the book to check.
	 * @return `true` if all copies of the book have been ordered, `false` otherwise.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 */
	public static boolean isAllCopiesareOrdered(Book book) throws SQLException {
		String query = "SELECT numberOfOrderedCopies,numberOfCopies  FROM books WHERE  book_name = ? AND book_author = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, book.getBookName());
		pstmt.setString(2, book.getauthorName());

		ResultSet rs = pstmt.executeQuery();
		boolean b = false;
		if (rs.next()) {
			int orderedCopies = rs.getInt("numberOfOrderedCopies");
			int bookCopies = rs.getInt("numberOfCopies");
			b = orderedCopies == bookCopies;
			rs.close();
			pstmt.close();
			System.out.printf("number of ordered copies is equal to number of copies: %b%n", b);
		}
		return b; // Returns true if there are available copies
	}

	
	/**
	 * Checks if a book with the specified name and author exists in the database.
	 * 
	 * This method queries the database to determine if there is a book with the exact name and author provided.
	 * It returns `true` if such a book exists, otherwise returns `false`.
	 * 
	 * @param book The `Book` object representing the book to check for existence.
	 * @return `true` if the book exists in the database, `false` otherwise.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 */
	public static boolean doesBookWithNameAndAuthorExist(Book book) throws SQLException {
		String query = "SELECT * FROM books WHERE book_name = ? AND book_author = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, book.getBookName());
			pstmt.setString(2, book.getauthorName());

			System.out.println("Executing query: " + query);
			System.out.println("Book name: " + book.getBookName());
			System.out.println("Author name: " + book.getauthorName());

			try (ResultSet rs = pstmt.executeQuery()) {
				boolean exists = rs.next(); // Returns true if the book exists
				System.out.printf("book exists: %b\n", exists);
				return exists;
			}
		}

	}

	
	/**
	 * Searches for a book in the database by its ID.
	 * 
	 * This method queries the database to find a book with the specified `bookId`.
	 * If the book is found, a `Book` object is created and returned with all the book details.
	 * If no book with the given ID is found, the method returns `null`.
	 * 
	 * @param bookId The ID of the book to search for in the database.
	 * @return A `Book` object containing the book details if found, or `null` if no book with the specified ID exists.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 */
	public static Book searchBookById(int bookId) throws SQLException {
		String query = "SELECT * FROM books WHERE book_id = ?"; // Query to search by book ID
		Book book = null;

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, bookId); // Set the book ID parameter

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					//Create a Book object from the result set
					book = new Book(rs.getInt("book_id"), rs.getString("book_name"), rs.getBytes("book_image"),
							rs.getString("book_topic"), rs.getString("book_description"), rs.getString("book_author"),
							rs.getInt("numberOfCopies"), rs.getInt("numberOfAvailableCopies"),
							rs.getDate("firstReturnDate"), rs.getString("book_place"),
							rs.getInt("numberOfOrderedCopies"));
				}
			}
		}

		return book; // Return the book if found, otherwise null
	}

	
	/**
	 * Checks if a book exists in the database by its ID.
	 * 
	 * This method queries the database to check if a book with the given `bookID` exists in the `books` table.
	 * It returns `true` if the book exists, and `false` otherwise.
	 * 
	 * @param bookID The ID of the book to check for in the database.
	 * @return `true` if the book with the specified ID exists, `false` otherwise.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 */
	public static boolean doesBookExistById(int bookID) throws SQLException {
		String query = "SELECT COUNT(*) FROM books WHERE book_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, bookID);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getInt(1) > 0; // Returns true if the book exists
		}
		return false;
	}

	
	/**
	 * Checks if a book exists in the database by its ID, and verifies that the book's name and author are not null.
	 * 
	 * This method queries the database to check if a book with the given `bookID` exists, and ensures that both the
	 * `book_name` and `book_author` fields are not null. It returns `true` if the book exists and has a non-null name 
	 * and author, and `false` otherwise.
	 * 
	 * @param bookID The ID of the book to check for in the database.
	 * @return `true` if the book with the specified ID exists and has a non-null name and author, `false` otherwise.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 */
	public static boolean doesBookWithNameAndAuthorExistById(int bookID) throws SQLException {
		String query = "SELECT COUNT(*) FROM books WHERE book_id = ? AND book_name IS NOT NULL AND book_author IS NOT NULL";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, bookID);

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getInt(1) > 0; // Returns true if the book exists with name and author
		}
		return false;
	}

	
	
	/**
	 * Checks if a book is available for borrowing or if all copies of the book are ordered.
	 * 
	 * This method queries the database to check the availability of a book by its ID. It checks the number of available copies,
	 * the number of ordered copies, and the total number of copies for the book. It returns `true` if there are available copies
	 * for borrowing or if all copies of the book have been ordered. It throws an exception if the provided book object is `null`.
	 * 
	 * @param book The Book object containing the book ID to check for availability.
	 * @return `true` if the book is available for borrowing or if all copies have been ordered, `false` otherwise.
	 * @throws SQLException If an SQL error occurs during the query execution.
	 * @throws IllegalArgumentException If the provided book object is `null`.
	 */
	public static boolean isBookAvailableOrAllCopiesOrderedById(Book book) throws SQLException {
		if (book == null) {
			throw new IllegalArgumentException("Book object cannot be null.");
		}

		String query = "SELECT numberOfAvailableCopies, numberOfOrderedCopies, numberOfCopies FROM books WHERE book_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, book.getBookID()); // Use the book ID from the Book object

		ResultSet rs = pstmt.executeQuery();
		boolean isAvailableOrAllOrdered = false;

		if (rs.next()) {
			int availableCopies = rs.getInt("numberOfAvailableCopies");
			int orderedCopies = rs.getInt("numberOfOrderedCopies");
			int totalCopies = rs.getInt("numberOfCopies");

			//Check if the book is available or all copies are ordered
			isAvailableOrAllOrdered = availableCopies > 0 && availableCopies > orderedCopies;

			System.out.printf(
					"Book ID %d: available copies = %d, ordered copies = %d, total copies = %d, is available or all ordered: %b%n",
					book.getBookID(), availableCopies, orderedCopies, totalCopies, isAvailableOrAllOrdered);
		}

		//Close resources
		rs.close();
		pstmt.close();

		return isAvailableOrAllOrdered; // Returns true if there are available copies or all copies are ordered
	}

	/**
	 * Checks if a book exists in the database by its barcode.
	 *
	 * @param barcode The barcode of the book to check.
	 * @return {@code true} if the book with the specified barcode exists in the database, {@code false} otherwise.
	 * @throws SQLException If there is an error querying the database.
	 */
	public static boolean doesBookExistByBarcode(String barcode) throws SQLException {
		String query = "SELECT COUNT(*) FROM books WHERE barcode_number = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, barcode);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0; // Returns true if the book exists
				}
			}
		}
		return false;
	}

	
	/**
	 * Checks if a book with the given barcode is available or if all copies are ordered.
	 * The method will return true if the book is available (has available copies) or if all copies of the book are ordered.
	 * 
	 * @param barcode The barcode of the book to check.
	 * @return {@code true} if the book is available or if all copies are ordered, {@code false} if not available and not all copies are ordered.
	 * @throws IllegalArgumentException If the barcode is {@code null} or empty.
	 * @throws SQLException If there is an error querying the database.
	 */
	public static boolean isBookAvailableOrAllCopiesOrderedByBarcode(String barcode) throws SQLException {
		if (barcode == null || barcode.isEmpty()) {
			throw new IllegalArgumentException("Barcode cannot be null or empty.");
		}

		String query = "SELECT numberOfAvailableCopies, numberOfOrderedCopies, numberOfCopies FROM books WHERE barcode_number = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, barcode);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int availableCopies = rs.getInt("numberOfAvailableCopies");
					int orderedCopies = rs.getInt("numberOfOrderedCopies");
					int totalCopies = rs.getInt("numberOfCopies");

					// Check if the book is available or all copies are ordered
					boolean isAvailableOrAllOrdered = availableCopies > 0 && availableCopies > orderedCopies;

					System.out.printf(
							"Book Barcode %s: available copies = %d, ordered copies = %d, total copies = %d, is available or all ordered: %b%n",
							barcode, availableCopies, orderedCopies, totalCopies, isAvailableOrAllOrdered);

					return isAvailableOrAllOrdered;
				}
			}
		}
		return false; // Book not found or error occurred
	}

	
	/**
	 * Checks if a book is available for borrowing.
	 * The method will return true if there are available copies of the book,
	 * and false if no copies are available or all copies have been ordered.
	 * 
	 * @param book The book to check for availability.
	 * @return {@code true} if there are available copies of the book, {@code false} otherwise.
	 * @throws SQLException If there is an error querying the database.
	 */
	public static boolean isBookAvailable(Book book) throws SQLException {
		String query = "SELECT numberOfAvailableCopies, numberOfOrderedCopies FROM books WHERE book_id = ? AND BINARY book_name = ? AND BINARY book_topic = ? AND BINARY book_author = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, book.getBookID());
		pstmt.setString(2, book.getBookName());
		pstmt.setString(3, book.getBookTopic());
		pstmt.setString(4, book.getauthorName());

		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			int availableCopies = rs.getInt("numberOfAvailableCopies");
			int orderedCopies = rs.getInt("numberOfOrderedCopies");

			// בדוק אם יש עותקים זמינים או אם כל העותקים כבר הוזמנו
			boolean isAvailable = availableCopies > orderedCopies;

			rs.close();
			pstmt.close();
			System.out.println(isAvailable);
			return isAvailable; // מחזיר true אם יש עותקים זמינים
		}

		rs.close();
		pstmt.close();
		return false; // הספר לא קיים או שאין עותקים זמינים
	}

	
	
	/**
	 * Checks if a book exists in the database based on the book's ID, name, topic, and author.
	 * The method returns true if a book with the exact details exists in the database.
	 * 
	 * @param book The book to check for existence.
	 * @return {@code true} if the book exists, {@code false} otherwise.
	 * @throws SQLException If there is an error querying the database.
	 */
	public static boolean doesBookExist(Book book) throws SQLException {
		String query = "SELECT * FROM books WHERE book_id = ? AND BINARY book_name = ? AND BINARY book_topic = ? AND BINARY book_author = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, book.getBookID());
		pstmt.setString(2, book.getBookName());
		pstmt.setString(3, book.getBookTopic());
		pstmt.setString(4, book.getauthorName());

		ResultSet rs = pstmt.executeQuery();

		boolean exists = rs.next(); // Returns true if the book exists
		rs.close();
		pstmt.close();
		System.out.println(exists);
		return exists;
	}
	
	/////////////// BOOK////////////BOOK/////////////BOOK/////////BOOK//////////////////////////////////
	/////////////// BOOK////////////BOOK/////////////BOOK/////////BOOK//////////////////////////////////

	///////////////////////////// SUBSCRIBER///////////SUBSCRIBER////////////SUBSCRIBER///////////////////
	///////////////////////////// SUBSCRIBER///////////SUBSCRIBER////////////SUBSCRIBER///////////////////

	
	/**
	 * Retrieves a subscriber from the database based on the subscriber's ID.
	 * 
	 * @param SubscriberId The ID of the subscriber to retrieve.
	 * @return A {@link Subscriber} object if the subscriber is found, {@code null} otherwise.
	 * @throws SQLException If there is an error querying the database.
	 */
	public static Subscriber getSubscriber(String SubscriberId) throws SQLException {
		String query = "SELECT * FROM Subscriber WHERE subscriber_id  = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, SubscriberId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				return new Subscriber(rs.getInt("subscriber_id"), rs.getString("subscriber_name"),
						rs.getInt("detailed_subscription_history"), rs.getString("subscriber_phone_number"),
						rs.getString("subscriber_email"));
			}
		}
		return null;
	}

	/**
	 * Retrieves detailed information of a subscriber based on the subscriber's ID.
	 * 
	 * @param subscriberId The ID of the subscriber whose details need to be fetched.
	 * @return A {@link Subscriber} object containing the subscriber's details, or {@code null} if the subscriber is not found.
	 * @throws SQLException If there is an error querying the database.
	 */
	public static Subscriber getSubscriberDetails(int subscriberId) throws SQLException {
		String query = "SELECT * FROM Subscriber WHERE subscriber_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId); // השתמש ב-setInt במקום setString
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// הנחה שהשדות בטבלה תואמים לשדות במחלקת Subscriber
				return new Subscriber(rs.getInt("subscriber_id"), rs.getString("subscriber_name"),
						rs.getString("username"), // הוספת שם המשתמש
						rs.getString("subscriber_email"), rs.getString("subscriber_phone_number"),
						rs.getString("password"), // הוספת סיסמה
						rs.getInt("detailed_subscription_history"), // אם יש שדה כזה
						rs.getString("status") // שליפת הסטטוס
				);
			}
		}
		return null; // אם לא נמצא מנוי, מחזירים null
	}

	
	/**
	 * Updates the phone number and email address of a subscriber in the database.
	 * If either the phone number or email address has changed, the update is performed
	 * and the changes are logged in the change history.
	 * 
	 * @param subscriber The {@link Subscriber} object containing the updated phone number and email.
	 * @throws SQLException If there is an error while interacting with the database.
	 */
	public static void updateSubscriber(Subscriber subscriber) throws SQLException {
		// שליפת הערכים הנוכחיים
		String selectQuery = "SELECT subscriber_phone_number, subscriber_email FROM Subscriber WHERE subscriber_id = ?";
		String currentPhone = null;
		String currentEmail = null;

		try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
			selectStmt.setInt(1, subscriber.getSubscriberId());
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				currentPhone = rs.getString("subscriber_phone_number");
				currentEmail = rs.getString("subscriber_email");
			}
		}

		// בדיקה אם יש שינוי בטלפון או באימייל
		boolean phoneChanged = currentPhone == null || !currentPhone.equals(subscriber.getSubscriberPhoneNumber());
		boolean emailChanged = currentEmail == null || !currentEmail.equals(subscriber.getSubscriberEmail());

		// עדכון רק אם יש שינוי
		if (phoneChanged || emailChanged) {
			String updateQuery = "UPDATE Subscriber SET subscriber_phone_number=?, subscriber_email=? WHERE subscriber_id=?";

			try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
				pstmt.setString(1, subscriber.getSubscriberPhoneNumber());
				pstmt.setString(2, subscriber.getSubscriberEmail());
				pstmt.setInt(3, subscriber.getSubscriberId());
				pstmt.executeUpdate();

				// תיעוד השינויים
				if (phoneChanged) {
					insertChangeHistory(subscriber.getSubscriberId(), "phone", currentPhone,
							subscriber.getSubscriberPhoneNumber());
				}

				if (emailChanged) {
					insertChangeHistory(subscriber.getSubscriberId(), "email", currentEmail,
							subscriber.getSubscriberEmail());
				}
			}
		}
	}

	
	/**
	 * Inserts a record into the subscriber's change history table to track changes
	 * made to a subscriber's details, such as phone number or email.
	 * 
	 * @param subscriberId The ID of the subscriber whose data was changed.
	 * @param changeType The type of change (e.g., "phone", "email").
	 * @param oldValue The previous value of the field before the change.
	 * @param newValue The new value of the field after the change.
	 * @throws SQLException If there is an error while interacting with the database.
	 */
	private static void insertChangeHistory(int subscriberId, String changeType, String oldValue, String newValue)
			throws SQLException {
		String insertQuery = "INSERT INTO subscriber_changes_history (subscriber_id, change_type, old_value, new_value, change_date) VALUES (?, ?, ?, ?, NOW())";

		try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
			pstmt.setInt(1, subscriberId);
			pstmt.setString(2, changeType);
			pstmt.setString(3, oldValue);
			pstmt.setString(4, newValue);
			pstmt.executeUpdate();
		}
	}

	
	/**
	 * Retrieves a list of all subscribers from the database.
	 * 
	 * @return An ArrayList containing all the subscribers.
	 * @throws SQLException If there is an error while interacting with the database.
	 */
	public static ArrayList<Subscriber> getAllSubscribers() throws SQLException {
		ArrayList<Subscriber> subscribers = new ArrayList<>();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM subscriber");

		while (rs.next()) {
			subscribers.add(new Subscriber(rs.getInt("subscriber_id"), rs.getString("subscriber_name"),
					rs.getInt("detailed_subscription_history"), rs.getString("subscriber_phone_number"),
					rs.getString("subscriber_email")));
		}
		return subscribers;
	}

	
	/**
	 * Adds a new subscriber to the database if a subscriber with the same username does not already exist.
	 * 
	 * This method first checks if a subscriber with the given username already exists in the database. If it does,
	 * an exception is thrown. If not, the method inserts a new subscriber into the database with the provided details.
	 * 
	 * @param subscriber The `Subscriber` object containing the subscriber's details to be added.
	 * @throws SQLException If the username already exists or if there is any error during the database operations.
	 */
	public static void addSubscriberIfNotExists(Subscriber subscriber) throws SQLException {
		// בדיקה אם המנוי קיים לפי username
		String query = "SELECT COUNT(*) FROM subscriber WHERE BINARY username = ?";
		PreparedStatement pstmtCheck = connection.prepareStatement(query);
		pstmtCheck.setString(1, subscriber.getUsername());
		ResultSet rs = pstmtCheck.executeQuery();

		if (rs.next() && rs.getInt(1) > 0) {
			throw new SQLException("Username subscriber already exists");
		}
		// הוספת המנוי החדש לטבלה המאוחדת
		String queryInsert = "INSERT INTO subscriber (subscriber_id, subscriber_name, username, subscriber_email, subscriber_phone_number, password, detailed_subscription_history, status,registerDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?,NOW())";
		try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
			pstmt.setInt(1, subscriber.getSubscriberId());
			pstmt.setString(2, subscriber.getSubscriberName());
			pstmt.setString(3, subscriber.getUsername());
			pstmt.setString(4, subscriber.getSubscriberEmail());
			pstmt.setString(5, subscriber.getSubscriberPhoneNumber());
			pstmt.setString(6, subscriber.getPassword());
			pstmt.setInt(7, subscriber.getDetailedSubscriptionHistory());
			pstmt.setString(8, "active");
			// ערך ברירת מחדל לסטטוס חדש

			pstmt.executeUpdate();
		}
	}


	
	/**
	 * Validates whether a subscriber exists with the given subscriber ID, username, and password.
	 * 
	 * This method checks if a subscriber exists in the database with the provided subscriber ID, username, and password.
	 * If a matching record is found, the method returns `true`; otherwise, it returns `false`.
	 * 
	 * @param subscriberId The ID of the subscriber to validate.
	 * @param username The username of the subscriber to validate.
	 * @param password The password of the subscriber to validate.
	 * @return `true` if a subscriber with the provided ID, username, and password exists, `false` otherwise.
	 * @throws SQLException If there is an error during the database operation.
	 */
	public static boolean validateSubscriber(int subscriberId, String username, String password) throws SQLException {
		String query = "SELECT * FROM subscriber WHERE BINARY username = ? AND BINARY password = ? AND subscriber_id=?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setInt(3, subscriberId);

			ResultSet rs = stmt.executeQuery();
			return rs.next(); // מחזיר true אם נמצאה התאמה, false אם לא
		}
	}

	/**
	 * Validates whether a subscriber exists with the given subscriber ID and username for password reset.
	 * 
	 * This method checks if a subscriber exists in the database with the provided subscriber ID and username.
	 * It is typically used to verify if the subscriber is eligible for a password reset.
	 * 
	 * @param subscriberId The ID of the subscriber to validate.
	 * @param username The username of the subscriber to validate.
	 * @return `true` if a subscriber with the provided ID and username exists, `false` otherwise.
	 * @throws SQLException If there is an error during the database operation.
	 */
	public static boolean validateSubscriberReset(int subscriberId, String username) throws SQLException {
		String query = "SELECT * FROM subscriber WHERE subscriber_id = ? AND BINARY username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);
			stmt.setString(2, username);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	
	
	/**
	 * Updates the password of a subscriber and logs the change in the subscriber's history.
	 * 
	 * This method retrieves the old password from the database, updates the subscriber's password with the
	 * provided new password, and records the password change in the `subscriber_changes_history` table.
	 * 
	 * @param subscriberId The ID of the subscriber whose password is to be updated.
	 * @param username The username of the subscriber whose password is to be updated.
	 * @param newPassword The new password to set for the subscriber.
	 * @return `true` if the password update and history insertion were successful, `false` otherwise.
	 * @throws SQLException If there is an error during the database operation.
	 */
	public static boolean updateSubscriberPassword(int subscriberId, String username, String newPassword)
			throws SQLException {
		// שליפת הסיסמה הישנה מהטבלה
		String selectQuery = "SELECT password FROM subscriber WHERE subscriber_id = ?";
		String oldPassword = null;

		try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
			selectStmt.setInt(1, subscriberId);
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				oldPassword = rs.getString("password"); // שמירת הסיסמה הישנה
			}
		}

		// עדכון הסיסמה בטבלת המנויים
		String updateQuery = "UPDATE subscriber SET password = ? WHERE subscriber_id = ? AND username = ?";
		try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
			updateStmt.setString(1, newPassword);
			updateStmt.setInt(2, subscriberId);
			updateStmt.setString(3, username);
			int affectedRows = updateStmt.executeUpdate(); // שימוש ב-executeUpdate במקום executeQuery

			if (affectedRows > 0) {
				// הוספת ההיסטוריה לטבלת subscriber_changes_history
				String insertQuery = "INSERT INTO subscriber_changes_history (subscriber_id, change_type, old_value, new_value, change_date) VALUES (?, ?, ?, ?, NOW())";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
					insertStmt.setInt(1, subscriberId);
					insertStmt.setString(2, "password");
					insertStmt.setString(3, oldPassword);
					insertStmt.setString(4, newPassword);
					insertStmt.executeUpdate();

					return true; // מחזיר true אם שני העדכונים הצליחו
				}
			}
		}

		return false; // מחזיר false אם לא התרחש עדכון
	}

	
	
	/**
	 * Validates whether a subscriber's status is "active".
	 * 
	 * This method checks the `status` of a subscriber in the database by their subscriber ID. 
	 * It returns `true` if the status is "active", otherwise returns `false`.
	 * 
	 * @param subscriberId The ID of the subscriber whose status is to be checked.
	 * @return `true` if the subscriber's status is "active", `false` otherwise.
	 * @throws SQLException If there is an error during the database operation.
	 */
	public static boolean validateSubscriberStatus(int subscriberId) throws SQLException {
		String query = "SELECT status FROM Subscriber WHERE subscriber_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, subscriberId);

		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			String status = rs.getString("status");
			rs.close();
			pstmt.close();
			return status.equals("active");
		}

		rs.close();
		pstmt.close();
		return false;
	}

	
	/**
	 * Checks whether a subscriber exists in the database by their subscriber ID.
	 * 
	 * This method queries the database to check if a subscriber with the given ID exists.
	 * It returns `true` if a subscriber with the specified ID is found, otherwise `false`.
	 * 
	 * @param subscriberId The ID of the subscriber to check for existence.
	 * @return `true` if the subscriber exists in the database, `false` otherwise.
	 * @throws SQLException If there is an error during the database operation.
	 */
	public static boolean doesSubscriberExist(int subscriberId) throws SQLException {
		String query = "SELECT subscriber_id FROM Subscriber WHERE subscriber_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, subscriberId);

		ResultSet rs = pstmt.executeQuery();
		boolean exists = rs.next();

		rs.close();
		pstmt.close();
		return exists;
	}
	///////////////////////////// SUBSCRIBER///////////SUBSCRIBER////////////SUBSCRIBER///////////////////
	///////////////////////////// SUBSCRIBER///////////SUBSCRIBER////////////SUBSCRIBER///////////////////

	////////////// ORDER///////////ORDER/////////////////ORDER//////////////ORDER/////////////ORDER////
	////////////// ORDER///////////ORDER/////////////////ORDER//////////////ORDER/////////////ORDER////
	
	
	
	/**
	 * Checks if a subscriber has already ordered a specific book.
	 * 
	 * This method queries the database to check if a specific subscriber has already placed an order
	 * for a specific book. It returns `true` if the order exists, otherwise `false`.
	 * 
	 * @param subscriberId The ID of the subscriber.
	 * @param bookId The ID of the book.
	 * @return `true` if the subscriber has ordered the book, `false` otherwise.
	 * @throws SQLException If there is an error during the database operation.
	 */
	public static boolean checkExistanceOrder(int subscriberId, int bookId) throws SQLException {
		String query = "SELECT COUNT(*) FROM orderedbooks WHERE subscriber_Id = ? AND book_Id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, subscriberId);
		pstmt.setInt(2, bookId);

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		rs.close();
		pstmt.close();

		return count > 0;
	}

	
	
	/**
	 * Completes an order for a specific subscriber and book.
	 * 
	 * This method marks an order as fulfilled by performing the following operations:
	 * - Deletes the order from the `orderedbooks` table.
	 * - Updates the order status in the `orderedbookshistory` table to 'Fulfilled'.
	 * - Decreases the `numberOfOrderedCopies` of the specified book in the `Books` table by 1.
	 * If all operations are successful, the method returns `true`; otherwise, it returns `false`.
	 * 
	 * @param subscriberId The ID of the subscriber who made the order.
	 * @param bookId The ID of the book that was ordered.
	 * @return `true` if the order was successfully completed; `false` otherwise.
	 * @throws SQLException If there is an error during any of the database operations.
	 */
	public static boolean completeOrder(int subscriberId, int bookId) throws SQLException {
		String deleteQuery = "DELETE FROM orderedbooks WHERE subscriber_id = ? AND book_id = ?";
		String updateOrderStatusQuery = "UPDATE orderedbookshistory SET order_status = 'Fulfilled' WHERE subscriber_id = ? AND book_id=?";
		String updateOrderedCopiesQuery = "UPDATE Books SET numberOfOrderedCopies = numberOfOrderedCopies - 1 WHERE book_id = ?";

		PreparedStatement pstmt1 = connection.prepareStatement(deleteQuery);
		pstmt1.setInt(1, subscriberId);
		pstmt1.setInt(2, bookId);

		PreparedStatement pstmt2 = connection.prepareStatement(updateOrderStatusQuery);
		pstmt2.setInt(1, subscriberId);
		pstmt2.setInt(2, bookId);

		PreparedStatement pstmt3 = connection.prepareStatement(updateOrderedCopiesQuery);
		pstmt3.setInt(1, bookId);

		int result1 = pstmt1.executeUpdate();
		int result2 = pstmt2.executeUpdate();
		int result3 = pstmt3.executeUpdate();

		pstmt1.close();
		pstmt2.close();
		pstmt3.close();

		return (result1 > 0 && result2 > 0 && result3 > 0);
	}

	
	
	/**
	 * Saves a new order and updates the number of ordered copies for a specific book.
	 * 
	 * This method performs the following operations:
	 * - Checks if the order already exists for the given subscriber and book.
	 * - If the order does not exist, inserts a new record into the `OrderedBooks` table.
	 * - Inserts a new record into the `orderedbookshistory` table to track the order history.
	 * - Updates the `Books` table to increment the number of ordered copies for the specified book.
	 * 
	 * @param orderedBook The ordered book object containing information about the order.
	 * @return `true` if the order was successfully saved and the book copies were updated; `false` otherwise.
	 * @throws SQLException If there is an error during any of the database operations.
	 */
	public static boolean saveOrderAndUpdateCopies(OrderedBook orderedBook) throws SQLException {
		String checkQuery = "SELECT COUNT(*) FROM OrderedBooks WHERE subscriber_id = ? AND book_id = ?";
		PreparedStatement pstmtCheck = connection.prepareStatement(checkQuery);
		pstmtCheck.setInt(1, orderedBook.getSubscriberId());
		pstmtCheck.setInt(2, orderedBook.getBookId());

		ResultSet rs = pstmtCheck.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		int result1 = 0, result2 = 0, result3 = 0;

		if (count == 0) {
			String insertQuery = "INSERT INTO OrderedBooks (subscriber_id, book_id, book_name,Order_date) VALUES (?, ?, ?,NOW())";
			String insertHistoryQuery = "INSERT INTO orderedbookshistory (subscriber_id,book_id,order_date,order_status) VALUES (?, ?, NOW(), ?)";
			String updateQuery = "UPDATE books SET numberOfOrderedCopies = numberOfOrderedCopies +1 WHERE book_id = ?";

			PreparedStatement pstmt1 = connection.prepareStatement(insertQuery);
			pstmt1.setInt(1, orderedBook.getSubscriberId());
			pstmt1.setInt(2, orderedBook.getBookId());
			pstmt1.setString(3, orderedBook.getBookName());

			PreparedStatement pstmt2 = connection.prepareStatement(insertHistoryQuery);
			pstmt2.setInt(1, orderedBook.getSubscriberId());
			pstmt2.setInt(2, orderedBook.getBookId());
			// pstmt2.setString(3, formattedDate);
			pstmt2.setString(3, orderedBook.getOrderStatus());

			PreparedStatement pstmt3 = connection.prepareStatement(updateQuery);
			pstmt3.setInt(1, orderedBook.getBookId());

			result1 = pstmt1.executeUpdate();
			result2 = pstmt2.executeUpdate();
			result3 = pstmt3.executeUpdate();

			pstmt1.close();
			pstmt2.close();
			pstmt3.close();

		}
		return (result1 > 0 && result2 > 0 && result3 > 0);

	}

	
	
	/**
	 * Updates the start and end order dates for a book after it has been returned by a subscriber.
	 * 
	 * This method performs the following:
	 * - Checks if the book exists in the `borrowedbooks` table with the given return date.
	 * - If found, selects the record with the earliest `order_date` from the `orderedbooks` table where `start_order_date` is NULL.
	 * - If a matching record is found, it updates the `start_order_date` and `end_order_date` in the `orderedbooks` table for the corresponding subscriber.
	 * 
	 * @param bookId The ID of the book to update.
	 * @param returnDate The return date of the book.
	 * @throws SQLException If there is an error during the database operations.
	 */
	public static void UpdateStartAndEndOrder(int bookId, Date returnDate) throws SQLException {
		// Query to check if a record exists in borrowedbooks for the given bookId and
		// returnDate
		String selectBorrowedBooksQuery = "SELECT * FROM borrowedbooks WHERE book_id = ? AND return_date = ?";

		// Query to select the record with the minimum orderDateTime where
		// start_order_date is NULL
		String selectOrderedBooksQuery = "SELECT * FROM orderedbooks WHERE book_id = ? AND start_order_date IS NULL ORDER BY order_date ASC LIMIT 1";

		// Query to update start_order_date and end_order_date
		String updateOrderedBooksQuery = "UPDATE orderedbooks SET start_order_date = NOW(), end_order_date = DATE_ADD(NOW(), INTERVAL 2 DAY) WHERE book_id = ? AND subscriber_id = ?";

		try (PreparedStatement pstmtSelectBorrowed = connection.prepareStatement(selectBorrowedBooksQuery)) {
			pstmtSelectBorrowed.setInt(1, bookId);
			pstmtSelectBorrowed.setDate(2, returnDate);

			try (ResultSet rsBorrowed = pstmtSelectBorrowed.executeQuery()) {
				if (rsBorrowed.next()) {
					// Record found in borrowedbooks
					try (PreparedStatement pstmtSelectOrdered = connection.prepareStatement(selectOrderedBooksQuery)) {
						pstmtSelectOrdered.setInt(1, bookId);

						try (ResultSet rsOrdered = pstmtSelectOrdered.executeQuery()) {
							if (rsOrdered.next()) {
								// Matching record found in orderedbooks
								int subscriberID = rsOrdered.getInt("subscriber_id");

								try (PreparedStatement pstmtUpdate = connection
										.prepareStatement(updateOrderedBooksQuery)) {

									pstmtUpdate.setInt(1, bookId);
									pstmtUpdate.setInt(2, subscriberID);

									pstmtUpdate.executeUpdate();
								}
							}
						}
					}
				}
			}
		}
	}

	
	
	/**
	 * Deletes orders from the 'orderedbooks' table where the 'end_order_date' has passed,
	 * and updates related tables such as 'orderedbookshistory' and 'books'.
	 * 
	 * The method performs the following tasks:
	 * - Fetches subscriber IDs and book IDs from 'orderedbooks' where 'end_order_date' is passed.
	 * - Updates the order status to 'Cancelled' in 'orderedbookshistory'.
	 * - Decreases the number of ordered copies in the 'books' table.
	 * - Deletes the expired order from the 'orderedbooks' table.
	 * 
	 * @param connection The active database connection used to execute the queries.
	 * @throws SQLException if a database error occurs while executing the queries.
	 */
	public static void deleteOrder(Connection connection) throws SQLException {
		// Query to fetch all subscriber IDs and book IDs when end_order_date has passed
		String getIdQuery = "SELECT subscriber_id, book_id FROM orderedbooks WHERE end_order_date <= NOW()";

		// Query to update the order status
		String updateOrderStatus = "UPDATE orderedbookshistory SET order_status = 'Cancelled' WHERE subscriber_id = ? AND book_id=?";

		// Query to update the number of ordered copies
		String updateOrderesCopy = "UPDATE books SET numberOfOrderedCopies = numberOfOrderedCopies - 1 WHERE book_id = ?";

		// Query to delete rows from orderedbooks
		String deleteQuery = "DELETE FROM orderedbooks WHERE end_order_date <= NOW()";

		try (PreparedStatement getIdStmt = connection.prepareStatement(getIdQuery);
				PreparedStatement updateStmt = connection.prepareStatement(updateOrderStatus);
				PreparedStatement updateCopiesStmt = connection.prepareStatement(updateOrderesCopy);
				PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
			// Fetch subscriber IDs and book IDs
			ResultSet resultSet = getIdStmt.executeQuery();

			while (resultSet.next()) {
				int subscriberId = resultSet.getInt("subscriber_id");
				int bookId = resultSet.getInt("book_id");

				String selectOrderedBooksQuery = "SELECT * FROM orderedbooks WHERE book_id = ? AND start_order_date IS NULL ORDER BY order_date ASC LIMIT 1";

				// Query to update start_order_date and end_order_date
				String updateOrderedBooksQuery = "UPDATE orderedbooks SET start_order_date = ?, end_order_date = DATE_ADD(?, INTERVAL 2 DAY) WHERE book_id = ? AND subscriber_id = ?";

				try (PreparedStatement pstmtSelectOrdered = connection.prepareStatement(selectOrderedBooksQuery)) {
					pstmtSelectOrdered.setInt(1, bookId);

					try (ResultSet rsOrdered = pstmtSelectOrdered.executeQuery()) {
						if (rsOrdered.next()) {
							// Matching record found in orderedbooks
							int subscriberIDToUpdateStartDate = rsOrdered.getInt("subscriber_id");
							Date actualReturnDate = new Date(System.currentTimeMillis()); // Current date

							try (PreparedStatement pstmtUpdate = connection.prepareStatement(updateOrderedBooksQuery)) {
								pstmtUpdate.setDate(1, actualReturnDate); // Set start_order_date
								pstmtUpdate.setDate(2, actualReturnDate); // Used in DATE_ADD
								pstmtUpdate.setInt(3, bookId);
								pstmtUpdate.setInt(4, subscriberIDToUpdateStartDate);

								pstmtUpdate.executeUpdate();
							}
						}
					}
				}

				// Update order status
				updateStmt.setInt(1, subscriberId);
				updateStmt.setInt(2, bookId);
				updateStmt.executeUpdate();

				// Update number of ordered copies
				updateCopiesStmt.setInt(1, bookId);
				updateCopiesStmt.executeUpdate();
			}

			// Delete rows from orderedbooks
			deleteStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	
	/**
	 * Checks if there are any pending orders for a specific book.
	 * This method queries the 'OrderedBooks' table to check if the given book ID has any pending orders.
	 * It returns true if there are pending orders for the specified book, and false otherwise.
	 * 
	 * @param bookId The ID of the book to check for pending orders.
	 * @return true if the book has pending orders, false otherwise.
	 * @throws SQLException if a database error occurs while executing the query.
	 */
	public static boolean hasBookPendingOrders(int bookId) throws SQLException {
		String query = "SELECT COUNT(*) FROM OrderedBooks WHERE book_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, bookId);

		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		rs.close();
		pstmt.close();

		return count > 0;
	}

///////////////////////////21.1.25
	
	/**
	 * Retrieves a list of ordered books for a specific subscriber.
	 * This method queries the 'orderedbookshistory' and 'books' tables to fetch the ordered books
	 * associated with the given subscriber ID. It returns a list of `OrderedBooksHistory` objects
	 * that contain information about each book, such as its name, author, and order status.
	 *
	 * @param subscriberId The ID of the subscriber whose ordered books are to be fetched.
	 * @return A list of `OrderedBooksHistory` objects representing the ordered books for the given subscriber.
	 */
	public static ArrayList<OrderedBooksHistory> getOrderedBooksBySubscriberId(int subscriberId) {
		ArrayList<OrderedBooksHistory> orderedBooks = new ArrayList<>();
		String query = "SELECT o.*, b.book_name, b.book_author AS author " + "FROM orderedbookshistory o "
				+ "JOIN books b ON o.book_id = b.book_id " + "WHERE o.subscriber_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Calendar cal = Calendar.getInstance();
				java.sql.Date orderDate = rs.getDate("order_date");
				if (orderDate != null) {
					cal.setTime(orderDate);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					orderDate = new java.sql.Date(cal.getTimeInMillis());
				}
				OrderedBooksHistory orderedBook = new OrderedBooksHistory(rs.getInt("order_id"), subscriberId,
						rs.getInt("book_id"), rs.getString("book_name"), // הוסף שם ספר
						rs.getString("author"), // הוסף מחבר
						orderDate, rs.getString("order_status"));
				orderedBooks.add(orderedBook);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderedBooks;
	}

//////////////////////////////////////21.1.25
	
	/**
	 * Checks if a subscriber has borrowed a specific book.
	 *
	 * @param subscriberID The ID of the subscriber.
	 * @param bookID The ID of the book.
	 * @return {@code true} if the subscriber has borrowed the book; {@code false} otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public static boolean isSubscriberHasBorrowOfTheBook(int subscriberID, int bookID) throws SQLException {
		String query = "SELECT COUNT(*) FROM borrowedbooks WHERE book_id = ? AND subscriber_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, bookID);
			pstmt.setInt(2, subscriberID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0; // Return true if count > 0
			}
		}
		return false;
	}

	
	/**
	 * Checks if a subscriber can pick up a book based on the order status.
	 * 
	 * This method checks whether the book is available for pickup by verifying if 
	 * the `start_order_date` in the `orderedbooks` table is set for the given 
	 * subscriber and book combination. If the `start_order_date` is non-null, 
	 * it indicates that the book can be picked up.
	 * 
	 * @param subscriberId The ID of the subscriber.
	 * @param bookId The ID of the book.
	 * @return {@code true} if the book can be picked up (i.e., start_order_date is not null);
	 *         {@code false} if the book cannot be picked up (i.e., start_order_date is null).
	 * @throws SQLException if a database access error occurs.
	 */
	public static boolean doesSubscriberCanPickUPBook(int subscriberId, int bookId) throws SQLException {
		String query = "SELECT start_order_date FROM orderedbooks WHERE subscriber_id = ? AND book_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setInt(1, subscriberId);
			pstmt.setInt(2, bookId);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Date startOrderDate = rs.getDate("start_order_date");
				return startOrderDate != null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * Retrieves a list of ordered books for a specific subscriber by their subscriber ID.
	 * The method retrieves details such as book ID, book name, order date, first return date,
	 * start order date, and end order date.
	 * 
	 * @param subscriberId The ID of the subscriber whose ordered books are to be fetched.
	 * @return A list of {@link OrderedBook} objects representing the ordered books for the subscriber.
	 * @throws SQLException if a database access error occurs.
	 */
	public static ArrayList<OrderedBook> getOrderedBookBySubscriberIdToReaderCARD(int subscriberId) throws SQLException 
	{
		System.out.println("Executing DB query for subscriber ID: " + subscriberId);
		ArrayList<OrderedBook> orderedBooks = new ArrayList<>();

		String query = "SELECT o.subscriber_id, o.book_id, o.book_name, o.order_date, "
				+ "b.firstReturnDate, o.start_order_date, o.end_order_date " + "FROM orderedbooks o "
				+ "JOIN books b ON o.book_id = b.book_id " + "WHERE o.subscriber_id = ?";

		System.out.println("SQL Query: " + query);

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, subscriberId);
			System.out.println("Executing query with subscriber ID: " + subscriberId);

			try (ResultSet rs = pstmt.executeQuery()) 
			{
				while (rs.next()) {
					System.out.println("Found order - Book ID: " + rs.getInt("book_id") + ", Book Name: "
							+ rs.getString("book_name"));
		            // עבור order_date
		            Calendar cal = Calendar.getInstance();
		            cal.setTimeInMillis(rs.getTimestamp("order_date").getTime());
		            cal.add(Calendar.HOUR_OF_DAY, 3);
		            cal.add(Calendar.MINUTE, 30);
		            LocalDateTime adjustedOrderDate = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
		            // עבור end_order_date
		            LocalDateTime adjustedEndOrderDate = null;
		            if (rs.getTimestamp("end_order_date") != null) {
		                cal.setTimeInMillis(rs.getTimestamp("end_order_date").getTime());
		                cal.add(Calendar.HOUR_OF_DAY, 3);
		                cal.add(Calendar.MINUTE, 30);
		                adjustedEndOrderDate = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
		            }
		          
					OrderedBook orderedBook = new OrderedBook(rs.getInt("subscriber_id"), rs.getInt("book_id"),
							rs.getString("book_name"), adjustedOrderDate,
							rs.getObject("firstReturnDate", LocalDate.class),
							rs.getObject("start_order_date", LocalDate.class),
							adjustedEndOrderDate);
					orderedBooks.add(orderedBook);
				}
				System.out.println("Total orders found: " + orderedBooks.size());
			}
		}
		return orderedBooks;
	}

	////////////// ORDER///////////ORDER/////////////////ORDER//////////////ORDER/////////////ORDER////
	////////////// ORDER///////////ORDER/////////////////ORDER//////////////ORDER/////////////ORDER////

////////////////////////////Librarian////////////Librarian/////////////////Librarian/////////////////Librarian
////////////////////////////Librarian////////////Librarian/////////////////Librarian/////////////////Librarian
////////////////////////////Librarian////////////Librarian/////////////////Librarian/////////////////Librarian

	
	
	/**
	 * Updates the password for a librarian identified by their username.
	 * The password is updated in the "librarians" table where the username matches.
	 * 
	 * @param username The username of the librarian whose password is to be updated.
	 * @param newPassword The new password to set for the librarian.
	 * @throws SQLException if a database access error occurs.
	 */
	public static void updateLibrarianPassword(String username, String newPassword) throws SQLException {
		String query = "UPDATE librarians SET password = ? WHERE BINARY username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, newPassword);
			stmt.setString(2, username);
			stmt.executeUpdate();
		}
	}

	
	
	/**
	 * Adds a new librarian to the "Librarians" table in the database.
	 * First, it checks if the username already exists. If it does, an exception is thrown.
	 * If the username is unique, the librarian's details are inserted into the table.
	 * 
	 * @param librarian The librarian object containing the details to be added.
	 * @throws SQLException if a database access error occurs or if the username already exists.
	 */
	public static void addLibrarian(Librarian librarian) throws SQLException {
		// בדיקה אם שם המשתמש כבר קיים
		String checkQuery = "SELECT * FROM Librarians WHERE BINARY Username = ?";
		PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
		checkStmt.setString(1, librarian.getUsername());
		ResultSet rs = checkStmt.executeQuery();

		if (rs.next()) {
			throw new SQLException("Username already exists");
		}

		String query = "INSERT INTO Librarians (LibrarianID, Username, Password, FirstName, LastName, Email) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(query);

		stmt.setInt(1, librarian.getLibrarianId());
		stmt.setString(2, librarian.getUsername());
		stmt.setString(3, librarian.getPassword());
		stmt.setString(4, librarian.getFirstName());
		stmt.setString(5, librarian.getLastName());
		stmt.setString(6, librarian.getEmail());

		stmt.executeUpdate();

		stmt.close();
		checkStmt.close();
		rs.close();
	}
	
	
	

	/**
	 * Validates the librarian's reset request by checking if the provided librarian ID
	 * matches the one associated with the provided username.
	 * 
	 * @param librarianId The ID of the librarian.
	 * @param username The username of the librarian.
	 * @return true if the librarian ID and username match; false otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public static boolean validateLibrarianReset(int librarianId, String username) throws SQLException {
		String query = "SELECT * FROM Librarians WHERE LibrarianID = ? AND BINARY Username = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, librarianId);
		stmt.setString(2, username);

		ResultSet rs = stmt.executeQuery();
		boolean isValid = rs.next();

		rs.close();
		stmt.close();

		return isValid;
	}

	
	
	/**
	 * Retrieves the password of a librarian based on their username.
	 * 
	 * @param username The username of the librarian.
	 * @return The password of the librarian, or null if the username does not exist.
	 * @throws SQLException if a database access error occurs.
	 */
	public static String getLibrarianPassword(String username) throws SQLException {
		String query = "SELECT Password FROM Librarians WHERE BINARY Username = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, username);

		ResultSet rs = stmt.executeQuery();
		String password = null;

		if (rs.next()) {
			password = rs.getString("Password");
		}

		rs.close();
		stmt.close();

		return password;
	}

	/**
	 * Validates the librarian's username and password.
	 * If the credentials are valid, it returns the librarian's full name (FirstName + LastName).
	 * Otherwise, it returns null.
	 * 
	 * @param username The username of the librarian.
	 * @param password The password of the librarian.
	 * @return The full name of the librarian (FirstName + LastName) if valid credentials, or null if invalid.
	 * @throws SQLException if a database access error occurs.
	 */
	public static String validateLibrarian(String username, String password) throws SQLException {
		// Adjusted query to select FirstName and LastName
		String query = "SELECT FirstName, LastName FROM Librarians WHERE BINARY Username = ? AND BINARY Password = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				// Concatenate FirstName and LastName
				String firstName = rs.getString("FirstName");
				String lastName = rs.getString("LastName");
				return firstName + " " + lastName; // Return full name
			} else {
				return null; // Return null if no match is found
			}
		}
	}

	
	/**
	 * Inserts a message into the LibrarianMessages table to log information about a loan extension.
	 * 
	 * @param request The ExtendLoanRequest containing details about the loan extension, such as 
	 *                book ID, book name, subscriber ID, and extension date.
	 * @throws SQLException if a database access error occurs.
	 */
	public static void insertLibrarianMessage(ExtendLoanRequest request) throws SQLException {
		String insertMessage = "INSERT INTO LibrarianMessages (message_text, message_date) VALUES (?, ?)";
		String messageText = String.format("Loan extended for Book ID: %d (%s) by Subscriber ID: %d",
				request.getBookId(), request.getBookName(), request.getSubscriberId());

		PreparedStatement pstmt = connection.prepareStatement(insertMessage);
		pstmt.setString(1, messageText);
		pstmt.setDate(2, request.getExtensionDate());
		pstmt.executeUpdate();
		pstmt.close();
	}
////////////////////////////Librarian////////////Librarian/////////////////Librarian/////////////////Librarian
////////////////////////////Librarian////////////Librarian/////////////////Librarian/////////////////Librarian

	///////////////// RETURN////////////////////RETURN//////////////////RETURN//////////////RETURN/////////
	///////////////// RETURN////////////////////RETURN//////////////////RETURN//////////////RETURN/////////
	
	/**
	 * Updates the first return date for a book in the `books` table based on the orders and return dates 
	 * from the `borrowedbooks` table.
	 *
	 * The method retrieves the first return date and the number of ordered copies for a specific book, 
	 * and then updates the `firstReturnDate` in the `books` table using the earliest return date of a 
	 * borrowed book for that specific book.
	 *
	 * @param OrderedBook The `OrderedBook` object containing the book ID and subscriber ID.
	 * @throws SQLException If a database access error occurs.
	 */
	public static void updateFirstReturnDateFromOrder(OrderedBook OrderedBook) throws SQLException {
		String getFirstReturnDateToUpdateThedateForOrder = "SELECT firstReturnDate FROM books WHERE book_id = ?";
		String getOrdersNum = "SELECT numberOfOrderedCopies FROM books WHERE book_id = ?";
		String getDate = "SELECT return_date FROM borrowedbooks WHERE book_id = ? ORDER BY return_date ASC LIMIT 1 OFFSET ?";
		String updateDate = "UPDATE books SET firstReturnDate = ? WHERE book_id = ?";

		int bookId = OrderedBook.getBookId();
		int subscriberId = OrderedBook.getSubscriberId();
		int numOfOrders = 0;
		Date returnDate = null;
		Date getFirstReturnDateToUpdatePickUpColumn = null;

		// get the First Return Date To Update PickUp Column
		try (PreparedStatement pstmt1 = connection.prepareStatement(getFirstReturnDateToUpdateThedateForOrder)) {
			pstmt1.setInt(1, bookId);
			try (ResultSet rs1 = pstmt1.executeQuery()) {
				if (rs1.next()) {

					getFirstReturnDateToUpdatePickUpColumn = rs1.getDate(1);
					System.out.println(
							"First Return Date to update pickup column: " + getFirstReturnDateToUpdatePickUpColumn);

				}
			}
		}

		// Fetch the number of orders
		try (PreparedStatement pstmt3 = connection.prepareStatement(getOrdersNum)) {
			pstmt3.setInt(1, bookId);
			try (ResultSet rs = pstmt3.executeQuery()) {
				if (rs.next()) {
					numOfOrders = rs.getInt(1);
				}
			}
		}

		// Fetch the return date
		if (numOfOrders > 0) { // Only proceed if there are orders
			try (PreparedStatement pstmt4 = connection.prepareStatement(getDate)) {
				pstmt4.setInt(1, bookId);
				pstmt4.setInt(2, numOfOrders); // Offset is zero-based
				try (ResultSet rs2 = pstmt4.executeQuery()) {
					if (rs2.next()) {
						returnDate = rs2.getDate(1);

					}
				}
			}
		}
		if (returnDate != null) {
			// Update the first return date in the books table
			try (PreparedStatement pstmt5 = connection.prepareStatement(updateDate)) {
				pstmt5.setDate(1, returnDate);
				pstmt5.setInt(2, bookId);
				pstmt5.executeUpdate();

			}
		}
	}

	/**
	 * Updates the first return date for a book in the `books` table, only if the book has no existing orders.
	 * If the book has no orders, the method retrieves the earliest return date from the `borrowedbooks` table 
	 * and updates the `firstReturnDate` in the `books` table.
	 *
	 * @param returnBook The `ReturnBook` object containing the book ID.
	 * @throws SQLException If a database access error occurs.
	 */
	public static void updateFirstReturnDate(ReturnBook returnBook) throws SQLException {
		String query1 = "SELECT numberOfOrderedCopies FROM books WHERE book_id = ?";
		String firstDateQuery = "SELECT MIN(return_date) FROM borrowedbooks WHERE book_id = ?";
		String updateQuery2 = "UPDATE books SET firstReturnDate = ? WHERE book_id = ?";

		try {
			// Check the number of orders for the book
			try (PreparedStatement pstmt = connection.prepareStatement(query1)) {
				pstmt.setInt(1, returnBook.getBookId());
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					int numOfOrders = rs.getInt("numberOfOrderedCopies");

					if (numOfOrders > 0) {
						// Exit if there are existing orders
						return;
					} else {
						// Get the earliest return date
						Date firstReturnDate = null;
						try (PreparedStatement pstmt2 = connection.prepareStatement(firstDateQuery)) {
							pstmt2.setInt(1, returnBook.getBookId());
							ResultSet rs2 = pstmt2.executeQuery();

							if (rs2.next()) {
								// first column in ResuleSet
								firstReturnDate = rs2.getDate(1);
							}
						}

						// Update the first return date in the books table
						try (PreparedStatement pstmt3 = connection.prepareStatement(updateQuery2)) {
							pstmt3.setDate(1, firstReturnDate);
							pstmt3.setInt(2, returnBook.getBookId());
							pstmt3.executeUpdate();
						}

					}
				}
			}
		} catch (SQLException e) {
			// remember to delete the message after that
			System.err.println("Error while updating the first return date: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handles overdue books by checking if the return date exceeds a one-week overdue period.
	 * If the book is overdue, the subscriber's account is frozen for one month.
	 * This process includes updating the subscriber's status, inserting records into the 
	 * frozen accounts table, and logging the event in the frozen account history.
	 *
	 * @param returnBook The `ReturnBook` object containing the subscriber's ID and the book's ID.
	 * @throws SQLException If a database access error occurs during any SQL operation.
	 */
	public static void handleOverdue(ReturnBook returnBook) throws SQLException {
		// Query to check if the return is late
		String queryReturnDate = "SELECT return_date FROM borrowedbooks WHERE subscriber_id = ? AND book_id = ?";
		String freezeSubscriberQuery = "UPDATE subscriber SET status = 'frozen' WHERE subscriber_id = ?";

		connection.setAutoCommit(false); // Start transaction

		try (PreparedStatement checkStmt = connection.prepareStatement(queryReturnDate);
				PreparedStatement freezeStmt = connection.prepareStatement(freezeSubscriberQuery)) {

			checkStmt.setInt(1, returnBook.getSubscriberId());
			checkStmt.setInt(2, returnBook.getBookId());

			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {
				java.sql.Date returnDateSQL = rs.getDate("return_date");
				if (returnDateSQL != null) { // Ensure return_date is not null
					LocalDate returnDate = returnDateSQL.toLocalDate();
					LocalDate currentDate = LocalDate.now();
					// Check if the book is overdue by more than a week
					if (currentDate.isAfter(returnDate.plusWeeks(1))) {
						freezeStmt.setInt(1, returnBook.getSubscriberId());
						freezeStmt.executeUpdate();

						// insert into frozen table
						String queryInsert = "INSERT INTO frozenaccounts (subscriber_id, frozen_start_date, frozen_end_date) VALUES (?, ?, ?)";
						String queryInsert2 = "INSERT INTO frozenaccountshistory (subscriber_id, frozen_start_date, frozen_end_date) VALUES (?, ?, ?)";

						try (PreparedStatement pstmt = connection.prepareStatement(queryInsert);
								PreparedStatement pstmt2 = connection.prepareStatement(queryInsert2);) {
							pstmt.setInt(1, returnBook.getSubscriberId());
							pstmt.setDate(2, java.sql.Date.valueOf(currentDate)); // Convert LocalDate to java.sql.Date
							// Calculate the end frozen date (e.g., 1 month from current date)
							LocalDate endFrozenDate = currentDate.plusMonths(1);
							pstmt.setDate(3, java.sql.Date.valueOf(endFrozenDate));

							// query2
							pstmt2.setInt(1, returnBook.getSubscriberId());
							pstmt2.setDate(2, java.sql.Date.valueOf(currentDate)); // Convert LocalDate to java.sql.Date
							pstmt2.setDate(3, java.sql.Date.valueOf(endFrozenDate));

							// execute
							pstmt.executeUpdate();
							pstmt2.executeUpdate();

						}

					}
				}
			}
			connection.commit(); // Commit the transaction
		} catch (SQLException e) {
			connection.rollback(); // Rollback the transaction on failure
			throw e; // Re-throw the exception for further handling
		} finally {
			connection.setAutoCommit(true); // Reset to default auto-commit mode
		}
	}
	
	///////////////// RETURN////////////////////RETURN//////////////////RETURN//////////////RETURN/////////
	///////////////// RETURN////////////////////RETURN//////////////////RETURN//////////////RETURN/////////

	//////// BORROW/////////////////////BORROW/////////////////BORROW/////////////BORROW/////////////
	//////// BORROW/////////////////////BORROW/////////////////BORROW/////////////BORROW/////////////
	//////// BORROW/////////////////////BORROW/////////////////BORROW/////////////BORROW/////////////

	
	/**
	 * Retrieves the book ID associated with a given barcode. This method queries the
	 * database for a record in the `books` table that matches the provided barcode
	 * and returns the corresponding book ID.
	 *
	 * @param barcode The barcode of the book as a `String`.
	 * @return The `book_id` associated with the given barcode.
	 * @throws SQLException If a database access error occurs or if no book is found
	 *                      for the provided barcode.
	 */

	public static int getBookIdByBarcode(String barcode) throws SQLException {
	    String query = "SELECT book_id FROM books WHERE barcode_number = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, barcode);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("book_id"); // Return the book_id
	            } else {
	                throw new SQLException("Book not found for barcode: " + barcode);
	            }
	        }
	    }
	}
	/**
	 * Saves a borrowed book record and updates related tables. This method first
	 * checks if the book is already borrowed by the subscriber. If not, it inserts
	 * the borrowing record, updates the available copies count, logs the borrowing
	 * history, and updates the first return date.
	 *
	 * @param borrowedBook The `BorrowedBook` object containing borrowing details.
	 * @return true if the borrowing was successful and all updates were committed,
	 *         false if the book is already borrowed by the subscriber or an error
	 *         occurred.
	 * @throws SQLException If a database access error occurs during any SQL
	 *                      operation.
	 */
public static boolean saveBorrowedBookAndUpdateCount(BorrowedBook borrowedBook) throws SQLException {
    int bookId;
    String source = borrowedBook.getSource();

    if ("BOOK_ID".equals(source)) {
        // If the source is BOOK_ID, use the input as book ID
        bookId = borrowedBook.getBookId();
    } else if ("BARCODE".equals(source)) {
        // If the source is BARCODE, fetch the book ID using the barcode
        try {
            bookId = getBookIdByBarcode(String.valueOf(borrowedBook.getBookId()));
            System.out.println("BookIdDB:"+bookId);
        } catch (SQLException e) {
            System.out.println("Error: Barcode not found in the database.");
            return false; // Return false if barcode is not found
        }
    } else {
        System.out.println("Error: Invalid source.");
        return false; // Return false if source is invalid
    }

    // Check if the book is already borrowed by the subscriber
    String checkQuery = "SELECT COUNT(*) FROM borrowedbooks WHERE subscriber_id = ? AND book_id = ? AND return_date >= CURDATE()";
    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
        checkStmt.setInt(1, borrowedBook.getSubscriberId());
        checkStmt.setInt(2, bookId);

        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                // The book is already borrowed by the subscriber
                return false;
            }
        }
    }

    // Define the queries
    String insertQuery = "INSERT INTO BorrowedBooks (subscriber_id, book_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
    String updateQuery = "UPDATE Books SET numberOfAvailableCopies = numberOfAvailableCopies - 1 WHERE book_id = ?";
    String historyQuery = "INSERT INTO BorrowedBooksHistory (subscriber_id, book_id, borrow_date, return_date, actual_return_date, is_late) VALUES (?, ?, ?, ?, NULL, false)";
    String updateFirstReturnDateQuery = "UPDATE books b SET b.firstReturnDate = (SELECT MIN(bb.return_date) FROM borrowedbooks bb WHERE bb.book_id = b.book_id AND bb.return_date >= CURDATE()) WHERE b.book_id = ?";

    // Perform the operations in a transaction
    connection.setAutoCommit(false);
    try (PreparedStatement pstmt1 = connection.prepareStatement(insertQuery);
         PreparedStatement pstmt2 = connection.prepareStatement(updateQuery);
         PreparedStatement pstmt3 = connection.prepareStatement(historyQuery);
         PreparedStatement pstmt4 = connection.prepareStatement(updateFirstReturnDateQuery)) {

        // Set parameters for the insert query
        pstmt1.setInt(1, borrowedBook.getSubscriberId());
        pstmt1.setInt(2, bookId);
        pstmt1.setDate(3, borrowedBook.getBorrowDate());
        pstmt1.setDate(4, borrowedBook.getReturnDate());

        // Set parameters for the update query
        pstmt2.setInt(1, bookId);

        // Set parameters for the history query
        pstmt3.setInt(1, borrowedBook.getSubscriberId());
        pstmt3.setInt(2, bookId);
        pstmt3.setDate(3, borrowedBook.getBorrowDate());
        pstmt3.setDate(4, borrowedBook.getReturnDate());

        // Set parameters for the update first return date query
        pstmt4.setInt(1, bookId);

        // Execute the queries
        int result1 = pstmt1.executeUpdate();
        int result2 = pstmt2.executeUpdate();
        int result3 = pstmt3.executeUpdate();
        int result4 = pstmt4.executeUpdate();

        // Commit the transaction if all queries succeed
        if (result1 > 0 && result2 > 0 && result3 > 0 && result4 > 0) {
            connection.commit();
            return true;
        } else {
            connection.rollback();
            return false;
        }
    } catch (SQLException e) {
        connection.rollback();
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}

	
	/**
	 * Deletes a borrowed book record from the `borrowedbooks` table, updates the actual return date in 
	 * the `borrowedbookshistory` table, and updates the number of available copies of the book in the `books` table.
	 * Additionally, it checks if there are any ordered copies of the book and updates their status accordingly.
	 *
	 * @param returnBook The `ReturnBook` object containing the subscriber ID and book ID of the borrowed book.
	 * @return `true` if all updates and deletions were successful, otherwise `false`.
	 * @throws SQLException If any SQL errors occur during the execution of the queries.
	 */
	public static boolean deleteBorrowedBookAndUpdatenumberOfAvailableCopiesAndActiualReturnDate(ReturnBook returnBook)
			throws SQLException {
		String updateQuery1 = "UPDATE borrowedbookshistory SET actual_return_date = ? WHERE subscriber_id = ? AND book_id = ?";
		String updateQuery2 = "UPDATE books SET numberOfAvailableCopies = numberOfAvailableCopies +1 WHERE book_id = ?";
		String returnDate = "SELECT return_date FROM borrowedbooks WHERE subscriber_id = ? AND book_id = ?";
		String query1 = "SELECT numberOfOrderedCopies FROM books WHERE book_id = ?";		
		
		
		// Update actual Return date
		PreparedStatement pstmt1 = connection.prepareStatement(updateQuery1);
		pstmt1.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
		pstmt1.setInt(2, returnBook.getSubscriberId()); // Assuming getSubscriberID() fetches the subscriber's ID
		pstmt1.setInt(3, returnBook.getBookId()); // Assuming getBookID() fetches the book's ID

		/// update available copies
		PreparedStatement pstmt2 = connection.prepareStatement(updateQuery2);
		pstmt2.setInt(1, returnBook.getBookId());

		try (PreparedStatement pstmt4 = connection.prepareStatement(returnDate)) {
			pstmt4.setInt(1, returnBook.getSubscriberId());
			pstmt4.setInt(2, returnBook.getBookId());
			ResultSet rs = pstmt4.executeQuery();

			if (rs.next()) {
				Date return_date = rs.getDate("return_date");

				try (PreparedStatement pstmt = connection.prepareStatement(query1)) {
					pstmt.setInt(1, returnBook.getBookId());
					ResultSet rs2 = pstmt.executeQuery();

					if (rs2.next()) {
						int numOfOrders = rs2.getInt("numberOfOrderedCopies");

						if (numOfOrders > 0) {

							UpdateStartAndEndOrder(returnBook.getBookId(), return_date);
						} else {
						}
					}
				}
			}
		}

		String deleteQuery = "DELETE FROM borrowedbooks WHERE subscriber_id = ? AND book_id = ?";
		String deleteExtension = "DELETE FROM loanextensions WHERE subscriber_id = ? AND book_id = ? ";

		
		// Delete loan extensions
		PreparedStatement pstmt4 = connection.prepareStatement(deleteExtension);
		pstmt4.setInt(1, returnBook.getSubscriberId());
		pstmt4.setInt(2, returnBook.getBookId());

		// delete borrow
		PreparedStatement pstmt3 = connection.prepareStatement(deleteQuery);
		pstmt3.setInt(1, returnBook.getSubscriberId()); // Assuming getSubscriberID() fetches the subscriber's ID
		pstmt3.setInt(2, returnBook.getBookId()); // Assuming getBookID() fetches the book's ID

		int result1 = pstmt1.executeUpdate();
		int result2 = pstmt2.executeUpdate();
		int result3 = pstmt3.executeUpdate();
		int result4 = pstmt4.executeUpdate();


		pstmt1.close();
		pstmt2.close();
		pstmt3.close();
		pstmt4.close();


		return (result1 > 0 && result2 > 0 && result3 > 0);
	}

	//////// BORROW/////////////////////BORROW/////////////////BORROW/////////////BORROW/////////////
	//////// BORROW/////////////////////BORROW/////////////////BORROW/////////////BORROW/////////////
	//////// BORROW/////////////////////BORROW/////////////////BORROW/////////////BORROW/////////////

	////// EXTEND/////////////EXTEND///////////////EXTEND//////////////////EXTEND////////////EXTEND///////
	////// EXTEND/////////////EXTEND///////////////EXTEND//////////////////EXTEND////////////EXTEND///////
	
	/**
	 * Checks if the return date of a borrowed book is within the extension window (7 days or less before the return date).
	 * This method calculates the difference in days between the current date and the return date for a given book and subscriber.
	 * If the return date is within the next 7 days, the method allows an extension; otherwise, it does not.
	 *
	 * @param bookId The ID of the book to check.
	 * @param subscriberId The ID of the subscriber who borrowed the book.
	 * @return `true` if the return date is within the extension window (7 days or less before the return date), 
	 *         otherwise `false`.
	 * @throws SQLException If any SQL errors occur during the execution of the query.
	 */
	public static boolean isWithinExtensionWindow(int bookId, int subscriberId) throws SQLException {
		String query = "SELECT return_date FROM BorrowedBooks WHERE book_id = ? AND subscriber_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, bookId);
		pstmt.setInt(2, subscriberId);

		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			Date returnDate = rs.getDate("return_date");
			Date currentDate = new java.sql.Date(System.currentTimeMillis());

			// חישוב ההפרש בימים בין תאריך ההחזרה לתאריך הנוכחי
			long diffInMillies = returnDate.getTime() - currentDate.getTime();
			long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

			rs.close();
			pstmt.close();

			System.out.println("Return Timestamp: " + returnDate);
			System.out.println("Current Timestamp: " + currentDate);
			System.out.println("Days Difference: " + diffInDays);

			// מאפשר הארכה אם נשארו 7 ימים או פחות עד למועד ההחזרה
			return diffInDays <= 7 && diffInDays >= 0;
		}

		rs.close();
		pstmt.close();
		return false;
	}

	
	/**
	 * Extends the loan period for a book by 14 days and records the extension in the appropriate tables. 
	 * The method updates the `BorrowedBooks` table with the new return date, records the extension in the 
	 * `LoanExtensions` table, and adds a history entry in the `LoanExtensionHistory` table.
	 * 
	 * @param request The `ExtendLoanRequest` object containing the subscriber's ID, book ID, book name, 
	 *                author, and extension date.
	 * @throws SQLException If any SQL errors occur during the execution of the queries.
	 */
	public static void extendLoan(ExtendLoanRequest request) throws SQLException
	{
		// Get original return date
		String getOriginalDateQuery = "SELECT return_date FROM BorrowedBooks WHERE book_id = ? AND subscriber_id = ?";
		PreparedStatement getDateStmt = connection.prepareStatement(getOriginalDateQuery);
		getDateStmt.setInt(1, request.getBookId());
		getDateStmt.setInt(2, request.getSubscriberId());
		ResultSet dateRs = getDateStmt.executeQuery();
		Date originalReturnDate = null;
		if (dateRs.next()) {
			originalReturnDate = dateRs.getDate("return_date");
		}
		dateRs.close();
		getDateStmt.close();

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null; // New statement for loan_extension_history

		try {
			// Update BorrowedBooks
						String updateBorrowedQuery = "UPDATE BorrowedBooks SET return_date = DATE_ADD(?, INTERVAL 14 DAY),notificationSent=0 "
								+ "WHERE subscriber_id = ? AND book_id = ?";
			pstmt1 = connection.prepareStatement(updateBorrowedQuery);
			pstmt1.setDate(1, originalReturnDate);
			pstmt1.setInt(2, request.getSubscriberId());
			pstmt1.setInt(3, request.getBookId());

			// Update BorrowedBooksHistory
			String updateHistoryQuery = "UPDATE BorrowedBooksHistory SET return_date = DATE_ADD(?, INTERVAL 14 DAY) "
					+ "WHERE subscriber_id = ? AND book_id = ? AND actual_return_date IS NULL";
			pstmt3 = connection.prepareStatement(updateHistoryQuery);
			pstmt3.setDate(1, originalReturnDate);
			pstmt3.setInt(2, request.getSubscriberId());
			pstmt3.setInt(3, request.getBookId());

			String insertExtensionQuery = "INSERT INTO loanextensions "
					+ "(subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, librarian_name) "
					+ "VALUES (?, ?, ?, ?, ?, DATE_ADD(?, INTERVAL 14 DAY), ?, 'approved', 'By Subscriber')";
			pstmt2 = connection.prepareStatement(insertExtensionQuery, Statement.RETURN_GENERATED_KEYS);

			pstmt2.setInt(1, request.getSubscriberId());
			pstmt2.setInt(2, request.getBookId());
			pstmt2.setString(3, request.getBookName());
			pstmt2.setString(4, request.getAuthor());
			pstmt2.setDate(5, originalReturnDate);
			pstmt2.setDate(6, originalReturnDate);
			pstmt2.setDate(7, request.getExtensionDate());

			// Execute updates and insert into loanextensions
			pstmt1.executeUpdate();
			pstmt2.executeUpdate();
			pstmt3.executeUpdate();

			// Get the generated extension_id from loanextensions
			ResultSet generatedKeys = pstmt2.getGeneratedKeys();
			int extensionId = -1;
			if (generatedKeys.next()) {
				extensionId = generatedKeys.getInt(1); // Retrieve the auto-generated extension_id
			}
			generatedKeys.close();

			String insertHistoryQuery = "INSERT INTO loan_extension_history "
					+ "(extension_id, subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, action_taken, librarian_name) "
					+ "VALUES (?, ?, ?, ?, ?, ?, DATE_ADD(?, INTERVAL 14 DAY), ?, 'approved', 'extended', 'By Subscriber')";
			pstmt4 = connection.prepareStatement(insertHistoryQuery);
			pstmt4.setInt(1, extensionId); // Use the generated extension_id
			pstmt4.setInt(2, request.getSubscriberId());
			pstmt4.setInt(3, request.getBookId());
			pstmt4.setString(4, request.getBookName());
			pstmt4.setString(5, request.getAuthor());
			pstmt4.setDate(6, originalReturnDate);
			pstmt4.setDate(7, originalReturnDate);
			pstmt4.setDate(8, request.getExtensionDate());

			pstmt4.executeUpdate(); // Insert the history record
		} finally {
			// Close all statements
			if (pstmt1 != null)
				pstmt1.close();
			if (pstmt2 != null)
				pstmt2.close();
			if (pstmt3 != null)
				pstmt3.close();
			if (pstmt4 != null)
				pstmt4.close();
		}
	}

	
	
	/**
	 * Extends the return date for a borrowed book and records the extension in the database.
	 * This method ensures that the necessary checks are performed before making updates to the database,
	 * such as verifying if the book exists in the relevant tables, updating the borrow record, 
	 * and inserting a loan extension record and its history.
	 * 
	 * @param bookId        The ID of the book being extended.
	 * @param subscriberId  The ID of the subscriber requesting the extension.
	 * @param newReturnDate The new return date for the borrowed book.
	 * @param librarianName The name of the librarian approving the extension.
	 * @return              {@code true} if the extension was successfully applied, {@code false} otherwise.
	 * @throws SQLException If a database error occurs during the operation.
	 */
	public static boolean extendReturnDate(int bookId, int subscriberId, Date newReturnDate, String librarianName)
			throws SQLException {
		// Prepare SQL queries to check if rows exist before attempting any updates
		String checkBorrowedBooksQuery = "SELECT 1 FROM library.borrowedbooks WHERE book_id = ? AND subscriber_id = ?";
		String checkHistoryQuery = "SELECT 1 FROM library.borrowedbookshistory WHERE book_id = ? AND subscriber_id = ? AND actual_return_date IS NULL";
		String checkLoanExtensionQuery = "SELECT 1 FROM library.loanextensions WHERE book_id = ? AND subscriber_id = ?";
		String checkBookQuery = "SELECT book_name, book_author FROM library.books WHERE book_id = ?";

		boolean isBorrowedBookExists = false;
		boolean isHistoryExists = false;
		boolean isLoanExtensionExists = false;
		boolean isBookExists = false;

		String bookName = null;
		String author = null;

		// Check if the row exists in borrowedbooks
		try (PreparedStatement checkStmt1 = connection.prepareStatement(checkBorrowedBooksQuery)) {
			checkStmt1.setInt(1, bookId);
			checkStmt1.setInt(2, subscriberId);
			try (ResultSet rs1 = checkStmt1.executeQuery()) {
				isBorrowedBookExists = rs1.next();
			}
		}

		// Check if the row exists in borrowedbookshistory
		try (PreparedStatement checkStmt2 = connection.prepareStatement(checkHistoryQuery)) {
			checkStmt2.setInt(1, bookId);
			checkStmt2.setInt(2, subscriberId);
			try (ResultSet rs2 = checkStmt2.executeQuery()) {
				isHistoryExists = rs2.next();
			}
		}

		// Check if the row exists in loanextensions
		try (PreparedStatement checkStmt3 = connection.prepareStatement(checkLoanExtensionQuery)) {
			checkStmt3.setInt(1, bookId);
			checkStmt3.setInt(2, subscriberId);
			try (ResultSet rs3 = checkStmt3.executeQuery()) {
				isLoanExtensionExists = rs3.next();
			}
		}

		// Check if the row exists in books and fetch book name and author
		try (PreparedStatement checkStmt4 = connection.prepareStatement(checkBookQuery)) {
			checkStmt4.setInt(1, bookId);
			try (ResultSet rs4 = checkStmt4.executeQuery()) {
				if (rs4.next()) {
					bookName = rs4.getString("book_name");
					author = rs4.getString("book_author");
					isBookExists = true;
				}
			}
		}

		connection.setAutoCommit(false); // Start transaction

		// Get original return date from BorrowedBooks if the record exists
		Date originalReturnDate = null;
		if (isBorrowedBookExists) {
			String getOriginalDateQuery = "SELECT return_date FROM library.borrowedbooks WHERE book_id = ? AND subscriber_id = ?";
			try (PreparedStatement getDateStmt = connection.prepareStatement(getOriginalDateQuery)) {
				getDateStmt.setInt(1, bookId);
				getDateStmt.setInt(2, subscriberId);
				try (ResultSet dateRs = getDateStmt.executeQuery()) {
					if (dateRs.next()) {
						originalReturnDate = dateRs.getDate("return_date");
					}
				}
			}
		}

		if (isBorrowedBookExists) {
			System.out.println("Updating BorrowedBooks...");
			System.out.println("bookId: " + bookId + ", subscriberId: " + subscriberId + ", originalReturnDate: "
					+ originalReturnDate);

			String updateBorrowedQuery = "UPDATE library.borrowedbooks SET return_date = ? WHERE book_id = ? AND subscriber_id = ?";
			try (PreparedStatement pstmt1 = connection.prepareStatement(updateBorrowedQuery)) {
				pstmt1.setDate(1, newReturnDate);
				pstmt1.setInt(2, bookId);
				pstmt1.setInt(3, subscriberId);

				int rowsAffected = pstmt1.executeUpdate();
				System.out.println("Rows affected: " + rowsAffected);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Only update BorrowedBooksHistory if the record exists
		if (isHistoryExists) {
			String updateHistoryQuery = "UPDATE library.borrowedbookshistory " + "SET return_date = ? "
					+ "WHERE book_id = ? AND subscriber_id = ? AND actual_return_date IS NULL";
			try (PreparedStatement pstmt3 = connection.prepareStatement(updateHistoryQuery)) {
				pstmt3.setDate(1, originalReturnDate);
				pstmt3.setInt(2, bookId);
				pstmt3.setInt(3, subscriberId);
				pstmt3.executeUpdate();
			}
		}

		// Only update Books if the record exists
		if (isBookExists) {
			String updateBookQuery = "UPDATE library.books SET firstReturnDate = CASE WHEN ? < firstReturnDate THEN ? ELSE firstReturnDate END WHERE book_id = ?";
			try (PreparedStatement pstmt4 = connection.prepareStatement(updateBookQuery)) {
				pstmt4.setDate(1, newReturnDate);
				pstmt4.setDate(2, newReturnDate);
				pstmt4.setInt(3, bookId);
				pstmt4.executeUpdate();
			}
		}

//nhla+roba
		// Insert into loanextensions table regardless of the above conditions
		// Example of updating or inserting with book_author
		String insertExtensionQuery = "INSERT INTO loanextensions "
				+ "(subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, librarian_name) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'approved', ?)"; // Added librarian_name to insert query

		try (PreparedStatement pstmt6 = connection.prepareStatement(insertExtensionQuery,
				Statement.RETURN_GENERATED_KEYS)) {
			pstmt6.setInt(1, subscriberId);
			pstmt6.setInt(2, bookId);
			pstmt6.setString(3, bookName); // Use actual book name
			pstmt6.setString(4, author); // Use actual author name
			pstmt6.setDate(5, originalReturnDate);
			pstmt6.setDate(6, newReturnDate); // Set new return date as the updated value
			pstmt6.setDate(7, java.sql.Date.valueOf(java.time.LocalDate.now())); // Set current date as extension date
			pstmt6.setString(8, librarianName); // Set librarian's name

			// ... (rest of your code remains unchanged)

			pstmt6.executeUpdate(); // Insert into loanextensions

			// Get the generated extension_id from loanextensions
			ResultSet generatedKeys = pstmt6.getGeneratedKeys();
			int extensionId = -1;
			if (generatedKeys.next()) {
				extensionId = generatedKeys.getInt(1); // Retrieve the auto-generated extension_id
			}
			generatedKeys.close();
//nhla+roba
			// Insert into loan_extension_history table
			String insertHistoryQuery2 = "INSERT INTO library.loan_extension_history "
					+ "(extension_id, subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, action_taken, librarian_name) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'approved', 'extended', ?)"; // Include librarian_name

			try (PreparedStatement pstmt7 = connection.prepareStatement(insertHistoryQuery2)) {
				pstmt7.setInt(1, extensionId); // Use the generated extension_id
				pstmt7.setInt(2, subscriberId);
				pstmt7.setInt(3, bookId);
				pstmt7.setString(4, bookName);
				pstmt7.setString(5, author);
				pstmt7.setDate(6, originalReturnDate);
				pstmt7.setDate(7, newReturnDate); // Set new return date
				pstmt7.setDate(8, java.sql.Date.valueOf(java.time.LocalDate.now()));
				pstmt7.setString(9, librarianName); // Set librarian's name

				pstmt7.executeUpdate(); // Insert the history record
			}

		}

		// Commit transaction
		connection.commit();
		return true;
	}

	/**
	 * Inserts a rejected loan extension request into the database and records the rejection in the loan extension history.
	 * This method adds a new entry to the loan extensions table with the status 'rejected' and stores the rejection reason.
	 * It also inserts a record into the loan extension history table to track the rejection event.
	 * 
	 * @param request          The {@link ExtendLoanRequest} object containing the details of the loan extension request.
	 * @param rejectionReason A string representing the reason for the rejection of the extension request.
	 * @throws SQLException If an SQL error occurs while inserting data into the database.
	 */
	public static void insertRejectedExtension(ExtendLoanRequest request, String rejectionReason) throws SQLException {
		String insertQuery = "INSERT INTO loanextensions (subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, librarian_name, rejection_reason) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), 'rejected', 'By Subscriber', ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, request.getSubscriberId());
			pstmt.setInt(2, request.getBookId());
			pstmt.setString(3, request.getBookName());
			pstmt.setString(4, request.getAuthor());
			pstmt.setDate(5, request.getOriginalReturnDate());
			pstmt.setDate(6, request.getNewReturnDate());
			pstmt.setString(7, rejectionReason);

			pstmt.executeUpdate();

			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			int extensionId = -1;
			if (generatedKeys.next()) {
				extensionId = generatedKeys.getInt(1);
			}

			insertRejectedExtensionHistory(extensionId, request, rejectionReason);
		}
	}

	
	/**
	 * Inserts a rejected loan extension request into the database with additional information provided by the librarian.
	 * This method adds a new entry to the loan extensions table with the status 'rejected', including the librarian's name 
	 * and the reason for the rejection. It also records the rejection in the loan extension history table.
	 *
	 * @param request          The {@link ExtendReturnDateRequest} object containing the details of the loan extension request.
	 * @param rejectionReason A string representing the reason for the rejection of the extension request.
	 * @throws SQLException If an SQL error occurs while inserting data into the database.
	 */
	public static void insertRejectedExtensionLibrarian(ExtendReturnDateRequest request, String rejectionReason)
			throws SQLException {
		// SQL query to insert a rejected loan extension
		String insertQuery = "INSERT INTO loanextensions (subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, librarian_name, rejection_reason) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), 'rejected', ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
			// Set parameters for the query
			pstmt.setInt(1, request.getSubscriberId()); // subscriber_id
			pstmt.setInt(2, request.getBookId()); // book_id
			pstmt.setString(3, request.getBookName()); // book_name
			pstmt.setString(4, request.getAuthor()); // author
            pstmt.setNull(5, java.sql.Types.DATE); // original_return_date
            pstmt.setNull(6, java.sql.Types.DATE); // new_return_date
			//pstmt.setDate(5, request.getOriginalReturnDate()); // original_return_date
			//pstmt.setDate(6, request.getNewReturnDate()); // new_return_date
			pstmt.setString(7, request.getLibrarianName()); // librarian_name
			pstmt.setString(8, rejectionReason); // rejection_reason

			// Execute the query
			pstmt.executeUpdate();

			// Retrieve the generated extension ID
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			int extensionId = -1;
			if (generatedKeys.next()) {
				extensionId = generatedKeys.getInt(1);
			}

			// Insert into the rejected extension history table
			insertRejectedExtensionHistoryLibrarian(extensionId, request, rejectionReason);
		}
	}
	
	
	

	/**
	 * Inserts a record of the rejected loan extension into the loan extension history table.
	 * This method logs the details of the rejected loan extension, including the action taken (cancelled), 
	 * the librarian's name, the rejection reason, and the action date.
	 * 
	 * @param extensionId     The unique identifier for the loan extension.
	 * @param request         The {@link ExtendReturnDateRequest} object containing the details of the loan extension request.
	 * @param rejectionReason A string representing the reason for the rejection of the extension request.
	 * @throws SQLException If an SQL error occurs while inserting data into the loan extension history table.
	 */
	private static void insertRejectedExtensionHistoryLibrarian(int extensionId, ExtendReturnDateRequest request,
			String rejectionReason) throws SQLException {
		String insertHistoryQuery = "INSERT INTO loan_extension_history (extension_id, subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, action_taken, action_date, librarian_name, rejection_reason) VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(), 'rejected', 'cancelled', NOW(), ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertHistoryQuery)) {
			pstmt.setInt(1, extensionId); // extension_id
			pstmt.setInt(2, request.getSubscriberId()); // subscriber_id
			pstmt.setInt(3, request.getBookId()); // book_id
			pstmt.setString(4, request.getBookName()); // book_name
			pstmt.setString(5, request.getAuthor()); // author
			pstmt.setNull(6, java.sql.Types.DATE); // original_return_date
            pstmt.setNull(7, java.sql.Types.DATE); // new_return_date
			//pstmt.setDate(6, request.getOriginalReturnDate()); // original_return_date
			//pstmt.setDate(7, request.getNewReturnDate()); // new_return_date
			pstmt.setString(8, request.getLibrarianName()); // librarian_name
			pstmt.setString(9, rejectionReason); // rejection_reason

			pstmt.executeUpdate();
		}
	}

	/**
	 * Updates the notificationSent status for a specific borrowed book record in the `borrowedbooks` table.
	 * This method sets the notificationSent flag (indicating whether the notification has been sent) 
	 * for a specific subscriber and book.
	 * 
	 * @param subscriberId The ID of the subscriber.
	 * @param bookId       The ID of the borrowed book.
	 * @param notificationSent The status indicating whether the notification has been sent (1 for sent, 0 for not sent).
	 * @return true if at least one row was updated, false otherwise.
	 * @throws SQLException If an SQL error occurs while updating the notificationSent value.
	 */
	public static boolean updateNotificationSent(int subscriberId, int bookId, int notificationSent)
			throws SQLException {
		String updateQuery = "UPDATE borrowedbooks SET notificationSent = ? WHERE subscriber_id = ? AND book_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setInt(1, notificationSent);
			pstmt.setInt(2, subscriberId);
			pstmt.setInt(3, bookId);

			int rowsUpdated = pstmt.executeUpdate();
			return rowsUpdated > 0; // Return true if at least one row was updated
		}
	}

	
	/**
	 * Inserts a record into the `loan_extension_history` table when a loan extension is rejected.
	 * This method logs the details of the rejected extension, including the extension ID, subscriber 
	 * information, book details, and the rejection reason.
	 * 
	 * @param extensionId The ID of the extension being recorded.
	 * @param request The `ExtendLoanRequest` object containing the loan extension request details.
	 * @param rejectionReason The reason for rejecting the loan extension.
	 * @throws SQLException If an SQL error occurs while inserting the rejection history record.
	 */
	private static void insertRejectedExtensionHistory(int extensionId, ExtendLoanRequest request,
			String rejectionReason) throws SQLException {
		String insertHistoryQuery = "INSERT INTO loan_extension_history (extension_id, subscriber_id, book_id, book_name, author, original_return_date, new_return_date, extension_date, extension_status, action_taken, action_date, librarian_name, rejection_reason) VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(), 'rejected', 'cancelled', NOW(), 'By Subscriber', ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertHistoryQuery)) {
			pstmt.setInt(1, extensionId);
			pstmt.setInt(2, request.getSubscriberId());
			pstmt.setInt(3, request.getBookId());
			pstmt.setString(4, request.getBookName());
			pstmt.setString(5, request.getAuthor());
			pstmt.setDate(6, request.getOriginalReturnDate());
			pstmt.setDate(7, request.getNewReturnDate());
			pstmt.setString(8, rejectionReason);

			pstmt.executeUpdate();
		}
	}

	////// EXTEND/////////////EXTEND///////////////EXTEND//////////////////EXTEND////////////EXTEND///////
	////// EXTEND/////////////EXTEND///////////////EXTEND//////////////////EXTEND////////////EXTEND///////
	////// EXTEND/////////////EXTEND///////////////EXTEND//////////////////EXTEND////////////EXTEND///////

}
