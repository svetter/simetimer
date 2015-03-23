package simetimer;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class SaveManager {
	
	private File preferences;
	private Component owner;

	public SaveManager(Component owner) {
		preferences = new File("SimeTimer.options");
		this.owner = owner;
	}
	
	
	
	// MAIN METHODS
	
	/**
	 * saves the given String into the given File and handles errors
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
	 * and returns it, handles errors
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
	
	

	// PREFERENCES
	
	/**
	 * saves the given String into the default preferences file
	 * @param data the String to save in the preferences file
	 * 						 divided by {@link System.lineSeparator()}
	 * @throws IOException on unknown I/O errors
	 */
	void savePreferences(String data) throws IOException {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(preferences));
		output.write(data);
		output.close();
	}
	
	/**
	 * loads a set of Strings from the default preferences file
	 * and returns it in an array
	 * @return a String array containing the lines of the default preferences file 
	 * @throws FileNotFoundException when the preferences file could not be found
	 * @throws IOException on unknown I/O errors
	 */
	String[] loadPreferences() throws FileNotFoundException, IOException {
		String[] result;
		BufferedReader input;
		input = new BufferedReader(new FileReader(preferences));
		result = input.lines().toArray(i -> new String[i]);
		input.close();
		return result;
	}

}