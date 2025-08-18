package logic;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents the history of a book return transaction.
 * This class stores details about the subscriber, the book, the borrow and return dates,
 * and whether the book was returned late.
 */
public class ReturnBookHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int historyId;
    private int subscriberId;
    private int bookId;
    private String bookName; // Field for book name
    private String author; // Field for author
    private Date borrowDate;
    private Date returnDate;
    private Date actualReturnDate;
    private boolean isLate; // This corresponds to TINYINT in DB

    /**
     * Constructs a ReturnBookHistory object with all necessary details.
     * 
     * @param historyId the unique history ID for this return transaction
     * @param subscriberId the ID of the subscriber returning the book
     * @param bookId the ID of the returned book
     * @param bookName the name of the returned book
     * @param author the author of the returned book
     * @param borrowDate the date when the book was borrowed
     * @param returnDate the date the book was due to be returned
     * @param actualReturnDate the actual date the book was returned
     * @param isLate whether the book was returned late
     */
    public ReturnBookHistory(int historyId, int subscriberId, int bookId, String bookName, String author,
                             Date borrowDate, Date returnDate, Date actualReturnDate, boolean isLate) {
        this.historyId = historyId;
        this.subscriberId = subscriberId;
        this.bookId = bookId;
        this.bookName = bookName; // Initialize book name
        this.author = author; // Initialize author
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.isLate = isLate; // Initialize late status
    }

    /**
     * Constructs a ReturnBookHistory object with just the subscriber ID.
     * Other fields are initialized with default values.
     * 
     * @param subscriberId the ID of the subscriber
     */
    public ReturnBookHistory(int subscriberId) {
        this.subscriberId = subscriberId;
        this.bookId = 0; // Default value or you can set it as needed
        this.bookName = ""; // Default value
        this.author = ""; // Default value
        this.borrowDate = null; // Default value
        this.returnDate = null; // Default value
        this.actualReturnDate = null; // Default value
        this.isLate = false; // Default value
    }

    // Getters
    /**
     * Gets the unique history ID.
     * 
     * @return the history ID
     */
    public int getHistoryId() { return historyId; }
    
    /**
     * Gets the subscriber ID.
     * 
     * @return the ID of the subscriber
     */
    public int getSubscriberId() { return subscriberId; }
    
    /**
     * Gets the book ID.
     * 
     * @return the ID of the returned book
     */
    public int getBookId() { return bookId; }
    
    /**
     * Gets the book name.
     * 
     * @return the name of the returned book
     */
    public String getBookName() { return bookName; } // Getter for book name
    
    /**
     * Gets the author of the returned book.
     * 
     * @return the author of the returned book
     */
    public String getAuthor() { return author; } // Getter for author
    
    /**
     * Gets the borrow date.
     * 
     * @return the date when the book was borrowed
     */
    public Date getBorrowDate() { return borrowDate; }
    
    /**
     * Gets the return date (due date).
     * 
     * @return the due date for returning the book
     */
    public Date getReturnDate() { return returnDate; }
    
    /**
     * Gets the actual return date.
     * 
     * @return the actual date when the book was returned
     */
    public Date getActualReturnDate() { return actualReturnDate; }
    
    /**
     * Gets whether the book was returned late.
     * 
     * @return "Yes" if late, "No" if on time
     */
    public String getIsLate() {  return isLate ? "Yes" : "No"; } // Getter for isLate
}
