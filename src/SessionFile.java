import java.io.Serializable;


public class SessionFile implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public SessionFile() {}

		public String name;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
}
