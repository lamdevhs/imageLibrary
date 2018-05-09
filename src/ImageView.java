import java.awt.*;
import java.awt.image.*;

import javax.swing.*;


public class ImageView extends JPanel {
	private ImageModel model;
	private JLabel image;
	
	ImageView(ImageModel model_) {
		image = new JLabel();
		add(image);
		model = model_;
	}

	public void display(int imgSize) {
		int realh = model.buffered.getHeight();
		int realw = model.buffered.getWidth();
		double maxSize = Math.max(realh, realw);
		double factor = (double)imgSize / maxSize;
		int width = Math.min(imgSize, (int)(realw * factor));
		int height = Math.min(imgSize, (int)(realh * factor));
		Image scaled = model.buffered.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
		image.setIcon(new ImageIcon(scaled));
	}
}
