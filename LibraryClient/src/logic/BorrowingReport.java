package logic;

import java.io.Serializable;


/**
 * Represents a report about book borrowings for a specific month and year.
 * Includes the option to store a chart image for visualization of the data.
 */
public class BorrowingReport implements Serializable {
    private int month;
    private int year;
    private byte[] chartImage;
    private boolean exists;
    
    
    /**
     * Constructor used for requests. 
     * Initializes a report for a specific month and year, with no chart image.
     * @param month The month for the report.
     * @param year The year for the report.
     */
    public BorrowingReport(int month, int year) {
        this.month = month;
        this.year = year;
        this.exists = false;
    }
    
    /**
     * Constructor used for responses. 
     * Initializes a report with a chart image, indicating that the report exists.
     * @param month The month for the report.
     * @param year The year for the report.
     * @param chartImage The chart image to visualize the report data.
     */
    public BorrowingReport(int month, int year, byte[] chartImage) {
        this.month = month;
        this.year = year;
        this.chartImage = chartImage;
        this.exists = (chartImage != null);
    }
    
    // Getters and Setters
    
    /**
     * Gets the month of the report.
     * @return The month of the report.
     */
    public int getMonth() { return month; }
    
    /**
     * Sets the month for the report.
     * @param month The month of the report.
     */
    public void setMonth(int month) { this.month = month; }
    
    /**
     * Gets the year of the report.
     * @return The year of the report.
     */
    public int getYear() { return year; }
    
    /**
     * Sets the year for the report.
     * @param year The year of the report.
     */
    public void setYear(int year) { this.year = year; }
    
    /**
     * Gets the chart image for the report.
     * @return The chart image.
     */
    public byte[] getChartImage() { return chartImage; }
    
    /**
     * Sets the chart image for the report.
     * @param chartImage The chart image.
     */
    public void setChartImage(byte[] chartImage) { this.chartImage = chartImage; }
    
    /**
     * Checks if the report exists (i.e., if it has a chart image).
     * @return True if the report exists, false otherwise.
     */
    public boolean exists() { return exists; }
    
    /**
     * Sets the existence status of the report.
     * @param exists True if the report exists, false otherwise.
     */
    public void setExists(boolean exists) { this.exists = exists; }
}
