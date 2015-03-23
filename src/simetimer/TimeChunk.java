package simetimer;

import java.util.Date;


/**
 * Represents the segment of time between the press of the timer's
 * start button and the press of its stop button.
 * Stores the elapsed time in a long (in milliseconds), plus
 * a {@link Date} instance from both the beginning and the end
 * of the measurement.
 * @author Simon Vetter
 *
 */
public class TimeChunk {
	private long stoppedTime;
	private Date startDate;
	
	
	/**
	 * creates new TimeChunk from given arguments
	 * @param stoppedTime the total length of the TimeChunk, in milliseconds
	 * @param startDate a {@link Date} instance from the start of the measurement
	 */
	TimeChunk(long stoppedTime, Date startDate) {
		this.stoppedTime = stoppedTime;
		this.startDate = startDate;
	}
	/**
	 * creates new TimeChunk from given arguments
	 * @param startDateMillis the start date represented in milliseconds
	 * @param stoppedTime the total length of the TimeChunk, in milliseconds
	 */
	TimeChunk(long startDateMillis, long stoppedTime) {
		this(stoppedTime, new Date(startDateMillis));
	}
	
	
	/**
	 * get the total length of the TimeChunk
	 * @return the total length of the TimeChunk, in milliseconds
	 */
	long getStoppedTime() {
		return stoppedTime;
	}
	
	/**
	 * get the {@link Date} instance from the start of the measurement
	 * @return the {@link Date} instance from the start of the measurement
	 */
	Date getStartDate() {
		return startDate;
	}
	
	/**
	 * get the {@link Date} instance from the end of the measurement
	 * @return the {@link Date} instance from the end of the measurement
	 */
	Date getEndDate() {
		return new Date(startDate.getTime() + stoppedTime);
	}
	
	int compareTo(TimeChunk otherTimeChunk) {
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
	
}