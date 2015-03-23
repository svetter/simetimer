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
	
	private File preferencesFile;
	private SimeTimer owner;

	/**
	 * constructor. Initializes preferences {@link File}
	 * and stores owner {@link JFrame}
	 * @param owner the parent {@link JFrame} for save/load dialogs
	 */
	public SaveManager(SimeTimer owner) {
		preferencesFile = new File(PREFERENCES_PATH);
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
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(saveFile));
		} catch (FileNotFoundException e) {
			// save file not found
			JOptionPane.showMessageDialog(owner,
																		"The save file could not be found.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
			return -1L;
		}
		// no Exception
		long data;
		try {
			data = Long.parseLong(input.readLine());
			input.close();
		} catch (NumberFormatException e) {
			// save file corrupted
			try {
				input.close();
			} catch (IOException e1) {}
			if (JOptionPane.showConfirmDialog(owner,
																				"The save file could not be read.\nDo you wish to delete it?",
																				"Load Error",
																				JOptionPane.YES_NO_CANCEL_OPTION,
																				JOptionPane.ERROR_MESSAGE)
					== JOptionPane.YES_OPTION) {
				// user wants to delete save file
				if (!saveFile.delete()) {
					JOptionPane.showMessageDialog(owner,
																				"The save file could not be deleted.",
																				"Delete Error",
																				JOptionPane.ERROR_MESSAGE);
				}
			}
			return -1L;
		} catch (IOException e) {
			// unknown error
			try {
				input.close();
			} catch (IOException e1) {}
			JOptionPane.showMessageDialog(owner,
																		"An unknown error has occured.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
			return -1L;
		}
		// no Exceptions
		return data;
	}
	
	
	
	// CHUNKS
	
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
	
	SimeTimerProject loadProjectFromFile(String loadPath) {
		SimeTimerProject result = new SimeTimerProject();
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(loadPath));
			while (input.available() > Long.BYTES) {
				result.addTimeChunk(new TimeChunk(input.readLong(),
																					input.readLong()));
			}
			input.close();
		} catch (FileNotFoundException e) {
			// load file not found
			JOptionPane.showMessageDialog(owner,
																		"The load file could not be found.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			// unknown error
			JOptionPane.showMessageDialog(owner,
																		"An unknown error has occured while loading.",
																		"Load Error",
																		JOptionPane.ERROR_MESSAGE);
		}
		// TODO R�ckgabe bei Fehlern?
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
		// compile data
		StringBuilder data = new StringBuilder();
		data.append(xPosition)
				.append(System.lineSeparator())
				.append(yPosition)
				.append(System.lineSeparator())
				.append(usedPath)
				.append(System.lineSeparator());
		// write into file
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(preferencesFile));
			output.write(data.toString());
			output.close();
		} catch (IOException e) {
			// unknown error
			System.err.println("Preferences saving failed.");
		}
	}
	
	/**
	 * loads preferenecs from the default preferences file
	 * and feeds them back to the {@link SimeTimer} using the
	 * setPreferences() method.
	 * Handles Exceptions.
	 */
	void loadAndSetPreferences() {
		int xPosition = SimeTimer.DEFAULT_X_POSITION;
		int yPosition = SimeTimer.DEFAULT_Y_POSITION;
		String usedPath = SimeTimer.DEFAULT_PATH;
		String[] lines;
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(preferencesFile));
			// read lines
			lines = input.lines().toArray(i -> new String[i]);
			// retrieve preferences
			xPosition = (int) Double.parseDouble(lines[0]);
			yPosition = (int) Double.parseDouble(lines[1]);
			usedPath = lines[2].equals("null") ? null : lines[2];
			// lastly
			input.close();
		} catch (FileNotFoundException e) {
			// preferences file couldn't be found
			System.err.println("Preferences loading failed: File not found.");
		} catch (NumberFormatException e) {
			// file corrupted
			System.err.println("Preferences loading failed: File corrupted.");
		} catch (IOException e) {
			// unknown error
			System.err.println("Failed to close preferences file.");
		}
		// feed back preferences
		owner.setPreferences(xPosition, yPosition, usedPath);
	}

}