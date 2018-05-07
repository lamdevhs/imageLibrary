import java.io.Serializable;


public class Session implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Session() {
		
	}
}
