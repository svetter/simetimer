package simetimer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class SaveManager {
	
	public static final int FILE_FORMAT_PLAIN = 0x504C4149,
													FILE_FORMAT_BYTE = 0x42595445;
	
	/**
	 * default path for the preferences file
	 */
	public static final String PREFERENCES_PATH = "SimeTimer.options";
	
	public static final String SEPARATOR = "\t";
	public static final String NULL_PATH = "*null*";
	
	private static final String SAVE_ERROR = "Save error",
															LOAD_ERROR = "Load error",
															SAVING_FAILED = "Saving to file failed:\n",
															LOADING_FAILED = "Loading from file failed:\n",
															PREF_SAVING_FAILED = "Preferences saving failed: ",
															PREF_LOADING_FAILED = "Preferences loading failed: ",
															CLOSING_FAILED = "File could not be closed.",
															REASON_FILE_NOT_FOUND = "The file could not be found.",
															REASON_FILE_CORRUPTED = "The file could not be read.",
															REASON_UNKNOWN = "An unknown error occured.";
	
	private SimeTimer owner;

	/**
	 * constructor stores owner {@link JFrame}
	 * @param owner the parent {@link JFrame} for save/load dialogs
	 */
	public SaveManager(SimeTimer owner) {
		this.owner = owner;
	}
	
	// TODO update documentation
	
	// MAIN METHODS
	
	/**
	 * saves the given String into the given File.
	 * Handles Exceptions.
	 * @param data the String to save into the given File
	 * @param saveFile the File to save the given String into
	 */
	public void saveToPlainFile(String data, File saveFile) {
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(saveFile));
			output.write(data);
			output.close();
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		SAVING_FAILED + REASON_UNKNOWN,
																		SAVE_ERROR,
																		JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * loads the first line from the given File, parses it to a long
	 * and returns it.
	 * Handles Exceptions.
	 * @param saveFile the File to load from
	 * @return a long parsed from the File's first line or -1L on errors
	 */
	public long loadFromPlainFile(File saveFile) {
		BufferedReader input = null;
		long data;
		try {
			input = new BufferedReader(new FileReader(saveFile));
			data = Long.parseLong(input.readLine());
		} catch (FileNotFoundException e) {
			// save file not found
			JOptionPane.showMessageDialog(owner,
																		LOADING_FAILED + REASON_FILE_NOT_FOUND,
																		LOAD_ERROR,
																		JOptionPane.ERROR_MESSAGE);
			return -1L;
		} catch (NumberFormatException | IOException e) {
			// save file corrupted
			JOptionPane.showConfirmDialog(owner,
																		LOADING_FAILED + REASON_FILE_CORRUPTED,
																		LOAD_ERROR,
																		JOptionPane.YES_NO_CANCEL_OPTION,
																		JOptionPane.ERROR_MESSAGE);
			return -1L;
		} finally {
			// try to close file
			try {
				input.close();
			} catch (IOException e) {
				System.err.println(CLOSING_FAILED);
			} catch (NullPointerException e) {
				// wasn't opened at all
			}
		}
		// no Exceptions
		return data;
	}
	
	
	
	// TIMECHUNKS
	
	public void saveProject(SimeTimerProject project, File saveFile, int fileFormat) {
		try {
			if (fileFormat == FILE_FORMAT_PLAIN) {
				saveProjectToPlainFile(project, saveFile);
			} else if (fileFormat == FILE_FORMAT_BYTE) {
				saveProjectToByteFile(project, saveFile);
			} else {
				throw new IllegalArgumentException("File format unknown");
			}
		} catch (FileNotFoundException e) {
			// save file not found
			JOptionPane.showMessageDialog(owner,
																		SAVING_FAILED + REASON_FILE_NOT_FOUND,
																		SAVE_ERROR,
																		JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		SAVING_FAILED + REASON_UNKNOWN,
																		SAVE_ERROR,
																		JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public SimeTimerProject loadProject(File saveFile, int fileFormat) {
		try {
			if (fileFormat == FILE_FORMAT_PLAIN) {
				return loadProjectFromPlainFile(saveFile);
			} else if (fileFormat == FILE_FORMAT_BYTE) {
				return loadProjectFromByteFile(saveFile);
			} else {
				throw new IllegalArgumentException("File format unknown");
			}
		} catch (FileNotFoundException e) {
			// save file not found
			JOptionPane.showMessageDialog(owner,
																		LOADING_FAILED + REASON_FILE_NOT_FOUND,
																		LOAD_ERROR,
																		JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			// save file corrupted
			JOptionPane.showMessageDialog(owner,
																		LOADING_FAILED + REASON_FILE_CORRUPTED,
																		LOAD_ERROR,
																		JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		LOADING_FAILED + REASON_UNKNOWN,
																		LOAD_ERROR,
																		JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	// BYTE
	
	/**
	 * saves a given {@link SimeTimerProject} to the specified file.
	 * Uses byte coded file format.
	 * Handles Exceptions.
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @throws FileNotFoundException,IOException 
	 */
	public void saveProjectToByteFile(SimeTimerProject project, File saveFile) throws FileNotFoundException,
																																										IOException {
		DataOutputStream output = new DataOutputStream(new FileOutputStream(saveFile));
		// for every TimeChunk:
		for (int i=0; i<project.size(); i++) {
			// write startDate as long
			output.writeLong(project.getTimeChunkAt(i).getStartDate().getTime());
			// write stoppedTime
			output.writeLong(project.getTimeChunkAt(i).getStoppedTime());
		}
		output.close();
	}
	
	/**
	 * loads a {@link SimeTimerProject} from the specified file.
	 * Can only read files written by {@link saveProjectToByteFile(SimeTimerProject, File)}.
	 * Handles Exceptions.
	 * @param saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @return a new {@link SimeTimerProject} with the data from the file
	 * 				 or null when Exceptions occur.
	 * @throws FileNotFoundException, IOException 
	 */
	public SimeTimerProject loadProjectFromByteFile(File saveFile) throws FileNotFoundException,
																																				IOException {
		SimeTimerProject result = new SimeTimerProject();
		DataInputStream input = null;
		input = new DataInputStream(new FileInputStream(saveFile));
		while (input.available() >= 2 * Long.BYTES) {
			result.addTimeChunk(new TimeChunk(input.readLong(),
																				input.readLong()));
		}
		// try to close file
		try {
			input.close();
		} catch (IOException e) {
			System.err.println(CLOSING_FAILED);
		}
		// no Exceptions
		result.sortTimes();
		return result;
	}
	
	
	// PLAIN
	
	/**
	 * saves a given {@link SimeTimerProject} to the specified file.
	 * Uses a plain text file format.
	 * Handles Exceptions.
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @throws IOException when unknown IOExceptions occur
	 */
	public void saveProjectToPlainFile(SimeTimerProject project, File saveFile) throws IOException {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(saveFile));
		output.write("");
		for (int i=0; i<project.size(); i++) {
			output.write(Long.toString(project.getTimeChunkAt(i).getStartDate().getTime()) +
									 SEPARATOR +
									 Long.toString(project.getTimeChunkAt(i).getStoppedTime()) +
									 System.lineSeparator());
		}
		output.close();
	}
	
	/**
	 * loads a {@link SimeTimerProject} from the specified file.
	 * Can only read files written by {@link saveProjectToPlainFile(SimeTimerProject, File)}.
	 * @param saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @return a new {@link SimeTimerProject} with the data from the file
	 * @throws FileNotFoundException
	 * @throws NumberFormatException
	 * @throws ArrayIndexOutOfBoundsException
	 * @throws IOException
	 */
	public SimeTimerProject loadProjectFromPlainFile(File saveFile) throws FileNotFoundException,
																																				 NumberFormatException,
																																				 ArrayIndexOutOfBoundsException,
																																				 IOException {
		SimeTimerProject result = new SimeTimerProject();
		BufferedReader input = null;
		input = new BufferedReader(new FileReader(saveFile));
		String line = input.readLine();
		String[] splitted;
		while (line != null && !line.isEmpty()) {
			splitted = line.split(SEPARATOR);
			result.addTimeChunk(new TimeChunk(Long.parseLong(splitted[0]),
					 															Long.parseLong(splitted[1])));
			line = input.readLine();
		}
		try {
			input.close();
		} catch (IOException e) {
			System.err.println(CLOSING_FAILED);
		}
		// no Exceptions
		return result;
	}
	
	

	// PREFERENCES
	
	/**
	 * saves the given data into the default preferences file.
	 * Handles Exceptions.
	 * @param xPosition last x position of the {@link JFrame}
	 * @param yPosition last y position of the {@link JFrame}
	 * @param usedPath last used save/load path
	 */
	public void savePreferences(int xPosition,
															int yPosition,
															File usedFile) {
		// check for positions too close to the screen edge
		if (xPosition < 0) {
			xPosition = 0;
		}
		if (yPosition < 0) {
			yPosition = 0;
		}
		if (xPosition > SimeTimer.SCREEN_WIDTH - SimeTimer.FRAME_WIDTH) {
			xPosition = SimeTimer.SCREEN_WIDTH - SimeTimer.FRAME_WIDTH;
		}
		if (yPosition > SimeTimer.SCREEN_HEIGHT - SimeTimer.FRAME_HEIGHT) {
			yPosition = SimeTimer.SCREEN_HEIGHT - SimeTimer.FRAME_HEIGHT;
		}
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(PREFERENCES_PATH));
			// write data
			output.writeInt(xPosition);
			output.writeInt(yPosition);
			output.writeUTF(usedFile == null ? NULL_PATH : usedFile.getAbsolutePath());
			output.close();
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.err.println(PREF_SAVING_FAILED + REASON_FILE_NOT_FOUND);
		} catch (IOException e) {
			// unknown error
			System.err.println(PREF_SAVING_FAILED + REASON_UNKNOWN);
		}
	}
	
	/**
	 * loads preferenecs from the default preferences file
	 * and feeds them back to the {@link SimeTimer} using the
	 * setPreferences() method.
	 * Handles Exceptions.
	 */
	public void loadAndSetPreferences() {
		// set default values
		int xPosition = SimeTimer.DEFAULT_X_POSITION;
		int yPosition = SimeTimer.DEFAULT_Y_POSITION;
		String usedPath = SimeTimer.DEFAULT_PATH;
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(PREFERENCES_PATH));
			// read data
			xPosition = input.readInt();
			yPosition = input.readInt();
			usedPath = input.readUTF();
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.err.println(PREF_LOADING_FAILED + REASON_FILE_NOT_FOUND);
		} catch (IOException e) {
			// unknown error
			System.err.println(PREF_LOADING_FAILED + REASON_UNKNOWN);
		} finally {
			// try to close file
			try {
				input.close();
			} catch (IOException e) {
				System.err.println(CLOSING_FAILED);
			} catch (NullPointerException e) {
				// wasn't opened at all
			}
		}
		// check validity
		if (xPosition < 0 ||
				yPosition < 0 ||
				xPosition > SimeTimer.SCREEN_WIDTH - SimeTimer.FRAME_WIDTH ||
				yPosition > SimeTimer.SCREEN_HEIGHT - SimeTimer.FRAME_HEIGHT) {
			xPosition = SimeTimer.DEFAULT_X_POSITION;
			yPosition = SimeTimer.DEFAULT_Y_POSITION;
			usedPath = SimeTimer.DEFAULT_PATH;
		}
		File usedFile = usedPath != null && !usedPath.equals(NULL_PATH) ? new File(usedPath) : null;
		// feed back preferences
		owner.setPreferences(xPosition, yPosition, usedFile);
	}

}