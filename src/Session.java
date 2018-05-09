import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;


public class Session extends Observable {
	public static void log(String string) {
		U.log("(Session) " + string);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public File folder;

	public HashMap<String, ImageModel> images;
	
	public Session() {}

	public Session(String name_, File folder_) {
		name = name_;
		folder = folder_;
	}

	public static Session fromData(SessionData data) {
		return new Session(data.name, new File(data.folder));
	}

	public int refresh() {
		int report = U.checkValidFolder(folder);
		if (report != U.OK) {
			return report;
		}
		// else
		images = new HashMap<String, ImageModel>();
		extractImages(folder, images);
		log("extractImages: " + images.toString());
		return U.OK;
	}

	public SessionData data() {
		SessionData data = new SessionData();
		data.name = name;
		if (folder != null)
			data.folder = folder.getAbsolutePath();
		else
			data.folder = null;
		return data;
	}

	public ArrayList<ImageModel> getImages() {
		ArrayList<ImageModel> output = new ArrayList<ImageModel>();
		Iterator<String> iiter = images.keySet().iterator();
		while(iiter.hasNext()) {
			String key = iiter.next();
			output.add(images.get(key));
		}
		return output;
	}

	private void extractImages(File dir,
		HashMap<String, ImageModel> images)
	{
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()){
				extractImages(file, images);
			}
			else {
				ImageModel image = ImageModel.fromFile(file);
				if (image != null) { // valid image
					images.put(image.getKey(folder.getAbsolutePath()), image);
				}
			}
		}
	}

}
