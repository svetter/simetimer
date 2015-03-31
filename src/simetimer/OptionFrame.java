package simetimer;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class OptionFrame extends JFrame {
	
	private static final long serialVersionUID = -4351186530101301374L;
	
	// layout constants
	private static final int DEFAULT_GAP = 10,
													 NO_GAP = 0,
													 BIG_GAP = 20;
	private static final int VERTICAL_CHECKBOX_SIZE = 20;
	private static final int FIRST_ITEM_ROW_SIZE = VERTICAL_CHECKBOX_SIZE,
													 SECOND_ITEM_ROW_SIZE = VERTICAL_CHECKBOX_SIZE,
													 THIRD_ITEM_ROW_SIZE = VERTICAL_CHECKBOX_SIZE,
													 FOURTH_ITEM_ROW_SIZE = 25,
													 FIFTH_ITEM_ROW_SIZE = 30;
	public static final int FIRST_ITEM_COLUMN_SIZE = 110,
													SECOND_ITEM_COLUMN_SIZE = 90,
													THIRD_ITEM_COLUMN_SIZE = 80,
													FOURTH_ITEM_COLUMN_SIZE = 60,
													FULL_WIDTH_COLUMN_SIZE = 270;
	public static final int FIRST_ITEM_ROW_OFFSET = DEFAULT_GAP,
													SECOND_ITEM_ROW_OFFSET = FIRST_ITEM_ROW_OFFSET
																									 + FIRST_ITEM_ROW_SIZE
																									 + NO_GAP,
													THIRD_ITEM_ROW_OFFSET = SECOND_ITEM_ROW_OFFSET
																									+ SECOND_ITEM_ROW_SIZE
																									+ NO_GAP,
													FOURTH_ITEM_ROW_OFFSET = THIRD_ITEM_ROW_OFFSET
																									 + THIRD_ITEM_ROW_SIZE
																									 + DEFAULT_GAP,
													FIFTH_ITEM_ROW_OFFSET = FOURTH_ITEM_ROW_OFFSET
																									+ FOURTH_ITEM_ROW_SIZE
																									+ BIG_GAP;
	public static final int FIRST_ITEM_COLUMN_OFFSET = DEFAULT_GAP,
													SECOND_ITEM_COLUMN_OFFSET = FIRST_ITEM_COLUMN_OFFSET
																											+ FIRST_ITEM_COLUMN_SIZE
																											+ NO_GAP,
													FOURTH_ITEM_COLUMN_OFFSET = FULL_WIDTH_COLUMN_SIZE
																											- FOURTH_ITEM_COLUMN_SIZE,
													THIRD_ITEM_COLUMN_OFFSET = FOURTH_ITEM_COLUMN_OFFSET
																										 - DEFAULT_GAP
																										 - THIRD_ITEM_COLUMN_SIZE;
	public static final int[] CHECKBOX_OFFSET = {FIRST_ITEM_ROW_OFFSET,
																							 SECOND_ITEM_ROW_OFFSET,
																							 THIRD_ITEM_ROW_OFFSET};
	// window constants
	public static final int FRAME_WIDTH = DEFAULT_GAP
																				+ FULL_WIDTH_COLUMN_SIZE
																				+ DEFAULT_GAP
																				- 10
																				+ 6;
	public static final int FRAME_HEIGHT = FIFTH_ITEM_ROW_OFFSET
																				 + FIFTH_ITEM_ROW_SIZE
																				 + DEFAULT_GAP
																				 + 28;
	
	
	private final ConfigManager config;
	
	// layout elements
	private JCheckBox[] saveLoadCheckboxes = {new JCheckBox(), new JCheckBox(), new JCheckBox()};
	private JLabel fileFormatLabel = new JLabel();
	private JComboBox<String> fileFormatCombobox = new JComboBox<String>();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	
	// logic variables
	
	
	
	public OptionFrame(SimeTimer owner, ConfigManager config) {
		super("SimeTimer Options");
		this.config = config;
		
		// initializing frame
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLocation(owner.getLocationOnScreen().x + ((SimeTimer.FRAME_WIDTH - FRAME_WIDTH) / 2),
								owner.getLocationOnScreen().y + ((SimeTimer.FRAME_HEIGHT - FRAME_HEIGHT) / 2));
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);
		
		// setting layout
		
		String[] checkBoxStrings = {"load last save on startup",
																"show save prompt when loading a project",
																"show save prompt when closing the window"};
		boolean[] checkBoxStatus = {config.loadLastSaveOnStartup(),
																config.askForSaveOnLoad(),
																config.askForSaveOnClose()};
		for (int i=0; i<saveLoadCheckboxes.length; i++) {
			saveLoadCheckboxes[i].setBounds(FIRST_ITEM_COLUMN_OFFSET - 4,
																			CHECKBOX_OFFSET[i],
																			FULL_WIDTH_COLUMN_SIZE,
																			VERTICAL_CHECKBOX_SIZE);
			saveLoadCheckboxes[i].setText(checkBoxStrings[i]);
			saveLoadCheckboxes[i].setFont(new Font("Dialog", Font.PLAIN, 12));
			saveLoadCheckboxes[i].setSelected(checkBoxStatus[i]);
			cp.add(saveLoadCheckboxes[i]);
		}
		
		fileFormatLabel.setBounds(FIRST_ITEM_COLUMN_OFFSET,
															FOURTH_ITEM_ROW_OFFSET,
															FIRST_ITEM_COLUMN_SIZE,
															FOURTH_ITEM_ROW_SIZE);
		fileFormatLabel.setText("Save file format: ");
		fileFormatLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		cp.add(fileFormatLabel);
		
		fileFormatCombobox.setBounds(SECOND_ITEM_COLUMN_OFFSET,
																 FOURTH_ITEM_ROW_OFFSET,
																 SECOND_ITEM_COLUMN_SIZE,
																 FOURTH_ITEM_ROW_SIZE);
		fileFormatCombobox.setModel(new DefaultComboBoxModel<String>(new String[] {"Plain text", "Byte coded"}));
		fileFormatCombobox.setFont(new Font("Dialog", Font.PLAIN, 12));
		fileFormatCombobox.setSelectedIndex(config.fileFormat() != SaveManager.FILE_FORMAT_BYTE ? 0 : 1);
		cp.add(fileFormatCombobox);
		
		okButton.setBounds(FOURTH_ITEM_COLUMN_OFFSET,
													 FIFTH_ITEM_ROW_OFFSET,
													 FOURTH_ITEM_COLUMN_SIZE,
													 FIFTH_ITEM_ROW_SIZE);
		okButton.setText("OK");
		okButton.setFont(new Font("Dialog", Font.BOLD, 12));
		cp.add(okButton);
		
		cancelButton.setBounds(THIRD_ITEM_COLUMN_OFFSET,
													 FIFTH_ITEM_ROW_OFFSET,
													 THIRD_ITEM_COLUMN_SIZE,
													 FIFTH_ITEM_ROW_SIZE);
		cancelButton.setText("Cancel");
		cancelButton.setFont(new Font("Dialog", Font.BOLD, 12));
		cp.add(cancelButton);
		
		
		// button functionalities
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				config.setOptions(saveLoadCheckboxes[0].isSelected(),
													saveLoadCheckboxes[1].isSelected(),
													saveLoadCheckboxes[2].isSelected(),
													fileFormatCombobox.getSelectedIndex() != 1
														? SaveManager.FILE_FORMAT_PLAIN
														: SaveManager.FILE_FORMAT_BYTE);
				config.saveConfiguration();
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		
		
		// on window close with unsaved changes: ask user whether they want to save
		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent evt) {}
			@Override
			public void windowClosed(WindowEvent evt) {}
			@Override
			public void windowClosing(WindowEvent evt) {
				if (changesMade()
						&& JOptionPane.showConfirmDialog(evt.getComponent(),
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
	
	private boolean changesMade() {
		return saveLoadCheckboxes[0].isSelected() ^ config.loadLastSaveOnStartup()
					 || saveLoadCheckboxes[1].isSelected() ^ config.askForSaveOnLoad()
					 || saveLoadCheckboxes[2].isSelected() ^ config.askForSaveOnClose()
					 || (fileFormatCombobox.getSelectedIndex() == 0
					 		 && config.fileFormat() == SaveManager.FILE_FORMAT_BYTE)
					 || (fileFormatCombobox.getSelectedIndex() == 1
					 		 && config.fileFormat() == SaveManager.FILE_FORMAT_PLAIN);
	}
	
}