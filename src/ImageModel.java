import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageModel {

	private File file;
	public BufferedImage buffered;
	
	public ImageModel(File file_, BufferedImage buffered_) {
		file = file_;
		buffered = buffered_;
	}
	
	public static ImageModel fromFile(File file) {
		try {
			BufferedImage buffered = ImageIO.read(file);
			ImageModel image = new ImageModel(file, buffered);
			// TODO Auto-generated method stub
			return image;
		}
		catch (IOException ioe) {
			return null; // error: file is probably not an image
		}
		catch (IllegalArgumentException iae) {
			return null;
		}
	}

	public String getKey(String rootPath) {
		String path = file.getAbsolutePath();
		if (path.startsWith(rootPath)) {
			return path.substring(rootPath.length() + 1);
			// ^ to catch the '/' separator in between the relative path
			// and the rootPath
		}
		else return path; // incase of symlinked images: return absolute path
	}

}
