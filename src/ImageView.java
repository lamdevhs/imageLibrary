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
		this.setBorder(BorderFactory
			.createTitledBorder(model.file.getName()));
	}

	public void display(int imgSize) {
		imgSize -= 20;
		if (imgSize <= 0) return;
		int realh = model.buffered.getHeight();
		int realw = model.buffered.getWidth();
		double maxSize = Math.max(realh, realw);
		double factor = (double)imgSize / maxSize;
		int width = Math.min(Math.min(imgSize, (int)(realw * factor)), realw);
		int height = Math.min(Math.min(imgSize, (int)(realh * factor)), realh);
		Image scaled = model.buffered.getScaledInstance(
			width, height, Image.SCALE_AREA_AVERAGING);
		image.setIcon(new ImageIcon(scaled));
	}

	public void setSelected(boolean isSelected) {
		if (isSelected) this.setBackground(U.Colors.mediumBG);
		else this.setBackground(Color.WHITE);
	}
}
