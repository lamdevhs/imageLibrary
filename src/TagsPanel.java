import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
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

	// == tags area
	private JPanel wrapper = new JPanel();
	private JPanel aboveTagsBox = new JPanel();
	private JTextField searchBox = new JTextField(13);
	
	private JPanel tagsPanel = new JPanel();
	private Box tagsBox = Box.createVerticalBox();
	
	// == filters area
	private JPanel filtersPanel = new JPanel();
	private Box filtersBox = Box.createVerticalBox();
	
	// == menus
	private JPopupMenu oneTagMenu = new JPopupMenu();
	private JMenuItem addToSel = new JMenuItem("Add to Selected Images");
	private JMenuItem rmvFromSel = new JMenuItem("Remove from Selected Images");
	private JMenuItem addToVisible = new JMenuItem("Add to Filtered Images");
	private JMenuItem rmvFromVisible = new JMenuItem("Remove from Filtered Images");
	private JMenuItem renameTag = new JMenuItem("Rename Tag");
	private JMenuItem delTag = new JMenuItem("Delete Tag");

	private JButton menuButton = new JButton("Menu");
	private JPopupMenu generalMenu = new JPopupMenu();
	private JMenuItem newTag = new JMenuItem("Create New Tag");
	
	public TagsPanel(Session session_, JFrame frame_) {
		session = session_;
		frame = frame_;
		JLabel padding =  new JLabel("                               ");
		JLabel padding2 = new JLabel("                               ");
		
		addToSel.addActionListener(listener);
		addToVisible.addActionListener(listener);
		rmvFromSel.addActionListener(listener);
		rmvFromVisible.addActionListener(listener);
		renameTag.addActionListener(listener);
		delTag.addActionListener(listener);

		newTag.addActionListener(listener);

		oneTagMenu.add(addToSel);
		oneTagMenu.add(addToVisible);
		oneTagMenu.addSeparator();
		oneTagMenu.add(rmvFromSel);
		oneTagMenu.add(rmvFromVisible);
		oneTagMenu.addSeparator();
		oneTagMenu.add(renameTag);
		oneTagMenu.addSeparator();
		oneTagMenu.add(delTag);
		
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
		

		filtersBox.removeAll();
		filtersBox.repaint();
		Iterator<String> iter = session.filters.keySet().iterator();
		while(iter.hasNext()) {
			Filter filter = session.filters.get(iter.next());
			FilterView filterview = new FilterView(filter);
			filterview.addMouseListener(listener);
			filtersBox.add(filterview);
		}
		
		revalidate();
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		refresh();
	}
	
	private void createNewTag() {
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

	private void renameTag(Tag tag) {
		log("ping: renameTag");
		String newName = U.input(frame,
			"Old name: " + U.quoted(tag.name) +
			"\n\nNew name:");
		if (newName == null) {
			log("abandon");
			return;
		}
		// else
		int report = session.renameTag(newName, tag);
		if (report != U.OK) {
			namingTagError(report);
			return;
		}
	}

	private void namingTagError(int error) {
		String errmsg = "An unknown error was encountered.";
		if (error == U.IMPOSSIBLE) {
			errmsg = "A tag with that name already exists.";
		}
		else if (error == U.INVALID) {
			errmsg = "The given name is not a valid tag name.";
		}
		U.error(frame, errmsg);
	}

	private void deleteTag(Tag tag) {
		log("deleteTag");
		int answer = U.confirm(frame,
			"Name of the tag: " +
			  U.quoted(tag.name) +
			"\n\nDeleting a tag cannot be undone, " +
			"however no image will be deleted. Proceed?");
		if (answer != JOptionPane.YES_OPTION) {
			log("(abandon)");
			return;
		}
		// else
		session.deleteTag(tag);
		log("deleteTag done");
	}

	private class Listener
	implements
		DocumentListener, // to listen to the searchbox
		MouseListener,
		ActionListener // button and menus
	{
		
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
			else if (source == renameTag) {
				renameTag(selectedTag);
			}
			else if (source == delTag) {
				deleteTag(selectedTag);
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
					session.removeFilter(filterview.filter.tag.name);
				}
				else if (SwingUtilities.isRightMouseButton(ev)) {
					log("ping rightClick on filter");
					this.selectedTag = filterview.filter.tag;
					oneTagMenu.show(ev.getComponent(), ev.getX(), ev.getY());
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
