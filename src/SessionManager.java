import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.*;


public class SessionManager extends JFrame implements Observer {
	public void log(String string) {
		U.log("(MainFrame) " + string);
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

	// South
		private Box south = Box.createHorizontalBox();
		private JButton quit = new JButton("Quit");
		private JButton open = new JButton("Open");

	// North
		private JPanel north = new JPanel();
		private JLabel text = new JLabel("Session Manager");
	
	public SessionManager(App app_, Model model_) {
		// super(owner, true);
		
		model = model_;
		app = app_;
		Dimension oneButton = new JButton("MMMMM").getPreferredSize();

		setTitle("Session Manager");
		setSize(300,400);
		setLocationRelativeTo(null); // center frame on screen
		addWindowListener(listener);

		getContentPane().add(inside);
		inside.setLayout(new BorderLayout());
		inside.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Widgets
			rename.setPreferredSize(oneButton);
			delete.setPreferredSize(oneButton);
			create.setPreferredSize(oneButton);
			quit.setPreferredSize(oneButton);
			open.setPreferredSize(oneButton);

			rename.addActionListener(listener);
			delete.addActionListener(listener);
			create.addActionListener(listener);
			quit.addActionListener(listener);
			open.addActionListener(listener);

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
			west.setPreferredSize(oneButton);
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

	@Override
	public void update(Observable arg0, Object arg1) {
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
				log("create session");
				String msg = "Name for the new session :";
				String name = U.input(SessionManager.this, msg);
				if (name == null) {
					log("abandon");
					return;
				}
				// else
				log("name given: " + name);
				int sessionIndex = model.createSession(name);
				log("matching session index: " + sessionIndex);
				if (sessionIndex < 0) {
					this.namingSessionError(sessionIndex);
					return;
				}
				// else
				// nothing to do -- Observer patter will update
				// the manager
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
					log("rename");
					String newName = U.input(SessionManager.this, "New name for the session :");
					if (newName == null) {
						log("abandon");
						return;
					}
					// else
					int report = model.renameSession(newName, sessionIndex);
					if (report < 0) {
						this.namingSessionError(report);
						return;
					}
					// else
					log("renaming done");
					log("sessionsList: " + Arrays.toString(model.getSessionsNames()));
					// the SessionManager dialog should now get refreshed automatically
					// thanks to the Observer pattern
				}
				else if (source == delete) {
					log("delete");
					int answer = U.confirm(SessionManager.this, "Deleting a session cannot be undone. Proceed?");
					if (answer != JOptionPane.YES_OPTION) {
						log("abandon");
						return;
					}
					// else
					model.deleteSession(sessionIndex);
					log("deleting done");
					// the SessionManager dialog should now get refreshed automatically
					// thanks to the Observer pattern
				}
				else if (source == open) {
					log("open");
					SessionManager.this.app.openSession(SessionManager.this, sessionIndex);
				}
			}
		}
		
		public void namingSessionError(int error) {
			String errmsg = "A user error was encountered.";
			if (error == U.IMPOSSIBLE) {
				errmsg = "A session with that name already exists.";
			}
			else if (error == U.INVALID) {
				errmsg = "The given name is not a valid session name.";
			}
			U.error(SessionManager.this, errmsg);
		}
		
		@Override
		public void windowClosing(WindowEvent ev) {
			app.quit(SessionManager.this);
		}
		
	}

}
