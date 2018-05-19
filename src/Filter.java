
public class Filter implements Comparable<Filter>{
	public Tag tag;
	//public boolean negated = false;
	
	Filter(Tag tag_) {
		tag = tag_;
	}

	@Override
	public int compareTo(Filter other) {
		if (other.tag == tag) //&& other.negated == negated)
			return 0;
		else
			return -1;
	}
}
