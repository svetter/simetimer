package simetimer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * The {@link SimeTimer} is a slick but powerful stopwatch designed
 * to keep track of the time one puts into one ore many different projects.
 * As well as one can use it like a traditional stopwatch
 * (including a lap function), you can accumulate sessions, of which the
 * {@link SimeTimer} will store start date and time as well as the
 * elapsed time between two presses of the start/stop-button.
 * 
 * @author Simon Vetter
 *
 */
public class SimeTimer extends JFrame {
	
	
	private static final long serialVersionUID = -6143989045338224554L;
	
	
	// LAYOUT CONSTANTS
	/**
	 * determines how many rows the {@link JTable} can show at once
	 */
	private static final int TABLE_ROWS = 10;
	
	private static final int DEFAULT_GAP = 10,
													 SMALL_GAP = 5,
													 BIG_GAP = 15;
	private static final int FIRST_ITEM_ROW_SIZE = 40,
													 SECOND_ITEM_ROW_SIZE = 25,
													 THIRD_ITEM_ROW_SIZE = 25,
													 FOURTH_ITEM_ROW_SIZE = TABLE_ROWS * 16 + 38;
	private static final int FIRST_ITEM_COLUMN_SIZE = THIRD_ITEM_ROW_SIZE,
													 SECOND_ITEM_COLUMN_SIZE = 75,
													 THIRD_ITEM_COLUMN_SIZE = SECOND_ITEM_COLUMN_SIZE,
													 FOURTH_ITEM_COLUMN_SIZE = 80,
													 DOUBLE_WIDTH_COLUMN_SIZE = FIRST_ITEM_COLUMN_SIZE
													 														+ DEFAULT_GAP
													 														+ SECOND_ITEM_COLUMN_SIZE
													 														+ DEFAULT_GAP
													 														+ THIRD_ITEM_COLUMN_SIZE,
													 FULL_WIDTH_COLUMN_SIZE = FIRST_ITEM_COLUMN_SIZE
													 													+ DEFAULT_GAP
													 													+ SECOND_ITEM_COLUMN_SIZE
													 													+ DEFAULT_GAP
													 													+ THIRD_ITEM_COLUMN_SIZE
													 													+ DEFAULT_GAP
													 													+ FOURTH_ITEM_COLUMN_SIZE;
	private static final int FIRST_ITEM_ROW_OFFSET = DEFAULT_GAP,
													 SECOND_ITEM_ROW_OFFSET = FIRST_ITEM_ROW_OFFSET
													 													+ FIRST_ITEM_ROW_SIZE
													 													+ SMALL_GAP,
													 THIRD_ITEM_ROW_OFFSET = SECOND_ITEM_ROW_OFFSET
													 												 + SECOND_ITEM_ROW_SIZE
													 												 + DEFAULT_GAP,
													 FOURTH_ITEM_ROW_OFFSET = THIRD_ITEM_ROW_OFFSET
													 													+ THIRD_ITEM_ROW_SIZE
													 													+ BIG_GAP;
	private static final int FIRST_ITEM_COLUMN_OFFSET = DEFAULT_GAP,
													 SECOND_ITEM_COLUMN_OFFSET = FIRST_ITEM_COLUMN_OFFSET
													 														 + FIRST_ITEM_COLUMN_SIZE
													 														 + DEFAULT_GAP,
													 THIRD_ITEM_COLUMN_OFFSET = SECOND_ITEM_COLUMN_OFFSET
													 														+ SECOND_ITEM_COLUMN_SIZE
													 														+ DEFAULT_GAP,
													 FOURTH_ITEM_COLUMN_OFFSET = THIRD_ITEM_COLUMN_OFFSET
													 														 + THIRD_ITEM_COLUMN_SIZE
													 														 + DEFAULT_GAP;
	
	
	// window constants
	/**
	 * the width of the {@link JFrame}, corrected (+6)
	 */
	public static final int FRAME_WIDTH = FIRST_ITEM_COLUMN_OFFSET
																				+ FULL_WIDTH_COLUMN_SIZE
																				+ DEFAULT_GAP
																				+ 6;
	/**
	 * the height of the {@link JFrame}, corrected (+28)
	 */
	public static final int FRAME_HEIGHT = FOURTH_ITEM_ROW_OFFSET
																				 + FOURTH_ITEM_ROW_SIZE
																				 + DEFAULT_GAP
																				 + 28;
	/**
	 * the system's main screen width, set at runtime
	 */
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	/**
	 * the system's main screen height, set at runtime
	 */
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	/**
	 * supplies the SimeTimer's identity to subclasses which
	 * cannot access it directly
	 */
	private final SimeTimer THIS = this;
	
	
	
	
	
