import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.*;

// Dialog box used to open/create/delete/rename/fix a Session,
// and of course quit the application.
public class SessionManager extends JFrame implements Observer {
	public static void log(String string) {
		U.log("(SessionManager) " + string);
	}

	private App app;
	private Model model;
	private int returnValue = U.INVALID;

	private JPanel inside = new JPanel();

	private Listener listener = new Listener();
	
	// Center
		private JScrollPane sessionsPane;
		private JList sessions;

	// West
		private JPanel west = new JPanel();
		private JButton rename = new JButton("Rename");
		private JButton delete = new JButton("Delete");
		private JButton create = new JButton("New");
		private JButton changeFolder = new JButton("Change Folder");

	// South
		private Box south = Box.createHorizontalBox();
		private JButton quit = new JButton("Quit");
		private JButton open = new JButton("Open");

	// North
		private JPanel north = new JPanel();
		private JLabel text = new JLabel("Session Manager");
	
	public SessionManager(App app_, Model model_) {
		
		model = model_;
		app = app_;
		Dimension buttonDim = changeFolder.getPreferredSize();

		setTitle("Session Manager");
		setSize(500,400);
		setLocationRelativeTo(null); // center frame on screen
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(listener);

		getContentPane().add(inside);
		inside.setLayout(new BorderLayout());
		inside.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Widgets
		rename.setPreferredSize(buttonDim);
		delete.setPreferredSize(buttonDim);
		create.setPreferredSize(buttonDim);
		quit.setPreferredSize(buttonDim);
		open.setPreferredSize(buttonDim);
		changeFolder.setPreferredSize(buttonDim);

		rename.addActionListener(listener);
		delete.addActionListener(listener);
		create.addActionListener(listener);
		quit.addActionListener(listener);
		open.addActionListener(listener);
		changeFolder.addActionListener(listener);

		sessions = new JList();
		sessions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// North
		north.add(text);
		inside.add(north, BorderLayout.NORTH);

		// Center
		sessionsPane = new JScrollPane (sessions);
		sessionsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		inside.add(sessionsPane, BorderLayout.CENTER);

		// West
		west.setLayout(new FlowLayout());
		west.setPreferredSize(buttonDim);
		west.add(changeFolder);
		west.add(rename);
		west.add(delete);
		west.add(create);
		inside.add(west, BorderLayout.WEST);

		// South
		south.add(quit);
		south.add(Box.createGlue());
		south.add(open);
		inside.add(south, BorderLayout.SOUTH);
			
		model.addObserver(this);
		readModel();
		setVisible(true);
	}
	
	private void readModel() {
		log("SessionManager: readModel called");
		Object[] array = model.getSessionsNames();
		sessions.setListData(array);
	}

	public void createSession() {
		log("create session");
		String msg = "Name for the new session :";
		String name = U.input(this, msg);
		if (name == null) {
			log("abandon");
			return;
		}
		// else
		log("name chosen: " + name);

		int report = model.checkNewSessionName(name, null);
		log("report on name chosen: " + report);
		if (report != U.OK) {
			namingSessionError(report);
			return;
		}

		// else
		int sessionIndex = model.addNewSession(name, null, null);

		changeSessionFolder(sessionIndex);
		
		// nothing further to do -- Observer pattern will update
		// `this` session manager
	}

	public void renameSession(int sessionIndex) {
		log("rename");
		if (model.allSessions.size() <= sessionIndex)
			return; // should never happen!

		String newName = U.input(this,
			"Old name: " +
			U.quoted(model.allSessions.get(sessionIndex).name) +
			"\n\nNew name:");
		if (newName == null) {
			log("abandon");
			return;
		}
		// else
		int report = model.renameSession(newName, sessionIndex);
		if (report != U.OK) {
			this.namingSessionError(report);
			return;
		}
		// else
		// the SessionManager dialog should now get refreshed automatically
		// thanks to the Observer pattern
	}

	public void deleteSession(int sessionIndex) {
		log("delete");
		if (model.allSessions.size() <= sessionIndex)
			return; // should never happen!
		
		int answer = U.confirm(this,
				"Name of the session: " +
				  U.quoted(model.allSessions.get(sessionIndex).name) +
				"\n\nDeleting a session cannot be undone. Proceed?");
				
		if (answer != JOptionPane.YES_OPTION) {
			log("abandon");
			return;
		}
		// else
		model.deleteSession(sessionIndex);
		log("deleting done");
		// this SessionManager dialog should now get refreshed
		// automatically thanks to the Observer pattern
	}

	public void namingSessionError(int error) {
		String errmsg = "An unknown error was encountered.";
		if (error == U.IMPOSSIBLE) {
			errmsg = "A session with that name already exists.";
		}
		else if (error == U.INVALID) {
			errmsg = "The given name is not a valid session name.";
		}
		U.error(this, errmsg);
	}

	public void changeSessionFolder(int sessionIndex) {
		log("changeSessionFolder");
		File folder = U.folderDialog(this,
			"Choose the session's image folder", null);
		if (U.checkValidFolder(folder) != U.OK)
		{
			log("folderDialog -> is null ? " + (folder == null));
			U.warning(this, "Warning: no valid folder was chosen.");
		}
		else {
			log("folderDialog -> "+ folder.getAbsolutePath());
		}

		Session session = model.allSessions.get(sessionIndex);
		session.folderPath = (folder == null) ? null : folder.getAbsolutePath();
		session.folder = folder; // can be null
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		log("ping update()");
		readModel();
	}

	private class Listener
	extends WindowAdapter
	implements ActionListener
	{
		public void log(String string) {
			U.log("(SessionManager.Listener) " + string);
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			Object source = ev.getSource();
			if (source == create) {
				createSession();
			}
			else if (source == quit) {
				app.quit(SessionManager.this);
			}
			else { // source == open, delete or rename
				int sessionIndex = sessions.getSelectedIndex();
				log("index selected: " + sessions.getSelectedIndex());
				if (sessionIndex == -1) {
					U.error(SessionManager.this, "No session was selected.");
					return;
				}
				if (source == rename) {
					renameSession(sessionIndex);
				}
				else if (source == delete) {
					deleteSession(sessionIndex);
				}
				else if (source == open) {
					log("open");
					SessionManager.this.app.openSession(SessionManager.this, sessionIndex);
				}
				else if (source == changeFolder) {
					changeSessionFolder(sessionIndex);
				}
			}
		}
		
		@Override
		public void windowClosing(WindowEvent ev) {
			app.quit(SessionManager.this);
		}
		
	}

}
