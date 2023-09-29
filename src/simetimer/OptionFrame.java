package simetimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;
import java.net.URL;



public class OptionFrame extends JFrame {
	
	@Serial
	private static final long serialVersionUID = -4351186530101301374L;
	
	// layout constants
	private static final int	DEFAULT_GAP					= 10,
								BIG_GAP						= 20;
	private static final int	CHECKBOX_COUNT				= ConfigManager.DEFAULT_BOOL_OPTIONS.length,
								CHECKBOX_HEIGHT				= 20,
								VERTICAL_CHECKBOX_GAP		= 0;
	private static final int	CHECKBOX_ROW_HEIGHT			= CHECKBOX_HEIGHT * CHECKBOX_COUNT + VERTICAL_CHECKBOX_GAP * (CHECKBOX_COUNT - 1),
								SECOND_ITEM_ROW_HEIGHT		= 25,
								THIRD_ITEM_ROW_HEIGHT		= 25,
								FOURTH_ITEM_ROW_HEIGHT		= 30;
	public static final int		FIRST_ITEM_COLUMN_WIDTH		= 110,
								FULL_COLUMN_WIDTH			= 270;
	public static final int		CHECKBOX_ROW_OFFSET			= DEFAULT_GAP,
								SECOND_ITEM_ROW_OFFSET		= CHECKBOX_ROW_OFFSET + CHECKBOX_ROW_HEIGHT + DEFAULT_GAP,
								THIRD_ITEM_ROW_OFFSET		= SECOND_ITEM_ROW_OFFSET + SECOND_ITEM_ROW_HEIGHT + DEFAULT_GAP,
								FOURTH_ITEM_ROW_OFFSET		= THIRD_ITEM_ROW_OFFSET + THIRD_ITEM_ROW_HEIGHT + BIG_GAP;
	public static final int		FIRST_ITEM_COLUMN_OFFSET	= DEFAULT_GAP,
								SECOND_ITEM_COLUMN_OFFSET	= FIRST_ITEM_COLUMN_OFFSET + FIRST_ITEM_COLUMN_WIDTH;
	// window constants
	public static final int		FRAME_WIDTH					= DEFAULT_GAP + FULL_COLUMN_WIDTH - 10 + DEFAULT_GAP + 16;
	public static final int		FRAME_HEIGHT				= FOURTH_ITEM_ROW_OFFSET + FOURTH_ITEM_ROW_HEIGHT + DEFAULT_GAP + 38;
	
	
	private final ConfigManager config;
	
	// layout elements
	private final JCheckBox[] checkboxes = new JCheckBox[CHECKBOX_COUNT];
	private final JLabel tableSizeLabel = new JLabel();
	private final JSpinner tableSizeSpinner = new JSpinner();
	private final JLabel fileFormatLabel = new JLabel();
	private final JComboBox<String> fileFormatCombobox = new JComboBox<>();
	private final JButton okButton = new JButton();
	private final JButton cancelButton = new JButton();
	
