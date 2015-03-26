package simetimer;

import java.util.ArrayList;
import java.util.List;

// TODO: Documentation

/**
 * models a project in the SimeTimer application
 * @author Simon Vetter
 *
 */
public class SimeTimerProject {
	
	private List<TimeChunk> timeChunks;

	public SimeTimerProject() {
		timeChunks = new ArrayList<TimeChunk>(10);
	}
	
	public TimeChunk getTimeChunkAt(int index) {
		sortTimes();
		return timeChunks.get(index);
	}
	
	public void addTimeChunk(TimeChunk timeChunk) {
		timeChunks.add(timeChunk);
	}
	
	public int size() {
		return timeChunks.size();
	}
	
	public long getProjectTime() {
		long result = 0L;
		for (TimeChunk timeChunk : timeChunks) {
			result += timeChunk.getStoppedTime();
		}
		return result;
	}
	
	public long getLastChunkTime() {
		try {
			return timeChunks.get(size()-1).getStoppedTime();
		} catch (IndexOutOfBoundsException e) {
			return 0L;
		}
	}
	
	public void sortTimes() {
		timeChunks.sort((t1, t2) -> t1.compareTo(t2));
	}
	
}