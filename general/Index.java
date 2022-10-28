package general;

public class Index {

	// a way to browse all the indexes of a "DataArray"

	private int[][] architecture;

	private int[] currentIndex;

	public Index(int[][] architecture) {
		this.architecture = architecture;
		init();
	}

	public int getIndexOfMultidimensionnalArray() {
		return currentIndex[0];
	}

	public int[] getIndexInsideMultidimensionnalArray() {
		int[] t = new int[currentIndex.length - 1];
		for (int h = 0; h < t.length; h++) {
			t[h] = currentIndex[h + 1];
		}
		return t;
	}

	public void increase() {
		int u = currentIndex[0];
		for (int h = architecture[u].length; h >= 1; h--) {
			if (currentIndex[h] < architecture[u][h - 1] - 1) {
				currentIndex[h]++;
				return;
			} else {
				if (h != 1) {
					currentIndex[h] = 0;
				} else {
					if (u + 1 < architecture.length) {
						currentIndex = new int[1 + architecture[u + 1].length];
						currentIndex[0] = u + 1;
					} else {
						currentIndex = null;
					}
				}
			}
		}
	}

	public boolean maxNotReached() {
		return currentIndex != null;
	}

	public void init() {
		if (architecture.length > 0) {
			currentIndex = new int[1 + architecture[0].length];
		} else {
			currentIndex = null;
		}
	}

	public String toString() {
		if (currentIndex == null) {
			return "null";
		}
		String s = "index : ";

		for (int i = 0; i < currentIndex.length; i++) {
			s += currentIndex[i] + " ";
		}
		return s;
	}
}
