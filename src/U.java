import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;


// Utility Class full of static methods and values
// useful for the whole program.
public class U {
	public static class Colors {
		public static Color darkBG = new Color(39,40,34);
		public static Color lightBG = new Color(219,229,239);
		public static Color mediumBG = new Color(148,213,239);
		public static Color lightBlue = new Color(102,217,239);
		public static Color lightGreen = new Color(166,226,43);
		public static Color magenta = new Color(249,38,97);
		public static Color orange = new Color(253, 151, 31);
	}

	public static int IMPOSSIBLE = -1;
	public static int INVALID = -2;
	public static int OK = 0;

	public static String catPaths(String head, String tail) {
		return head + "/" + tail;
	}

	public static void log(String string) {
		System.out.println("[log]\t" + string);
	}
	
	public static void elog(String string) {
		System.out.println("[ERROR LOG]\t" + string);
	}

	public static void error(JFrame owner, String errmsg) {
		JOptionPane.showMessageDialog(owner, errmsg, "User Error",
			JOptionPane.ERROR_MESSAGE);
	}

	public static void warning(JFrame owner, String errmsg) {
		JOptionPane.showMessageDialog(owner, errmsg, "Warning",
			JOptionPane.WARNING_MESSAGE);
	}
	
	public static String input(JFrame owner, String msg) {
		return JOptionPane.showInputDialog(owner, msg);
	}
	
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
			e1.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			return false; // or true?
		}
		return true;
	}

	public static File folderDialog(JFrame owner, String title, String dir) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// chooser.setCurrentDirectory(new File("/opt/demo_area.ln"));
		if (dir != null) chooser.setCurrentDirectory(new File(dir));
		
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
	
	public static JPanel centered(JComponent component){
		JPanel wrapper = new JPanel();
		wrapper.add(component);
		return wrapper;
	}
	
	public static JLabel monospaceLabel(String s, Color color, int size, int style) {
		JLabel label = new JLabel(s);
		Font font = new Font(Font.MONOSPACED, style, size);
		label.setFont(font);
		label.setForeground(color);
		return label;
	}
	
	public static JLabel monospaceLabel(String s, Color color, int size) {
		return U.monospaceLabel(s, color, size, Font.PLAIN);
	}

	public static String quoted(String name) {
		return "\"" + name + "\"";
	}

	public static JMenuItem deadMenu(String string) {
		JMenuItem item = new JMenuItem(string);
		item.setEnabled(false);
		return item;
	}
	
	public static BufferedImage readImage(File file) {
		try {
			BufferedImage bim = ImageIO.read(file);
			return bim;
		}
		catch (IOException ioe) {
			return null; // file is probably not an image
		}
		catch (IllegalArgumentException iae) {
			return null;
		}
	}

}
