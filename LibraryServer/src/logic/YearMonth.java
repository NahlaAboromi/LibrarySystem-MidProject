package logic;

import java.io.Serializable;
import java.time.LocalDate;


/**
 * Represents a specific year and month. This class provides methods for obtaining
 * the first and last day of the month, formatting the year and month as a string,
 * and obtaining the current month.
 */
public class YearMonth implements Serializable{
    private int year;
    private int month;

    /**
     * Constructs a YearMonth object with the specified year and month.
     * 
     * @param year the year (e.g., 2025)
     * @param month the month (1 to 12)
     * @throws IllegalArgumentException if the month is not between 1 and 12
     */
    public YearMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        this.year = year;
        this.month = month;
    }

    /**
     * Gets the year value.
     * 
     * @return the year (e.g., 2025)
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the month value.
     * 
     * @return the month (1 to 12)
     */
    public int getMonth() {
        return month;
    }

    /**
     * Gets the first day of the month as a LocalDate.
     * 
     * @return the first day of the month
     */
    public LocalDate getFirstDayOfMonth() {
        return LocalDate.of(year, month, 1);
    }

    /**
     * Gets the last day of the month as a LocalDate.
     * 
     * @return the last day of the month
     */
    public LocalDate getLastDayOfMonth() {
        return LocalDate.of(year, month, 1).withDayOfMonth(getFirstDayOfMonth().lengthOfMonth());
    }

    /**
     * Gets the current month based on the system's current date.
     * 
     * @return a YearMonth object representing the current month
     */
    public static YearMonth currentMonth() {
        LocalDate now = LocalDate.now();
        return new YearMonth(now.getYear(), now.getMonthValue());
    }

    /**
     * Returns a string representation of this YearMonth object.
     * 
     * @return a string in the format "YearMonth{year=YYYY, month=MM}"
     */
    @Override
    public String toString() {
        return "YearMonth{year=" + year + ", month=" + month + '}';
    }

    
    /**
     * Returns the year and month as a formatted string in the "YYYY-MM" format.
     * 
     * @return a string in the format "YYYY-MM"
     */
    public String toFormattedString() {
        return String.format("%04d-%02d", year, month);
    }
}

