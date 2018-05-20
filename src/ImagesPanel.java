import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;


public class ImagesPanel extends JPanel implements Observer {
	public static void log(String string) {
		U.log("(ImagesPanel) " + string);
	}
	
	private Session session;
	private ImageViewList images;
	private Listener listener = new Listener();
	private int hpadding;
	private int vpadding;
	private int ncol;
	// ^ number of columns to use to display the image thumbnails
	
	private JPanel displayArea = new JPanel();
		// ^ where the images are displayed
	private JScrollPane scroller;
	private JPopupMenu panelMenu = new JPopupMenu();
	private JMenuItem sortByName = new JMenuItem("Name");
	private JMenuItem sortByPath = new JMenuItem("Path");
	private JMenuItem sortBySize = new JMenuItem("Size");
	private JMenuItem sortByHeight = new JMenuItem("Height");
	private JMenuItem sortByWidth = new JMenuItem("Width");
	private JMenuItem selectNone = new JMenuItem("Select None");
	
	private int sortedBy = ImageViewList.BY_NAME;

	private ImageInfoBar infoBar = new ImageInfoBar();

	//private int imageSize = 50;

	public ImagesPanel(Session session_, int ncol_, int hpadding_, int vpadding_) {
		session = session_;
		hpadding = hpadding_;
		vpadding = vpadding_;
		ncol = ncol_;

		setLayout(new BorderLayout());
		
		sortByName.addActionListener(listener);
		sortByPath.addActionListener(listener);
		sortBySize.addActionListener(listener);
		sortByHeight.addActionListener(listener);
		sortByWidth.addActionListener(listener);
		selectNone.addActionListener(listener);

		JMenuItem sortBy = new JMenuItem("Sort by:");
		sortBy.setEnabled(false);
		panelMenu.add(selectNone);
		panelMenu.addSeparator();
		panelMenu.add(sortBy);
		panelMenu.add(sortByName);
		panelMenu.add(sortByPath);
		panelMenu.add(sortBySize);
		panelMenu.add(sortByHeight);
		panelMenu.add(sortByWidth);

		displayArea.setLayout(null);
		displayArea.setBackground(Color.WHITE);
		displayArea.addMouseWheelListener(listener);
		displayArea.addMouseListener(listener);

		scroller = new JScrollPane(displayArea);
		scroller.addComponentListener(this.listener);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scroller, BorderLayout.CENTER);
		displayArea.setPreferredSize(scroller.getPreferredSize());

		infoBar.setImage(null);

		add(infoBar, BorderLayout.NORTH);

		session.filteringState.addObserver(this);
		session.selectionState.addObserver(this);
		refreshImages();
	}
	
	@Override
	public void update(Observable whatChanged, Object arg1) {
		log("ping update");
		if (whatChanged == session.filteringState) refreshImages();
		else if (whatChanged == session.selectionState) refreshSelection();
		
	}

	private void refreshSelection() {
		for (int i = 0; i < images.size(); i++) {
			ImageView image = images.get(i);
			image.setSelected(session.selection.contains(image.model));
		}
	}

	public void refreshImages() {
		displayArea.removeAll();
		displayArea.repaint();
		getImages();
		sortImages();
		refreshLayout();
	}
	
	public void getImages() {
		images = new ImageViewList();
		Iterator<ImageModel> iter_imodel = session.visibleImages.iterator();
		while (iter_imodel.hasNext()) {
			ImageView iview = new ImageView(iter_imodel.next());
			images.add(iview);
			displayArea.add(iview);
			iview.addMouseListener(listener);
		}
	}
	
	public void sortImages() {
		log("ping sortImages()");
		images.quickSort(this.sortedBy);
	}
	
	public void refreshLayout() {
		Dimension dim = scroller.getSize();
		int thisWidth = (int) dim.width - 18;
		int imgSize = ((thisWidth - hpadding) / ncol) - hpadding;
		
		for (int i = 0; i < images.size(); i++) {
			ImageView image = images.get(i);
			int row = i / ncol;
			int col = i % ncol;
			int x = hpadding + col*(imgSize + hpadding);
			int y = vpadding + row*(imgSize + vpadding);
			image.setBounds(x, y, imgSize, imgSize);
			image.revalidate();
			image.display(imgSize);
			image.setSelected(session.selection.contains(image.model));
		}
		int newHeight;
		if (images.size() % ncol == 0) // last row of images is full
			newHeight = (images.size() / ncol)*(vpadding + imgSize) + vpadding; 
		else // last row of images is only partially filled
			newHeight = ((images.size() / ncol) + 1)*(vpadding + imgSize) + vpadding; 
		displayArea.setPreferredSize(new Dimension(dim.width - 18, newHeight));
		displayArea.revalidate();
	}
	
	private class Listener
	implements ComponentListener, MouseWheelListener,
		ActionListener, MouseListener
	{

		@Override
		public void actionPerformed(ActionEvent ev) {
			Object source = ev.getSource();
			if (source == sortByName) {
				sortedBy = ImageViewList.BY_NAME;
				sortImages();
				refreshLayout();
			}
			else if (source == sortByPath) {
				sortedBy = ImageViewList.BY_PATH;
				sortImages();
				refreshLayout();
			}
			else if (source == sortBySize) {
				sortedBy = ImageViewList.BY_SIZE;
				sortImages();
				refreshLayout();
			}
			else if (source == sortByHeight) {
				sortedBy = ImageViewList.BY_HEIGHT;
				sortImages();
				refreshLayout();
			}
			else if (source == sortByWidth) {
				sortedBy = ImageViewList.BY_WIDTH;
				sortImages();
				refreshLayout();
			}
			else if (source == selectNone) {
				session.selectNone();
			}
		}

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
			refreshLayout();
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent ev) {
			int notches = ev.getWheelRotation();
			ncol = Math.max(ncol + notches, 1);
			refreshLayout();
			log(notches + " - " + ncol);
		}

		@Override
		public void mouseClicked(MouseEvent ev) {
		}

		@Override
		public void mouseEntered(MouseEvent ev) {
			Object source = ev.getSource();
			if (source instanceof ImageView) {
				ImageView image = (ImageView)source;
				infoBar.setImage(image);
			}
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent ev) {
			Component source = ev.getComponent();
			if (SwingUtilities.isRightMouseButton(ev))
			{
				panelMenu.show(source, ev.getX(), ev.getY());
			}
			else if (SwingUtilities.isLeftMouseButton(ev)
				&& source instanceof ImageView) {
				ImageView image = (ImageView)source;
				session.changeSelection(image.model);
			}
		}
		
	}

}
