import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;


public class MainFrame extends JFrame {

	public static void log(String string) {
		U.log("(MainFrame) " + string);
	}

	private App app;
	private Session session;

	private Listener listener = new Listener();
	
	private TagPanel tagPanel;
	private ImagesPanel imagesPanel;
	//private StatusBar statusBar;
	private UpperPanel upperPanel;

	MainFrame(App app_, Session session_){
		app = app_;
		session = session_;
		
		setTitle("ImgLibrary - " + session.name);
		setSize(600,600);
		setLocationRelativeTo(null); // center frame on screen
		addWindowListener(listener);
		
		//JPanel centerPanel = new JPanel();
		imagesPanel = new ImagesPanel(session_, 3, 20, 20);
		JScrollPane imagesScroller = new JScrollPane(imagesPanel);
		imagesPanel.container = imagesScroller;
		imagesScroller.addComponentListener(imagesPanel.listener);
		imagesPanel.setPreferredSize(imagesScroller.getPreferredSize());
		imagesScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		imagesScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//centerPanel.add(imagesScroller, BorderLayout.CENTER);
		add(imagesScroller, BorderLayout.CENTER);
		

		tagPanel = new TagPanel(session_);
		add(tagPanel, BorderLayout.WEST);
		
		upperPanel = new UpperPanel(session_);
		add(upperPanel, BorderLayout.NORTH);

		setVisible(true);
		
		imagesPanel.setDimensions();

		readSession();
	}

	public void readSession() {}


	private class Listener
	extends WindowAdapter {
		public void log(String string) {
			U.log("(MainFrame.Listener) " + string);
		}

		@Override
		public void windowClosing(WindowEvent ev) {
			app.closeSession(MainFrame.this);
		}
	}

}