	// logic variables
	
	
	/**
	 * constructor. Stores owner {@link SimeTimer} and {@link ConfigManager}
	 * and initializes frame
	 * @param owner the owning {@link SimeTimer}
	 * @param config the owning {@link SimeTimer}'s {@link ConfigManager}
	 */
	public OptionFrame(SimeTimer owner, ConfigManager config) {
		super("SimeTimer Options");
		this.config = config;
		
		// set window icon
		URL iconURL = getClass().getClassLoader().getResource("simetimer/options.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(iconURL));
		// initializing frame
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		int xLocation = owner.getLocationOnScreen().x + ((owner.FRAME_WIDTH()  - FRAME_WIDTH ) / 2);
		int yLocation = owner.getLocationOnScreen().y + ((owner.FRAME_HEIGHT() - FRAME_HEIGHT) / 2);
		setLocation(xLocation, yLocation);
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);
		
		
		
		// setting layout
		
		String[] checkBoxStrings = {"load last save on startup",
									"autosave all changes",
									"show comment prompt when pressing stop",
									"show comment prompt when pressing cut",
									"show save prompt when loading a project",
									"show save prompt when closing the window",
									"show wide table (requires restart)"};
		boolean[] checkBoxStatus = {config.loadLastSaveOnStartup,
									config.autosave,
									config.askForCommentOnStop,
									config.askForCommentOnCut,
									config.askForSaveOnLoad,
									config.askForSaveOnClose,
									config.wideTable};
		for (int i=0; i<CHECKBOX_COUNT; i++) {
			checkboxes[i] = new JCheckBox();
			checkboxes[i].setBounds(
					FIRST_ITEM_COLUMN_OFFSET - 4,
					CHECKBOX_ROW_OFFSET + i * (CHECKBOX_HEIGHT + VERTICAL_CHECKBOX_GAP),
					FULL_COLUMN_WIDTH,
					CHECKBOX_HEIGHT);
			checkboxes[i].setText(checkBoxStrings[i]);
			checkboxes[i].setFont(new Font("Dialog", Font.PLAIN, 12));
			checkboxes[i].setSelected(checkBoxStatus[i]);
			cp.add(checkboxes[i]);
		}
		
		// table size
		tableSizeLabel.setBounds(
				FIRST_ITEM_COLUMN_OFFSET,
				SECOND_ITEM_ROW_OFFSET,
				FIRST_ITEM_COLUMN_WIDTH,
				SECOND_ITEM_ROW_HEIGHT);
		tableSizeLabel.setText("Table size: ");
		tableSizeLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(tableSizeLabel);
		
		tableSizeSpinner.setBounds(
				SECOND_ITEM_COLUMN_OFFSET,
				SECOND_ITEM_ROW_OFFSET,
				50,
				SECOND_ITEM_ROW_HEIGHT);
		tableSizeSpinner.setModel(new SpinnerNumberModel(
				config.tableSize,
				ConfigManager.MIN_TABLE_ROW_COUNT,
				ConfigManager.MAX_TABLE_ROW_COUNT,
				1));
		cp.add(tableSizeSpinner);
		
		// file format
		fileFormatLabel.setBounds(
				FIRST_ITEM_COLUMN_OFFSET,
				THIRD_ITEM_ROW_OFFSET,
				FIRST_ITEM_COLUMN_WIDTH,
				THIRD_ITEM_ROW_HEIGHT);
		fileFormatLabel.setText("Save file format: ");
		fileFormatLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(fileFormatLabel);
		
		fileFormatCombobox.setBounds(
				SECOND_ITEM_COLUMN_OFFSET,
				THIRD_ITEM_ROW_OFFSET,
				90,
				THIRD_ITEM_ROW_HEIGHT);
		fileFormatCombobox.setModel(new DefaultComboBoxModel<>(new String[]{"Plain text", "Byte coded"}));
		fileFormatCombobox.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileFormatCombobox.setSelectedIndex(config.fileFormat != SaveManager.FILE_FORMAT_BYTE ? 0 : 1);
		cp.add(fileFormatCombobox);
		
		// OK BUTTON
		okButton.setBounds(
				FULL_COLUMN_WIDTH - 60,
				FOURTH_ITEM_ROW_OFFSET,
				60,
				FOURTH_ITEM_ROW_HEIGHT);
		okButton.setText("OK");
		okButton.setFont(new Font("Dialog", Font.BOLD, 12));
		cp.add(okButton);
		
		cancelButton.setBounds(
				okButton.getX() - DEFAULT_GAP - 80,
				FOURTH_ITEM_ROW_OFFSET,
				80,
				FOURTH_ITEM_ROW_HEIGHT);
		cancelButton.setText("Cancel");
		cancelButton.setFont(new Font("Dialog", Font.BOLD, 12));
		cp.add(cancelButton);
		
		
		// button functionalities
		
		okButton.addActionListener(evt -> {
			boolean[] boolOptions = new boolean[CHECKBOX_COUNT];
			for (int i=0; i<CHECKBOX_COUNT; i++) {
				boolOptions[i] = checkboxes[i].isSelected();
			}
			config.setOptions(
					boolOptions,
					(int) tableSizeSpinner.getModel().getValue(),
					fileFormatCombobox.getSelectedIndex() != 1 ? SaveManager.FILE_FORMAT_PLAIN : SaveManager.FILE_FORMAT_BYTE);
			config.saveConfiguration();
			dispose();
		});
		
		cancelButton.addActionListener(evt -> dispose());
		
		
		// on window close with unsaved changes: ask user whether they want to save
		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent evt) {}
			@Override
			public void windowClosed(WindowEvent evt) {}
			@Override
			public void windowClosing(WindowEvent evt) {
				if (changesMade() && JOptionPane.showConfirmDialog(evt.getComponent(),
							"You didn't confirm the changes.\nDo you want to keep them?",
							"Options not saved",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE)
						== JOptionPane.YES_OPTION) {
					// user wants to save options
					okButton.doClick();
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
		setVisible(true);
	}
	
	
	/**
	 * examines whether the user made any effective changes to the options
	 * (so checking a checkbox twice doesn't count as a change)
	 * @return true if effective changes have been made, else false 
	 */
	private boolean changesMade() {
		boolean boolsChanged = false;
		for (int i = 0; i < CHECKBOX_COUNT; i++) {
			boolsChanged |= checkboxes[i].isSelected() != config.boolOptions()[i];
		}
		return boolsChanged
				|| (int) tableSizeSpinner.getModel().getValue() != config.tableSize
				|| (fileFormatCombobox.getSelectedIndex() == 0 && config.fileFormat == SaveManager.FILE_FORMAT_BYTE)
				|| (fileFormatCombobox.getSelectedIndex() == 1 && config.fileFormat == SaveManager.FILE_FORMAT_PLAIN);
	}
	
}