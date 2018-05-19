import java.awt.Color;
import java.awt.Font;

import javax.swing.*;


public class UpperPanel extends JPanel {
	private Session session;

	public UpperPanel(Session session_) {
		super();
		session = session_;

		add(U.monospaceLabel(session.title(), U.Colors.darkBG, 18));
		setBackground(U.Colors.lightBG);
	}

}
