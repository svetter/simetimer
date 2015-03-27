package simetimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a project in the {@link SimeTimer} application.
 * Main features are designed around an {@link ArrayList}
 * which holds the project's main data, the {@link TimeChunk}s.
 * 
 * @author Simon Vetter
 *
 */
public class SimeTimerProject {
	
	/**
	 * stores all the project's {@link TimeChunk}s 
	 */
	private List<TimeChunk> timeChunks;
	
	/**
	 * constructor. Initializes the {@link ArrayList}
	 */
	public SimeTimerProject() {
		timeChunks = new ArrayList<TimeChunk>(10);
	}
	
	/**
	 * returns the {@link TimeChunk} stored at the given index
	 * in the {@link ArrayList}.
	 * @param index the index of the desired {@link TimeChunk}
	 * @return the {@link TimeChunk} stored at the given index
	 */
	public TimeChunk getTimeChunkAt(int index) {
		sortTimes();
		return timeChunks.get(index);
	}
	
	/**
	 * add a new {@link TimeChunk} to the project
	 * @param timeChunk the {@link TimeChunk} to be added
	 */
	public void addTimeChunk(TimeChunk timeChunk) {
		timeChunks.add(timeChunk);
	}
	
	/**
	 * returns the number of {@link TimeChunk}s
	 * stored in the {@link ArrayList}
	 * @return the project's number of {@link TimeChunk}s
	 */
	public int size() {
		return timeChunks.size();
	}
	
	/**
	 * returns the total time of all the project's
	 * {@link TimeChunk}s added together
	 * @return the project's total time in milliseconds
	 */
	public long getProjectTime() {
		long result = 0L;
		for (TimeChunk timeChunk : timeChunks) {
			result += timeChunk.getStoppedTime();
		}
		return result;
	}
	
	/**
	 * returns the stoppedTime of the last added {@link TimeChunk}
	 * @return the stoppedTime of the last added {@link TimeChunk}
	 */
	public long getLastChunkTime() {
		try {
			return timeChunks.get(size()-1).getStoppedTime();
		} catch (IndexOutOfBoundsException e) {
			return 0L;
		}
	}
	
	/**
	 * sorts the project's {@link TimeChunk}s in ascending order
	 * by startDate firstly and stoppedTime secondly
	 */
	public void sortTimes() {
		timeChunks.sort((t1, t2) -> t1.compareTo(t2));
	}
	
}