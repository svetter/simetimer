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

import javax.swing.JOptionPane;


/**
 * This class manages next to all of the I/O for the {@link SimeTimer}.
 * It saves and loads project files as well as app options and preferences
 * and provides different file formats for the project files.
 * 
 * @author Simon Vetter
 *
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
	 * Used to represent a nonexistent usedFile path in the method
	 * {@link #saveConfig(boolean, boolean, boolean, boolean, boolean, int, int, int, File)}.
	 * An empty file will be translated into this {@link String}.
	 */
	public static final String NULL_PATH = "*null*";
	
	/**
	 * default path for the preferences file
	 */
	public static final String CONFIG_PATH = "SimeTimer.cfg";
	
	// unified error messages
	private static final String SAVE_ERROR = "Save error",
															LOAD_ERROR = "Load error",
															SAVING_FAILED = "Saving to file failed:\n",
															LOADING_FAILED = "Loading from file failed:\n",
															CONFIG_SAVING_FAILED = "Configuration saving failed: ",
															CONFIG_LOADING_FAILED = "Configuration loading failed: ",
															CLOSING_FAILED = "File could not be closed.",
															REASON_FILE_NOT_FOUND = "The file could not be found.",
															REASON_FILE_CORRUPTED = "The file could not be read.",
															REASON_UNKNOWN = "An unknown error occured.";
	
	
	
	
	
	// SAVE/LOAD PROJECTS
	
	/**
	 * saves the given {@link SimeTimerProject} into the given {@link File}
	 * using the given file format. Delegates the main work to a submethod
	 * for each file format.
	 * Handles Exceptions.
	 * @param owner the {@link SimeTimer} to which {@link JOptionPane}s should
	 * 				be associated
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @param fileFormat an int constant to represent the file format to use
	 */
	public static void saveProject(SimeTimer owner,
																 SimeTimerProject project,
																 File saveFile,
																 int fileFormat) {
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
	
	/**
	 * loads a {@link SimeTimerProject} from the given {@link File} using the
	 * given file format. Delegates the main work to a submethod
	 * for each file format.
	 * Handles Exceptions.
	 * @param owner the {@link SimeTimer} to which {@link JOptionPane}s should
	 * 				be associated
	 * @param saveFile saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @param fileFormat an int constant to represent the file format to use
	 * @return a new {@link SimeTimerProject} with the data from the file
	 */
	public static SimeTimerProject loadProject(SimeTimer owner,
																						 File saveFile,
																						 int fileFormat) {
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
	public static void saveProjectToPlainFile(SimeTimerProject project,
																						File saveFile) throws IOException {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(saveFile));
		output.write("");
		for (int i=0; i<project.size(); i++) {
			output.write(Integer.toString(i)
									 + SEPARATOR
									 + Long.toString(project.getTimeChunk(i).getStartDate().getTime())
									 + SEPARATOR
									 + Long.toString(project.getTimeChunk(i).getStoppedTime())
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
	 * @throws IOException when an unknown error occured
	 */
	public static SimeTimerProject loadProjectFromPlainFile(File saveFile) throws FileNotFoundException,
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
			result.addTimeChunk(new TimeChunk(Long.parseLong(splitted[1]),
					 															Long.parseLong(splitted[2]),
					 															splitted[3]));
			line = input.readLine();
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
	
	
	
	// BYTE
	
	/**
	 * saves a given {@link SimeTimerProject} to the specified file.
	 * Uses byte coded file format.
	 * @param project the {@link SimeTimerProject} to save
	 * @param saveFile the {@link File} to save the {@link SimeTimerProject} in
	 * @throws FileNotFoundException when the save file could not be found
	 * @throws IOException when an unknown error occured
	 */
	public static void saveProjectToByteFile(SimeTimerProject project,
																					 File saveFile) throws FileNotFoundException,
																					 											 IOException {
		DataOutputStream output = new DataOutputStream(new FileOutputStream(saveFile));
		// for every TimeChunk:
		for (int i=0; i<project.size(); i++) {
			// write startDate as long
			output.writeLong(project.getTimeChunk(i).getStartDate().getTime());
			// write stoppedTime
			output.writeLong(project.getTimeChunk(i).getStoppedTime());
		}
		output.close();
	}
	
	/**
	 * loads a {@link SimeTimerProject} from the specified file.
	 * Can only read files written by {@link #saveProjectToByteFile(SimeTimerProject, File)}.
	 * @param saveFile the {@link File} to load the {@link SimeTimerProject} from
	 * @return a new {@link SimeTimerProject} with the data from the file
	 * @throws FileNotFoundException when the save file could not be found
	 * @throws IOException when an unknown error occured
	 */
	public static SimeTimerProject loadProjectFromByteFile(File saveFile) throws FileNotFoundException,
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
	
	
	
	// SAVE/LOAD PREFERENCES
	
	/**
	 * saves the given data into the default preferences file.
	 * Handles Exceptions.
	 * @param loadLastSaveOnStartup loadLastSaveOnStartup
	 * @param askForCommentOnStop askForCommentOnStop
	 * @param askForCommentOnCut askForCommentOnCut
	 * @param askForSaveOnLoad askForSaveOnLoad
	 * @param askForSaveOnClose askForSaveOnClose
	 * @param fileFormat fileFormat
	 * @param xPosition xPosition
	 * @param yPosition yPosition
	 * @param usedFile usedFile
	 */
	static void saveConfig(boolean loadLastSaveOnStartup,
												 boolean askForCommentOnStop,
												 boolean askForCommentOnCut,
												 boolean askForSaveOnLoad,
												 boolean askForSaveOnClose,
												 int fileFormat,
												 int xPosition,
												 int yPosition,
												 File usedFile) {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(CONFIG_PATH));
			// write data
			// options:
			output.writeBoolean(loadLastSaveOnStartup);
			output.writeBoolean(askForCommentOnStop);
			output.writeBoolean(askForCommentOnCut);
			output.writeBoolean(askForSaveOnLoad);
			output.writeBoolean(askForSaveOnClose);
			output.writeInt(fileFormat);
			// preferences:
			output.writeInt(xPosition);
			output.writeInt(yPosition);
			output.writeBoolean(usedFile != null);
			if (usedFile != null) {
				output.writeUTF(usedFile.getAbsolutePath());
			}
			output.close();
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.err.println(CONFIG_SAVING_FAILED + REASON_FILE_NOT_FOUND);
		} catch (IOException | NullPointerException e) {
			// unknown error
			System.err.println(CONFIG_SAVING_FAILED + REASON_UNKNOWN);
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
		boolean loadLastSaveOnStartup = ConfigManager.DEFAULT_LOAD_LAST_SAVE_ON_STARTUP;
		boolean askForCommentOnStop = ConfigManager.DEFAULT_ASK_FOR_COMMENT_ON_STOP;
		boolean askForCommentOnCut = ConfigManager.DEFAULT_ASK_FOR_COMMENT_ON_CUT;
		boolean askForSaveOnLoad = ConfigManager.DEFAULT_ASK_FOR_SAVE_ON_LOAD;
		boolean askForSaveOnClose = ConfigManager.DEFAULT_ASK_FOR_SAVE_ON_CLOSE;
		int fileFormat = ConfigManager.DEFAULT_FILE_FORMAT;
		int xPosition = ConfigManager.DEFAULT_X_POSITION;
		int yPosition = ConfigManager.DEFAULT_Y_POSITION;
		String usedPath = ConfigManager.DEFAULT_PATH;
		// is returned, states if there were loading problems
		boolean noProblems = true;
		// begin IO work
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(CONFIG_PATH));
			// read data
			// options:
			loadLastSaveOnStartup = input.readBoolean();
			askForCommentOnStop = input.readBoolean();
			askForCommentOnCut = input.readBoolean();
			askForSaveOnLoad = input.readBoolean();
			askForSaveOnClose = input.readBoolean();
			fileFormat = input.readInt();
			// preferences:
			xPosition = input.readInt();
			yPosition = input.readInt();
			boolean usedPathWritten = input.readBoolean();
			if (usedPathWritten) {
				usedPath = input.readUTF();
			}
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.err.println(CONFIG_LOADING_FAILED + REASON_FILE_NOT_FOUND);
			noProblems = false;
		} catch (IOException e) {
			// unknown error
			System.err.println(CONFIG_LOADING_FAILED + REASON_UNKNOWN);
			noProblems = false;
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
		// feed back options and preferences
		callback.setOptions(loadLastSaveOnStartup,
												askForCommentOnStop,
												askForCommentOnCut,
												askForSaveOnLoad,
												askForSaveOnClose,
												fileFormat);
		callback.setPreferences(xPosition,
														yPosition,
														usedPath);
		return noProblems;
	}
	
	
	
	// SAVE/LOAD SINGLE TIMES
	
	/**
	 * saves the given String into the given File.
	 * Handles Exceptions.
	 * @param owner the {@link SimeTimer} to which {@link JOptionPane}s should
	 * 				be associated
	 * @param data the String to save into the given File
	 * @param saveFile the File to save the given String into
	 */
	public static void saveToPlainFile(SimeTimer owner,
																		 String data,
																		 File saveFile) {
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
	 * @param owner the {@link SimeTimer} to which {@link JOptionPane}s should
	 * 				be associated
	 * @param saveFile the File to load from
	 * @return a long parsed from the File's first line or -1L on errors
	 */
	public static long loadFromPlainFile(SimeTimer owner,
																			 File saveFile) {
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

}