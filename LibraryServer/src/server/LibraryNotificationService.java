package server;
import javax.mail.Session;

import logic.Book;
import logic.Subscriber;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


/**
 * A service to handle sending email notifications for library-related events.
 * This includes notifications about book arrivals and reminders for book returns.
 */
public class LibraryNotificationService {

    private String smtpHostServer;

    
    /**
     * Constructor to initialize the LibraryNotificationService with the specified SMTP host server.
     *
     * @param smtpHostServer The SMTP host server address to use for sending emails.
     */
    public LibraryNotificationService(String smtpHostServer) {
        this.smtpHostServer = smtpHostServer;
    }

    
    /**
     * Sends a notification to a subscriber informing them that their requested book has arrived at the library.
     * 
     * @param subscriberName The name of the subscriber to receive the notification.
     * @param bookName The name of the book that has arrived.
     * @param email The email address of the subscriber to send the notification to.
     */
    public void sendBookArrivalNotification(String subscriberName, String bookName,String email ) {
        try {
            // Step 1: Configure SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");  // Use port 587 for TLS
            props.put("mail.smtp.starttls.enable", "true");  // Enable STARTTLS for port 587
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", "10000");  // Optional: Set connection timeout
            props.put("mail.smtp.timeout", "10000");  // Optional: Set socket timeout

            // Step 2: Create a session with authentication
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication("blibbraude@gmail.com", "etmp nqvo jmzi chpz");
                }
            });

            // Step 3: Define the email content
            String subject = "Library Notification: Book Arrival";
            String body = "Dear " + subscriberName + ",\n\n"
            	    + "We are pleased to inform you that your book " + bookName + " has arrived at the library and is ready for pick-up during our working hours.\n\n"
            	    + "Please be advised that if the book is not collected within the next two days, your order will be canceled and removed from the system.\n\n"
            	    + "Best regards,\nLibrary Team";
            // Step 4: Send the email
            EmailUtil.sendEmail(session, email, subject, body);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Sends a reminder notification to a subscriber informing them that a borrowed book is due for return.
     * 
     * @param subscriberName The name of the subscriber to receive the reminder.
     * @param bookName The name of the book that is due for return.
     * @param email The email address of the subscriber to send the reminder to.
     */
    public void sendReturnReminderNotificationForBorrow(String subscriberName, String bookName, String email) {
        try {
            // Step 1: Configure SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHostServer); // Use the provided SMTP server
            props.put("mail.smtp.port", "587");  // Use port 587 for TLS
            props.put("mail.smtp.starttls.enable", "true");  // Enable STARTTLS for port 587
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", "10000");  // Optional: Set connection timeout
            props.put("mail.smtp.timeout", "10000");  // Optional: Set socket timeout

            // Step 2: Create a session with authentication
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication("blibbraude@gmail.com", "etmp nqvo jmzi chpz"); // Replace with actual credentials
                }
            });

            // Step 3: Define the email content
            String subject = "Library Notification: Book Return Reminder";
            String body = String.format(
                "Dear %s,\n\n" +
                "This is a friendly reminder that the book \"%s\" is due for return tomorrow.\n" +
                "Please make sure to return it by the end of the day to avoid any late fees.\n\n" +
                "Thank you for using our library services!\n\n" +
                "Best regards,\n" +
                "Your Library Team",
                subscriberName, bookName
            );

            // Step 4: Send the email
            EmailUtil.sendEmail(session, email, subject, body);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
