import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.*;


public class LeftSidePanel extends JPanel implements Observer {
	private static void log(String s) {
		U.log("(LeftSidePanel) " + s);
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
	private JMenuItem addToSel = new JMenuItem("Selected Images");
	private JMenuItem rmvFromSel = new JMenuItem("Selected Images");
	private JMenuItem addToVisible = new JMenuItem("All Images");
	private JMenuItem rmvFromVisible = new JMenuItem("All Images");
	private JMenuItem renameTag = new JMenuItem("Rename Tag");
	private JMenuItem delTag = new JMenuItem("Delete Tag");

	private JButton menuButton = new JButton("Menu");
	private JPopupMenu generalMenu = new JPopupMenu();
	private JMenuItem newTag = new JMenuItem("Create New Tag");
	
	public LeftSidePanel(Session session_, JFrame frame_) {
		session = session_;
		frame = frame_;
		JLabel padding =  new JLabel("                               ");
		JLabel padding2 = new JLabel("                               ");
		
		addToSel.addActionListener(listener);
		addToSel.setToolTipText("Add this tag to all the selected images on the right side.");
		addToVisible.addActionListener(listener);
		addToVisible.setToolTipText("Add this tag to all the filtered images. "
				+ "The filtered images are those displayed on the right side");
		rmvFromSel.addActionListener(listener);
		rmvFromSel.setToolTipText("Remove this tag from all the selected images on the right side.");
		rmvFromVisible.addActionListener(listener);
		rmvFromVisible.setToolTipText("Remove this tag from all the filtered images. "
				+ "The filtered images are those displayed on the right side");
		renameTag.addActionListener(listener);
		delTag.addActionListener(listener);

		newTag.addActionListener(listener);

		oneTagMenu.add(U.deadMenu("Add Tag to Images:"));
		oneTagMenu.add(addToSel);
		oneTagMenu.add(addToVisible);
		oneTagMenu.addSeparator();

		oneTagMenu.add(U.deadMenu("Remove Tag from Images:"));
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
		session.tagsState.addObserver(this);
		refresh();
	}


	private void refresh() {
		String search = searchBox.getText();
		log("refresh called - search: " + search);
		
		tagsBox.removeAll();
		tagsBox.repaint();
		Iterator<Tag> tag_iter = session.tags.values().iterator();
		while (tag_iter.hasNext()) {
			Tag tag = tag_iter.next();
			if (tag.name.contains(search)
			&& !session.filters.containsKey(tag.name))
			{
				TagView tagview = new TagView(tag);
				tagview.addMouseListener(listener);
				tagsBox.add(tagview);
			}
		}
		

		filtersBox.removeAll();
		filtersBox.repaint();
		Iterator<Filter> filter_iter = session.filters.values().iterator();
		while(filter_iter.hasNext()) {
			Filter filter = filter_iter.next();
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
		// `this` LeftSidePanel
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
				session.removeImagesFromTag(selectedTag, true);
			}
			else if (source == addToVisible) {
				session.addImagesToTag(selectedTag, false);
			}
			else if (source == rmvFromVisible) {
				session.removeImagesFromTag(selectedTag, false);
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
