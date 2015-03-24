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
	
	/**
	 * default path for the preferences file
	 */
	public static final String PREFERENCES_PATH = "SimeTimer.options";
	
	private SimeTimer owner;

	/**
	 * constructor stores owner {@link JFrame}
	 * @param owner the parent {@link JFrame} for save/load dialogs
	 */
	public SaveManager(SimeTimer owner) {
		this.owner = owner;
	}
	
	
	
	// MAIN METHODS
	
	/**
	 * saves the given String into the given File and handles errors.
	 * Handles Exceptions.
	 * @param data the String to save into the given File
	 * @param saveFile the File to save the given String into
	 */
	void saveToFile(String data, File saveFile) {
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(saveFile));
			output.write(data);
			output.close();
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		"An unknown error has occured while saving.",
																		"Save Error",
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
	long loadFromFile(File saveFile) {
		BufferedReader input = null;
		long data;
		try {
			input = new BufferedReader(new FileReader(saveFile));
			data = Long.parseLong(input.readLine());
		} catch (FileNotFoundException e) {
			// save file not found
			JOptionPane.showMessageDialog(owner,
																		"The save file could not be found.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
			return -1L;
		} catch (NumberFormatException | IOException e) {
			// save file corrupted
			if (JOptionPane.showConfirmDialog(owner,
																				"The save file could not be read.\nDo you wish to delete it?",
																				"Load Error",
																				JOptionPane.YES_NO_CANCEL_OPTION,
																				JOptionPane.ERROR_MESSAGE)
					== JOptionPane.YES_OPTION) {
				// user wants to delete save file
				try {
					input.close();
				} catch (IOException e1) {}
				if (!saveFile.delete()) {
					// failed to delete file
					JOptionPane.showMessageDialog(owner,
																				"The save file could not be deleted.",
																				"Delete Error",
																				JOptionPane.ERROR_MESSAGE);
				}
			}
			return -1L;
		} finally {
			// try to close file
			try {
				input.close();
			} catch (IOException e) {
				System.err.println("Save file could not be closed.");
			}
		}
		// no Exceptions
		return data;
	}
	
	
	
	// CHUNKS
	
	/**
	 * saves a given {@link SimeTimerProject} to a file on the specified path.
	 * Uses byte coded file format.
	 * Handles Exceptions.
	 * @param project the {@link SimeTimerProject} to save
	 * @param savePath the filepath to save the {@link SimeTimerProject} in
	 */
	void saveProjectToFile(SimeTimerProject project, String savePath) {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(savePath));
			// for every TimeChunk:
			for (int i=0; i<project.getNumberOfTimeChunks(); i++) {
				// write startDate as long
				output.writeLong(project.getTimeChunk(i).getStartDate().getTime());
				// write stoppedTime
				output.writeLong(project.getTimeChunk(i).getStoppedTime());
			}
			output.close();
		} catch (FileNotFoundException e) {
			// save file not found
			JOptionPane.showMessageDialog(owner,
																		"The save file could not be found.",
																		"Save Error",
																		JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		"An unknown error has occured while saving.",
																		"Save Error",
																		JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * loads a {@link SimeTimerProject} from a file on the specified path.
	 * Can only read files written by {@link saveProjectToFile(SimeTimerProject, String)}.
	 * Handles Exceptions.
	 * @param loadPath the filepath to load the {@link SimeTimerProject} from
	 * @return a new {@link SimeTimerProject} with the data from the file
	 * 				 or null when Exceptions occur.
	 */
	SimeTimerProject loadProjectFromFile(String loadPath) {
		SimeTimerProject result = new SimeTimerProject();
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(loadPath));
			while (input.available() >= 2 * Long.BYTES) {
				result.addTimeChunk(new TimeChunk(input.readLong(),
																					input.readLong()));
			}
		} catch (FileNotFoundException e) {
			// load file not found
			JOptionPane.showMessageDialog(owner,
																		"The load file could not be found.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		"An unknown error has occured while loading.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
			return null;
		} finally {
			// try to close file
			try {
				input.close();
			} catch (IOException e) {
				System.err.println("Save file could not be closed.");
			}
		}
		// no Exceptions
		result.sortTimes();
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
	void savePreferences(int xPosition,
											 int yPosition,
											 String usedPath) {
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
			output.writeUTF(usedPath == null ? "null" : usedPath);
			output.close();
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.err.println("Preferences saving failed: File not found.");
		} catch (IOException e) {
			// unknown error
			System.err.println("Preferences saving failed: Unknown error occured while writing file.");
		}
	}
	
	/**
	 * loads preferenecs from the default preferences file
	 * and feeds them back to the {@link SimeTimer} using the
	 * setPreferences() method.
	 * Handles Exceptions.
	 */
	void loadAndSetPreferences() {
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
			System.err.println("Preferences loading failed: File not found.");
		} catch (IOException e) {
			// unknown error
			System.err.println("Preferences loading failed: Unknown error occured while reading file.");
		} finally {
			// try to close file
			try {
				input.close();
			} catch (IOException e) {
				System.err.println("Preferences file could not be closed.");
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
		if (usedPath == "null") {
			usedPath = SimeTimer.DEFAULT_PATH;
		}
		// feed back preferences
		owner.setPreferences(xPosition, yPosition, usedPath);
	}

}