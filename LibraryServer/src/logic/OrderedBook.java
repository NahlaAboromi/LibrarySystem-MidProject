package logic;

import java.io.Serializable;
import java.sql.Date; // Use java.sql.Date consistently for database operations
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an ordered book in the library system. This class holds the details
 * of a book order, including subscriber information, book details, order dates,
 * and order status.
 */
public class OrderedBook implements Serializable {

    private static final long serialVersionUID = 1L;

    private String order_status;
    private int subscriber_id;
    private int book_id;
    private String book_name;
    private LocalDateTime order_date;
    private LocalDate startOrderDate; // New field
    private LocalDateTime endOrderDate; // New field

    /**
     * Constructor for creating an OrderedBook with all the necessary fields.
     *
     * @param subscriber_id The ID of the subscriber ordering the book.
     * @param book_id The ID of the ordered book.
     * @param book_name The name of the ordered book.
     * @param order_date The timestamp when the order was placed.
     * @param firstReturnDateOfTheOrderedBook The first expected return date of the ordered book.
     * @param startOrderDate The start date of the order.
     * @param endOrderDate The end date of the order.
     */
    public OrderedBook(int subscriber_id, int book_id, String book_name, LocalDateTime order_date,
                       LocalDate firstReturnDateOfTheOrderedBook, LocalDate startOrderDate,
                       LocalDateTime endOrderDate) {
        this.subscriber_id = subscriber_id;
        this.book_id = book_id;
        this.book_name = book_name;
        this.order_date = order_date;
        this.startOrderDate = startOrderDate;
        this.endOrderDate = endOrderDate;
    }

    /**
     * Constructor for creating an OrderedBook with fewer fields, typically when
     * only essential order information is needed.
     *
     * @param subscriber_id The ID of the subscriber ordering the book.
     * @param book_id The ID of the ordered book.
     * @param book_name The name of the ordered book.
     * @param order_date The timestamp when the order was placed.
     * @param order_status The status of the order (e.g., pending, fulfilled).
     */
    public OrderedBook(int subscriber_id, int book_id, String book_name, LocalDateTime order_date, String order_status) {
        this.subscriber_id = subscriber_id;
        this.book_id = book_id;
        this.book_name = book_name;
        this.order_date = order_date;
        this.order_status = order_status; // Initialize order status
    }

    // Getters
    /**
     * Gets the subscriber ID associated with the order.
     *
     * @return The subscriber ID.
     */
    public int getSubscriberId() {
        return subscriber_id;
    }

    /**
     * Gets the book ID of the ordered book.
     *
     * @return The book ID.
     */
    public int getBookId() {
        return book_id;
    }

    /**
     * Gets the name of the ordered book.
     *
     * @return The name of the book.
     */
    public String getBookName() {
        return book_name;
    }

    /**
     * Gets the timestamp when the order was placed.
     *
     * @return The order date.
     */
    public LocalDateTime getOrderDate() {
        return order_date;
    }

    /**
     * Gets the status of the order.
     *
     * @return The order status (e.g., pending, fulfilled).
     */
    public String getOrderStatus() {
        return order_status; // Added getter for order_status
    }

    /**
     * Gets the start date of the order.
     *
     * @return The start order date.
     */
    public LocalDate getStartOrderDate() {
        return startOrderDate; // Added getter
    }

    /**
     * Gets the end date of the order.
     *
     * @return The end order date.
     */
    public LocalDateTime getEndOrderDate() {
        return endOrderDate; // Added getter
    }

    // Setters
    
    /**
     * Sets the subscriber ID associated with the order.
     *
     * @param subscriber_id The subscriber ID to set.
     */
    public void setSubscriberId(int subscriber_id) {
        this.subscriber_id = subscriber_id;
    }

    /**
     * Sets the book ID for the ordered book.
     *
     * @param book_id The book ID to set.
     */
    public void setBookId(int book_id) {
        this.book_id = book_id;
    }

    /**
     * Sets the name of the ordered book.
     *
     * @param book_name The name of the book to set.
     */
    public void setBookName(String book_name) { // Accept parameter
        this.book_name = book_name; // Corrected assignment
    }

    /**
     * Sets the timestamp when the order was placed.
     *
     * @param order_date The order date to set.
     */
    public void setOrderDate(LocalDateTime order_date) {
        this.order_date = order_date;
    }
    
    /**
     * Sets the status of the order.
     *
     * @param order_status The order status to set.
     */
    public void setOrderStatus(String order_status) { // Accept parameter for setting status
        this.order_status = order_status; 
    }
    
    /**
     * Sets the start date of the order.
     *
     * @param startOrder The start order date to set.
     */
    public void setStartOrderDate(LocalDate startOrder) {
        this.startOrderDate = startOrder; 
    }
    
    /**
     * Sets the end date of the order.
     *
     * @param endOrder The end order date to set.
     */
    public void setEndOrderDate(LocalDateTime endOrder) { 
        this.endOrderDate = endOrder; 
    }
}
