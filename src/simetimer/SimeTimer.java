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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * a slick stopwatch
 * @author Simon Vetter
 *
 */
public class SimeTimer extends JFrame {
	
	
	private static final long serialVersionUID = -6143989045338224554L;
	
	
	// LAYOUT CONSTANTS
	/**
	 * determines how many rows the {@link JTable} can show at once
	 */
	public static final int TABLE_ROWS = 10;
	
	private static final int DEFAULT_GAP = 10,
													 SMALL_GAP = 5,
													 BIG_GAP = 15;
	private static final int FIRST_ITEM_ROW_SIZE = 40,
													 SECOND_ITEM_ROW_SIZE = 25,
													 THIRD_ITEM_ROW_SIZE = 25,
													 FOURTH_ITEM_ROW_SIZE = TABLE_ROWS * 16 + 22;
	private static final int FIRST_ITEM_COLUMN_SIZE = 95,
													 SECOND_ITEM_COLUMN_SIZE = 95,
													 THIRD_ITEM_COLUMN_SIZE = 80,
													 DOUBLE_WIDTH_COLUMN_SIZE = FIRST_ITEM_COLUMN_SIZE +
													 														DEFAULT_GAP +
													 														SECOND_ITEM_COLUMN_SIZE,
													 FULL_WIDTH_COLUMN_SIZE = FIRST_ITEM_COLUMN_SIZE +
													 													DEFAULT_GAP +
													 													SECOND_ITEM_COLUMN_SIZE +
													 													DEFAULT_GAP +
													 													THIRD_ITEM_COLUMN_SIZE;
	private static final int FIRST_ITEM_ROW_OFFSET = DEFAULT_GAP,
													 SECOND_ITEM_ROW_OFFSET = FIRST_ITEM_ROW_OFFSET +
													 													FIRST_ITEM_ROW_SIZE +
													 													SMALL_GAP,
													 THIRD_ITEM_ROW_OFFSET = SECOND_ITEM_ROW_OFFSET +
													 												 SECOND_ITEM_ROW_SIZE +
													 												 DEFAULT_GAP,
													 FOURTH_ITEM_ROW_OFFSET = THIRD_ITEM_ROW_OFFSET +
													 													THIRD_ITEM_ROW_SIZE +
													 													BIG_GAP;
	private static final int FIRST_ITEM_COLUMN_OFFSET = DEFAULT_GAP,
													 SECOND_ITEM_COLUMN_OFFSET = FIRST_ITEM_COLUMN_OFFSET +
													 														 FIRST_ITEM_COLUMN_SIZE +
													 														 DEFAULT_GAP,
													 THIRD_ITEM_COLUMN_OFFSET = SECOND_ITEM_COLUMN_OFFSET +
													 														SECOND_ITEM_COLUMN_SIZE +
													 														DEFAULT_GAP;
	
	//window constants
	/**
	 * the width of the {@link JFrame}, corrected (+6)
	 */
	public static final int FRAME_WIDTH = FIRST_ITEM_COLUMN_OFFSET +
																				FULL_WIDTH_COLUMN_SIZE +
																				DEFAULT_GAP +
																				6;
	/**
	 * the height of the {@link JFrame}, corrected (+28)
	 */
	public static final int FRAME_HEIGHT = FOURTH_ITEM_ROW_OFFSET +
																				 FOURTH_ITEM_ROW_SIZE +
																				 DEFAULT_GAP +
																				 28;
	/**
	 * the system's main screen width, set at runtime
	 */
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	/**
	 * the system's main screen height, set at runtime
	 */
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
													 
	
	
	// app behaviour
	/**
	 * determines whether the SimeTimer asks the user if he wants to save
	 * when they attempt to close an unsaved project
	 */
	private static final boolean ASK_FOR_SAVE_ON_CLOSE = false;
	/**
	 * determines whether the SimeTimer asks the user if he wants to save
	 * when they attempt to load a project while having another unsaved one open
	 */
	private static final boolean ASK_FOR_SAVE_ON_LOAD = true;
	/**
	 * determines whether the SimeTimer asks the user if he wants to load
	 * the last used project when the app is started
	 */
	private static final boolean ASK_FOR_LOAD_ON_STARTUP = false;
	
	
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
	private JLabel totalTimeLabel = new JLabel();
	private JLabel chunkTimeLabel = new JLabel();
	private JToggleButton startStopButton = new JToggleButton();
	private JButton cutButton = new JButton();
	private JButton saveButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton resetButton = new JButton();
	
	private JTable table = new JTable(0, 3);
	private DefaultTableModel tableModel;
	private JScrollPane tableScrollPane = new JScrollPane(table);
	
	// logic variables
	private long currentStartTime;
	private long lastProjectTime;
	
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
		
		totalTimeLabel.setBounds(FIRST_ITEM_COLUMN_OFFSET,
												FIRST_ITEM_ROW_OFFSET,
												DOUBLE_WIDTH_COLUMN_SIZE,
												FIRST_ITEM_ROW_SIZE);
		totalTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		cp.add(totalTimeLabel);
		
