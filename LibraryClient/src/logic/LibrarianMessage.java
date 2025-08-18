package logic;

import java.io.Serializable;
import java.sql.Date;


/**
 * Represents a message from a librarian.
 * This class holds information about the message text and the date it was sent.
 */
public class LibrarianMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String messageText;
    private Date messageDate;
    
    
    /**
     * Constructs a new LibrarianMessage with the specified message text and date.
     * 
     * @param messageText The content of the message.
     * @param messageDate The date the message was sent.
     */
    public LibrarianMessage(String messageText, Date messageDate) {
        this.messageText = messageText;
        this.messageDate = messageDate;
    }
    
    // Getters
    /**
     * Gets the message text.
     * 
     * @return The content of the message.
     */
    public String getMessageText() {
        return messageText;
    }
    
    /**
     * Gets the date when the message was sent.
     * 
     * @return The date the message was sent.
     */
    public Date getMessageDate() {
        return messageDate;
    }
    
    // Setters
    /**
     * Sets the message text.
     * 
     * @param messageText The message content to set.
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    /**
     * Sets the date when the message was sent.
     * 
     * @param messageDate The date to set.
     */
    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }
}