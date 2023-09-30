package simetimer;

import javax.swing.*;
import java.io.*;



/**
 * This class manages next to all I/O for the {@link SimeTimer}.
 * It saves and loads project files as well as app options and preferences
 * and provides different file formats for the project files.
 * 
 * @author Simon Vetter
 */
public class SaveManager {
	
	/**
	 * File format PLAIN:
	 * Uses a plain text format and stores each {@link TimeChunk} in
	 * one line, represented as start date (long) in milliseconds, followed by
	 * a separator (constant) and the stopped time (long) in milliseconds.
	 * The lines are separated with {@link System}.lineSeparator()s.
	 */
	public static final int FILE_FORMAT_PLAIN = 0x504C4149;
	/**
	 * File format BYTE:
	 * Uses byte coding and stores all the {@link TimeChunk}s in sequence,
	 * each represented as start date (long) in milliseconds and the
	 * stopped time (long) in milliseconds.
	 */
	public static final int FILE_FORMAT_BYTE = 0x42595445;
	
	/**
	 * Used to separate startDate and stoppedTime in the PLAIN file format
	 */
	public static final String SEPARATOR = "\t";
	
	/**
	 * default path for the preferences file
	 */
	public static final String PREF_FILE_PATH = "SimeTimer.cfg";
	
	// unified error messages
	private static final String SAVE_ERROR				= "Save error";
	private static final String LOAD_ERROR				= "Load error";
	private static final String SAVING_FAILED			= "Saving to file failed:\n";
	private static final String LOADING_FAILED			= "Loading from file failed:\n";
	private static final String CLOSING_FAILED			= "File could not be closed.";
	private static final String REASON_FILE_NOT_FOUND	= "The file could not be found.";
	private static final String REASON_FILE_CORRUPTED	= "The file could not be read.";
	private static final String REASON_UNKNOWN			= "An unknown error occurred.";
	
	
	
	
	
	// SAVE/LOAD PROJECTS
	
	/**
	 * saves the given {@link SimeTimerProject} into the given {@link File}
	 * using the given file format. Delegates the main work to a submethod
	 * for each file format.
	 * Handles Exceptions.
	 * @param owner the {@link SimeTimer} to which {@link JOptionPane}s should be associated
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @param fileFormat an int constant to represent the file format to use
	 * @return true if loading went without Exceptions, else false
	 */
	public static boolean saveProject(SimeTimer owner, SimeTimerProject project, File saveFile, int fileFormat) {
		try {
			if (fileFormat == FILE_FORMAT_PLAIN) {
				saveProjectToPlainFile(project, saveFile);
				return true;
			} else if (fileFormat == FILE_FORMAT_BYTE) {
				saveProjectToByteFile(project, saveFile);
				return true;
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
		return false;
	}
	
	/**
	 * loads a {@link SimeTimerProject} from the given {@link File} using the
	 * given file format. Delegates the main work to a submethod
	 * for each file format.
	 * Handles Exceptions.
	 * @param owner the {@link SimeTimer} to which {@link JOptionPane}s should be associated
	 * @param saveFile saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @param fileFormat an int constant to represent the file format to use
	 * @return a new {@link SimeTimerProject} with the data from the file
	 */
	public static SimeTimerProject loadProject(SimeTimer owner, File saveFile, int fileFormat) {
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
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
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
	
	
	
	// PLAIN
	
	/**
	 * saves a given {@link SimeTimerProject} to the specified file.
	 * Uses a plain text file format.
	 * Handles Exceptions.
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @throws IOException when unknown IOExceptions occur
	 */
	public static void saveProjectToPlainFile(SimeTimerProject project, File saveFile) throws IOException {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(saveFile));
		output.write("");
		for (int i=0; i<project.size(); i++) {
			output.write(i
					+ SEPARATOR
					+ project.getTimeChunk(i).getStartDate().getTime()
					+ SEPARATOR
					+ project.getTimeChunk(i).getStoppedTime()
					+ SEPARATOR
					+ project.getTimeChunk(i).getComment()
					+ System.lineSeparator());
		}
		output.close();
	}
	
	/**
	 * loads a {@link SimeTimerProject} from the specified file.
	 * Can only read files written by {@link #saveProjectToPlainFile(SimeTimerProject, File)}.
	 * @param saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @return a new {@link SimeTimerProject} with the data from the file
	 * @throws FileNotFoundException when save file couldn't be found
	 * @throws NumberFormatException when numbers could not be parsed
	 * @throws ArrayIndexOutOfBoundsException when the file ended too soon
	 * @throws IOException when an unknown error occurred
	 */
	public static SimeTimerProject loadProjectFromPlainFile(File saveFile)
			throws FileNotFoundException, NumberFormatException, ArrayIndexOutOfBoundsException, IOException {
		SimeTimerProject result = new SimeTimerProject();
		BufferedReader input;
		input = new BufferedReader(new FileReader(saveFile));
		String line = input.readLine();
		String[] split;
		while (line != null && !line.isEmpty()) {
			split = line.split(SEPARATOR, -1);
			result.addTimeChunk(new TimeChunk(Long.parseLong(split[1]), Long.parseLong(split[2]), split[3]));
			line = input.readLine();
		}
		// try to close file
		try {
			input.close();
		} catch (IOException e) {
			System.err.println(CLOSING_FAILED);
		}
		// no exceptions
		result.sortTimes();
		return result;
	}
	
	
	
	// BYTE
	
	/**
	 * saves a given {@link SimeTimerProject} to the specified file.
	 * Uses byte coded file format.
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @throws FileNotFoundException when the save file could not be found
	 * @throws IOException when an unknown error occurred
	 */
	public static void saveProjectToByteFile(SimeTimerProject project, File saveFile)
			throws FileNotFoundException, IOException {
		DataOutputStream output = new DataOutputStream(new FileOutputStream(saveFile));
		// for every TimeChunk:
		for (int i=0; i<project.size(); i++) {
			// write startDate as long
			output.writeLong(project.getTimeChunk(i).getStartDate().getTime());
			// write stoppedTime
			output.writeLong(project.getTimeChunk(i).getStoppedTime());
			// write comment
			output.writeUTF(project.getTimeChunk(i).getComment());
		}
		output.close();
	}
	
	/**
	 * loads a {@link SimeTimerProject} from the specified file.
	 * Can only read files written by {@link #saveProjectToByteFile(SimeTimerProject, File)}.
	 * @param saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @return a new {@link SimeTimerProject} with the data from the file
	 * @throws FileNotFoundException when the save file could not be found
	 * @throws IOException when an unknown error occurred
	 */
	public static SimeTimerProject loadProjectFromByteFile(File saveFile) throws FileNotFoundException, IOException {
		SimeTimerProject result = new SimeTimerProject();
		DataInputStream input;
		input = new DataInputStream(new FileInputStream(saveFile));
		while (input.available() >= 2 * Long.BYTES) {
			result.addTimeChunk(new TimeChunk(input.readLong(), input.readLong(), input.readUTF()));
		}
		// try to close file
		try {
			input.close();
		} catch (IOException e) {
			System.err.println(CLOSING_FAILED);
		}
		// no exceptions
		result.sortTimes();
		return result;
	}
	
	
	
	// SAVE/LOAD PREFERENCES
	
	/**
	 * saves the given data into the default preferences file.
	 * Handles Exceptions.
	 * @param boolOptions all boolean option properties
	 * @param tableSize tableSize
	 * @param fileFormat fileFormat
	 * @param xPosition xPosition
	 * @param yPosition yPosition
	 * @param usedFile usedFile
	 */
	static void saveConfig(boolean[] boolOptions, int tableSize, int fileFormat, int xPosition, int yPosition, File usedFile) {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(PREF_FILE_PATH));
			// write data
			// options:
			for (boolean boolOption : boolOptions) {
				output.writeBoolean(boolOption);
			}
			output.writeInt(tableSize);
			output.writeInt(fileFormat);
			// preferences:
			output.writeInt(xPosition);
			output.writeInt(yPosition);
			output.writeBoolean(usedFile != null);
			if (usedFile != null) {
				output.writeUTF(usedFile.getAbsolutePath());
			}
			output.close();
		} catch (IOException | NullPointerException e) {
			// unknown error
			System.err.println("Configuration saving failed: " + REASON_UNKNOWN);
		}
	}
	
