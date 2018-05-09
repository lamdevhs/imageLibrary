import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class TagView extends JPanel {
	public Tag tag;
	
	TagView(Tag tag_) {
		tag = tag_;
		setLayout(new BorderLayout());
		JLabel label = new JLabel(tag.name);
		add(label, BorderLayout.CENTER);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(label.getPreferredSize());
	}
}
