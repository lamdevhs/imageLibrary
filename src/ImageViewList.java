import java.util.ArrayList;


public class ImageViewList extends ArrayList<ImageView> {

	public static int BY_NAME = 0;
	public static int BY_SIZE = 1;
	public static int NONE = 2;
	public static int BY_PATH = 3;
	public static int BY_WIDTH = 4;
	public static int BY_HEIGHT = 5;
	
	
	
	public void quickSort(int criterion) {
		if (criterion == NONE) return;
		int inf = 0;
		int sup = this.size() - 1;
		if (inf >= sup) return;
		int pivIx = this.partition(inf, sup, criterion);
		quickSort(inf, pivIx - 1, criterion);
		quickSort(pivIx + 1, sup, criterion);
	}
	
	private void quickSort(int inf, int sup, int criterion) {
		if (inf >= sup) return;
		int pivIx = this.partition(inf, sup, criterion);
		quickSort(inf, pivIx - 1, criterion);
		quickSort(pivIx + 1, sup, criterion);
	}
	
	private void swap(int i, int j) {
		ImageView tmp = this.get(i);
		this.set(i, this.get(j));
		this.set(j, tmp);
	}
	
	// if a < b  --> compare(a,b) <  0
	// if a > b  --> compare(a,b) >  0
	// if a <= b --> compare(a,b) <= 0
	// if a >= b --> compare(a,b) >= 0
	private int compare (ImageView a, ImageView b, int criterion) {
		if (criterion == BY_NAME) {
			int cmp = a.model.file.getName()
				.compareTo(
					b.model.file.getName());
			return cmp;
		}
		else if (criterion == BY_PATH) {
			int cmp = a.model.key
					.compareTo(
						b.model.key);	
			return cmp;
		}
		else {
			long aSize, bSize;
			if (criterion == BY_SIZE) {
				aSize = ((long)a.model.buffered.getHeight())
					* ((long)a.model.buffered.getWidth());
				bSize = ((long)b.model.buffered.getHeight())
					* ((long)b.model.buffered.getWidth());
			}
			else if (criterion == BY_WIDTH) {
				aSize = a.model.buffered.getWidth();
				bSize = b.model.buffered.getWidth();
			}
			else  // if (criterion == BY_HEIGHT)
			{
				aSize = a.model.buffered.getHeight();
				bSize = b.model.buffered.getHeight();
			}
			long cmp = aSize - bSize;
			if (cmp > 0) return 1;
			else if (cmp < 0) return -1;
			else return 0;
		}
		//return 0;
		
	}
	
	private int partition(int start, int end, int criterion) {
		// start < end by hypothesis, especially, start != end
		int pivIx = end;
		ImageView pivot = this.get(pivIx);
		int i = start;
		int j = end - 1; // >= i
		while (i <= j) {
			//by recursive hypothesis,
			//any element strictly before i
			//is strictly inferior to pivot
			//any element strictly after j
			//is superior or equal to pivot
			
			//search next problem from the left
			while (i <= j && this.compare(this.get(i), pivot, criterion) < 0)
				{ i++; }
			
			//search next problem from the right
			while (j >= i && this.compare(this.get(j), pivot, criterion) >= 0)
				{ j--; }
			
			if (i < j) {
				this.swap(i, j);
				i++;
				j--;
			}
			// Otherwise, if not (i < j) {
			//   (j == i) is impossible: it'd mean there's an
			//   element which is both < and >= to the pivot.
			//   So, it means (j > i), and even further, (j == i - 1)
			//   (it's possible here (j < start), incidentally)
			//   but at any rate it means we reached the end
			//   of the process, and we only need to swap i and pivot
			//   then return i;
			// }
		}
		this.swap(pivIx, i);
		return i;
	}
}
