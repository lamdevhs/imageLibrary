import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.*;


public class TagsPanel extends JPanel implements Observer {
	private static void log(String s) {
		U.log("(TagsPanel) " + s);
	}
	Session session;
	private Listener listener = new Listener();

	// westPanel
		private JPanel westPanel = new JPanel();
		private JPanel westPanelHeader = new JPanel();
		private JTextField searchBox = new JTextField(13);
		private Box tagsBox = Box.createVerticalBox();
		private Box filtersBox = Box.createVerticalBox();

	// eastPanel
		private JPanel eastPanel = new JPanel();
	
	public TagsPanel(Session session_) {
		session = session_;

		
		// westPanel
			westPanel.setLayout(new BorderLayout());
			westPanel.setBorder(BorderFactory.createTitledBorder("All Tags"));
			
			westPanelHeader.setLayout(new BorderLayout());
			westPanelHeader.add(U.centered(new JButton("New")), BorderLayout.NORTH);
			westPanelHeader.add(U.centered(new JLabel("Search:")), BorderLayout.CENTER);
			westPanelHeader.add(searchBox, BorderLayout.SOUTH);

			westPanel.add(westPanelHeader, BorderLayout.NORTH);
			westPanel.add(new JLabel("                          "), BorderLayout.CENTER);
			westPanel.add(tagsBox, BorderLayout.SOUTH);
			
			add(westPanel, BorderLayout.WEST);	
		
		// eastPanel
			eastPanel.setLayout(new BorderLayout());
			eastPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
			eastPanel.add(new JLabel("                          "), BorderLayout.NORTH);
			eastPanel.add(filtersBox, BorderLayout.SOUTH);
			
			add(eastPanel, BorderLayout.EAST);
		
		searchBox.getDocument().addDocumentListener(listener);
		session.addObserver(this);
		refresh();
	}


	private void refresh() {
		String search = searchBox.getText();
		log("refresh called - search: " + search);
		
		tagsBox.removeAll();
		tagsBox.repaint();
		boolean tagsBox_isEmpty = true;
		ArrayList<Tag> tags = session.getTags();
		for (int i = 0; i < tags.size(); i++) {
			Tag tag = tags.get(i);
			if (tag.name.contains(search)) {
				tagsBox_isEmpty = false;
				TagView tagview = new TagView(tag);
				tagview.addMouseListener(listener);
				tagsBox.add(tagview);
			}
		}
	//	if (tagsBox_isEmpty) tagsBox.add(new JLabel("(empty)"));
		

		filtersBox.removeAll();
		filtersBox.repaint();
		ArrayList<Filter> filters = session.getFilters();
		for (int i = 0; i < filters.size(); i++) {
			Filter filter = filters.get(i);
			FilterView filterview = new FilterView(filter);
			filterview.addMouseListener(listener);
			filtersBox.add(filterview);
		}
		
		if (filters.size() == 0);
			//filtersBox.add(new JLabel("(empty)"));
		
		revalidate();
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		refresh();
	}

	private class Listener
	implements DocumentListener, MouseListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			refresh();
			
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			refresh();
			
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			refresh();
			
		}

		//////////////////////
		
		@Override
		public void mouseClicked(MouseEvent ev) {
			Object source = ev.getSource();
			if (SwingUtilities.isLeftMouseButton(ev)) {
				if (source instanceof TagView) {
					TagView tagview = (TagView)source;
					session.addFilter(tagview.tag.name, false);
				}
				else if (source instanceof FilterView) {
					FilterView filterview = (FilterView)source;
					session.removeFilter(filterview.filter);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
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
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
