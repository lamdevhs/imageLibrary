import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageModel {

	public String key;
	public File file;
	public BufferedImage buffered;
	
	public ImageModel(String key_) {
		key = key_;
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

	public static String getImageKey(String rootPath, File file) {
		String path = file.getAbsolutePath();
		if (path.startsWith(rootPath)) {
			return path.substring(rootPath.length() + 1);
			// ^ the (+1) is to trim the '/' which separates
			// the relative path from the rootPath
		}
		else return path;
	}

	public String getLocation() {
		return this.key.substring(0,
			this.key.length()
			- this.file.getName().length());
	}

	public String getFullSize() {
		if (buffered == null) return "";
		return this.buffered.getWidth()
			+ "x"
			+ this.buffered.getHeight();
	}
}
