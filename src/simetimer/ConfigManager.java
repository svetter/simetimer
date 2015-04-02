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
	
	
	// DEFAULT OPTIONS AND PREFERENCES
	// Options
	/**
	 * default option for whether the SimeTimer automatically loads
	 * the last used project (if available) when the app is started
	 */
	public static final boolean DEFAULT_LOAD_LAST_SAVE_ON_STARTUP = true;
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
	 * determines the file format that will be used
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
	 * @param owner the owner {@link SimeTimer}
	 */
	public ConfigManager(SimeTimer owner) {
		this.owner = owner;
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
		loadLastSaveOnStartup = DEFAULT_LOAD_LAST_SAVE_ON_STARTUP;
		askForCommentOnStop = DEFAULT_ASK_FOR_COMMENT_ON_STOP;
		askForCommentOnCut = DEFAULT_ASK_FOR_COMMENT_ON_CUT;
		askForSaveOnLoad = DEFAULT_ASK_FOR_SAVE_ON_LOAD;
		askForSaveOnClose = DEFAULT_ASK_FOR_SAVE_ON_CLOSE;
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
		} else if (yPosition > SimeTimer.SCREEN_HEIGHT - SimeTimer.FRAME_HEIGHT) {
			yPosition = SimeTimer.SCREEN_HEIGHT - SimeTimer.FRAME_HEIGHT;
		}
		// save data into file
		SaveManager.saveConfig(loadLastSaveOnStartup,
													 askForCommentOnStop,
													 askForCommentOnCut,
													 askForSaveOnLoad,
													 askForSaveOnClose,
													 fileFormat,
													 xPosition,
													 yPosition,
													 usedFile);
	}
	
	/**
	 * Validates and applies the given options.
	 * All values are stored in the object variables with the same names.
	 * @param loadLastSaveOnStartup loadLastSaveOnStartup
	 * @param askForCommentOnStop askForCommentOnStop
	 * @param askForCommentOnCut askForCommentOnCut
	 * @param askForSaveOnLoad askForSaveOnLoad
	 * @param askForSaveOnClose askForSaveOnClose
	 * @param fileFormat fileFormat
	 */
	void setOptions(boolean loadLastSaveOnStartup,
									boolean askForCommentOnStop,
									boolean askForCommentOnCut,
									boolean askForSaveOnLoad,
									boolean askForSaveOnClose,
									int fileFormat) {
		// check validity
		if (fileFormat != SaveManager.FILE_FORMAT_PLAIN
				&& fileFormat != SaveManager.FILE_FORMAT_BYTE) {
			// reset file format to default
			fileFormat = ConfigManager.DEFAULT_FILE_FORMAT;
		}
		// set values
		this.loadLastSaveOnStartup = loadLastSaveOnStartup;
		this.askForCommentOnStop = askForCommentOnStop;
		this.askForCommentOnCut = askForCommentOnCut;
		this.askForSaveOnLoad = askForSaveOnLoad;
		this.askForSaveOnClose = askForSaveOnClose;
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
				|| yPosition > SimeTimer.SCREEN_HEIGHT - SimeTimer.FRAME_HEIGHT) {
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