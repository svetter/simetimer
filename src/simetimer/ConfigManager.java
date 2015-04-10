package simetimer;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * This class handles the {@link SimeTimer}'s options and preferences
 * with options being the part of the configuration that the user
 * can directly set and change in the {@link OptionFrame} and preferences
 * being slightly more subtle configuration details that are saved and
 * loaded in the background. 
 * 
 * @author Simon Vetter
 *
 */
public class ConfigManager {
	
	private SimeTimer owner;
	
	
	/**
	 * sets the minimum number of rows on the table that has to be displayed
	 */
	public static final int MIN_TABLE_ROW_COUNT = 0;
	/**
	 * sets the maximum number of rows on the table that can be displayed
	 */
	public static final int MAX_TABLE_ROW_COUNT = 50;
	
	// DEFAULT OPTIONS AND PREFERENCES
	// Options
	/**
	 * default option for whether the app automatically loads
	 * the last used project (if available) when the app is started
	 */
	public static final boolean DEFAULT_LOAD_LAST_SAVE_ON_STARTUP = true;
	/**
	 * default option for whether the app automatically saves all changes
	 * to the last used {@link File} immediately
	 */
	public static final boolean DEFAULT_AUTOSAVE = false; 
	/**
	 * default option for whether the app shows a dialog which
	 * asks the user to type in a comment for the last
	 * {@link TimeChunk} when they press the stop button
	 */
	public static final boolean DEFAULT_ASK_FOR_COMMENT_ON_STOP = false;
	/**
	 * default option for whether the app shows a dialog which
	 * asks the user to type in a comment for the last
	 * {@link TimeChunk} when they press the cut button
	 */
	public static final boolean DEFAULT_ASK_FOR_COMMENT_ON_CUT = false;
	/**
	 * default option for whether the SimeTimer asks the user
	 * if he wants to save when they attempt to load a project
	 * while having another unsaved one open
	 */
	public static final boolean DEFAULT_ASK_FOR_SAVE_ON_LOAD = true;
	/**
	 * default option for whether the SimeTimer asks the user if he
	 * wants to save when they attempt to close an unsaved project
	 */
	public static final boolean DEFAULT_ASK_FOR_SAVE_ON_CLOSE = true;
	/**
	 * array representation of all the boolean default option properties
	 */
	public static final boolean[] DEFAULT_BOOL_OPTIONS = {DEFAULT_LOAD_LAST_SAVE_ON_STARTUP,
																												DEFAULT_AUTOSAVE,
																												DEFAULT_ASK_FOR_COMMENT_ON_STOP,
																												DEFAULT_ASK_FOR_COMMENT_ON_CUT,
																												DEFAULT_ASK_FOR_SAVE_ON_LOAD,
																												DEFAULT_ASK_FOR_SAVE_ON_CLOSE};
	/**
	 * the default table size
	 */
	public static final int DEFAULT_TABLE_SIZE = 10;
	/**
	 * the default file format that will be used
	 * to save and load {@link SimeTimerProject}s
	 */
	public static final int DEFAULT_FILE_FORMAT = SaveManager.FILE_FORMAT_PLAIN;
	// Preferences
	/**
	 * the default x position of the {@link JFrame} on screen
	 */
	public static final int DEFAULT_X_POSITION = (int) ((3. / 4.) * SimeTimer.SCREEN_WIDTH);
	/**
	 * the default y position of the {@link JFrame} on screen
	 */
	public static final int DEFAULT_Y_POSITION = (int) ((1. / 5.) * SimeTimer.SCREEN_HEIGHT);
	/**
	 * the default file path for the {@link JFileChooser}
	 */
	public static final String DEFAULT_PATH = null;
	
	
	
	// actual configuration data, package visibility
	// options
	/**
	 * determines whether the application tries to load the last used file
	 * (save or load) on startup
	 */
	boolean loadLastSaveOnStartup;
	/**
	 * determines whether the app automatically saves all changes
	 * to the last used {@link File} immediately
	 */
	boolean autosave;
	/**
	 * determines whether the app shows a dialog which asks the user to type in
	 * a comment for the last {@link TimeChunk} when they press the stop button
	 */
	boolean askForCommentOnStop;
	/**
	 * determines whether the app shows a dialog which asks the user to type in
	 * a comment for the last {@link TimeChunk} when they press the cut button
	 */
	boolean askForCommentOnCut;
	/**
	 * determines whether the application asks the user if he wants to save
	 * his current project first when the load button is pressed and
	 * the current project contains unsaved data.
	 */
	boolean askForSaveOnLoad;
	/**
	 * determines whether the application asks the user if he wants to save
	 * his current project first when the window is about to be closed and
	 * the current project contains unsaved data.
	 */
	boolean askForSaveOnClose;
	/**
	 * returns an array representation of all the boolean option properties
	 * @return an array representation of all the boolean option properties
	 */
	boolean[] boolOptions() {
		return new boolean[] {loadLastSaveOnStartup,
													autosave,
													askForCommentOnStop,
													askForCommentOnCut,
													askForSaveOnLoad,
													askForSaveOnClose};
	}
	/**
	 * sets all the boolean options properties
	 * @param boolOptions an array representation of
	 * 										all the boolean option properties
	 */
	void setBoolOptions(boolean[] boolOptions) {
		this.loadLastSaveOnStartup = boolOptions[0];
		this.autosave = boolOptions[1];
		this.askForCommentOnStop = boolOptions[2];
		this.askForCommentOnCut = boolOptions[3];
		this.askForSaveOnLoad = boolOptions[4];
		this.askForSaveOnClose = boolOptions[5];
	}
	/**
	 * determines the size of the table in the {@link SimeTimer}'s main window.
	 * Can be any of the values defined in {@link ConfigManager}.TABLE_SIZES
	 */
	int tableSize;
	/**
	 * determines the file format used for saving and loading project files.
	 * Can be {@link SaveManager}.FILE_FORMAT_PLAIN or
	 * {@link SaveManager}.FILE_FORMAT_BYTE
	 */
	int fileFormat;
	// preferences
	/**
	 * the [@link File} that the user lastly used to save or load a project.
	 * Canceled save or load operations do not count, but unsuccessful ones do.
	 */
	File usedFile;
	// window position doesn't need to be stored
	
	
	
	
	/**
	 * constructor. Stores the owner {@link SimeTimer}.
	 * Loads default properties.
	 * @param owner the owner {@link SimeTimer}
	 */
	public ConfigManager(SimeTimer owner) {
		this.owner = owner;
		setDefaults();
	}
	
