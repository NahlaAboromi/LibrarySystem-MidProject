package logic;

import java.io.Serializable; // Make sure to import this
import java.sql.Date;

/**
 * Represents the history of ordered books in the library system.
 * This class holds details about book orders, including subscriber information,
 * book details, order date, and order status.
 */
public class OrderedBooksHistory implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	private int orderId;
	    private int subscriberId;
	    private int bookId;
	    private Date orderDate;
	    private String orderStatus;
	    private String bookName;  // הוסף שדה זה
	    private String author;    // הוסף שדה זה	    
	    
	    /**
	     * Constructor for creating an OrderedBooksHistory object with only the subscriber ID.
	     * This constructor can be used for initializing an object when only subscriber ID is known.
	     *
	     * @param subscriberId The ID of the subscriber placing the order.
	     */
	    public OrderedBooksHistory(int subscriberId) {
	        this.subscriberId = subscriberId;
	    }

	    /**
	     * Constructor for creating an OrderedBooksHistory object with all relevant fields.
	     * This constructor is used to initialize an object with detailed order information.
	     *
	     * @param orderId The ID of the order.
	     * @param subscriberId The ID of the subscriber placing the order.
	     * @param bookId The ID of the ordered book.
	     * @param bookName The name of the ordered book.
	     * @param author The author of the ordered book.
	     * @param orderDate The date the order was placed.
	     * @param orderStatus The status of the order (e.g., pending, fulfilled).
	     */
	    public OrderedBooksHistory(int orderId, int subscriberId, int bookId, 
	                                String bookName, String author,  // הוסף פרמטרים אלו
	                                Date orderDate, String orderStatus) {
	        this.orderId = orderId;
	        this.subscriberId = subscriberId;
	        this.bookId = bookId;
	        this.bookName = bookName;    // הוסף השמה
	        this.author = author;        // הוסף השמה
	        this.orderDate = orderDate;
	        this.orderStatus = orderStatus;
	    }
	    // Getters and Setters
	    /**
	     * Gets the order ID of the ordered book.
	     *
	     * @return The order ID.
	     */
	    public int getOrderId() { return orderId; }
	    
	    /**
	     * Sets the order ID of the ordered book.
	     *
	     * @param orderId The order ID to set.
	     */
	    public void setOrderId(int orderId) { this.orderId = orderId; }
	    
	    /**
	     * Gets the subscriber ID associated with the order.
	     *
	     * @return The subscriber ID.
	     */
	    public int getSubscriberId() { return subscriberId; }
	    
	    /**
	     * Sets the subscriber ID associated with the order.
	     *
	     * @param subscriberId The subscriber ID to set.
	     */
	    public void setSubscriberId(int subscriberId) { this.subscriberId = subscriberId; }
	    
	    /**
	     * Gets the book ID of the ordered book.
	     *
	     * @return The book ID.
	     */
	    public int getBookId() { return bookId; }
	    
	    /**
	     * Sets the book ID for the ordered book.
	     *
	     * @param bookId The book ID to set.
	     */
	    public void setBookId(int bookId) { this.bookId = bookId; }
	    
	    /**
	     * Gets the date when the order was placed.
	     *
	     * @return The order date.
	     */
	    public Date getOrderDate() { return orderDate; }
	    
	    /**
	     * Sets the date when the order was placed.
	     *
	     * @param orderDate The order date to set.
	     */
	    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
	    
	    /**
	     * Gets the status of the order.
	     *
	     * @return The order status (e.g., pending, fulfilled).
	     */
	    public String getOrderStatus() { return orderStatus; }
	    
	    /**
	     * Sets the status of the order.
	     *
	     * @param orderStatus The order status to set.
	     */
	    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
	    
	    /**
	     * Gets the name of the ordered book.
	     *
	     * @return The book name.
	     */
	    public String getBookName() { return bookName; }
	    
	    /**
	     * Sets the name of the ordered book.
	     *
	     * @param bookName The book name to set.
	     */
	    public void setBookName(String bookName) { this.bookName = bookName; }

	    
	    /**
	     * Gets the author of the ordered book.
	     *
	     * @return The author.
	     */
	    public String getAuthor() { return author; }
	    
	    /**
	     * Sets the author of the ordered book.
	     *
	     * @param author The author to set.
	     */
	    public void setAuthor(String author) { this.author = author; }
	}