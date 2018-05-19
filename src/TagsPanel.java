import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.*;


public class TagsPanel extends JPanel implements Observer {
	private static void log(String s) {
		U.log("(TagsPanel) " + s);
	}
	private Session session;
	private JFrame frame;
	
	public JScrollPane scroller;
	private Listener listener = new Listener();

	// westPanel
	private JPanel wrapper = new JPanel();
	private JPanel aboveTagsBox = new JPanel();
	private JTextField searchBox = new JTextField(13);
	
	private JPanel tagsPanel = new JPanel();
	private Box tagsBox = Box.createVerticalBox();
	
	private JPopupMenu oneTagMenu = new JPopupMenu();
	private JMenuItem addToSel = new JMenuItem("Add to Selected Images");
	private JMenuItem rmvFromSel = new JMenuItem("Remove from Selected Images");
	private JMenuItem addToVisible = new JMenuItem("Add to Filtered Images");
	private JMenuItem rmvFromVisible = new JMenuItem("Remove from Filtered Images");

	private JButton menuButton = new JButton("Menu");
	private JPopupMenu generalMenu = new JPopupMenu();
	private JMenuItem newTag = new JMenuItem("Create New Tag");
	
	// eastPanel
	private JPanel filtersPanel = new JPanel();
	private Box filtersBox = Box.createVerticalBox();
	
	public TagsPanel(Session session_, JFrame frame_) {
		session = session_;
		frame = frame_;
		JLabel padding =  new JLabel("                               ");
		JLabel padding2 = new JLabel("                               ");
		
		addToSel.addActionListener(listener);
		addToVisible.addActionListener(listener);
		rmvFromSel.addActionListener(listener);
		rmvFromVisible.addActionListener(listener);

		newTag.addActionListener(listener);

		oneTagMenu.add(addToSel);
		oneTagMenu.add(addToVisible);
		oneTagMenu.addSeparator();
		oneTagMenu.add(rmvFromSel);
		oneTagMenu.add(rmvFromVisible);
		
		wrapper.setLayout(new BorderLayout());
		
		menuButton.addActionListener(listener);
		generalMenu.add(newTag);

		aboveTagsBox.setLayout(new BorderLayout());
		aboveTagsBox.add(U.centered(menuButton), BorderLayout.NORTH);
		aboveTagsBox.add(U.centered(new JLabel("Search:")), BorderLayout.CENTER);
		aboveTagsBox.add(searchBox, BorderLayout.SOUTH);
		wrapper.add(aboveTagsBox, BorderLayout.CENTER);
		
		
		tagsPanel.setLayout(new BorderLayout());
		tagsPanel.setBorder(BorderFactory.createTitledBorder("Tags"));
		tagsPanel.add(padding2, BorderLayout.NORTH);
		tagsPanel.add(tagsBox, BorderLayout.SOUTH);
		wrapper.add(tagsPanel, BorderLayout.SOUTH);
		
		filtersPanel.setLayout(new BorderLayout());
		filtersPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
		filtersPanel.add(padding, BorderLayout.NORTH);
		filtersPanel.add(filtersBox, BorderLayout.SOUTH);
		
		wrapper.add(filtersPanel, BorderLayout.NORTH);
		
		add(wrapper, BorderLayout.CENTER);
		
		scroller = new JScrollPane(this);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//scroller.addComponentListener(listener);
		
		this.setBackground(U.Colors.lightBG);
		filtersPanel.setBackground(U.Colors.lightBG);
		tagsPanel.setBackground(U.Colors.lightBG);
		
		searchBox.getDocument().addDocumentListener(listener);
		session.filteringState.addObserver(this);
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
		
		//if (filters.size() == 0);
			//filtersBox.add(new JLabel("(empty)"));
		
		revalidate();
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		refresh();
	}
	
	public void createNewTag() {
		log("ping: createNewTag");
		String msg = "Name for the new tag :";
		String name = U.input(frame, msg);
		if (name == null) {
			log("abandon");
			return;
		}
		// else
		log("name chosen: " + name);

		int report = session.checkNewTagName(name, null);
		log("report on name chosen: " + report);
		if (report != U.OK) {
			namingTagError(report);
			return;
		}

		// else
		session.addNewTag(name);
		
		// nothing to do -- Observer pattern will update
		// `this` TagsPanel
	}

	private void namingTagError(int error) {
		String errmsg = "A user error was encountered.";
		if (error == U.IMPOSSIBLE) {
			errmsg = "A tag with that name already exists.";
		}
		else if (error == U.INVALID) {
			errmsg = "The given name is not a valid tag name.";
		}
		U.error(frame, errmsg);
	}

	private class Listener
	implements DocumentListener, MouseListener, ActionListener, ComponentListener {
		
		private Tag selectedTag = null;
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			Object source = ev.getSource();
			if (source == menuButton) {
				generalMenu.show((Component)source,
					0, menuButton.getHeight());
			}
			else if (source == newTag) {
				createNewTag();
			}
		// oneTagMenu:
			else if (source == addToSel) {
				session.addImagesToTag(selectedTag, true);
			}
			else if (source == rmvFromSel) {
				session.removeImagesToTag(selectedTag, true);
			}
			else if (source == addToVisible) {
				session.addImagesToTag(selectedTag, false);
			}
			else if (source == rmvFromVisible) {
				session.removeImagesToTag(selectedTag, false);
			}
		}
		

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			refresh(); // searchBox was modified by user
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			refresh(); // searchBox was modified by user
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			refresh(); // searchBox was modified by user
		}

		//////////////////////
		
		@Override
		public void mouseClicked(MouseEvent ev) {
			Object source = ev.getSource();
			if (source instanceof TagView) {
				TagView tagview = (TagView)source;
				if (SwingUtilities.isLeftMouseButton(ev)) {
					session.addFilter(tagview.tag.name, false);
				}
				else if (SwingUtilities.isRightMouseButton(ev)) {
					log("ping rightClick on tag");
					this.selectedTag = tagview.tag;
					oneTagMenu.show(ev.getComponent(), ev.getX(), ev.getY());
				}
			}
			else if (source instanceof FilterView) {
				FilterView filterview = (FilterView)source;
				if (SwingUtilities.isLeftMouseButton(ev)) {
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
			//refreshLayout();
			
		}


		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		
	}

}
