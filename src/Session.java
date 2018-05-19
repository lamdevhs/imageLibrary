import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;



public class Session {
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
	private HashMap<String, ImageModel> images =
			new HashMap<String, ImageModel>();
	private HashMap<String, Tag> tags =
			new HashMap<String, Tag>();
	
	public HashMap<String, Filter> filters =
			new HashMap<String, Filter>();

	public HashSet<ImageModel> visibleImages =
			new HashSet<ImageModel>();

	public HashSet<ImageModel> selection =
			new HashSet<ImageModel>();
	

	public Observed selectionState = new Observed();
	public Observed filteringState = new Observed();
	

	public Session(String name_, File folder_) {
		name = name_;
		folder = folder_;
	}

	public static Session fromData(SessionData data) {
		File sessionFolder = (data.folder == null) ? null : new File(data.folder);
		Session s = new Session(data.name, sessionFolder);
		s.readTagsData(data.tags);
		return s;
	}
	

	private void readTagsData(HashMap<String, ArrayList<String>> tagsData){
		Iterator<String> iter = tagsData.keySet().iterator();
		while(iter.hasNext()) {
		// for each tagName
			String tagName = iter.next();
			Tag tag = new Tag(tagName);
			ArrayList<String> imagesKeys = tagsData.get(tagName);
			for (int i = 0; i < imagesKeys.size(); i++) {
			// for each image key  (aka its relative path)
				String key = imagesKeys.get(i);
				ImageModel imodel;
				if (images.containsKey(key)) {
					imodel = images.get(key);
					// get imodel if exists:
					// one unique ImageModel per image key
				}
				else {
					imodel = new ImageModel(key);
					images.put(key, imodel);
					// add image to known images
				}
				tag.images.add(imodel);
				// link tag to image and vice versa
			}
			tags.put(tagName, tag);
			// add tag to known tags
		}
	}

	public int refresh() {
		int report = U.checkValidFolder(folder);
		if (report != U.OK) {
			log("refreshing failed!");
			return report;
		}
		// else
		HashMap<String, ImageModel> newImages = new HashMap<String, ImageModel>();
		readFolder(folder, newImages);
		this.images = newImages;
		deleteOldImages();
		log("readFolder says: " + newImages.toString());
		refreshVisibleImages();
		return U.OK;
	}

	public SessionData data() {
		SessionData data = new SessionData();
		data.name = name;
		if (folder != null && U.checkValidFolder(folder) == U.OK)
			data.folder = folder.getAbsolutePath();
		else
			data.folder = null;
		data.tags = saveTags();
		return data;
	}
	
	private HashMap<String, ArrayList<String>> saveTags() {
		HashMap<String,ArrayList<String>> data = new HashMap<String,ArrayList<String>>();
		Iterator<String> tag_iter = tags.keySet().iterator();
		while(tag_iter.hasNext()) {
			String tagName = tag_iter.next();
			Tag tag = tags.get(tagName);
			data.put(tagName, tag.data());
		}
		return data;
	}

