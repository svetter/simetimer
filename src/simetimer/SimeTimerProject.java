package simetimer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimeTimerProject {
	
	private List<TimeChunk> timeChunks;

	public SimeTimerProject() {
		timeChunks = new ArrayList<TimeChunk>(10);
		new TimeChunk(1L, new Date());
	}
	
	TimeChunk getTimeChunk(int index) {
		sortTimes();
		return timeChunks.get(index);
	}
	
	void addTimeChunk(TimeChunk timeChunk) {
		timeChunks.add(timeChunk);
	}
	
	int getNumberOfTimeChunks() {
		return timeChunks.size();
	}
	
	String toSaveString() throws FileNotFoundException {
		sortTimes();
		StringBuilder result = new StringBuilder();
		//timeChunks.stream().forEach(chunk -> result.append(chunk.toSaveString()));
		
		
		
		return result.toString();
	}
	
	private void sortTimes() {
		timeChunks.sort((t1, t2) -> t1.compareTo(t2));
	}
	
}