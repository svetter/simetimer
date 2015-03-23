package simetimer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * a slick stopwatch
 * @author Simon Vetter
 *
 */
public class SimeTimer extends JFrame {

	private static final long serialVersionUID = -6143989045338224554L;

	// window constants
	/**
	 * the width of the {@link JFrame}, corrected (+6)
	 */
	private static final int FRAME_WIDTH = 310 + 6;
	/**
	 * the height of the {@link JFrame}, corrected (+28)
	 */
	private static final int FRAME_HEIGHT = 95 + 28;
	
	// default preferences
	/**
	 * the default x position of the {@link JFrame} on screen
	 */
	static final int DEFAULT_X_POSITION = (int) ((3./4.) * Toolkit.getDefaultToolkit().getScreenSize().width);
	/**
	 * the default y position of the {@link JFrame} on screen
	 */
	static final int DEFAULT_Y_POSITION = (int) ((1./3.) * Toolkit.getDefaultToolkit().getScreenSize().height);
	/**
	 * the default file path for the {@link JFileChooser}
	 */
	static final String DEFAULT_PATH = null;
	
	
	// frame elements
	private JLabel timerLabel = new JLabel();
	private JToggleButton startStopButton = new JToggleButton();
	private JButton saveButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton resetButton = new JButton();
	
	// logic variables
	private long accumulatedTime;
	private long currentStartTime;
	
	// objects for timed task
	private TimerTask displayTask;
	private Timer displayTimer;
	
	private SaveManager saveManager = new SaveManager(this);
	private String usedPath;
	
	
	
	/**
	 * constructor. Initializes frame and sets layout
	 */
	public SimeTimer() {
		super("SimeTimer");
		accumulatedTime = 0L;
		
		
		// initializing frame
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		saveManager.loadAndSetPreferences();
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);
		
		
		// setting layout
		
		timerLabel.setBounds(10, 10, 200, 40);
		timerLabel.setText("0:00:00.000");
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timerLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timerLabel.setEnabled(true);
		cp.add(timerLabel);
		
		startStopButton.setBounds(220, 10, 80, 40);
		startStopButton.setText("Start");
		startStopButton.setFont(new Font("Dialog", Font.PLAIN, 20));
		startStopButton.setEnabled(true);
		cp.add(startStopButton);
		
		saveButton.setBounds(10, 60, 95, 25);
		saveButton.setText("save time");
		saveButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		saveButton.setEnabled(true);
		cp.add(saveButton);
		
		loadButton.setBounds(115, 60, 95, 25);
		loadButton.setText("load time");
		loadButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		loadButton.setEnabled(true);
		cp.add(loadButton);
		
		resetButton.setBounds(220, 60, 80, 25);
		resetButton.setText("reset");
		resetButton.setFont(new Font("Dialog", Font.PLAIN, 16));
		resetButton.setBackground(new Color(255, 200, 200));
		resetButton.setEnabled(true);
		cp.add(resetButton);
		
		
		// button functionalities
		
		startStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (running()) {
					// button ON - START the timer
					currentStartTime = System.currentTimeMillis();
					startStopButton.setText("Stop");
					enableDisplayTimer(true);
				} else {
					// button OFF - STOP the timer
					accumulatedTime += System.currentTimeMillis() - currentStartTime;
					startStopButton.setText("Start");
					enableDisplayTimer(false);
					displayCurrentTime();
				}
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				reset();
			}
		});
		
		SaveLoadAction saveButtonAction = new SaveLoadAction(this) {
			private static final long serialVersionUID = 7117119994405070822L;
			@Override
			void call(JFileChooser fileChooser, JFrame owner) {
				// call fileChooser and store feedback
				int option = fileChooser.showSaveDialog(owner);
				if (option == JFileChooser.APPROVE_OPTION) {
					// user has approved save
	      	saveToFile(fileChooser.getSelectedFile());
	      	usedPath = fileChooser.getSelectedFile().getParent();
	      }
			}
		};
		saveButton.addActionListener(saveButtonAction);
		
		SaveLoadAction loadButtonAction = new SaveLoadAction(this) {
			private static final long serialVersionUID = 7117119994405070822L;
			@Override
			void call(JFileChooser fileChooser, JFrame owner) {
				// call fileChooser and store feedback
				int option = fileChooser.showOpenDialog(owner);
				if (option == JFileChooser.APPROVE_OPTION) {
					// user has approved save
					loadFromFile(fileChooser.getSelectedFile());
					usedPath = fileChooser.getSelectedFile().getParent();
				}
			}
		};
		loadButton.addActionListener(loadButtonAction);
		
		
		// save preferences on close
		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				savePreferences();
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
		});
		
		
		// frame ready
		setVisible(true);
	}
	
	
	
	/**
	 * shortcut to check whether the timer is running
	 * @return true if the startStopButton is toggled, else false
	 */
	private boolean running() {
		return startStopButton.isSelected();
	}
	
	/**
	 * resets the timer
	 */
	private void reset() {
		accumulatedTime = 0L;
		if (running()) {
			currentStartTime = System.currentTimeMillis();
		} else {
			displayCurrentTime();
		}
	}
	
	/**
	 * generates a String for the timerLabel from a given time in milliseconds
	 * @param time the stopped time to be displayed, in milliseconds
	 * @return a {@link String} representing the stopped time in a readable format
	 */
	private String timeToString(long time) {
		int millis = (int) (time % 1000);
		time -= millis;
		time /= 1000;
		int seconds = (int) (time % 60);
		time -= seconds;
		time /= 60;
		int minutes = (int) (time % 60);
		time -= minutes;
		time /= 60;
		int hours = (int) time;
		StringBuilder result = new StringBuilder();
		result.append(hours)
					.append(":")
					.append(Integer.toString(minutes).length() == 2 ? minutes : "0" + minutes)
					.append(":")
					.append(Integer.toString(seconds).length() == 2 ? seconds : "0" + seconds)
					.append(".")
					.append(Integer.toString(millis).length() == 3 ? millis :
									Integer.toString(millis).length() == 2 ? "0" + millis : "00" + millis);
		return result.toString();
	}
	
	/**
	 * updates the timerLabel to the current stopped time
	 */
	private void displayCurrentTime() {
		String result;
		if (running()) {
			result = timeToString(accumulatedTime +
														System.currentTimeMillis() - currentStartTime);
		} else {
			result = timeToString(accumulatedTime);
		}
		timerLabel.setText(result);
	}
	
	/**
	 * starts or stops the displayTimer that ensures a running time display
	 * @param enable set true to start the timer, false to stop it
	 */
	private void enableDisplayTimer(boolean enable) {
		if (enable) {
			// initializing new task and timer
			displayTask = new TimerTask() {
	      @Override
	      public void run() {
	      	displayCurrentTime();
	      }
			};
			displayTimer = new Timer();
			// scheduling
			displayTimer.schedule(displayTask,
														java.util.Calendar.getInstance().getTime(),
														7);
		} else {
			// stop timer
			displayTimer.cancel();
		}
	}
	
	/**
	 * saves the measured time at the moment of mathod call into the saveManager
	 */
	private void saveToFile(File saveFile) {
		long time = accumulatedTime;
		if (running()) {
			time += System.currentTimeMillis() - currentStartTime;
		}
		saveManager.saveToFile(Long.toString(time) + System.lineSeparator(), saveFile);
	}
	
	/**
	 * gets the saved time from the saveManager and puts it in place of the measured time
	 */
	private void loadFromFile(File saveFile) {
		long loaded = saveManager.loadFromFile(saveFile);
		if (loaded == -1L) {
			return;
		}
		accumulatedTime = loaded;
		if (running()) {
			currentStartTime = System.currentTimeMillis();
		} else {
			displayCurrentTime();
		}
	}
	
	
	// PREFERENCES
	/**
	 * feeds preference data into saveManager to be saved there
	 */
	private void savePreferences() {
		saveManager.savePreferences(getLocationOnScreen().x,
																getLocationOnScreen().y,
																usedPath);
	}
	
	/**
	 * this method is called by the {@link SaveManager} to feed back
	 * the loaded preferences
	 * @param xPosition last saved x position of the {@link JFrame}
	 * @param yPosition last saved y position of the {@link JFrame}
	 * @param usedPath last saved save/load path
	 */
	void setPreferences(int xPosition,
											int yPosition,
											String usedPath) {
		setLocation(xPosition, yPosition);
		this.usedPath = usedPath;
	}
	
	
	
	// MAIN
	/**
	 * main method. Starts the application
	 * @param args not used
	 */
	public static void main(String[] args) {
		new SimeTimer();
	}



	/**
	 * helper class to implement the save and load function.
	 * SaveLoadAction is called on click on save/load button
	 * @author Simon Vetter
	 *
	 */
	abstract class SaveLoadAction extends AbstractAction {
		private static final long serialVersionUID = -5391589383905897304L;
		private JFrame owner;
		private JFileChooser fileChooser;
		
		/**
		 * constructor
		 * @param owner parent {@link JFrame} for the {@link JFileChooser}
		 */
		public SaveLoadAction(JFrame owner) {
      super();
      this.owner = owner;
		}
		
		/**
		 * triggers display of a {@link JFileChooser} and saves if user approved
		 */
		public void actionPerformed(ActionEvent evt) {
			fileChooser = new JFileChooser(usedPath == null ? null : new File(usedPath));
			fileChooser.setMinimumSize(new Dimension(350, 300));
			// only display directories and .save files
			// (and files without an extension)
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() ||
								 file.isFile() && file.getName().endsWith(".save") ||
								 !file.getName().contains(".");
				}
				@Override
				public String getDescription() {
					return ".save files";
				}
			});
			// call customizable method
			call(fileChooser, owner);
		}
		
		/**
		 * method to overwrite on construction. Is called with a prepared {@link JFileChooser}
		 * @param fileChooser a {@link JFileChooser} with preselected path and {@link FileFilter}
		 * @param owner parent {@link JFrame} for the {@link JFileChooser}
		 */
		abstract void call(JFileChooser fileChooser, JFrame owner);
	}
	
}