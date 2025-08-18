package logic;

import java.io.Serializable; // Make sure to import this
//import java.sql.Date;

import java.util.Date;

/**
 * Represents the history of changes made to a subscriber's data.
 * This class records details about each change, such as the change type,
 * the old and new values, and the date the change occurred.
 */
public class SubscriberChangesHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private int changeHistoryId;
    private int subscriberId;
    private String changeType;
    private String oldValue;
    private String newValue;
    private Date changeDate;

    /**
     * Constructs a SubscriberChangesHistory object with the specified details.
     * 
     * @param changeHistoryId the unique ID of the change history entry
     * @param subscriberId the ID of the subscriber whose data was changed
     * @param changeType the type of change (e.g., "email", "name")
     * @param oldValue the old value before the change
     * @param newValue the new value after the change
     * @param changeDate the date the change occurred
     */
    public SubscriberChangesHistory(int changeHistoryId, int subscriberId, String changeType, 
                                    String oldValue, String newValue, Date changeDate) {
        this.changeHistoryId = changeHistoryId;
        this.subscriberId = subscriberId;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeDate = changeDate;
    }

    // Getters
    /**
     * Gets the change history ID.
     * 
     * @return the unique identifier for the change history entry
     */
    public int getChangeHistoryId() {
        return changeHistoryId;
    }

    /**
     * Gets the subscriber ID.
     * 
     * @return the ID of the subscriber whose data was changed
     */
    public int getSubscriberId() {
        return subscriberId;
    }

    /**
     * Gets the type of change (e.g., "email", "name").
     * 
     * @return the type of change
     */
    public String getChangeType() {
        return changeType;
    }

    /**
     * Gets the old value before the change.
     * 
     * @return the old value
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * Gets the new value after the change.
     * 
     * @return the new value
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Gets the date the change occurred.
     * 
     * @return the change date
     */
    public Date getChangeDate() {
        return changeDate;
    }

    // Setters
    /**
     * Sets the change history ID.
     * 
     * @param changeHistoryId the new change history ID
     */
    public void setChangeHistoryId(int changeHistoryId) {
        this.changeHistoryId = changeHistoryId;
    }

    /**
     * Sets the subscriber ID.
     * 
     * @param subscriberId the new subscriber ID
     */
    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * Sets the type of change (e.g., "email", "name").
     * 
     * @param changeType the new type of change
     */
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    /**
     * Sets the old value before the change.
     * 
     * @param oldValue the new old value
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * Sets the new value after the change.
     * 
     * @param newValue the new value
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    /**
     * Sets the date the change occurred.
     * 
     * @param changeDate the new change date
     */
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    /**
     * Returns a string representation of the SubscriberChangesHistory object.
     * 
     * @return a string containing the change history ID, subscriber ID, change type,
     *         old value, new value, and change date
     */
    @Override
    public String toString() {
        return "SubscriberChangesHistory{" +
               "changeHistoryId=" + changeHistoryId +
               ", subscriberId=" + subscriberId +
               ", changeType='" + changeType + '\'' +
               ", oldValue='" + oldValue + '\'' +
               ", newValue='" + newValue + '\'' +
               ", changeDate=" + changeDate +
               '}';
    }
}
