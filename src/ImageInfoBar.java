import java.awt.*;

import javax.swing.*;


public class ImageInfoBar extends JPanel {
	
	private ImageView image = null;
	
	ImageInfoBar() {
		//this.setPreferredSize(new Dimension(0, 25));
		this.setBackground(U.Colors.darkBG);
	}
	
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		if (image == null) return;
//		Font font = new Font("Sans Serif", Font.PLAIN, 18);
//		g.setFont(font);
//		g.drawString(image.model.key, 5, 20);
//	}
	
	public void setImage(ImageView image_) {
		image = image_;
		refresh();
		//this.repaint();
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
