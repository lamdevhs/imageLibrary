import java.awt.BorderLayout;

import javax.swing.*;


public class TagPanel extends JPanel {

	Session session;
	
	private Box north = Box.createVerticalBox();
	
	public TagPanel(Session session_) {
		super();
		session = session_;
		north.add(new JTextField());
		north.add(new JLabel("bla bla"));
		north.add(new JLabel("bla bla bla"));
		add(north, BorderLayout.NORTH);
	}

}
