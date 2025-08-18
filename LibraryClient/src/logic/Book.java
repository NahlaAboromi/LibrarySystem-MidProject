package logic;

import java.io.Serializable; // Make sure to import this
import java.sql.Date;


/**
 * Represents a Book entity, implementing Serializable for persistence.
 */
public class Book implements Serializable { // Implements Serializable
    private static final long serialVersionUID = 1L; // Optional but recommended for version control

    /**
     * The ID of the book in the library system.
     */
    private int bookID;
    
    /**
     * The name of the book.
     */
    private String bookName;
    
    /**
     * The image associated with the book, stored as a byte array.
     */
    private byte[] bookImage;
    
    /**
     * The topic or genre of the book.
     */
    private String bookTopic;
    
    /**
     * A brief description of the book.
     */
    private String bookDescription;
    
    /**
     * The name of the author of the book.
     */
    private String authorName;
    
    /**
     * The total number of copies available in the library.
     */
    private int numberOfCopies;
    
    /**
     * The number of available copies of the book in the library.
     */
    private int numberOfAvailableCopies;
    
    /**
     * The first date by which the book is expected to be returned.
     */
    private Date firstReturnDate;
    
    /**
     * The physical location of the book within the library (e.g., shelf, section).
     */
    private String bookPlace;
    
    /**
     * The number of copies of the book that have been ordered but not yet arrived.
     */
    private int numberOfOrderedCopies;
    
    /**
     * The barcode of the book, used for easy identification and tracking.
     */
    private String barcode;

 
    /**
     * Constructs a new Book object with a barcode.
     *
     * @param barcode             The unique barcode of the book.
     * @param bookName            The name of the book.
     * @param bookImage           The image of the book (stored as a byte array).
     * @param bookTopic           The topic or genre of the book.
     * @param bookDescription     A description of the book.
     * @param authorName          The name of the book's author.
     * @param numberOfCopies      The total number of copies available for the book.
     * @param numberOfAvailableCopies The number of available copies of the book.
     * @param firstReturnDate     The first return date of the book.
     * @param bookPlace           The location where the book is stored.
     * @param numberOfOrderedCopies The number of copies that are ordered.
     */
    public Book(String barcode, String bookName, byte[] bookImage, String bookTopic, String bookDescription, 
                String authorName, int numberOfCopies, int numberOfAvailableCopies, Date firstReturnDate, 
                String bookPlace, int numberOfOrderedCopies) {
        this.barcode = barcode;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.bookTopic = bookTopic;
        this.bookDescription = bookDescription;
        this.authorName = authorName;
        this.numberOfCopies = numberOfCopies;
        this.numberOfAvailableCopies = numberOfAvailableCopies;
        this.firstReturnDate = firstReturnDate;
        this.bookPlace = bookPlace;
        this.numberOfOrderedCopies = numberOfOrderedCopies;
    }
    
    
    /**
     * Constructs a new Book object without a barcode.
     *
     * @param bookID              The unique identifier for the book.
     * @param bookName            The name of the book.
     * @param bookImage           The image of the book (stored as a byte array).
     * @param bookTopic           The topic or genre of the book.
     * @param bookDescription     A description of the book.
     * @param authorName          The name of the book's author.
     * @param numberOfCopies      The total number of copies available for the book.
     * @param numberOfAvailableCopies The number of available copies of the book.
     * @param firstReturnDate     The first return date of the book.
     * @param bookPlace           The location where the book is stored.
     * @param numberOfOrderedCopies The number of copies of the book that are ordered.
     */
    public Book(int bookID, String bookName, byte[] bookImage, String bookTopic, String bookDescription,String authorName,int numberOfCopies,int numberOfAvailableCopies,Date firstReturnDate,String bookPlace,int numberOfOrderedCopies) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.bookTopic = bookTopic;
        this.bookDescription = bookDescription;
        this.authorName=authorName;
        this.numberOfCopies=numberOfCopies;
        this.numberOfAvailableCopies=numberOfAvailableCopies;
        this.firstReturnDate=firstReturnDate;
        this.bookPlace=bookPlace;
        this.numberOfOrderedCopies=numberOfOrderedCopies;
    }

    
    /**
     * Gets the unique identifier of the book.
     *
     * @return The unique identifier of the book.
     */
    public int getBookID() {
        return bookID;
    }
    
    /**
     * Gets the barcode of the book.
     *
     * @return The unique barcode of the book.
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Gets the name of the book.
     *
     * @return The name of the book.
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Gets the image of the book as a byte array.
     *
     * @return The byte array representing the book's image.
     */
    public byte[] getBookImage() {
        return bookImage;
    }

    
    /**
     * Gets the topic or genre of the book.
     *
     * @return The topic or genre of the book.
     */
    public String getBookTopic() {
        return bookTopic;
    }

    /**
     * Gets the description of the book.
     *
     * @return The description of the book.
     */
    public String getBookDescription() {
        return bookDescription;
    }
    
    /**
     * Gets the name of the book's author.
     *
     * @return The name of the book's author.
     */
    public String getauthorName() {
        return authorName;
    }
    
    /**
     * Gets the total number of copies of the book.
     *
     * @return The total number of copies of the book.
     */
    public int getNumberOfCopies() {
        return numberOfCopies;
    }
    
    /**
     * Gets the number of available copies of the book.
     *
     * @return The number of available copies of the book.
     */
    public int getNumberOfAvailableCopies() {
        return numberOfAvailableCopies;
    }
    
    /**
     * Gets the number of ordered copies of the book.
     *
     * @return The number of ordered copies of the book.
     */
    public int numberOfOrderedCopies() {
        return numberOfAvailableCopies;
    }
    
    
    /**
     * Gets the first return date of the book.
     *
     * @return The first return date of the book.
     */
    public Date getFirstReturnDate() {
        return firstReturnDate;
    }
    
    
    /**
     * Gets the place where the book is stored.
     *
     * @return The location where the book is stored.
     */
    public String getBookPlace() {
        return bookPlace;
    }
    
}
