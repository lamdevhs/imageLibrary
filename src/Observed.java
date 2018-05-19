import java.util.Observable;


// This class was created because Observable.setChanged()
// is a private method, and without it the observers aren't actually
// notified, so if we want to use Observable as it is used in Session,
// we have to override notifyObservers() with the method below.
public class Observed extends Observable {
	public void notifyObservers() {
		super.setChanged();
		super.notifyObservers();
	}
}
