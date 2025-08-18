package logic;

import java.io.Serializable;
import java.sql.Date;


/**
 * Represents a loan extension request for a book in the library system.
 * This class stores the details of a loan extension including the book information,
 * the subscriber requesting the extension, the new return date, and the librarian's handling of the extension.
 */
public class LoanExtension implements Serializable {
	private static final long serialVersionUID = 1L; // Add this line

	private int extensionId;
	private int subscriberId;
	private int bookId;
	private String bookName;
	private String author;
	private Date originalReturnDate;
	private Date newReturnDate;
	private Date extensionDate;
	private String extensionStatus;
	private String librarianName; // New field for librarian's name
	private String rejectionReason;

	/**
     * Constructor for creating a LoanExtension with required details.
     *
     * @param extensionId The unique identifier for the loan extension.
     * @param subscriberId The subscriber's ID requesting the extension.
     * @param bookId The ID of the book being extended.
     * @param bookName The name of the book.
     * @param author The author of the book.
     * @param originalReturnDate The original return date of the book.
     * @param newReturnDate The new return date after the extension.
     * @param extensionDate The date when the extension was requested.
     * @param extensionStatus The status of the extension request (e.g., approved, pending).
     * @param librarianName The name of the librarian who handled the extension.
     */
	public LoanExtension(int extensionId, int subscriberId, int bookId, String bookName, String author,
			Date originalReturnDate, Date newReturnDate, Date extensionDate, String extensionStatus,
			String librarianName) { // Added librarianName
		this.extensionId = extensionId;
		this.subscriberId = subscriberId;
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.originalReturnDate = originalReturnDate;
		this.newReturnDate = newReturnDate;
		this.extensionDate = extensionDate;
		this.extensionStatus = extensionStatus;
		this.librarianName = librarianName; // Assigning librarian's name
	}

	/**
     * Constructor for creating a LoanExtension with rejection reason.
     *
     * @param extensionId The unique identifier for the loan extension.
     * @param subscriberId The subscriber's ID requesting the extension.
     * @param bookId The ID of the book being extended.
     * @param bookName The name of the book.
     * @param author The author of the book.
     * @param originalReturnDate The original return date of the book.
     * @param newReturnDate The new return date after the extension.
     * @param extensionDate The date when the extension was requested.
     * @param extensionStatus The status of the extension request (e.g., approved, pending).
     * @param librarianName The name of the librarian who handled the extension.
     * @param rejectionReason The reason why the extension was rejected, if applicable.
     */
	public LoanExtension(int extensionId, int subscriberId, int bookId, String bookName, String author,
			Date originalReturnDate, Date newReturnDate, Date extensionDate, String extensionStatus,
			String librarianName, String rejectionReason) {
		this.extensionId = extensionId;
		this.subscriberId = subscriberId;
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.originalReturnDate = originalReturnDate;
		this.newReturnDate = newReturnDate;
		this.extensionDate = extensionDate;
		this.extensionStatus = extensionStatus;
		this.librarianName = librarianName;
		this.rejectionReason = rejectionReason;
	}

	// Add getters and setters for all fields

	/**
     * Gets the rejection reason for the loan extension request.
     *
     * @return The rejection reason, or null if the extension was not rejected.
     */
	public String getRejectionReason() {
		return rejectionReason;
	}

	/**
     * Sets the rejection reason for the loan extension request.
     *
     * @param rejectionReason The reason for rejection.
     */
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	
	/**
     * Gets the name of the librarian who handled the loan extension.
     *
     * @return The name of the librarian.
     */
	public String getLibrarianName() {
		return librarianName; // Getter for librarian's name
	}

	/**
     * Sets the name of the librarian who handled the loan extension.
     *
     * @param librarianName The name of the librarian to set.
     */
	public void setLibrarianName(String librarianName) {
		this.librarianName = librarianName; // Setter for librarian's name
	}

	/**
     * Gets the unique identifier for the loan extension.
     *
     * @return The extension ID.
     */
	public int getExtensionId() {
		return extensionId;
	}

	/**
     * Sets the unique identifier for the loan extension.
     *
     * @param extensionId The extension ID to set.
     */
	public void setExtensionId(int extensionId) {
		this.extensionId = extensionId;
	}

	/**
     * Gets the subscriber's ID requesting the loan extension.
     *
     * @return The subscriber's ID.
     */
	public int getSubscriberId() {
		return subscriberId;
	}

	
	/**
     * Sets the subscriber's ID requesting the loan extension.
     *
     * @param subscriberId The subscriber's ID to set.
     */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
     * Gets the book ID being extended.
     *
     * @return The book ID.
     */
	public int getBookId() {
		return bookId;
	}

	/**
     * Sets the book ID for the loan extension.
     *
     * @param bookId The book ID to set.
     */
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	/**
     * Gets the name of the book being extended.
     *
     * @return The name of the book.
     */
	public String getBookName() {
		return bookName;
	}

	/**
     * Sets the name of the book being extended.
     *
     * @param bookName The name of the book to set.
     */
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	/**
     * Gets the author of the book being extended.
     *
     * @return The author of the book.
     */
	public String getAuthor() {
		return author;
	}

	/**
     * Sets the author of the book being extended.
     *
     * @param author The author to set.
     */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
     * Gets the original return date for the loaned book.
     *
     * @return The original return date.
     */
	public Date getOriginalReturnDate() {
		return originalReturnDate;
	}

	/**
     * Sets the original return date for the loaned book.
     *
     * @param originalReturnDate The original return date to set.
     */
	public void setOriginalReturnDate(Date originalReturnDate) {
		this.originalReturnDate = originalReturnDate;
	}

	 /**
     * Gets the new return date after the extension.
     *
     * @return The new return date.
     */
	public Date getNewReturnDate() {
		return newReturnDate;
	}

	/**
     * Sets the new return date after the extension.
     *
     * @param newReturnDate The new return date to set.
     */
	public void setNewReturnDate(Date newReturnDate) {
		this.newReturnDate = newReturnDate;
	}

	/**
     * Gets the date when the loan extension was requested.
     *
     * @return The extension date.
     */
	public Date getExtensionDate() {
		return extensionDate;
	}

	/**
     * Sets the date when the loan extension was requested.
     *
     * @param extensionDate The extension date to set.
     */
	public void setExtensionDate(Date extensionDate) {
		this.extensionDate = extensionDate;
	}

	/**
     * Gets the status of the loan extension request.
     *
     * @return The extension status (e.g., approved, pending).
     */
	public String getExtensionStatus() {
		return extensionStatus;
	}

	/**
     * Sets the status of the loan extension request.
     *
     * @param extensionStatus The extension status to set.
     */
	public void setExtensionStatus(String extensionStatus) {
		this.extensionStatus = extensionStatus;
	}

	/**
     * Provides a string representation of the LoanExtension object.
     *
     * @return A string representing the loan extension details.
     */
	@Override
	public String toString() {
		return "LoanExtension{" + "extensionId=" + extensionId + ", subscriberId=" + subscriberId + ", bookId=" + bookId
				+ ", bookName='" + bookName + '\'' + ", author='" + author + '\'' + ", originalReturnDate="
				+ originalReturnDate + ", newReturnDate=" + newReturnDate + ", extensionDate=" + extensionDate
				+ ", extensionStatus='" + extensionStatus + '\'' + '}';
	}
}