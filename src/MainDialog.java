import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MainDialog extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public static MainDialog currentDialog;
	
	private JTextField patternNameField;
	public JTextField numRowsField;
	public JTextField numColsField;
	private JTextField rowsPerPageField;
	private JTextField framesPerRowField;
	private JTextField gapField;
	private JTextField fontSizeField;
	private JTextField fpsField;
	private JButton dualButton;
	private JButton generateButton;
	
	private static final Font font = new Font("SansSerif", Font.TRUETYPE_FONT, 18);
	
	public static int parseInt(String string, int defaultValue) {
		if (string == null)
			return defaultValue;
		if (string.isEmpty())
			return defaultValue;
		return Integer.parseInt(string);
	}
	
	private void updateNumericTextField(JTextField field) {
		String text = field.getText();
		generateButton.setEnabled(true);
		if (text.isEmpty())
			return;
		try {
			Integer.parseInt(text);
		} catch (NumberFormatException e) {
			generateButton.setEnabled(false);
		}
	}
	
	private void setNumericTextField(JTextField field) {
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateNumericTextField(field);
			}

			public void removeUpdate(DocumentEvent e) {
				updateNumericTextField(field);
			}

			public void insertUpdate(DocumentEvent e) {
				updateNumericTextField(field);
			}
		});
	}

	private static void createAndShowGUI() {
		MainDialog frame = new MainDialog();
		frame.setLayout(new SpringLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container container = frame.getContentPane();

		createConstantLabel(frame, ".CODE.", 23, JLabel.RIGHT);
		createConstantLabel(frame, "Generator", 23, JLabel.LEFT);
		frame.patternNameField = createTextField(frame, "Filename: ");
		frame.numRowsField = createTextField(frame, "Rows: ", "8");
		frame.setNumericTextField(frame.numRowsField);
		frame.numColsField = createTextField(frame, "Columns: ", "8");
		frame.setNumericTextField(frame.numColsField);
		frame.rowsPerPageField = createTextField(frame, "Rows per page: ", "8");
		frame.setNumericTextField(frame.rowsPerPageField);
		frame.framesPerRowField = createTextField(frame, "Frames per row: ", "4");
		frame.setNumericTextField(frame.framesPerRowField);
		frame.gapField = createTextField(frame, "Gap size: ", "1");
		frame.setNumericTextField(frame.gapField);
		frame.fontSizeField = createTextField(frame, "Font size: ", "7");
		frame.setNumericTextField(frame.fontSizeField);
		frame.fpsField = createTextField(frame, "FPS: ", "8");
		frame.setNumericTextField(frame.fpsField);
		frame.dualButton = createButton(frame, "Do Both");
		frame.dualButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generator.workWithTwoMusics();
				JOptionPane.showMessageDialog(frame, "Done");
			}
		});
		frame.generateButton = createButton(frame, "Generate");
		frame.generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generator.writeToFile = Generator.visualize = true;
				Generator.sheetExclusion = Generator.frameSimplification = false;
				String fileName = frame.patternNameField.getText().replace(".xlsx", "");
				int fps = parseInt(frame.fpsField.getText(), 24);
				int numRows = parseInt(frame.numRowsField.getText(), 8);
				int numCols = parseInt(frame.numColsField.getText(), 8);
				int rowsPerPage = parseInt(frame.rowsPerPageField.getText(), 8);
				int framesPerRow = parseInt(frame.framesPerRowField.getText(), 4);
				// Fix laziness
				if (fileName.equals("ET")) {
					framesPerRow = 3;
					frame.framesPerRowField.setText(framesPerRow + "");
				}
				int gap = parseInt(frame.gapField.getText(), 1);
				int fontSize = parseInt(frame.fontSizeField.getText(), 12);
				Generator.workWithPattern(fileName, fps, numRows, numCols, rowsPerPage, -1, framesPerRow, gap, fontSize);
			}
		});
		
		SpringUtilities.makeCompactGrid(container, frame.getContentPane().getComponentCount() / 2, 2, 5, 5, 5, 5);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		currentDialog = frame;
	}
	
	@SuppressWarnings("unused")
	private static void createNull(JFrame frame) {
		JButton b = new JButton();
		b.setVisible(false);
		frame.add(b);
	}
	
	private static void createConstantLabel(JFrame frame, String labelString, int fontSize, int alignment) {
		JLabel label = new JLabel(labelString);
		label.setHorizontalAlignment(alignment);
		label.setFont(new Font("SansSerif", Font.TRUETYPE_FONT, fontSize));
		frame.add(label);
	}
	
	private static JButton createButton(JFrame frame, String buttonString) {
		JButton button = new JButton(buttonString);
		frame.add(button);
		return button;
	}
	
	private static JTextField createTextField(JFrame frame, String labelString, String defaults) {
		JLabel label = new JLabel(labelString, JLabel.TRAILING);
		frame.add(label);
		JTextField textField = new JTextField();
		textField.setFont(font);
		textField.setPreferredSize(new Dimension(80, 30));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		if (defaults != null)
			textField.setText(defaults);
		frame.add(textField);
		label.setLabelFor(textField);
		return textField;
	}
	
	private static JTextField createTextField(JFrame frame, String labelString) {
		return createTextField(frame, labelString, null);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
