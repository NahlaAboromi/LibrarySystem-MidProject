package logic;

import java.io.Serializable;
import java.sql.Date;

import java.io.Serializable;
import java.sql.Date; // Use java.sql.Date consistently for database operations

/**
 * Represents a book return transaction.
 * This class stores information about the subscriber who borrowed the book,
 * the book's details, and the borrow and return dates.
 */
public class ReturnBook implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int subscriber_id;
    private int book_id;
    private Date borrow_date;
    private Date return_date;
    
    /**
     * Constructs a ReturnBook object with the specified details.
     * 
     * @param subscriber_id the ID of the subscriber returning the book
     * @param book_id the ID of the returned book
     * @param currentDate the date when the book was borrowed
     * @param returnDate the date when the book is being returned
     */
    public ReturnBook(int subscriber_id, int book_id,Date currentDate,Date returnDate) {
        this.subscriber_id = subscriber_id;
        this.book_id = book_id;
        this.borrow_date = currentDate;
        this.return_date = returnDate;
    }
    
    // Getters
    /**
     * Gets the subscriber ID.
     * 
     * @return the ID of the subscriber
     */
    public int getSubscriberId() { return subscriber_id; }
    
    /**
     * Gets the book ID.
     * 
     * @return the ID of the returned book
     */
    public int getBookId() { return book_id; }
    
    /**
     * Gets the borrow date of the book.
     * 
     * @return the date when the book was borrowed
     */
    public Date getBorrowDate() { return borrow_date; }
    
    /**
     * Gets the return date of the book.
     * 
     * @return the date when the book is returned
     */
    public Date getReturnDate() { return return_date; }
    
    // Setters
    /**
     * Sets the subscriber ID.
     * 
     * @param subscriber_id the ID of the subscriber
     */
    public void setSubscriberId(int subscriber_id) { this.subscriber_id = subscriber_id; }
    
    /**
     * Sets the book ID.
     * 
     * @param book_id the ID of the returned book
     */
    public void setBookId(int book_id) { this.book_id = book_id; }
    
    /**
     * Sets the borrow date of the book.
     * 
     * @param borrow_date the date when the book was borrowed
     */
    public void setBorrowDate(Date borrow_date) { this.borrow_date = borrow_date; }
    
    /**
     * Sets the return date of the book.
     * 
     * @param return_date the date when the book is returned
     */
    public void setReturnDate(Date return_date) { this.return_date = return_date; }

}

