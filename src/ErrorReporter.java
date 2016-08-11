import javax.swing.JOptionPane;

public class ErrorReporter {

	public static void report(Exception e) {
		JOptionPane.showMessageDialog(null, e);
	}
}
