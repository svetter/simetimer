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
import javax.swing.table.DefaultTableModel;

/**
 * a slick stopwatch
 * @author Simon Vetter
 *
 */
public class SimeTimer extends JFrame {

	private static final long serialVersionUID = -6143989045338224554L;

	// window constants
	/**
	 * determines how many rows the {@link JTable} can show at once
	 */
	public static final int TABLE_ROWS = 10;
	/**
	 * the width of the {@link JFrame}, corrected (+6)
	 */
	public static final int FRAME_WIDTH = 310 + 6;
	/**
	 * the height of the {@link JFrame}, corrected (+28)
	 */
	public static final int FRAME_HEIGHT = 95 + 37 + TABLE_ROWS * 16 + 28;
	/**
	 * the system's main screen width, set at runtime
	 */
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	/**
	 * the system's main screen height, set at runtime
	 */
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	
	// default preferences
	/**
	 * the default x position of the {@link JFrame} on screen
	 */
	public static final int DEFAULT_X_POSITION = (int) ((3./4.) * SCREEN_WIDTH);
	/**
	 * the default y position of the {@link JFrame} on screen
	 */
	public static final int DEFAULT_Y_POSITION = (int) ((1./3.) * SCREEN_HEIGHT);
	/**
	 * the default file path for the {@link JFileChooser}
	 */
	public static final String DEFAULT_PATH = null;
	
	
	
	// frame elements
	private JLabel timerLabel = new JLabel();
	private JToggleButton startStopButton = new JToggleButton();
	private JButton saveButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton resetButton = new JButton();
	
	private JTable table = new JTable(0, 2);
	private DefaultTableModel tableModel;
	private JScrollPane tableScrollPane = new JScrollPane(table);
	
	// logic variables
	private long accumulatedTime;
	private long currentStartTime;
	
	// objects for timed task
	private TimerTask displayTask;
	private Timer displayTimer;
	
	// project and saving
	private SimeTimerProject project;
	private SaveManager saveManager;
	/**
	 * last used path to save or load.
	 * Can be null!
	 */
	private String usedPath;
	
	
	
	/**
	 * constructor. Initializes frame and sets layout
	 */
	public SimeTimer() {
		super("SimeTimer");
		
		accumulatedTime = 0L;
		project = new SimeTimerProject();
		saveManager = new SaveManager(this);
		
		
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
		cp.add(timerLabel);
		
		startStopButton.setBounds(220, 10, 80, 40);
		startStopButton.setText("Start");
		startStopButton.setFont(new Font("Dialog", Font.PLAIN, 20));
		startStopButton.setEnabled(true);
		cp.add(startStopButton);
		
		saveButton.setBounds(10, 60, 95, 25);
		saveButton.setText("save times");
		saveButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(saveButton);
		
		loadButton.setBounds(115, 60, 95, 25);
		loadButton.setText("load times");
		loadButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(loadButton);
		
		resetButton.setBounds(220, 60, 80, 25);
		resetButton.setText("reset");
		resetButton.setFont(new Font("Dialog", Font.PLAIN, 16));
		resetButton.setBackground(new Color(255, 200, 200));
		cp.add(resetButton);
		
		
		tableScrollPane.setBounds(10, 100, 291, 23 + TABLE_ROWS * 16);
		tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 4218091309598726974L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(tableModel);
		table.setFillsViewportHeight(true);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setDragEnabled(false);
		tableScrollPane.setAutoscrolls(true);
		tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// set table colums
		tableModel.addColumn("Date");
		tableModel.addColumn("Time");
		cp.add(tableScrollPane);
		
		
		
		// button functionalities
		
		startStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (running()) {
					// button ON - START the timer
					currentStartTime = System.currentTimeMillis();
					startStopButton.setText("Stop");
					enableDisplayTimer(true);
					// TODO: Save date
					// TODO: List entry? or second label
				} else {
					// button OFF - STOP the timer
					accumulatedTime += System.currentTimeMillis() - currentStartTime;
					startStopButton.setText("Start");
					enableDisplayTimer(false);
					displayCurrentTime();
					// TODO: Save time to project
					// TODO: Refresh list
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
			// TODO: Save project instead of single time
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
			// TODO: Load project instead of single time
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
			public void windowActivated(WindowEvent evt) {}
			@Override
			public void windowClosed(WindowEvent evt) {}
			@Override
			public void windowClosing(WindowEvent evt) {
				savePreferences();
				// TODO: Prompt about saving
			}
			@Override
			public void windowDeactivated(WindowEvent evt) {}
			@Override
			public void windowDeiconified(WindowEvent evt) {}
			@Override
			public void windowIconified(WindowEvent evt) {}
			@Override
			public void windowOpened(WindowEvent evt) {}
		});
		

		
		// frame ready
		setVisible(true);
		
		
		// TODO: open previous project by default
		
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
		// TODO: reset whole project
		accumulatedTime = 0L;
		if (running()) {
			currentStartTime = System.currentTimeMillis();
		} else {
			displayCurrentTime();
		}
	}
	
	/**
	 * scrolls the {@link JTable} all the way down
	 */
	private void scrollDown() {
		tableScrollPane.getVerticalScrollBar().setValue(
				tableScrollPane.getVerticalScrollBar().getMaximum());
	}
	
	/**
	 * generates a String for the timerLabel from a given time in milliseconds
	 * @param time the stopped time to be displayed, in milliseconds
	 * @return a {@link String} representing the stopped time in a readable format
	 */
	public static String timeToString(long time) {
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
		// TODO: refresh second label
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
	
	
	// SAVE / LOAD
	
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
	private abstract class SaveLoadAction extends AbstractAction {
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