	// frame elements
	private JLabel totalTimeLabel = new JLabel();
	private JLabel chunkTimeLabel = new JLabel();
	private JToggleButton startStopButton = new JToggleButton();
	private JButton cutButton = new JButton();
	private JButton optionsButton = new JButton();
	private JButton saveButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton resetButton = new JButton();
	// table
	private JTable table = new JTable(0, 3);
	private DefaultTableModel tableModel;
	private JScrollPane tableScrollPane = new JScrollPane(table);
	
	
	// logic variables
	private long currentStartTime;
	private long lastProjectTime;
	private boolean unsavedData;
	
	// objects for timed task
	private TimerTask displayTask;
	private Timer displayTimer;
	
	// project and saving
	private SimeTimerProject project;
	ConfigManager config;
	
	
	
	/**
	 * constructor. Initializes frame and sets layout
	 */
	public SimeTimer() {
		super("SimeTimer");
		
		project = new SimeTimerProject();
		config = new ConfigManager(this);
		unsavedData = false;
		
		
		// initializing frame

		// set window icon
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getClassLoader().getResource("simetimer/simetimer.png")));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		config.initialize();
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
		
		startStopButton.setBounds(FOURTH_ITEM_COLUMN_OFFSET,
															FIRST_ITEM_ROW_OFFSET,
															FOURTH_ITEM_COLUMN_SIZE,
															FIRST_ITEM_ROW_SIZE);
		startStopButton.setText("Start");
		startStopButton.setFont(new Font("Dialog", Font.BOLD, 20));
		cp.add(startStopButton);
		
		cutButton.setBounds(FOURTH_ITEM_COLUMN_OFFSET,
												SECOND_ITEM_ROW_OFFSET,
												FOURTH_ITEM_COLUMN_SIZE,
												SECOND_ITEM_ROW_SIZE);
		cutButton.setText("cut");
		cutButton.setToolTipText("Finish the current segment and instantly begin a new one");
		cutButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(cutButton);
		
		saveButton.setBounds(SECOND_ITEM_COLUMN_OFFSET,
												 THIRD_ITEM_ROW_OFFSET,
												 SECOND_ITEM_COLUMN_SIZE,
												 THIRD_ITEM_ROW_SIZE);
		saveButton.setText("save");
		saveButton.setToolTipText("Save the current project to a file");
		saveButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(saveButton);
		
		loadButton.setBounds(THIRD_ITEM_COLUMN_OFFSET,
												 THIRD_ITEM_ROW_OFFSET,
												 THIRD_ITEM_COLUMN_SIZE,
												 THIRD_ITEM_ROW_SIZE);
		loadButton.setText("load");
		loadButton.setToolTipText("Load a project from a file");
		loadButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(loadButton);
		
		resetButton.setBounds(FOURTH_ITEM_COLUMN_OFFSET,
													THIRD_ITEM_ROW_OFFSET,
													FOURTH_ITEM_COLUMN_SIZE,
													THIRD_ITEM_ROW_SIZE);
		resetButton.setText("reset");
		resetButton.setFont(new Font("Dialog", Font.PLAIN, 14));
		resetButton.setBackground(new Color(255, 200, 200));
		cp.add(resetButton);
		
		optionsButton.setBounds(FIRST_ITEM_COLUMN_OFFSET,
														THIRD_ITEM_ROW_OFFSET,
														FIRST_ITEM_COLUMN_SIZE,
														THIRD_ITEM_ROW_SIZE);
		optionsButton.setIcon(
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getClassLoader().getResource("simetimer/options.png"))));
		cp.add(optionsButton);
		
		// TABLE
		// set ScrollPane properties
		tableScrollPane.setBounds(FIRST_ITEM_COLUMN_OFFSET,
															FOURTH_ITEM_ROW_OFFSET,
															FULL_WIDTH_COLUMN_SIZE,
															FOURTH_ITEM_ROW_SIZE);
		tableScrollPane.setAutoscrolls(true);
		tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
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
					long stopTime = System.currentTimeMillis();
					startStopButton.setText("Start");
					enableDisplayTimer(false);
					String comment = "";
					if (config.askForCommentOnStop) {
						comment = JOptionPane.showInputDialog(THIS,
																									"You can enter a comment for the last time chunk here:",
																									"Enter comment",
																									JOptionPane.PLAIN_MESSAGE);
						if (comment == null) {
							comment = "";
						}
					}
					// add TimeChunk to project
					project.addTimeChunk(new TimeChunk(currentStartTime,
																						 stopTime - currentStartTime,
																						 comment));
					// update labels and table
					refreshTimeLabels();
					tableModel.addRow(project.getStringArray(project.size()-1));
					scrollDown();
					// new unsaved data
					unsavedData = true;
				}
			}
		});
		
		cutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				long callTime = System.currentTimeMillis();
				scrollDown(); // FIXME remove
				if (!running()) {
					return;
				}
				String comment = "";
				if (config.askForCommentOnCut) {
					comment = JOptionPane.showInputDialog(THIS,
							"You can enter a comment for the last time chunk here:",
							"Enter comment",
							JOptionPane.PLAIN_MESSAGE);
					if (comment == null) {
						comment = "";
					}
				}
				// add TimeChunk to project
				project.addTimeChunk(new TimeChunk(currentStartTime,
																					 callTime - currentStartTime,
																					 comment));
				currentStartTime = callTime;
				updateProjectTime();
				tableModel.addRow(project.getStringArray(project.size()-1));
				scrollDown();
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
					config.usedFile = fileChooser.getSelectedFile();
					if (!config.usedFile.getName().contains(".")) {
						config.usedFile = new File(config.usedFile.getPath().concat(".stp"));
					}
					SaveManager.saveProject(THIS, project, config.usedFile, config.fileFormat);
					unsavedData = false;
				}
			}
		};
		saveButton.addActionListener(saveButtonAction);
		
		// LOAD
		SaveLoadAction loadButtonAction = new SaveLoadAction(this) {
			private static final long serialVersionUID = 7117119994405070822L;
			@Override
			void call(JFileChooser fileChooser, JFrame owner) {
				if (unsavedData && config.askForSaveOnLoad) {
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
					// user has approved load
					SimeTimerProject temp = SaveManager.loadProject(THIS, fileChooser.getSelectedFile(), config.fileFormat);
					if (temp != null) {
						// loading successful
						project = temp;
						unsavedData = false;
						updateProjectTime();
						refreshTimeLabels();
						refreshTable();
						config.usedFile = fileChooser.getSelectedFile();
					} else {
						// loading failed
						// do nothing
					}
				}
			}
		};
		loadButton.addActionListener(loadButtonAction);
		
		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				new OptionFrame(THIS, config);
			}
		});
		
		// CLOSE: save preferences and ask for save on close
		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent evt) {}
			@Override
			public void windowClosed(WindowEvent evt) {}
			@Override
			public void windowClosing(WindowEvent evt) {
				config.saveConfiguration();
				if (unsavedData && config.askForSaveOnClose) {
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
		// set Lable texts
		refreshTimeLabels();
		setVisible(true);
		
		
		
		if (config.loadLastSaveOnStartup
				&& config.usedFile != null
				&& config.usedFile.isFile()) {
			// open last used project
			SimeTimerProject temp = SaveManager.loadProject(THIS, config.usedFile, config.fileFormat);
			if (temp != null) {
				project = temp;
				refreshTimeLabels();
				refreshTable();
			} else {
				// loading failed - set usedFile to parent folder
				config.usedFile = config.usedFile.getParentFile();
			}
		}
	}
	
	
	
	
	/**
	 * shortcut to check whether the timer is running
	 * @return true if the startStopButton is toggled, else false
	 */
	private boolean running() {
		return startStopButton.isSelected();
	}
	
	
	/**
	 * resets the current project completely
	 */
	private void reset() {
		if (!unsavedData ||
				JOptionPane.showConfirmDialog(this,
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
	
	/**
	 * sets table model, properties and column names, widths and alignment
	 */
	private void initializeTable() {
		// set tableModel
		tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 4218091309598726974L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 3;
			}
		};
		table.setModel(tableModel);
		
		// set table properties
		table.setFillsViewportHeight(true);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDragEnabled(false);
		table.getTableHeader().setReorderingAllowed(true);
		
		// set table colums
		tableModel.addColumn("#");
		tableModel.addColumn("Start date");
		tableModel.addColumn("Time");
		tableModel.addColumn("Comment");
		TableColumnModel columns = table.getColumnModel();
		
		// set column widths
		// #
		columns.getColumn(0).setResizable(false);
		columns.getColumn(0).setMinWidth(12);
		columns.getColumn(0).setMaxWidth(30);
		columns.getColumn(0).setPreferredWidth(30);
		// Start date
		columns.getColumn(1).setResizable(false);
		columns.getColumn(1).setMinWidth(130);
		columns.getColumn(1).setMaxWidth(140);
		columns.getColumn(1).setPreferredWidth(140);
		// Time
		columns.getColumn(2).setResizable(false);
		columns.getColumn(2).setMinWidth(85);
		columns.getColumn(2).setMaxWidth(97);
		columns.getColumn(2).setPreferredWidth(97);
		// Comment
		columns.getColumn(3).setResizable(true);
		columns.getColumn(3).setMinWidth(100);
		columns.getColumn(3).setMaxWidth(1000);
		columns.getColumn(3).setPreferredWidth(267);
		
		// set alignment
		DefaultTableCellRenderer leftAligner = new DefaultTableCellRenderer();
		leftAligner.setHorizontalAlignment(SwingConstants.LEFT);
		DefaultTableCellRenderer rightAligner = new DefaultTableCellRenderer();
		rightAligner.setHorizontalAlignment(SwingConstants.RIGHT);
		DefaultTableCellRenderer centerAligner = new DefaultTableCellRenderer();
		centerAligner.setHorizontalAlignment(SwingConstants.CENTER);
		columns.getColumn(0).setCellRenderer(rightAligner);
		columns.getColumn(1).setCellRenderer(rightAligner);
		columns.getColumn(2).setCellRenderer(centerAligner);
		columns.getColumn(3).setCellRenderer(leftAligner);
		
		// store changed comments
		tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent evt) {
				if (evt.getColumn() != 3) {
					// not relevant
					return;
				}
				for (int i = evt.getFirstRow(); i <= evt.getLastRow(); i++) {
					String temp = project.getTimeChunk(i).getComment();
					// save comment in row i
					project.getTimeChunk(i).setComment((String) tableModel.getValueAt(i, 3));
					// if new value is not equal to the old one, there is new unsaved data
					if (!temp.equals(project.getTimeChunk(i).getComment())) {
						unsavedData = true;
					}
				}
			}
		});
		
		// set automatic scrolling to the bottom on changed values
		// FIXME
		tableScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				//e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
	}
	
	/**
	 * empties the table and refills it from current project
	 */
	private void refreshTable() {
		// empty the table
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}
		// add new data from project
		for (int i = 0; i < project.size(); i++) {
			tableModel.addRow(project.getStringArray(i));
		}
		scrollDown();
	}
	
	/**
	 * scrolls the table all the way down
	 */
	private void scrollDown() {
		// FIXME
		tableScrollPane.getVerticalScrollBar().setValue(tableScrollPane.getVerticalScrollBar().getMaximum());
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
		result
				.append(hours)
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
		String total, chunk;
		if (running()) {
			total = timeToString(lastProjectTime +
													 System.currentTimeMillis() - currentStartTime);
			chunk = timeToString(System.currentTimeMillis() - currentStartTime);
		} else {
			updateProjectTime();
			total = timeToString(lastProjectTime);
			chunk = timeToString(project.getLastChunk() != null ?
														 project.getLastChunk().getStoppedTime() : 0);
		}
		totalTimeLabel.setText(total);
		chunkTimeLabel.setText(chunk);
	}
	
	/**
	 * updates the lastProjectTime field
	 */
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
			displayTimer.schedule(displayTask, java.util.Calendar.getInstance().getTime(), 7);
		} else {
			// stop timer
			displayTimer.cancel();
		}
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
	 * helper class to implement the save and load function. SaveLoadAction is
	 * called on click on save/load button
	 * 
	 * @author Simon Vetter
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
			fileChooser = new JFileChooser(config.usedFile.getParentFile());
			fileChooser.setMinimumSize(new Dimension(350, 300));
			// only display directories and .stp files
			// (and files without an extension)
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() ||
								 file.isFile() && file.getName().endsWith(".stp")	||
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
		 * method to overwrite on construction. Is called with a prepared
		 * {@link JFileChooser}
		 * @param fileChooser a {@link JFileChooser} with preselected path and {@link FileFilter}
		 * @param owner parent {@link JFrame} for the {@link JFileChooser}
		 */
		abstract void call(JFileChooser fileChooser, JFrame owner);
	}
	
}