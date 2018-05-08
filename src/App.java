import java.io.File;

import javax.swing.*;


public class App {
	public static void main(String[] args) {
		new App("appdata");
	}

	public static void log(String string) {
		U.log("(App) " + string);
	}

	Model model;
	//MainFrame mainFrame;
	
	App(String rootpath) {
		Locator locator = new Locator(rootpath);
		model = new Model(locator);
		//mainFrame = new MainFrame(this);

		openSessionManager(null);
	}
	
	public void quit(JFrame caller) {
		int answer = U.confirm(caller, "Are you sure you want to quit?");
		if (answer != JOptionPane.YES_OPTION) return;
		model.save();
		log("quit app");
		System.exit(0);
	}

	public void openSessionManager(JFrame caller) {
		if (caller != null) caller.dispose();
		log("openSessionManager");
		// SessionManager sessionManager = 
		new SessionManager(this, model);
		// int sessionIndex = sessionManager.open();
	}

	public void openSession(JFrame caller, int sessionIndex) {
		if (caller != null) caller.dispose();
		log("openSession: sessionIndex = " + sessionIndex);
			// TODO test sessionIndex is a valid index before getting session
		Session session = model.allSessions.get(sessionIndex);

		new MainFrame(this, session);
	}

	public void closeSession(JFrame caller) {
		if (caller != null) caller.dispose();
		log("close session");
		openSessionManager(null);
	}


}