	/**
	 * loads configuration data from the config file
	 * and feeds them back to the {@link ConfigManager} using the
	 * setOptions() and setPreferences() methods.
	 * Handles Exceptions.
	 * @param callback the {@link ConfigManager}'s identity to
	 * 				feed the configuration data back to
	 * @return true if loading went down without Exceptions, else false
	 */
	static boolean loadAndSetConfig(ConfigManager callback) {
		// set default values
		boolean[] boolOptions = ConfigManager.DEFAULT_BOOL_OPTIONS;
		int tableSize = ConfigManager.DEFAULT_TABLE_SIZE;
		int fileFormat = ConfigManager.DEFAULT_FILE_FORMAT;
		int xPosition = ConfigManager.DEFAULT_X_POSITION;
		int yPosition = ConfigManager.DEFAULT_Y_POSITION;
		String usedPath = null;
		// is returned, states if there were loading problems
		boolean noProblems = true;
		// begin IO work
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(PREF_FILE_PATH));
			// read data
			// options:
			for (int i=0; i<boolOptions.length; i++) {
				boolOptions[i] = input.readBoolean();
			}
			tableSize = input.readInt();
			fileFormat = input.readInt();
			// preferences:
			xPosition = input.readInt();
			yPosition = input.readInt();
			if (input.readBoolean()) {
				usedPath = input.readUTF();
			}
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.out.println("Preferences file not found, using defaults.");
			noProblems = false;
		} catch (IOException e) {
			// unknown error
			System.err.println("Loading preferences file failed for unknown reason.");
			noProblems = false;
		} finally {
			// try to close file
			try {
				assert input != null;
				input.close();
			} catch (IOException e) {
				System.err.println(CLOSING_FAILED);
			} catch (NullPointerException e) {
				// wasn't opened at all
			}
		}
		// feed back options and preferences
		callback.setOptions(boolOptions, tableSize, fileFormat);
		callback.setPreferences(xPosition, yPosition, usedPath);
		return noProblems;
	}

}