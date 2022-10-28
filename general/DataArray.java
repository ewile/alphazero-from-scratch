package general;

import java.io.Serializable;

public class DataArray implements Serializable {

	// An Array of N- dimensional double array

	public int[][] architecture;
	public Data[] content;

	public DataArray(int[][] architecture) {
		this.architecture = architecture;
		content = new Data[architecture.length];
		for (int i = 0; i < architecture.length; i++) {
			content[i] = new Data(architecture[i]);
		}
	}

	public DataArray(Data[] content) {
		this.content = content;
		architecture = new int[content.length][];
		for (int h = 0; h < content.length; h++) {
			architecture[h] = content[h].numberOfInstancesAtDimension.clone();
		}
	}

	public DataArray clone() {
		Data[] d = new Data[architecture.length];
		for (int u = 0; u < architecture.length; u++) {
			d[u] = content[u].clone();
		}

		return new DataArray(d);
	}

	public Object get(int indexOfMultidimensionnalArray, int[] indexInsideMultidimensionnalArray) {
		return content[indexOfMultidimensionnalArray].get(indexInsideMultidimensionnalArray);
	}

	public double getDouble(int indexOfMultidimensionnalArray, int[] indexInsideMultidimensionnalArray) {
		return (double) get(indexOfMultidimensionnalArray, indexInsideMultidimensionnalArray);
	}

	public double getDouble(Index index) {
		return (double) get(index.getIndexOfMultidimensionnalArray(), index.getIndexInsideMultidimensionnalArray());
	}

	public void set(int indexOfMultidimensionnalArray, int[] indexInsideMultidimensionnalArray, double d) {
		content[indexOfMultidimensionnalArray].set(indexInsideMultidimensionnalArray, d);
	}

	public void set(Index index, double d) {
		content[index.getIndexOfMultidimensionnalArray()].set(index.getIndexInsideMultidimensionnalArray(), d);
	}

	public void set(int indexOfMultidimensionnalArray, int[] indexInsideMultidimensionnalArray, Object o) {
		content[indexOfMultidimensionnalArray].set(indexInsideMultidimensionnalArray, o);
	}

	public String toString() {
		String str = "";
		for (int u = 0; u < architecture.length; u++) {
			str += "N-dimmansionalArray " + u + " :\n" + content[u].toString();
		}
		return str;
	}

	public int getTotalNumberOfValues() {
		int t = 0;
		for (int y = 0; y < content.length; y++) {
			t += content[y].getTotalNumberOfValues();
		}
		return t;
	}

	public boolean equals(DataArray dataArray) {
		if (!Util.equals(dataArray.architecture, architecture)) {
			return false;
		}
		Index index = new Index(architecture);
		while (index.maxNotReached()) {
			if (dataArray.getDouble(index) != getDouble(index)) {
				return false;
			}
			index.increase();
		}
		return true;
	}

	public void printDifferencesWith(DataArray dataArray) {
		if (!Util.equals(dataArray.architecture, architecture)) {
			System.out.println("not the same architecture");
			return;
		}
		Index index = new Index(architecture);
		while (index.maxNotReached()) {
			if (dataArray.getDouble(index) != getDouble(index)) {
				System.out
						.println(index.toString() + "   " + getDouble(index) + "   vs   " + dataArray.getDouble(index));
			}
			index.increase();
		}

	}
}
