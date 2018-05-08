import javax.swing.*;


public class UpperPanel extends JPanel {
	private Session session;
	//private MainFrame view;

	public UpperPanel(Session session_) {
		super();
		session = session_;
		//view = view_;

		add(new JLabel("MainFrame -- Session: " + session.name));
	}

}
