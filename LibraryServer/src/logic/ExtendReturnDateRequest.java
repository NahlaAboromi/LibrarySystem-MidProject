package logic;

import java.io.Serializable;
import java.sql.Date;


/**
 * Represents a request to extend the return date of a borrowed book.
 * This class holds details about the book, subscriber, new return date, librarian's name, and original return date.
 */
public class ExtendReturnDateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookId;
    private int subscriberId;
    private Date newReturnDate;
    private String librarianName;
    private String bookName;
    private String author;
    private Date originalReturnDate;

    /**
     * Constructs an ExtendReturnDateRequest with the specified details.
     * 
     * @param bookId The ID of the book whose return date is being extended.
     * @param subscriberId The ID of the subscriber requesting the extension.
     * @param newReturnDate The new requested return date.
     * @param librarianName The name of the librarian handling the request.
     * @param bookName The name of the book.
     * @param author The author of the book.
     * @param originalReturnDate The original return date of the book.
     */
    public ExtendReturnDateRequest(int bookId, int subscriberId, Date newReturnDate, String librarianName, String bookName, String author, Date originalReturnDate) {
        this.bookId = bookId;
        this.subscriberId = subscriberId;
        this.newReturnDate = newReturnDate;
        this.librarianName = librarianName;
        this.bookName = bookName;
        this.author = author;
        this.originalReturnDate = originalReturnDate;
    }

    // Getters
    
    /**
     * Gets the ID of the book whose return date is being extended.
     * 
     * @return The book ID.
     */
    public int getBookId() {
        return bookId;
    }

    
    /**
     * Gets the ID of the subscriber requesting the extension.
     * 
     * @return The subscriber ID.
     */
    public int getSubscriberId() {
        return subscriberId;
    }

    /**
     * Gets the new requested return date.
     * 
     * @return The new return date.
     */
    public Date getNewReturnDate() {
        return newReturnDate;
    }

    /**
     * Gets the name of the librarian handling the request.
     * 
     * @return The librarian's name.
     */
    public String getLibrarianName() {
        return librarianName;
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
    } // Getter for librarian's name
}