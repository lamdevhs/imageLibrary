import java.awt.*;

import javax.swing.*;


public class ImageInfoBar extends JPanel {
	
	private ImageView image = null;
	
	ImageInfoBar() {
		this.setBackground(U.Colors.darkBG);
	}
	public void setImage(ImageView image_) {
		image = image_;
		refresh();
	}
	
	public void refresh() {
		if (image == null) return;
		removeAll();
		repaint();
		add(U.monospaceLabel(image.model.getLocation(), U.Colors.lightBlue, 18));
		add(U.monospaceLabel(image.model.file.getName(), U.Colors.lightGreen, 18));
		add(U.monospaceLabel("-", Color.WHITE, 18));
		add(U.monospaceLabel(image.model.getFullSize(), U.Colors.magenta, 18));
		revalidate();
	}
}
