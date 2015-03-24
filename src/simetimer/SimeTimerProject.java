package simetimer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimeTimerProject {
	
	private List<TimeChunk> timeChunks;

	public SimeTimerProject() {
		timeChunks = new ArrayList<TimeChunk>(10);
		new TimeChunk(1L, new Date());
	}
	
	public TimeChunk getTimeChunk(int index) {
		sortTimes();
		return timeChunks.get(index);
	}
	
	public void addTimeChunk(TimeChunk timeChunk) {
		timeChunks.add(timeChunk);
	}
	
	public int getNumberOfTimeChunks() {
		return timeChunks.size();
	}
	
	public void sortTimes() {
		timeChunks.sort((t1, t2) -> t1.compareTo(t2));
	}
	
}