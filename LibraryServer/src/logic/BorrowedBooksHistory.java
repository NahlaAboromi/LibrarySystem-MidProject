package logic;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents the borrowing history of books by subscribers.
 * Contains information about each borrowing instance, including the borrow and return dates.
 */
public class BorrowedBooksHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int historyId;
    private int subscriberId;
    private int bookId;
    private String bookName; // Field for book name
    private String author; // Field for author
    private Date borrowDate;
    private Date returnDate;
    private Date actualReturnDate;
    private boolean isLate; // This will correspond to TINYINT in DB

    /**
     * Constructor with all fields initialized.
     * @param historyId The ID of the history record.
     * @param subscriberId The ID of the subscriber who borrowed the book.
     * @param bookId The ID of the borrowed book.
     * @param bookName The name of the borrowed book.
     * @param author The author of the borrowed book.
     * @param borrowDate The date the book was borrowed.
     * @param returnDate The date the book is due for return.
     * @param actualReturnDate The actual return date of the book.
     * @param isLate Whether the book was returned late.
     */
    public BorrowedBooksHistory(int historyId, int subscriberId, int bookId, String bookName, String author,
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
     * Constructor that accepts only the subscriber ID, typically used for tracking history for a specific subscriber.
     * @param subscriberId The ID of the subscriber.
     */
    public BorrowedBooksHistory(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    // Getters and Setters
    
    /**
     * Gets the history ID.
     * @return The history ID.
     */
    public int getHistoryId() { return historyId; }
    
    /**
     * Sets the history ID.
     * @param historyId The history ID.
     */
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    /**
     * Gets the subscriber ID.
     * @return The subscriber ID.
     */
    public int getSubscriberId() { return subscriberId; }
    
    /**
     * Sets the subscriber ID.
     * @param subscriberId The subscriber ID.
     */
    public void setSubscriberId(int subscriberId) { this.subscriberId = subscriberId; }

    
    /**
     * Gets the book ID.
     * @return The book ID.
     */
    public int getBookId() { return bookId; }
    
    /**
     * Sets the book ID.
     * @param bookId The book ID.
     */
    public void setBookId(int bookId) { this.bookId = bookId; }

    /**
     * Gets the name of the borrowed book.
     * @return The book name.
     */
    public String getBookName() { return bookName; } // Getter for book name
    
    /**
     * Sets the name of the borrowed book.
     * @param bookName The name of the book.
     */
    public void setBookName(String bookName) { this.bookName = bookName; } // Setter for book name

    /**
     * Gets the author of the borrowed book.
     * @return The author's name.
     */
    public String getAuthor() { return author; } // Getter for author
    
    /**
     * Sets the author of the borrowed book.
     * @param author The author's name.
     */
    public void setAuthor(String author) { this.author = author; } // Setter for author

    /**
     * Gets the date the book was borrowed.
     * @return The borrow date.
     */
    public Date getBorrowDate() { return borrowDate; }
    
    /**
     * Sets the borrow date of the book.
     * @param borrowDate The borrow date.
     */
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }

    /**
     * Gets the return date of the book.
     * @return The return date.
     */
    public Date getReturnDate() { return returnDate; }
    
    /**
     * Sets the return date of the book.
     * @param returnDate The return date.
     */
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    /**
     * Gets the actual return date of the book.
     * @return The actual return date.
     */
    public Date getActualReturnDate() { return actualReturnDate; }
   
    /**
     * Sets the actual return date of the book.
     * @param actualReturnDate The actual return date.
     */
    public void setActualReturnDate(Date actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    /**
     * Checks whether the book was returned late.
     * @return True if the book was returned late, false otherwise.
     */
    public boolean isLate() { return isLate; }
    
    /**
     * Sets the late status of the book.
     * @param isLate True if the book was returned late, false otherwise.
     */
    public void setLate(boolean isLate) { this.isLate = isLate; }
}
