package simetimer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Represents the segment of time between the press of the {@link SimeTimer}'s
 * start button and the press of its stop button.
 * Stores the elapsed time in a long (in milliseconds), plus
 * a {@link Date} instance from both the beginning and the end
 * of the measurement, plus an optional comment.
 * 
 * @author Simon Vetter
 */
public class TimeChunk {
	
	/**
	 * specifies the {@link DateFormat} used by the {@link #dateToString(Date)} method
	 */
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("d. M. yyyy, HH:mm:ss");
	
	private final Date startDate;
	private final long stoppedTime;
	private String comment;
	
	
	
	/**
	 * creates new TimeChunk from given arguments
	 * @param startDate a {@link Date} instance from the start of the measurement
	 * @param stoppedTime the total length of the TimeChunk, in milliseconds
	 * @param comment the comment to be associated with the TimeChunk
	 */
	public TimeChunk(Date startDate, long stoppedTime, String comment) {
		this.stoppedTime = stoppedTime;
		this.startDate = startDate;
		this.comment = comment;
	}
	/**
	 * creates new TimeChunk from given arguments
	 * @param startDateMillis the start date represented in milliseconds
	 * @param stoppedTime the total length of the TimeChunk, in milliseconds
	 * @param comment the comment to be associated with the TimeChunk
	 */
	public TimeChunk(long startDateMillis, long stoppedTime, String comment) {
		this(new Date(startDateMillis), stoppedTime, comment);
	}
	
	
	/**
	 * get the {@link Date} instance from the start of the measurement
	 * @return the {@link Date} instance from the start of the measurement
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * get the total length of the TimeChunk
	 * @return the total length of the TimeChunk, in milliseconds
	 */
	public long getStoppedTime() {
		return stoppedTime;
	}
	/**
	 * get the comment associated with this TimeChunk
	 * @return the associated comment as a String
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * sets the TimeChunk's comment
	 * @param comment the comment to be associated with the TimeChunk
	 */
	public void setComment(String comment) {
		if (comment == null) {
			throw new IllegalArgumentException("argument comment must not be null");
		}
		this.comment = comment;
	}
	
	/**
	 * compares two TimeChunks by their start time firstly and
	 * their length secondly. If both are equal, returns 0.
	 * @param otherTimeChunk the TimeChunk to compare this one too
	 * @return -1 if this TimeChunk is earler than the other one,
	 * 				 1 if it is later than the other one and
	 * 				 0 if they are equal
	 */
	public int compareTo(TimeChunk otherTimeChunk) {
		if (this.startDate.before(otherTimeChunk.startDate)) {
			return -1;
		} else if (this.startDate.after(otherTimeChunk.startDate)) {
			return 1;
		} else if (this.stoppedTime < otherTimeChunk.stoppedTime) {
			return -1;
		} else if (this.stoppedTime > otherTimeChunk.stoppedTime) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * generates a String for the table from a given {@link Date}
	 * @param date the {@link Date} to be displayed
	 * @return a {@link String} representing the {@link Date} in a readable format
	 */
	public static String dateToString(Date date) {
		return DATE_FORMAT.format(date);
	}
	
	/**
	 * generates a String for the table from a given time in milliseconds
	 * @param time the stopped time to be displayed, in milliseconds
	 * @return a {@link String} representing the stopped time in a readable format
	 */
	public static String timeToString(long time) {
		int millis = (int) (time % 1000);
		time -= millis;
		time /= 1000;
		int seconds = (int) (time % 60);
		time -= seconds;
		time /= 60;
		int minutes = (int) (time % 60);
		time -= minutes;
		time /= 60;
		int hours = (int) time;
		return hours +
				":" + (Integer.toString(minutes).length() == 2 ? minutes : "0" + minutes) +
				":" + (Integer.toString(seconds).length() == 2 ? seconds : "0" + seconds) +
				"." + (Integer.toString(millis).length() == 3 ? millis :
						Integer.toString(millis).length() == 2 ? "0" + millis : "00" + millis);
	}
	
}