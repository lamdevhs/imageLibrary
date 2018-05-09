import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class FilterView extends JPanel {
	public Filter filter;
	
	FilterView(Filter filter_) {
		filter = filter_;
		setLayout(new BorderLayout());
		JLabel label = new JLabel(filter.tag.name);
		add(label, BorderLayout.CENTER);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(label.getPreferredSize());
	}
}
