package logic;

import java.io.Serializable;

/**
 * Represents a subscriber in the system, including their personal information,
 * subscription details, and account status.
 */
public class Subscriber implements Serializable {
	private static final long serialVersionUID = 1L;
	private int subscriber_id;
	private String subscriber_name;
	private String username;
	private String subscriber_email;
	private String subscriber_phone_number;
	private String password;
	private int detailed_subscription_history;
	private String status;

	/**
	 * Constructs a Subscriber object with the specified details.
	 * 
	 * @param id       the unique ID of the subscriber
	 * @param name     the name of the subscriber
	 * @param username the username of the subscriber
	 * @param email    the email address of the subscriber
	 * @param phone    the phone number of the subscriber
	 * @param password the password for the subscriber's account
	 * @param history  the detailed subscription history
	 */
	public Subscriber(int id, String name, String username, String email, String phone, String password, int history) {
		this.subscriber_id = id;
		this.subscriber_name = name;
		this.username = username;
		this.subscriber_email = email;
		this.subscriber_phone_number = phone;
		this.password = password;
		this.detailed_subscription_history = history;
		this.status = "active";
	}

	/**
	 * Constructs a Subscriber object with the specified details.
	 * 
	 * @param subscriber_id                 the unique ID of the subscriber
	 * @param subscriber_name               the name of the subscriber
	 * @param detailed_subscription_history the detailed subscription history
	 * @param subscriber_phone_number       the phone number of the subscriber
	 * @param subscriber_email              the email address of the subscriber
	 */
	public Subscriber(int subscriber_id, String subscriber_name, int detailed_subscription_history,
			String subscriber_phone_number, String subscriber_email) {
		this.subscriber_id = subscriber_id;
		this.subscriber_name = subscriber_name;
		this.subscriber_phone_number = subscriber_phone_number;
		this.subscriber_email = subscriber_email;
		this.detailed_subscription_history = detailed_subscription_history;
	}

	/**
     * Constructs a Subscriber object with the specified details, including the account status.
     * 
     * @param id the unique ID of the subscriber
     * @param name the name of the subscriber
     * @param username the username of the subscriber
     * @param email the email address of the subscriber
     * @param phone the phone number of the subscriber
     * @param password the password for the subscriber's account
     * @param history the detailed subscription history
     * @param status the status of the subscriber's account
     */
	public Subscriber(int id, String name, String username, String email, String phone, String password, int history,
			String status) {
		this.subscriber_id = id;
		this.subscriber_name = name;
		this.username = username;
		this.subscriber_email = email;
		this.subscriber_phone_number = phone;
		this.password = password;
		this.detailed_subscription_history = history;
		this.status = status; // הגדרת הסטטוס
	}

	// Getters
	/**
     * Gets the subscriber's unique ID.
     * 
     * @return the unique ID of the subscriber
     */
	public int getSubscriberId() {
		return subscriber_id;
	}

	/**
     * Gets the name of the subscriber.
     * 
     * @return the name of the subscriber
     */
	public String getSubscriberName() {
		return subscriber_name;
	}

	/**
     * Gets the username of the subscriber.
     * 
     * @return the username of the subscriber
     */
	public String getUsername() {
		return username;
	}

	/**
     * Gets the email address of the subscriber.
     * 
     * @return the email address of the subscriber
     */
	public String getSubscriberEmail() {
		return subscriber_email;
	}

	/**
     * Gets the phone number of the subscriber.
     * 
     * @return the phone number of the subscriber
     */
	public String getSubscriberPhoneNumber() {
		return subscriber_phone_number;
	}

	/**
     * Gets the password of the subscriber's account.
     * 
     * @return the password of the subscriber's account
     */
	public String getPassword() {
		return password;
	}

	 /**
     * Gets the detailed subscription history.
     * 
     * @return the detailed subscription history of the subscriber
     */
	public int getDetailedSubscriptionHistory() {
		return detailed_subscription_history;
	}

	/**
     * Gets the status of the subscriber's account.
     * 
     * @return the status of the subscriber's account
     */
	public String getStatus() {
		return status;
	}

	// Setters
	/**
     * Sets the unique ID of the subscriber.
     * 
     * @param id the new unique ID of the subscriber
     */
	public void setSubscriberId(int id) {
		this.subscriber_id = id;
	}

	/**
     * Sets the name of the subscriber.
     * 
     * @param name the new name of the subscriber
     */
	public void setSubscriberName(String name) {
		this.subscriber_name = name;
	}

	/**
     * Sets the username of the subscriber.
     * 
     * @param username the new username of the subscriber
     */
	public void setUsername(String username) {
		this.username = username;
	}

	 /**
     * Sets the email address of the subscriber.
     * 
     * @param email the new email address of the subscriber
     */
	public void setSubscriberEmail(String email) {
		this.subscriber_email = email;
	}

	/**
     * Sets the phone number of the subscriber.
     * 
     * @param phone the new phone number of the subscriber
     */
	public void setSubscriberPhoneNumber(String phone) {
		this.subscriber_phone_number = phone;
	}

	 /**
     * Sets the password of the subscriber's account.
     * 
     * @param password the new password of the subscriber's account
     */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
     * Sets the detailed subscription history.
     * 
     * @param history the new detailed subscription history of the subscriber
     */
	public void setDetailedSubscriptionHistory(int history) {
		this.detailed_subscription_history = history;
	}

	/**
     * Sets the status of the subscriber's account.
     * 
     * @param status the new status of the subscriber's account
     */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
     * Returns a string representation of the Subscriber object, containing key details such as
     * ID, name, phone number, and email.
     * 
     * @return a string representation of the Subscriber object
     */
	@Override
	public String toString() {
		return "[ID=" + subscriber_id + ", Name=" + subscriber_name + ", Phone=" + subscriber_phone_number + ", Email="
				+ subscriber_email + "]";
	}

}
