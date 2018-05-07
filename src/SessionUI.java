import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;


public class SessionUI extends JDialog {
	
	private Model model;
	private int returnValue = U.INVALID;

	private JPanel inside = new JPanel();

	private Listener listener = new Listener();
	
	// Center
		private JScrollPane sessionsPane;
		private JList sessions;

	// West
		private JPanel west = new JPanel();
		//private Box west = Box.createVerticalBox();
		private JButton rename = new JButton("Rename");
		private JButton delete = new JButton("Delete");

	// South
		private Box south = Box.createHorizontalBox();
		private JButton quit = new JButton("Quit");
		private JButton create = new JButton("New");
		private JButton open = new JButton("Open");

	// North
		private JPanel north = new JPanel();
		private JLabel text = new JLabel("Session Manager");
	
	public SessionUI(JFrame owner, Model model_) {
		super(owner, true);
		
		model = model_;
		Dimension oneButton = new JButton("MMMMM").getPreferredSize();

		setTitle("Session Manager");
		setSize(300,400);

		getContentPane().add(inside);
		inside.setLayout(new BorderLayout());
		inside.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// North
			north.add(text);
			inside.add(north, BorderLayout.NORTH);

		// Center
			sessions = new JList(model.getSessions().toArray());
			sessions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			sessionsPane = new JScrollPane (sessions);
			sessions.setVisibleRowCount(6);
			sessionsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			inside.add(sessionsPane, BorderLayout.CENTER);

		// West
			rename.setPreferredSize(oneButton);
			delete.setPreferredSize(oneButton);

			rename.addActionListener(listener);
			delete.addActionListener(listener);

			west.setLayout(new FlowLayout());
			west.setPreferredSize(oneButton);
			west.add(rename);
			west.add(delete);
			inside.add(west, BorderLayout.WEST);

		// South
			quit.setPreferredSize(oneButton);
			create.setPreferredSize(oneButton);
			open.setPreferredSize(oneButton);

			quit.addActionListener(listener);
			create.addActionListener(listener);
			open.addActionListener(listener);

			south.add(quit);
			south.add(Box.createGlue());
			south.add(create);
			south.add(Box.createGlue());
			south.add(open);
			inside.add(south, BorderLayout.SOUTH);
	}
	
	public int openSession() {
		setVisible(true);
		
		// after the dialog is closed:
		if (returnValue == U.INVALID) {
			return U.quitApp();
		}
		else return returnValue;
	}
	

	public void closeDialog(int sessionIndex) {
		returnValue = sessionIndex;
		this.setVisible(false);
		// closes the dialog
		// the flow continues inside this.openSession()
	}

	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Object src = arg0.getSource();
			if (src == create) {
				U.log("create session");
				String msg = "Name for the new session :";
				String name = U.input(SessionUI.this, msg);
				if (name == null) {
					U.log("abandon");
					return;
				}
				// else
				U.log("name given: " + name);
				int sessionIndex = model.createSession(name);
				U.log("matching session index: " + sessionIndex);
				if (sessionIndex < 0) {
					this.namingSessionError(sessionIndex);
					return;
				}
				// else
				SessionUI.this.closeDialog(sessionIndex);
			}
			if (src == quit) {
				U.quitApp();
			}
			else { // open, delete or rename
				int sessionIndex = sessions.getSelectedIndex();
				U.log("index selected: " + sessions.getSelectedIndex());
				if (sessionIndex == -1) {
					U.error(SessionUI.this, "No session was selected.");
					return;
				}
				if (src == rename) {
					U.log("rename");
					String newName = U.input(SessionUI.this, "New name for the session :");
					if (newName == null) {
						U.log("abandon");
						return;
					}
					// else
					int report = model.renameSession(newName, sessionIndex);
					if (report < 0) {
						this.namingSessionError(report);
						return;
					}
					// else
					U.log("renaming done");
					// the SessionUI should now get refreshed automatically
					// thanks to the Observer pattern
				}
				else if (src == delete) {
					U.log("delete");
					int answer = U.confirm(SessionUI.this, "Deleting a session cannot be undone. Proceed?");
					if (answer != JOptionPane.YES_OPTION) {
						U.log("abandon");
						return;
					}
					// else
					model.deleteSession(sessionIndex);
					U.log("deleting done");
					// the SessionUI should now get refreshed automatically
					// thanks to the Observer pattern
				}
				else if (src == open) {
					U.log("open");
					SessionUI.this.closeDialog(sessionIndex);
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
			U.error(SessionUI.this, errmsg);
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model model = new Model();
		new MainUI(model);
		//ui.setVisible(true);
	}

}
