import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.*;


public class App {
	public static void log(String string) {
		U.log("(App) " + string);
	}

	Model model;
	Locator locator;
	
	App(String rootpath) {
		locator = new Locator(rootpath);
		model = new Model(locator);
		openSessionManager(null);
	}
	
	public void quit(JFrame caller) {
		//int answer = U.confirm(caller, "Are you sure you want to quit?");
		//if (answer != JOptionPane.YES_OPTION) return;
		// TODO ^ uncomment
		model.save();
		log("quit app");
		System.exit(0);
	}

	public void openSessionManager(JFrame caller) {
		if (caller != null) caller.dispose();
		log("openSessionManager");
		new SessionManager(this, model);
	}

	public void openSession(JFrame caller, int sessionIndex) {
		log("openSession: sessionIndex = " + sessionIndex);
		Session session = model.allSessions.get(sessionIndex);
		int report = session.refresh();
		if (report != U.OK) {
			U.error(caller, "The folder of this session is invalid. "
				+ (session.folderPath != null ?
					"\nFolder path: " + session.folderPath
				  :  "")
				+ "\nYou must fix it from the Session Manager before opening it.");
			return;
		}
		// else
		if (caller != null) caller.dispose();
		new MainFrame(this, session);
	}

	public void closeSession(JFrame caller) {
		if (caller != null) caller.dispose();
		log("close session");
		openSessionManager(null);
	}

	public void setIcon(JFrame frame) {
		BufferedImage icon = U.readImage(new File(locator.app_icon));
		if (icon != null) frame.setIconImage(icon);
	}


}