	private void refreshVisibleImages() {
		visibleImages.clear();
		Iterator<Filter> iter = filters.values().iterator();
		if (!iter.hasNext())
			visibleImages.addAll(images.values());
		else {
			visibleImages.addAll(iter.next().tag.images);
			while (iter.hasNext()) {
				visibleImages.retainAll(iter.next().tag.images);
			}
		}
		selection.retainAll(visibleImages);
		filteringState.notifyObservers();
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
	
	private void readFolder(File dir, Map<String, ImageModel> newImages)
	{
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()){
				// recursive scan
				readFolder(file, newImages);
			}
			else {
				// is it an image?
				BufferedImage buffered = ImageModel.readImage(file);
				if (buffered == null) continue;
				
				// else: it's an image
				ImageModel image;
				String key = ImageModel.getImageKey(this.folder.getAbsolutePath(), file);

				// do we aleady know this image?
				if (this.images.containsKey(key)) {
					image = this.images.get(key);
				}
				else {
					image = new ImageModel(key);
				}
				image.buffered = buffered;
				image.file = file;
				
				// put this image in the new images
				newImages.put(key, image);
			}
		}
	}
	
	private void deleteOldImages() {
		Iterator<String> tag_iter = tags.keySet().iterator();
		while(tag_iter.hasNext()) {
		// for each tag: remove all images which don't exist anymore
			Tag tag = tags.get(tag_iter.next());
			List<ImageModel> toRemove = new ArrayList<ImageModel>();
			Iterator<ImageModel> img_iter = tag.images.iterator();
			while(img_iter.hasNext()) {
			// for each image known to that tag:
				ImageModel img = img_iter.next();
				if (!this.images.containsKey(img.key)) {
				// if the image is no longer in this.images:
					toRemove.add(img);
				}
			}
			tag.images.removeAll(toRemove);
		}
	}
	
	public void addFilter(String tagname, boolean negated) {
		log("ping addFilter " + tagname);
		if (tags.containsKey(tagname)) {
			// ^ should always happen
			filters.put(tagname, new Filter(tags.get(tagname)));
			refreshVisibleImages();
		}
	}

	public void removeFilter(String tagName) {
		if (filters.containsKey(tagName)) {
			filters.remove(tagName);
			refreshVisibleImages();
		}
	}

	public void changeSelection(ImageModel image) {
		if (image == null || !visibleImages.contains(image)
				|| !visibleImages.contains(image)) {
			U.elog("Session.addToSelection: invalid argument");
			return;
		}
		if (selection.contains(image)) selection.remove(image);
		else selection.add(image);
		selectionState.notifyObservers();
	}

	public void addImagesToTag(Tag tag, boolean onlySelectedImages) {
		if (tag == null || !this.tags.containsKey(tag.name)) {
			return; // should never happen
		}
		if (onlySelectedImages) {
			tag.images.addAll(selection);
		}
		else {
			tag.images.addAll(visibleImages);
		}
		refreshVisibleImages();
	}

	public void removeImagesToTag(Tag tag, boolean onlySelectedImages) {
		if (tag == null || !this.tags.containsKey(tag.name)) {
			return; // should never happen
		}
		if (onlySelectedImages) {
			tag.images.removeAll(selection);
		}
		else {
			tag.images.removeAll(visibleImages);
		}
		refreshVisibleImages();
	}

	public int checkNewTagName(String name, Tag tag) {
		if (name == null || name.compareTo("") == 0) {
			return U.INVALID;
		}
		
		// Checks the name isn't taken already (by another tag)
		if (tags.containsKey(name) && tags.get(name) != tag) {
			return U.IMPOSSIBLE;
		}
		
		return U.OK;
	}

	// this function should never be called without first
	// having checked the validity of name and folder.
	public void addNewTag(String name) {
		// creating new tag
		Tag t = new Tag(name);
		tags.put(name, t);
		filteringState.notifyObservers();
	}

	public void deleteTag(Tag tag) {
		if (tag == null || !tags.containsValue(tag)) {
			return; // should never happen
		}
		tags.remove(tag.name);
		if (filters.containsKey(tag.name)) {
			removeFilter(tag.name);
			// ^ will notifyObservers() by itself
		}
		else {
			filteringState.notifyObservers();
		}
	}

	public int renameTag(String newName, Tag tag) {
		if (!tags.containsKey(tag.name))
			return 42; // should never happen

		String oldName = tag.name;
		int report = checkNewTagName(name, tag);
		
		if (report == U.OK) { // new name is valid
			tag.name = newName; // we do the renaming
			tags.remove(oldName);
			tags.put(newName, tag);
			if (filters.containsKey(oldName)) {
				removeFilter(oldName);
				addFilter(newName, false);
				// ^ will notifyObservers() by itself
			}
			else {
				filteringState.notifyObservers();
			}
		}
		log("renameTag: report = " + report);
		return report;
	}

	public String title() {
		String s = "[ " + this.name;
		if (U.checkValidFolder(this.folder) != U.OK)
			return s; // should not happen
		return s + " ] - - - [ " + this.folder.getAbsolutePath() + " ]";
	}
}
