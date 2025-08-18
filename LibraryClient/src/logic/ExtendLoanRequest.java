package logic;

import java.io.Serializable;
import java.sql.Date;


/**
 * Represents a request to extend the loan period for a borrowed book.
 * This class holds details about the book, subscriber, original return date, extension details, and status.
 */
public class ExtendLoanRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private int subscriberId;
	private int bookId;
	private String bookName;
	private String author;
	private Date originalReturnDate;
	private Date extensionDate;
	private String status;
	private Date newReturnDate;

	/**
     * Constructs an ExtendLoanRequest with the specified details.
     * 
     * @param subscriberId The ID of the subscriber requesting the extension.
     * @param bookId The ID of the book whose loan is being extended.
     * @param bookName The name of the book.
     * @param originalReturnDate The original return date of the book.
     * @param extensionDate The requested extension date.
     * @param newReturnDate The new return date after the extension.
     * @param author The author of the book.
     */
	public ExtendLoanRequest(int subscriberId, int bookId, String bookName, Date originalReturnDate, Date extensionDate,
			Date newReturnDate, String author) {
		this.subscriberId = subscriberId;
		this.bookId = bookId;
		this.bookName = bookName;
		this.originalReturnDate = originalReturnDate;
		this.extensionDate = extensionDate;
		this.newReturnDate = newReturnDate;
		this.author = author;
		this.status = null;
	}

	// Getters
	/**
     * Gets the ID of the subscriber requesting the loan extension.
     * 
     * @return The subscriber ID.
     */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
     * Gets the ID of the book whose loan is being extended.
     * 
     * @return The book ID.
     */
	public int getBookId() {
		return bookId;
	}

	/**
     * Gets the name of the book.
     * 
     * @return The book name.
     */
	public String getBookName() {
		return bookName;
	}

	/**
     * Gets the author of the book.
     * 
     * @return The author's name.
     */
	public String getAuthor() {
		return author;
	}

	/**
     * Gets the original return date of the book.
     * 
     * @return The original return date.
     */
	public Date getOriginalReturnDate() {
		return originalReturnDate;
	}

	/**
     * Gets the requested extension date.
     * 
     * @return The extension date.
     */
	public Date getExtensionDate() {
		return extensionDate;
	}

	/**
     * Gets the status of the loan extension request.
     * 
     * @return The status of the request.
     */
	public String getStatus() {
		return status;
	}

	 /**
     * Gets the new return date after the extension.
     * 
     * @return The new return date.
     */
	public Date getNewReturnDate() {
		return newReturnDate;
	}

	// Setters
	/**
     * Sets the ID of the subscriber requesting the loan extension.
     * 
     * @param subscriberId The subscriber ID.
     */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
     * Sets the ID of the book whose loan is being extended.
     * 
     * @param bookId The book ID.
     */
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	/**
     * Sets the name of the book.
     * 
     * @param bookName The book name.
     */
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	/**
     * Sets the author of the book.
     * 
     * @param author The author's name.
     */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
     * Sets the original return date of the book.
     * 
     * @param originalReturnDate The original return date.
     */
	public void setOriginalReturnDate(Date originalReturnDate) {
		this.originalReturnDate = originalReturnDate;
	}

	/**
     * Sets the requested extension date.
     * 
     * @param extensionDate The extension date.
     */
	public void setExtensionDate(Date extensionDate) {
		this.extensionDate = extensionDate;
	}

	/**
     * Sets the status of the loan extension request.
     * 
     * @param status The status of the request.
     */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
     * Sets the new return date after the extension.
     * 
     * @param newReturnDate The new return date.
     */
	public void setNewReturnDate(Date newReturnDate) {
		this.newReturnDate = newReturnDate;
	}
}
