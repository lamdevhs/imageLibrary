import java.awt.*;
import java.awt.image.*;

import javax.swing.*;


public class ImageView extends JPanel {
	public ImageModel model;
	private JLabel image;
	
	ImageView(ImageModel model_) {
		image = new JLabel();
		setLayout(new BorderLayout());
		add(image, BorderLayout.CENTER);
		model = model_;
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createTitledBorder(model.file.getName()));
	}

	public void display(int imgSize) {
		imgSize -= 10;
		if (imgSize <= 0) return;
		int realh = model.buffered.getHeight();
		int realw = model.buffered.getWidth();
		double maxSize = Math.max(realh, realw);
		double factor = (double)imgSize / maxSize;
		int width = Math.min(imgSize, (int)(realw * factor));
		int height = Math.min(imgSize, (int)(realh * factor));
		Image scaled = model.buffered.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
		image.setIcon(new ImageIcon(scaled));
		
	}

	public void setSelected(boolean isSelected) {
		if (isSelected) this.setBackground(U.Colors.lightBG);
		else this.setBackground(Color.WHITE);
	}
}
