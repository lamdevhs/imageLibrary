import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class U {
	public static int IMPOSSIBLE = -1;
	public static int INVALID = -2;
	public static int OK = 0;

	public static String catPaths(String head, String tail) {
		return head + "/" + tail;
	}

	public static void log(String string) {
		System.out.println("[log]\t" + string);
	}

	public static void error(JFrame owner, String errmsg) {
		JOptionPane.showMessageDialog(owner, errmsg, "User Error",
			JOptionPane.ERROR_MESSAGE);
	}
	
	// public static void error(JDialog owner, String errmsg) {
	// 	JOptionPane.showMessageDialog(owner, errmsg, "User Error",
	// 		JOptionPane.ERROR_MESSAGE);
	// }
	
	public static String input(JFrame owner, String msg) {
		return JOptionPane.showInputDialog(owner, msg);
	}
	
	// public static String input(JDialog owner, String msg) {
	// 	return JOptionPane.showInputDialog(owner, msg);
	// }

	// public static int confirm(JFrame owner, String msg) {
	// 	return JOptionPane.showConfirmDialog(owner, msg);
	// }
	
	public static int confirm(JFrame owner, String msg) {
		return JOptionPane.showConfirmDialog(owner, msg, "Confirmation",
			JOptionPane.YES_NO_OPTION);
	}
	
	// return null in case of failure
	public static Object fromXML(String path) {
		Object o;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		XMLDecoder decoder = null;
		
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e1) {
			U.log("fromXML: FileNotFoundException");
			//e1.printStackTrace();
			return null;
		}
		bis = new BufferedInputStream(fis);
		decoder = new XMLDecoder(bis);
		o = decoder.readObject();
		decoder.close();
		try {
			bis.close();
			fis.close();
		} catch (IOException e) {
			U.log("fromXML: IOException");
			//e.printStackTrace();
		}
		return o;
	}
	
	// returns false if failed
	public static boolean toXML(Object o, String path) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		XMLEncoder encoder = null;
		try {
			fos = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			U.log("toXML: FileNotFoundException");
			//e.printStackTrace();
			return false;
		}
		bos = new BufferedOutputStream(fos);
		encoder = new XMLEncoder(bos);
		
		encoder.writeObject(o);
		encoder.flush();
		encoder.close();
		try {
			bos.close();
			fos.close();
		} catch (IOException e) {
			U.log("toXML: IOException");
			//e.printStackTrace();
			return false; // or true?
		}
		return true;
	}

	public static File folderDialog(JFrame owner, String title, File dir) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new File("/opt/pics"));
		// if (dir != null) chooser.setCurrentDirectory(dir);
		// ^ to uncomment in the end
		
		int report = chooser.showOpenDialog(owner);
		if (report != JFileChooser.APPROVE_OPTION) return null;

		File result = chooser.getSelectedFile();
		if (!result.exists() || !result.isDirectory()) return null;
		else return result;
	}

	public static int checkValidFolder(File folder) {
		if (folder == null
			|| !folder.exists()
			|| !folder.isDirectory())
			return U.INVALID;
		else return U.OK;
	}

}
