import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class U {
	public static int IMPOSSIBLE = -1;
	public static int INVALID = -2;
	public static int OK = 0;

	public static void log(String string) {
		System.out.println("[LOG] " + string);
	}

	public static void error(JFrame owner, String errmsg) {
		JOptionPane.showMessageDialog(owner, errmsg, "User Error",
			JOptionPane.ERROR_MESSAGE);
	}
	
	public static void error(JDialog owner, String errmsg) {
		JOptionPane.showMessageDialog(owner, errmsg, "User Error",
			JOptionPane.ERROR_MESSAGE);
	}
	
	public static String input(JFrame owner, String msg) {
		return JOptionPane.showInputDialog(owner, msg);
	}
	
	public static String input(JDialog owner, String msg) {
		return JOptionPane.showInputDialog(owner, msg);
	}

	public static int confirm(JFrame owner, String msg) {
		return JOptionPane.showConfirmDialog(owner, msg);
	}
	
	public static int confirm(JDialog owner, String msg) {
		return JOptionPane.showConfirmDialog(owner, msg, "Confirmation",
			JOptionPane.YES_NO_OPTION);
	}

	public static int quitApp() {
		U.log("quit app");
		System.exit(0);
		return 0; // will of course never be reached
	}

}
