package general;

import java.io.Serializable;
import java.lang.reflect.Array;

public class Data implements Serializable {

	// Simply a N- dimensional double array

	public int[] numberOfInstancesAtDimension;
	public Object content;

	public Data(int[] numberOfInstancesAtDimension) {
		this.numberOfInstancesAtDimension = numberOfInstancesAtDimension;
		content = Array.newInstance(double.class, numberOfInstancesAtDimension);
	}

	public Data(int[] numberOfInstancesAtDimension, Object content) {
		this.numberOfInstancesAtDimension = numberOfInstancesAtDimension;
		this.content = content;
	}

	// create an array of the arrays ...
	public Data(Data[] subData) {
		for (int i = 0; i < subData.length; i++) {
			for (int j = i + 1; j < subData.length; j++) {
				if (!Util.equals(subData[i].numberOfInstancesAtDimension, subData[j].numberOfInstancesAtDimension)) {
					System.out.println("not the same dimensions !!");
				}
			}
		}
		numberOfInstancesAtDimension = new int[1 + subData[0].getNumberOfDimensions()];
		for (int i = 0; i < subData[0].getNumberOfDimensions(); i++) {
			numberOfInstancesAtDimension[i + 1] = subData[0].numberOfInstancesAtDimension[i];
		}
		numberOfInstancesAtDimension[0] = subData.length;

		content = Array.newInstance(double.class, numberOfInstancesAtDimension);

		for (int i = 0; i < subData.length; i++) {
			set(new int[] { i }, subData[i].content);
		}

	}

	public Data clone() {
		Data data = new Data(numberOfInstancesAtDimension);

		int[] index = new int[numberOfInstancesAtDimension.length];
		while (!maxIndexReached(index, numberOfInstancesAtDimension)) {
			data.set(index, getDouble(index));
			increaseIndex(index, numberOfInstancesAtDimension);
		}
		return data;
	}

	public Object get(int[] index) {
		Object o = content;
		for (int j = 0; j < index.length; j++) {
			o = Array.get(o, index[j]);
		}
		return o;
	}

	public double getDouble(int[] index) {
		Object o = content;
		for (int j = 0; j < getNumberOfDimensions(); j++) {
			o = Array.get(o, index[j]);
		}
		return (double) o;
	}

	public void set(int[] index, double d) {
		Object o = content;
		for (int j = 0; j < getNumberOfDimensions() - 1; j++) {
			o = Array.get(o, index[j]);
		}
		Array.set(o, index[getNumberOfDimensions() - 1], d);
	}

	public void set(int[] index, Object d) {
		Object o = content;
		for (int j = 0; j < index.length - 1; j++) {
			o = Array.get(o, index[j]);
		}
		Array.set(o, index[index.length - 1], d);
	}

	public int getLength() {
		return numberOfInstancesAtDimension[0];
	}

	public int getNumberOfDimensions() {
		return numberOfInstancesAtDimension.length;
	}

	public String toString() {
		String str = "architecture : ";
		for (int i = 0; i < numberOfInstancesAtDimension.length; i++) {
			str += numberOfInstancesAtDimension[i] + ", ";
		}
		int[] index = new int[numberOfInstancesAtDimension.length];
		while (!maxIndexReached(index, numberOfInstancesAtDimension)) {
			str += "\n";
			for (int i = 0; i < numberOfInstancesAtDimension.length; i++) {
				str += index[i] + ", ";
			}
			str += ": " + getDouble(index);
			increaseIndex(index, numberOfInstancesAtDimension);
		}
		return str;
	}

	public int getTotalNumberOfValues() {
		int p = 1;
		for (int y = 0; y < numberOfInstancesAtDimension.length; y++) {
			p *= numberOfInstancesAtDimension[y];
		}
		return p;
	}

	private boolean maxIndexReached(int[] currentIndex, int[] architecture) {
		return currentIndex[0] == architecture[0];
	}

	private void increaseIndex(int[] currentIndex, int[] architecture) {
		for (int h = architecture.length - 1; h >= 0; h--) {
			if (currentIndex[h] < architecture[h] - 1) {
				currentIndex[h]++;
				return;
			} else {
				if (h != 0) {
					currentIndex[h] = 0;
				} else {
					currentIndex[h]++;
				}
			}
		}
	}

}
