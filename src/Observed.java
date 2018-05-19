import java.util.Observable;


public class Observed extends Observable {
	public void notifyObservers() {
		super.setChanged();
		super.notifyObservers();
	}
}
