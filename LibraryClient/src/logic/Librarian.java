package logic;

import java.io.Serializable;


/**
 * Represents a librarian in the system.
 * This class holds information about the librarian such as their ID, name, credentials, and contact information.
 */
public class Librarian implements Serializable {
    private int librarianId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

    
    /**
     * Constructs a new Librarian with the specified details.
     * 
     * @param librarianId The unique ID of the librarian.
     * @param firstName The first name of the librarian.
     * @param lastName The last name of the librarian.
     * @param username The username for the librarian's account.
     * @param password The password for the librarian's account.
     * @param email The email address of the librarian.
     */
    public Librarian(int librarianId, String firstName, String lastName, String username, String password, String email) {
        this.librarianId = librarianId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    
    /**
     * Gets the unique ID of the librarian.
     * 
     * @return The librarian ID.
     */
    public int getLibrarianId() {
        return librarianId;
    }

    
    /**
     * Sets the unique ID of the librarian.
     * 
     * @param librarianId The librarian ID to set.
     */
    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    /**
     * Gets the first name of the librarian.
     * 
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the librarian.
     * 
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the librarian.
     * 
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the librarian.
     * 
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the username for the librarian's account.
     * 
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the librarian's account.
     * 
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    
    /**
     * Gets the password for the librarian's account.
     * 
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the librarian's account.
     * 
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email address of the librarian.
     * 
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the librarian.
     * 
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Returns a string representation of the librarian.
     * 
     * @return A string containing the librarian's ID, name, username, password, and email.
     */
    @Override
    public String toString() {
        return librarianId + " " +
               firstName + " " +
               lastName + " " +
               username + " " +
               password + " " +
               email;
    }

}
