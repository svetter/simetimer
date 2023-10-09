/*
 * Copyright 2015, 2020, 2023 Simon Vetter
 *
 * This file is part of SimeTimer.
 *
 * SimeTimer is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * SimeTimer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with SimeTimer.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package simetimer;

import java.util.ArrayList;
import java.util.List;



/**
 * Models a project in the {@link SimeTimer} application.
 * Main features are designed around an {@link ArrayList}
 * which holds the project's main data, the {@link TimeChunk}s.
 * 
 * @author Simon Vetter
 */
public class SimeTimerProject {
	
	/**
	 * stores all the project's {@link TimeChunk}s 
	 */
	private final List<TimeChunk> timeChunks;
	
	/**
	 * constructor. Initializes the {@link ArrayList}
	 */
	public SimeTimerProject() {
		timeChunks = new ArrayList<>(10);
	}
	
	/**
	 * returns the {@link TimeChunk} stored at the given index in the {@link ArrayList}.
	 * @param index the index of the desired {@link TimeChunk}
	 * @return the {@link TimeChunk} stored at the given index
	 */
	public TimeChunk getTimeChunk(int index) {
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
	 * returns the number of {@link TimeChunk}s stored in the {@link ArrayList}
	 * @return the project's number of {@link TimeChunk}s
	 */
	public int size() {
		return timeChunks.size();
	}
	
	/**
	 * returns the total time of all the project's {@link TimeChunk}s added together
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
	 * returns the last added {@link TimeChunk}
	 * @return the last added {@link TimeChunk}
	 * 				 or null if there is none
	 */
	public TimeChunk getLastChunk() {
		try {
			return timeChunks.get(size()-1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * returns a {@link String} array with length 3 consisting of the given
	 * row index + 1 and date and time of the TimeChunk represented as readable Strings.
	 * @param chunkIndex the index of the desired List entry
	 * @return a {@link String} array with length 3
	 */
	public String[] getStringArray(int chunkIndex) {
		return new String[] {Integer.toString(chunkIndex + 1),
												 TimeChunk.dateToString(timeChunks.get(chunkIndex).getStartDate()),
												 TimeChunk.timeToString(timeChunks.get(chunkIndex).getStoppedTime()),
												 timeChunks.get(chunkIndex).getComment()};
	}
	
	/**
	 * sorts the project's {@link TimeChunk}s in ascending order
	 * by startDate first and stoppedTime second
	 */
	public void sortTimes() {
		timeChunks.sort(TimeChunk::compareTo);
	}
	
}