import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageModel {

	public String key;
	public File file;
	public BufferedImage buffered;
	
//	public ImageModel(String key_, File file_) {
//		this(key_);
//		file = file_;
//	}
	
	public ImageModel(String key_) {
		key = key_;
	}
	
	public static BufferedImage readImage(File file) {
		try {
			BufferedImage bim = ImageIO.read(file);
			//buffered = buffered_;
			return bim;
		}
		catch (IOException ioe) {
			return null; // error: file is probably not an image
		}
		catch (IllegalArgumentException iae) {
			return null;
		}
	}

	public static String getImageKey(String rootPath, File file) {
		String path = file.getAbsolutePath();
		if (path.startsWith(rootPath)) {
			return path.substring(rootPath.length() + 1);
			// ^ to trim the '/' which separates
			// the relative path from the rootPath
		}
		else return path; // incase of symlinked images: return absolute path
	}

}
