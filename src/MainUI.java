import javax.swing.*;


public class MainUI extends JFrame {
	private Model model;
	
	MainUI(Model model_){
		model = model_;
		
		setSize(200,200);

		SessionUI sessionUI = new SessionUI(this, model);
		int res = sessionUI.openSession();
		U.log("MainUI: res: " + res);
		setVisible(true);
	}
}