		chunkTimeLabel.setBounds(FIRST_ITEM_COLUMN_OFFSET,
														 SECOND_ITEM_ROW_OFFSET,
														 DOUBLE_WIDTH_COLUMN_SIZE,
														 SECOND_ITEM_ROW_SIZE);
		chunkTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		chunkTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 24));
		cp.add(chunkTimeLabel);
		
		startStopButton.setBounds(THIRD_ITEM_COLUMN_OFFSET,
															FIRST_ITEM_ROW_OFFSET,
															THIRD_ITEM_COLUMN_SIZE,
															FIRST_ITEM_ROW_SIZE);
		startStopButton.setText("Start");
		startStopButton.setFont(new Font("Dialog", Font.BOLD, 20));
		cp.add(startStopButton);
		
		cutButton.setBounds(THIRD_ITEM_COLUMN_OFFSET,
												SECOND_ITEM_ROW_OFFSET,
												THIRD_ITEM_COLUMN_SIZE,
												SECOND_ITEM_ROW_SIZE);
		cutButton.setText("cut");
		cutButton.setToolTipText("Finish the current segment and instantly begin a new one");
		cutButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(cutButton);
		
		saveButton.setBounds(FIRST_ITEM_COLUMN_OFFSET,
												 THIRD_ITEM_ROW_OFFSET,
												 FIRST_ITEM_COLUMN_SIZE,
												 THIRD_ITEM_ROW_SIZE);
		saveButton.setText("save");
		saveButton.setToolTipText("Save the current project to a file");
		saveButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(saveButton);
		
		loadButton.setBounds(SECOND_ITEM_COLUMN_OFFSET,
												 THIRD_ITEM_ROW_OFFSET,
												 SECOND_ITEM_COLUMN_SIZE,
												 THIRD_ITEM_ROW_SIZE);
		loadButton.setText("load");
		loadButton.setToolTipText("Load a project from a file");
		loadButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(loadButton);
		
		resetButton.setBounds(THIRD_ITEM_COLUMN_OFFSET,
													THIRD_ITEM_ROW_OFFSET,
													THIRD_ITEM_COLUMN_SIZE,
													THIRD_ITEM_ROW_SIZE);
		resetButton.setText("reset");
		resetButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		resetButton.setBackground(new Color(255, 200, 200));
		cp.add(resetButton);
		
		
		// TABLE
		// set ScrollPane properties
		tableScrollPane.setBounds(FIRST_ITEM_COLUMN_OFFSET,
															FOURTH_ITEM_ROW_OFFSET,
															FULL_WIDTH_COLUMN_SIZE,
															FOURTH_ITEM_ROW_SIZE);
		tableScrollPane.setAutoscrolls(true);
		tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		initializeTable();
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
				} else {
					// button OFF - STOP the timer
					TimeChunk newestTimeChunk = new TimeChunk(currentStartTime);
					startStopButton.setText("Start");
					enableDisplayTimer(false);
					project.addTimeChunk(newestTimeChunk);
					lastProjectTime = project.getProjectTime();
					tableModel.addRow(newestTimeChunk.toStringArray(tableModel.getRowCount()+1));
					refreshTimeLabels();
					scrollDown();
				}
			}
		});
		
		cutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cut();
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				reset();
			}
		});
		
		// SAVE
		SaveLoadAction saveButtonAction = new SaveLoadAction(this) {
			private static final long serialVersionUID = 7117119994405070822L;
			@Override
			void call(JFileChooser fileChooser, JFrame owner) {
				// call fileChooser and store feedback
				int option = fileChooser.showSaveDialog(owner);
				if (option == JFileChooser.APPROVE_OPTION) {
					// user has approved save
	      	saveManager.saveProjectToByteFile(project, fileChooser.getSelectedFile());
	      	usedPath = fileChooser.getSelectedFile().getParent();
	      }
			}
		};
		saveButton.addActionListener(saveButtonAction);
		
		// LOAD
		SaveLoadAction loadButtonAction = new SaveLoadAction(this) {
			private static final long serialVersionUID = 7117119994405070822L;
			@Override
			void call(JFileChooser fileChooser, JFrame owner) {
				// TODO: unsaved changes? prompt
				if (ASK_FOR_SAVE_ON_LOAD) {
					if (JOptionPane.showConfirmDialog(owner,
																						"Your current project is not saved.\nDo you want to save it before loading?",
																						"Project not saved",
																						JOptionPane.YES_NO_OPTION,
																						JOptionPane.QUESTION_MESSAGE)
							== JOptionPane.YES_OPTION) {
						// user wants to save project
						saveButton.doClick();
					}
				}
				// call fileChooser and store feedback
				int option = fileChooser.showOpenDialog(owner);
				if (option == JFileChooser.APPROVE_OPTION) {
					// user has approved save
					project = saveManager.loadProjectFromByteFile(fileChooser.getSelectedFile());
					usedPath = fileChooser.getSelectedFile().getParent();
					refreshTimeLabels();
					refreshTable();
				}
			}
		};
		loadButton.addActionListener(loadButtonAction);
		
		
		// save preferences and ask for save on close
		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent evt) {}
			@Override
			public void windowClosed(WindowEvent evt) {}
			@Override
			public void windowClosing(WindowEvent evt) {
				savePreferences();
				// TODO: not saved already?
				if (ASK_FOR_SAVE_ON_CLOSE) {
					if (JOptionPane.showConfirmDialog(evt.getComponent(),
																						"Your current project is not saved.\nDo you want to save it before exiting?",
																						"Project not saved",
																						JOptionPane.YES_NO_OPTION,
																						JOptionPane.QUESTION_MESSAGE)
							== JOptionPane.YES_OPTION) {
						// user wants to save project
						saveButton.doClick();
					}
				}
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
		refreshTimeLabels();
		setVisible(true);
		
		
		// TODO: open previous project by default
		if (ASK_FOR_LOAD_ON_STARTUP) {
			// magic happens
		}
	}
	
	
	
	/**
	 * shortcut to check whether the timer is running
	 * @return true if the startStopButton is toggled, else false
	 */
	private boolean running() {
		return startStopButton.isSelected();
	}
	
	private void cut() {
		long temp = System.currentTimeMillis();
		if (!running()) {
			return;
		}
		TimeChunk newestTimeChunk = new TimeChunk(currentStartTime, temp - currentStartTime);
		currentStartTime = System.currentTimeMillis();
		project.addTimeChunk(newestTimeChunk);
		lastProjectTime = project.getProjectTime();
		tableModel.addRow(newestTimeChunk.toStringArray(tableModel.getRowCount()+1));
		scrollDown();
	}
	
	/**
	 * resets the timer
	 */
	private void reset() {
		if (JOptionPane.showConfirmDialog(this,
																			"Do you really want to reset your current project?",
																			"Reset project",
																			JOptionPane.YES_NO_CANCEL_OPTION,
																			JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.YES_OPTION) {
			// user wants to reset
			if (running()) {
				startStopButton.doClick();
			}
			project = new SimeTimerProject();
			refreshTimeLabels();
			refreshTable();
		}
	}
	
	private void initializeTable() {
		// set tableModel
		tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 4218091309598726974L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(tableModel);
		
		// set table properties
		table.setFillsViewportHeight(true);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setDragEnabled(false);
		
		// set table colums
		tableModel.addColumn("#");
		tableModel.addColumn("Start date");
		tableModel.addColumn("Time");
		
		TableColumnModel columns = table.getColumnModel();
		// set column widths
		columns.getColumn(0).setResizable(false);
		columns.getColumn(0).setMinWidth(12);
		columns.getColumn(0).setMaxWidth(35);
		columns.getColumn(0).setPreferredWidth(30);
		columns.getColumn(1).setResizable(false);
		columns.getColumn(1).setMinWidth(130);
		columns.getColumn(1).setMaxWidth(170);
		columns.getColumn(1).setPreferredWidth(140);
		columns.getColumn(2).setResizable(false);
		
		// set alignment
		DefaultTableCellRenderer rightAligner = new DefaultTableCellRenderer();
		rightAligner.setHorizontalAlignment(SwingConstants.RIGHT);
		DefaultTableCellRenderer centerAligner = new DefaultTableCellRenderer();
		centerAligner.setHorizontalAlignment(SwingConstants.CENTER);
		columns.getColumn(0).setCellRenderer(rightAligner);
		columns.getColumn(1).setCellRenderer(rightAligner);
		columns.getColumn(2).setCellRenderer(centerAligner);
	}
	
	private void refreshTable() {
		// empty the table
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}
		// add new data from project
		for (int i=0; i<project.size(); i++) {
			tableModel.addRow(project.getTimeChunkAt(i).toStringArray(i+1));
		}
		scrollDown();
	}
	
	/**
	 * scrolls the {@link JTable} all the way down
	 */
	private void scrollDown() {
		tableScrollPane.getVerticalScrollBar().setValue(
				tableScrollPane.getVerticalScrollBar().getMaximum() - 1);
	}
	
	/**
	 * generates a String for the time labels from a given time in milliseconds
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
	 * updates the time labels to the current stopped time
	 */
	private void refreshTimeLabels() {
		String total, chunk="0";
		if (running()) {
			total = timeToString(lastProjectTime +
													 System.currentTimeMillis() - currentStartTime);
			chunk = timeToString(System.currentTimeMillis() - currentStartTime);
		} else {
			updateProjectTime();
			total = timeToString(lastProjectTime);
			chunk = timeToString(project.getLastChunkTime());
		}
		totalTimeLabel.setText(total);
		chunkTimeLabel.setText(chunk);
	}
	
	private void updateProjectTime() {
		lastProjectTime = project.getProjectTime();
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
	      	refreshTimeLabels();
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
								 file.isFile() && file.getName().endsWith(".stp") ||
								 !file.getName().contains(".");
				}
				@Override
				public String getDescription() {
					return ".stp files";
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