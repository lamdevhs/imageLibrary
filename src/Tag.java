import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Tag {
	public String name;
	public Set<ImageModel> images;
	Tag(String name_) {
		name = name_;
		images = new HashSet<ImageModel>();
	}

	public ArrayList<String> data() {
		Iterator<ImageModel> img_iter = images.iterator();
		ArrayList<String> output = new ArrayList<String>();
		while(img_iter.hasNext()) {
			ImageModel img = img_iter.next();
			output.add(img.key);
		}
		return output;
	}

}