	/**
	 * loads and sets options and preferences from the {@link SaveManager}.
	 * If load errors occur, all properties are reset to their default.
	 */
	public void initialize() {
		if (!SaveManager.loadAndSetConfig(this)) {
			setDefaults();
		}
	}
	
	/**
	 * resets all stored options and preferences to their default.
	 */
	public void setDefaults() {
		setBoolOptions(DEFAULT_BOOL_OPTIONS);
		tableSize = DEFAULT_TABLE_SIZE;
		fileFormat = DEFAULT_FILE_FORMAT;
		usedFile = DEFAULT_PATH != null ? new File(DEFAULT_PATH) : null;
	}
	
	
	// SAVE/LOAD CONFIGURATION
	
	/**
	 * feeds preferences data into saveManager to be saved there
	 */
	void saveConfiguration() {
		// get position on screen
		int xPosition = owner.getLocationOnScreen().x;
		int yPosition = owner.getLocationOnScreen().y;
		// check for screen positions too close to the edge
		if (xPosition < 0) {
			xPosition = 0;
		} else if (xPosition > SimeTimer.SCREEN_WIDTH - SimeTimer.FRAME_WIDTH) {
			xPosition = SimeTimer.SCREEN_WIDTH - SimeTimer.FRAME_WIDTH;
		}
		if (yPosition < 0) {
			yPosition = 0;
		} else if (yPosition > SimeTimer.SCREEN_HEIGHT - owner.FRAME_HEIGHT()) {
			yPosition = SimeTimer.SCREEN_HEIGHT - owner.FRAME_HEIGHT();
		}
		// save data into file
		SaveManager.saveConfig(boolOptions(),
													 tableSize,
													 fileFormat,
													 xPosition,
													 yPosition,
													 usedFile);
	}
	
	/**
	 * Validates and applies the given options.
	 * All values are stored in the object variables with the same names.
	 * @param boolOptions an array representation of all the boolean option properties
	 * @param tableSize tableSize
	 * @param fileFormat fileFormat
	 */
	void setOptions(boolean[] boolOptions,
									int tableSize,
									int fileFormat) {
		// check validity
		if (tableSize < MIN_TABLE_ROW_COUNT
				|| tableSize > MAX_TABLE_ROW_COUNT) {
			// reset tableSize to default
			tableSize = ConfigManager.DEFAULT_TABLE_SIZE;
		}
		if (fileFormat != SaveManager.FILE_FORMAT_PLAIN
				&& fileFormat != SaveManager.FILE_FORMAT_BYTE) {
			// reset fileFormat to default
			fileFormat = ConfigManager.DEFAULT_FILE_FORMAT;
		}
		// set values
		setBoolOptions(boolOptions);
		this.tableSize = tableSize;
		// set new size for main window and table
		owner.tableSizeChanged();
		this.fileFormat = fileFormat;
	}
	
	/**
	 * Validates and applies the given preferences.
	 * All values are stored in the object variables with the same names.
	 * @param xPosition last saved x position of the {@link JFrame}
	 * @param yPosition last saved y position of the {@link JFrame}
	 * @param usedPath last saved save/load path
	 */
	void setPreferences(int xPosition, int yPosition, String usedPath) {
		// check validity
		if (xPosition < 0
				|| yPosition < 0
				|| xPosition > SimeTimer.SCREEN_WIDTH - SimeTimer.FRAME_WIDTH
				|| yPosition > SimeTimer.SCREEN_HEIGHT - owner.FRAME_HEIGHT()) {
			// reset position on screen to default
			xPosition = ConfigManager.DEFAULT_X_POSITION;
			yPosition = ConfigManager.DEFAULT_Y_POSITION;
		}
		File usedFile = usedPath != null
											&& !usedPath.equals(SaveManager.NULL_PATH)
										? new File(usedPath)
										: null;
		// position owner window
		owner.setLocation(xPosition, yPosition);
		// store usedFile
		this.usedFile = usedFile;
	}
	
}