package simetimer;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class ConfigManager {
	
	private SimeTimer owner;
	
	
	//DEFAULT OPTIONS AND PREFERENCES
	// Options
	/**
	 * default option for whether the SimeTimer automatically loads
	 * the last used project (if available) when the app is started
	 */
	public static final boolean DEFAULT_LOAD_LAST_SAVE_ON_STARTUP = true;
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
	public static final boolean DEFAULT_ASK_FOR_SAVE_ON_CLOSE = false;
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
	boolean loadLastSaveOnStartup;
	boolean askForSaveOnLoad;
	boolean askForSaveOnClose;
	int fileFormat;
	// preferences
	File usedFile;
	
	
	
	public ConfigManager(SimeTimer owner) {
		this.owner = owner;
	}
	
	public void initialize() {
		SaveManager.loadAndSetConfig(this);
	}
	
	
	//SAVE/LOAD CONFIGURATION
	
	/**
	 * feeds preferences data into saveManager to be saved there
	 */
	void saveConfiguration() {
		SaveManager.saveConfig(loadLastSaveOnStartup,
													 askForSaveOnLoad,
													 askForSaveOnClose,
													 fileFormat,
													 owner.getLocationOnScreen().x,
													 owner.getLocationOnScreen().y,
													 usedFile);
	}
	
	// TODO documentation
	void setOptions(boolean loadLastSaveOnStartup,
									boolean askForSaveOnLoad,
									boolean askForSaveOnClose,
									int fileFormat) {
		this.loadLastSaveOnStartup = loadLastSaveOnStartup;
		this.askForSaveOnLoad = askForSaveOnLoad;
		this.askForSaveOnClose = askForSaveOnClose;
		this.fileFormat = fileFormat;
	}
	
	/**
	 * this method is called by the {@link SaveManager} to feed back the loaded
	 * preferences
	 * @param xPosition last saved x position of the {@link JFrame}
	 * @param yPosition last saved y position of the {@link JFrame}
	 * @param usedPath last saved save/load path
	 */
	void setPreferences(int xPosition, int yPosition, File usedFile) {
		owner.setLocation(xPosition, yPosition);
		this.usedFile = usedFile;
	}
	
}