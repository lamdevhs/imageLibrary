import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
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

	// writing code in Java is like writing
	// your phone number in binary...
	public HashMap<String, ImageModel> images =
			new HashMap<String, ImageModel>();
	public HashMap<String, Tag> tags =
			new HashMap<String, Tag>();
	
	public ArrayList<Filter> filters =
			new ArrayList<Filter>();
	
	public Session() {}

	public Session(String name_, File folder_) {
		name = name_;
		folder = folder_;
		newTag("City: New York City: New York City: ...");
		newTag("Ci Size");
		newTag("Color: red");
	}

	public static Session fromData(SessionData data) {
		return new Session(data.name, new File(data.folder));
	}

	public int refresh() {
		int report = U.checkValidFolder(folder);
		if (report != U.OK) {
			log("refreshing failed!");
			return report;
		}
		// else
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

	public ArrayList<ImageModel> getAllImages() {
		ArrayList<ImageModel> output = new ArrayList<ImageModel>();
		Iterator<String> iter = images.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			output.add(images.get(key));
		}
		return output;
	}
	
	public ArrayList<ImageModel> getImages() {
		if (filters.size() == 0) return getAllImages();
		// else
		return new ArrayList<ImageModel>();
//		ArrayList<String> keys = filters.get(0).tag.images;
//		for (int i = 0; i < filters.size(); i++) {
//			intersectKeySets(keys, filters.get(i).tag.images);
//		}
//		return keysToImages(keys);
	}
	
	private void intersectKeySets(ArrayList<String> keys, ArrayList<String> otherKeys) {
		ArrayList<String> output;
//		
//		for (int i = 0; i < keys.size(); i++) {
//			String key = keys.get(i);
//			boolean hasIt = false;
//			for (int j = 0; j < otherKeys.size(); i++) {
//				if (Objects.equals(key, otherKeys.get(i))
//			}
//		}
//		
	}

	public ArrayList<ImageModel> keysToImages(ArrayList<String> keys) {
		ArrayList<ImageModel> output = new ArrayList<ImageModel>();
		for (int i = 0; i < keys.size(); i++) {
			output.add(images.get(keys.get(i)));
		}
		return output;
	}

	public ArrayList<Tag> getTags() {
		ArrayList<Tag> output = new ArrayList<Tag>();
		Iterator<String> iter = tags.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			output.add(tags.get(key));
		}
		return output;
	}
	
	public int newTag(String tagname) {
		tags.put(tagname, new Tag(tagname));
		return U.OK;
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

	public ArrayList<Filter> getFilters() {
		return filters;
	}
	
	public void addFilter(String tagname, boolean negated) {
		if (tags.containsKey(tagname)) {
			// ^ should always happen
			if (hasFilter(tagname, negated)) return;
			// else
			filters.add(new Filter(tags.get(tagname)));
			this.setChanged();
			this.notifyObservers();
		}
	}

	private boolean hasFilter(String tagname, boolean negated) {
		for (int i = 0; i < filters.size(); i++) {
			Filter other = filters.get(i);
			if (other.negated == negated
			&& other.tag.name == tagname)
				return true;
		}
		return false;
	}

	public void removeFilter(Filter filter) {
//		int index = -1;
//		for (int i = 0; i < filters.size(); i++) {
//			Filter other = filters.get(i);
//			if (other.negated == filter.negated
//			&& other.tag.name == filter.tag.name)
//			{
//				index = i;
//			}
//		}
//		if (index >= 0)
		filters.remove(filter);
		this.setChanged();
		this.notifyObservers();
	}

}
