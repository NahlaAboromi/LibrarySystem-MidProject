package logic;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents a book borrowed by a subscriber in a library system.
 * This class holds details about the subscriber, the borrowed book, and relevant dates.
 */
public class BorrowedBook implements Serializable {
    private static final long serialVersionUID = 1L;

    private int subscriber_id;
    private int book_id;
    private String source; // "BOOK_ID" or "BARCODE"
    private Date borrow_date;
    private Date return_date;
    private String book_name; // The name of the book
    private String author; // The author of the book

    /**
     * Constructs a BorrowedBook with the specified subscriber ID, book ID, borrow date, and return date.
     * 
     * @param subscriber_id The ID of the subscriber.
     * @param book_id The ID of the book.
     * @param currentDate The date when the book was borrowed.
     * @param returnDate The expected return date of the book.
     */
    public BorrowedBook(int subscriber_id, int book_id, Date currentDate, Date returnDate) {
        this.subscriber_id = subscriber_id;
        this.book_id = book_id;
        this.borrow_date = currentDate;
        this.return_date = returnDate;
    }

    /**
     * Constructs a BorrowedBook with the specified subscriber ID, book ID, book name, author, borrow date, and return date.
     * 
     * @param subscriber_id The ID of the subscriber.
     * @param book_id The ID of the book.
     * @param book_name The name of the book.
     * @param author The author of the book.
     * @param borrow_date The date when the book was borrowed.
     * @param return_date The expected return date of the book.
     */
    public BorrowedBook(int subscriber_id, int book_id, String book_name, String author, Date borrow_date, Date return_date) {
        this.subscriber_id = subscriber_id;
        this.book_id = book_id;
        this.book_name = book_name;
        this.author = author;
        this.borrow_date = borrow_date;
        this.return_date = return_date;
    }

    /**
     * Constructs a BorrowedBook with the specified subscriber ID, book ID, source, borrow date, and return date.
     * 
     * @param subscriberId The ID of the subscriber.
     * @param bookId The ID of the book.
     * @param source The source of the book identifier (e.g., "BOOK_ID" or "BARCODE").
     * @param borrowDate The date when the book was borrowed.
     * @param returnDate The expected return date of the book.
     */
    public BorrowedBook(int subscriberId, int bookId, String source, Date borrowDate, Date returnDate) {
        this.subscriber_id = subscriberId;
        this.book_id = bookId;
        this.source = source;
        this.borrow_date = borrowDate;
        this.return_date = returnDate;
    }

    // Getters

    /**
     * Gets the ID of the subscriber who borrowed the book.
     * 
     * @return The subscriber ID.
     */
    public int getSubscriberId() { return subscriber_id; }

    /**
     * Gets the ID of the borrowed book.
     * 
     * @return The book ID.
     */
    public int getBookId() { return book_id; }

    /**
     * Gets the name of the borrowed book.
     * 
     * @return The name of the book.
     */
    public String getBookName() { return book_name; }

    /**
     * Gets the author of the borrowed book.
     * 
     * @return The author's name.
     */
    public String getAuthor() { return author; }

    /**
     * Gets the borrow date of the book.
     * 
     * @return The borrow date.
     */
    public Date getBorrowDate() { return borrow_date; }

    /**
     * Gets the return date of the book.
     * 
     * @return The return date.
     */
    public Date getReturnDate() { return return_date; }

    /**
     * Gets the source of the book identifier.
     * 
     * @return The source (e.g., "BOOK_ID" or "BARCODE").
     */
    public String getSource() {
        return source;
    }

    // Setters

    /**
     * Sets the ID of the subscriber.
     * 
     * @param subscriber_id The new subscriber ID.
     */
    public void setSubscriberId(int subscriber_id) { this.subscriber_id = subscriber_id; }

    /**
     * Sets the ID of the book.
     * 
     * @param book_id The new book ID.
     */
    public void setBookId(int book_id) { this.book_id = book_id; }

    /**
     * Sets the name of the book.
     * 
     * @param book_name The new book name.
     */
    public void setBookName(String book_name) { this.book_name = book_name; }

    /**
     * Sets the author of the book.
     * 
     * @param author The new author's name.
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Sets the borrow date of the book.
     * 
     * @param borrow_date The new borrow date.
     */
    public void setBorrowDate(Date borrow_date) { this.borrow_date = borrow_date; }

    /**
     * Sets the return date of the book.
     * 
     * @param return_date The new return date.
     */
    public void setReturnDate(Date return_date) { this.return_date = return_date; }
}
