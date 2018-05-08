import java.io.Serializable;


public class SessionData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public SessionData() {}

		public String name;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }

		public String folder;
		public String getFolder() { return folder; }
		public void setFolder(String folder) { this.folder = folder; }
}
