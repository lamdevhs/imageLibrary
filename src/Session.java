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
	
	private ArrayList<Filter> filters =
			new ArrayList<Filter>();

	public HashSet<ImageModel> visibleImages =
			new HashSet<ImageModel>();

	public HashSet<ImageModel> selection =
			new HashSet<ImageModel>();
	

	public Observed selectionState = new Observed();
	public Observed filteringState = new Observed();
	

	public Session(String name_, File folder_) {
		name = name_;
		folder = folder_;
		//for (int i = 0; i < 200; i++) newTag("Tag " + i);
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
			// for each image key (== relative path)
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

	public ArrayList<ImageModel> getAllImages() {
		ArrayList<ImageModel> output = new ArrayList<ImageModel>();
		Iterator<String> iter = images.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			output.add(images.get(key));
		}
		return output;
	}
	
// 	public ImageModel[] getVisibleImages(int sortedBy) {
// 		ArrayList<ImageModel> r;
// //		if (filters.size() == 0) r = getAllImages();
// //		else r = new ArrayList<ImageModel>();
// 		return (ImageModel[])visibleImages.toArray();
// 		//return r;
// 	}

	private void refreshVisibleImages() {
		visibleImages.clear();
		if (filters.size() == 0)
			visibleImages.addAll(getAllImages());
		else {
			visibleImages.addAll(filters.get(0).tag.images);
			for (int i = 1; i < filters.size(); i++) {
				visibleImages.retainAll(filters.get(i).tag.images);
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
					image.buffered = buffered;
					image.file = file;
				}
				
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

	public ArrayList<Filter> getFilters() {
		return filters;
	}
	
	public void addFilter(String tagname, boolean negated) {
		if (tags.containsKey(tagname)) {
			// ^ should always happen
			if (hasFilter(tagname, negated)) return;
			// else
			filters.add(new Filter(tags.get(tagname)));
			refreshVisibleImages();
		}
	}

	private boolean hasFilter(String tagname, boolean negated) {
		for (int i = 0; i < filters.size(); i++) {
			Filter other = filters.get(i);
			//if (other.negated == negated &&
			if (other.tag.name == tagname)
				return true;
		}
		return false;
	}

	public void removeFilter(Filter filter) {
		filters.remove(filter);
		refreshVisibleImages();
	}

	public void changeSelection(ImageModel image) {
		if (image == null || !visibleImages.contains(image)
				|| !visibleImages.contains(image)) {
			U.elog("Session.addToSelection: invalid argument");
			return;
		}
		if (selection.contains(image)) selection.remove(image);
		else selection.add(image);
		//selectionState.
		selectionState.notifyObservers();
	}

	public void addTag(Tag tag, boolean onlySelectedImages) {
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

	public void removeTag(Tag tag, boolean onlySelectedImages) {
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
}
