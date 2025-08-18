package logic;

import java.io.Serializable;

/**
 * A wrapper class used to hold the response data and its associated type.
 * This class is designed to wrap either a list of books or subscribers
 * and allow the identification of the data type in case of a null value.
 */
public class ResponseWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private String type; // book or subscriber
    private Object data; 

    
    /**
     * Constructor for creating a ResponseWrapper object.
     * 
     * @param type The type of the data, e.g., "book" or "subscriber".
     * @param data The data to wrap (could be a list of books or subscribers).
     */
    public ResponseWrapper(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Gets the type of the data (either "book" or "subscriber").
     * 
     * @return The type of the data.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the wrapped data, which could be a list of books or subscribers.
     * 
     * @return The data associated with this response.
     */
    public Object getData() {
        return data;
    }
}
