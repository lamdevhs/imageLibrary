import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;


public class ImagesPanel extends JPanel {
	public static void log(String string) {
		U.log("(ImagesPanel) " + string);
	}
	
	private Session session;
	private ArrayList<ImageView> images = new ArrayList<ImageView>();
	public Listener listener = new Listener();
//	private ImageView foo;
//	private ImageView bar;
//	private JButton b;
	private int hpadding;
	private int vpadding;
	public Container container;
	private int ncol;

	public ImagesPanel(Session session_, int ncol_, int hpadding_, int vpadding_) {
		session = session_;
		hpadding = hpadding_;
		vpadding = vpadding_;
		ncol = ncol_;

		setLayout(null);
		getImages();
		setBackground(Color.WHITE);
		//foo = new ImageView("foo", 5);
//		foo = new JLabel("bla");
//		foo.setBackground(Color.RED);
//		//bar = new ImageView("bar", 5);
//		bar = new JLabel("bar");
//		bar.setBackground(Color.BLUE);
//		add(foo);
//		add(bar);
//		add(foo);
//		add(foo);
//		b = new JButton("foo");
//		add(b);
		
	}
	
	public void setDimensions() {
		
	}
	
	public void getImages() {
		ArrayList<ImageModel> imodels = session.getImages();
		if (imodels == null) log("imodels null !");
		else log("noooooooooo");
		for (int i = 0; i < imodels.size(); i++) {
			ImageView iview = new ImageView(imodels.get(i));
			images.add(iview);
			this.add(iview);
		}
	}
	
	public void refresh() {
		Dimension dim = container.getSize();
		//log(this.getSize() + "" + this.getPreferredSize() + "" + this.getMaximumSize() + "" + this.getMinimumSize());
		//log(container.getSize() + "" + container.getPreferredSize() + "" + container.getMaximumSize() + "" + container.getMinimumSize());
		int panelWidth = (int) dim.width - 18;
		int imgSize = ((panelWidth - hpadding) / ncol) - hpadding;
		
		for (int i = 0; i < images.size(); i++) {
			ImageView image = images.get(i);
			int row = i / ncol;
			int col = i % ncol;
			int x = hpadding + col*(imgSize + hpadding);
			int y = vpadding + row*(imgSize + vpadding);
			image.setBounds(x, y, imgSize, imgSize);
			image.revalidate();
			image.display(imgSize);
		}
		int newHeight;
		if (images.size() % ncol == 0)
			newHeight = (images.size() / ncol)*(vpadding + imgSize) + vpadding; 
		else
			newHeight = ((images.size() / ncol) + 1)*(vpadding + imgSize) + vpadding; 
		setPreferredSize(new Dimension(dim.width - 18, newHeight));
		revalidate();
	}

	private class Listener
	implements ComponentListener
	{

		@Override
		public void componentHidden(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			refresh();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
