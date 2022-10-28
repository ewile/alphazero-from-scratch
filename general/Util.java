package general;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import layers.BatchNormalizationLayer;
import layers.ConvolutionalLayer;
import layers.DeadLayer;
import layers.DuplicateLayer;
import layers.FullyConnectedLayer;
import layers.FusionLayer;
import layers.NoActionLayer;
import layers.NonRegularConvolutionalLayer;
import layers.ReluLayer;
import layers.SplitLayer;
import layers.TANHLayer;
import neural_network.Layer;

public class Util {

	public static void writeStringInFile(String fileName, String content) {
		try {
			System.out.print("\nwriting in files ...");
			FileOutputStream fileOutputStream = new FileOutputStream(Values.MEMORY_LOCATION + fileName + ".txt");
			final PrintStream printStream = new PrintStream(fileOutputStream);
			printStream.print(content);
			fileOutputStream.close();
			printStream.close();
			System.out.println("   done");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String readStringFromFile(String fileName) {
		try {
			System.out.print("\nreading string from file ...");
			byte[] b = Files.readAllBytes(Paths.get(Values.MEMORY_LOCATION + fileName + ".txt"));
			System.out.println("   done");
			return new String(b);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static int[] eraseFirstElement(int[] o) {
		int[] a = new int[o.length - 1];
		for (int u = 0; u < a.length; u++) {
			a[u] = o[u + 1];
		}
		return a;
	}

	private static String[] getSubStringsInBrackets(String str) {
		// if str looks like "{{a,b}, {-1,1, {47, 7}}, {9}}"
		// then the return is String[]{"{a,b}", "{-1,1, {47, 7}}", "{9}"}
		// IMPORTANT : every coma must be followed by a space

		int length = 0;

		int bracketCount = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '{') {
				bracketCount--;
			}
			if (c == '}') {
				bracketCount++;
			}
			if (c == ',' && bracketCount == -1) {
				length++;
			}
		}

		if (str.length() > 2) {
			length++;
		}

		String[] subStrings = new String[length];
		bracketCount = 0;
		int beginIndex = 1;
		int e = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '{') {
				bracketCount--;
			}
			if (c == '}') {
				bracketCount++;
			}
			if (c == ',' && bracketCount == -1) {
				subStrings[e] = str.substring(beginIndex, i);
				e++;
				beginIndex = i + 2;
			}
		}

		if (str.length() > 2) {
			subStrings[length - 1] = str.substring(beginIndex, str.length() - 1);
		}

		return subStrings;
	}

	private static String putSubStringsInBrackets(String[] subStrings) {
		String str = "{";
		for (int i = 0; i < subStrings.length; i++) {
			str += subStrings[i];
			if (i < subStrings.length - 1) {
				str += ", ";
			}
		}
		str += "}";
		return str;
	}

	public static String saveIntegerArrayToString(int[] array) {
		if (array == null) {
			return "null";
		}
		String[] s = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			s[i] = String.valueOf(array[i]);
		}

		return putSubStringsInBrackets(s);
	}

	public static int[] loadIntegerArrayFromString(String str) {
		if (str.equals("null")) {
			return null;
		}
		String[] s = getSubStringsInBrackets(str);
		int[] array = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			array[i] = Integer.valueOf(s[i]);
		}

		return array;
	}

	public static String saveDataToString(Data data) {
		if (data == null) {
			return "null";
		}

		if (data.getNumberOfDimensions() == 0) {
			return "empty_data";
		}

		if (data.getNumberOfDimensions() == 1) {
			int length = data.numberOfInstancesAtDimension[0];
			String[] s = new String[length];
			for (int i = 0; i < length; i++) {
				s[i] = String.valueOf(data.getDouble(new int[] { i }));
			}

			return putSubStringsInBrackets(s);
		}

		int length = data.numberOfInstancesAtDimension[0];
		String[] s = new String[length];
		for (int i = 0; i < length; i++) {
			Data subData = new Data(eraseFirstElement(data.numberOfInstancesAtDimension), data.get(new int[] { i }));
			s[i] = saveDataToString(subData);
		}

		return putSubStringsInBrackets(s);

	}

	public static Data loadDataFromString(String str) {
		if (str.equals("null")) {
			return null;
		}
		if (str.equals("empty_data")) {
			return new Data(new int[0]);
		}
		if (str.charAt(1) != '{') {
			// str looks like "{67.9, -3.2, 8.0, 9.0}"

			String[] s = getSubStringsInBrackets(str);

			Data data = new Data(new int[] { s.length });
			for (int i = 0; i < s.length; i++) {
				data.set(new int[] { i }, Double.valueOf(s[i]));
			}

			return data;
		}

		String[] subStrings = getSubStringsInBrackets(str);
		int firstDimension = subStrings.length;

		Data[] subData = new Data[firstDimension];
		for (int i = 0; i < firstDimension; i++) {
			subData[i] = loadDataFromString(subStrings[i]);
		}

		return new Data(subData);
	}

	public static String saveIntegerBidimensionalArrayToString(int[][] array) {
		if (array == null) {
			return "null";
		}

		String[] s = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			s[i] = saveIntegerArrayToString(array[i]);
		}
		return putSubStringsInBrackets(s);
	}

	public static int[][] loadIntegerBidimensionalArrayFromString(String str) {
		if (str.equals("null")) {
			return null;
		}
		String[] s = getSubStringsInBrackets(str);
		int[][] array = new int[s.length][];
		for (int i = 0; i < s.length; i++) {
			array[i] = loadIntegerArrayFromString(s[i]);
		}
		return array;

	}

	public static String saveDataArrayToString(DataArray dataArray) {
		if (dataArray == null) {
			return "null";
		}
		String[] s = new String[dataArray.architecture.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = saveDataToString(dataArray.content[i]);
		}
		return putSubStringsInBrackets(s);
	}

	public static DataArray loadDataArrayFromString(String str) {
		if (str.equals("null")) {
			return null;
		}
		String[] s = getSubStringsInBrackets(str);
		Data[] content = new Data[s.length];
		for (int i = 0; i < s.length; i++) {
			content[i] = loadDataFromString(s[i]);
		}
		return new DataArray(content);
	}

	public static String saveLayerToString(Layer layer) {
		int l;

		switch (layer.getLayerType()) {
			case Values.BATCH_NORMALIZATION_LAYER:
				l = 6;
				break;

			case Values.CONVOLUTIONAL_LAYER:
				l = 10;
				break;

			case Values.NON_REGULAR_CONVOLUTIONAL_LAYER:
				l = 10;
				break;

			case Values.SPLIT_LAYER:
				l = 4 + ((SplitLayer) layer).layers.length;
				break;

			default:
				l = 4;
				break;
		}

		String[] s = new String[l];

		s[0] = String.valueOf(layer.getLayerType());
		s[1] = saveIntegerBidimensionalArrayToString(layer.inputArchitecture);
		s[2] = saveIntegerBidimensionalArrayToString(layer.outputArchitecture);
		s[3] = saveDataArrayToString(layer.params);

		switch (layer.getLayerType()) {
			case Values.BATCH_NORMALIZATION_LAYER:
				s[4] = String.valueOf(((BatchNormalizationLayer) layer).getMean());
				s[5] = String.valueOf(((BatchNormalizationLayer) layer).getStandardDeviation());

				break;
			case Values.CONVOLUTIONAL_LAYER:
				s[4] = String.valueOf(((ConvolutionalLayer) layer).getInputFrameHeight());
				s[5] = String.valueOf(((ConvolutionalLayer) layer).getInputFrameWidth());
				s[6] = String.valueOf(((ConvolutionalLayer) layer).getEntryDepth());
				s[7] = String.valueOf(((ConvolutionalLayer) layer).getNumberOfFilters());
				s[8] = String.valueOf(((ConvolutionalLayer) layer).getKernelSize());
				s[9] = String.valueOf(((ConvolutionalLayer) layer).getPaddingValue());
				break;
			case Values.NON_REGULAR_CONVOLUTIONAL_LAYER:
				s[4] = String.valueOf(((NonRegularConvolutionalLayer) layer).getInputFrameHeight());
				s[5] = String.valueOf(((NonRegularConvolutionalLayer) layer).getInputFrameWidth());
				s[6] = String.valueOf(((NonRegularConvolutionalLayer) layer).getEntryDepth());
				s[7] = String.valueOf(((NonRegularConvolutionalLayer) layer).getNumberOfOutputFrames());
				s[8] = String.valueOf(((NonRegularConvolutionalLayer) layer).getKernelSize());
				s[9] = String.valueOf(((NonRegularConvolutionalLayer) layer).getPaddingValue());
				break;

			case Values.SPLIT_LAYER:
				for (int i = 0; i < ((SplitLayer) layer).layers.length; i++) {
					s[4 + i] = saveLayerToString(((SplitLayer) layer).layers[i]);
				}
				break;

			default:
				break;
		}

		return putSubStringsInBrackets(s);
	}

	public static Layer loadLayerFromString(String str) {

		String[] s = getSubStringsInBrackets(str);
		int layerType = Integer.valueOf(s[0]);
		int[][] inputArchitecture = loadIntegerBidimensionalArrayFromString(s[1]);
		int[][] outputArchitecture = loadIntegerBidimensionalArrayFromString(s[2]);
		DataArray params = loadDataArrayFromString(s[3]);

		Layer layer = null;

		switch (layerType) {

			case Values.BATCH_NORMALIZATION_LAYER:
				double mean = Double.valueOf(s[4]);
				double standardDeviation = Double.valueOf(s[5]);

				layer = new BatchNormalizationLayer(inputArchitecture);
				((BatchNormalizationLayer) layer).setMean(mean);
				((BatchNormalizationLayer) layer).setStandardDeviation(standardDeviation);

				break;

			case Values.CONVOLUTIONAL_LAYER:

				int inputFrameHeight = Integer.valueOf(s[4]);
				int inputFrameWidth = Integer.valueOf(s[5]);
				int entryDepth = Integer.valueOf(s[6]);
				int numberOfFilters = Integer.valueOf(s[7]);
				int kernelSize = Integer.valueOf(s[8]);
				double paddingValue = Double.valueOf(s[9]);

				layer = new ConvolutionalLayer(inputFrameHeight, inputFrameWidth, entryDepth, numberOfFilters,
						kernelSize,
						paddingValue);
				break;

			case Values.NON_REGULAR_CONVOLUTIONAL_LAYER:

				inputFrameHeight = Integer.valueOf(s[4]);
				inputFrameWidth = Integer.valueOf(s[5]);
				entryDepth = Integer.valueOf(s[6]);
				int numberOfOutputFrames = Integer.valueOf(s[7]);
				kernelSize = Integer.valueOf(s[8]);
				paddingValue = Double.valueOf(s[9]);

				layer = new NonRegularConvolutionalLayer(inputFrameHeight, inputFrameWidth, entryDepth,
						numberOfOutputFrames, kernelSize, paddingValue);
				break;

			case Values.SPLIT_LAYER:
				int numberOfSubLayers = s.length - 4;
				Layer[] layers = new Layer[numberOfSubLayers];
				for (int i = 0; i < numberOfSubLayers; i++) {
					layers[i] = loadLayerFromString(s[4 + i]);
				}
				layer = new SplitLayer(layers);
				break;
			case Values.DEAD_LAYER:
				layer = new DeadLayer(inputArchitecture);
				break;
			case Values.DUPLICATE_LAYER:
				layer = new DuplicateLayer(inputArchitecture);
				break;
			case Values.FULLY_CONNECTED_LAYER:
				layer = new FullyConnectedLayer(inputArchitecture, outputArchitecture);
				break;
			case Values.FUSION_LAYER:
				layer = new FusionLayer(inputArchitecture);
				break;
			case Values.NO_ACTION_LAYER:
				layer = new NoActionLayer(inputArchitecture);
				break;
			case Values.RELU_LAYER:
				layer = new ReluLayer(inputArchitecture);
				break;
			case Values.TANH_LAYER:
				layer = new TANHLayer(inputArchitecture);
				break;
		}

		layer.params = params;
		return layer;
	}

	public static String saveLayerArrayToString(Layer[] layerArray) {
		System.out.print("saving layer array to String : (on " + layerArray.length + ") : ");
		String[] s = new String[layerArray.length];

		for (int i = 0; i < s.length; i++) {
			s[i] = saveLayerToString(layerArray[i]);
			System.out.print((i + 1 + " "));
		}
		System.out.println();

		return putSubStringsInBrackets(s);
	}

	public static Layer[] loadLayerArrayFromString(String str) {
		System.out.print("loading layer array from String : (on ");

		String[] s = getSubStringsInBrackets(str);
		System.out.print(s.length + ") : ");
		Layer[] layers = new Layer[s.length];
		for (int i = 0; i < s.length; i++) {
			layers[i] = loadLayerFromString(s[i]);
			System.out.print((i + 1) + " ");
		}
		System.out.println();
		return layers;
	}

	public static void deleteAllFilesThatBeginsWith(String beginning) {
		File f = new File(Values.MEMORY_LOCATION); // must add "/" ???
		String[] fileNames = f.list();

		for (String fileName : fileNames) {
			if (fileName.length() >= beginning.length()
					&& fileName.substring(0, beginning.length()).equals(beginning)) {
				File file = new File(Values.MEMORY_LOCATION + fileName);

				if (!file.delete()) {
					System.out.println("Failed to delete the file : " + fileName);
				}
			}
		}

	}

	public static void serialize(String fileAddress, Object object) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fileAddress);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
			objectOutputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object deserialize(String fileAddress) {
		try {
			FileInputStream fileInputStream = new FileInputStream(fileAddress);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			Object object = objectInputStream.readObject();
			objectInputStream.close();
			return object;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static int indexOf(int[] a, int[][] b) {
		for (int i = 0; i < b.length; i++) {
			if (equals(a, b[i])) {
				return i;
			}
		}
		return Values.NOTHING;
	}

	public static int[] concat(int[] a, int[] b) {
		int[] c = new int[a.length + b.length];
		for (int u = 0; u < a.length; u++) {
			c[u] = a[u];
		}
		for (int h = 0; h < b.length; h++) {
			c[a.length + h] = b[h];
		}
		return c;
	}

	public static boolean equals(int[] a, int[] b) {
		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}

		}
		return true;
	}

	public static boolean equals(int[][] a, int[][] b) {
		if (a.length != b.length) {
			return false;
		}

		for (int i = 0; i < a.length; i++) {
			if (!equals(a[i], b[i])) {
				return false;
			}

		}
		return true;
	}

	public static double getMean(DataArray dta) {
		double mean = 0;
		int c = 0;
		Index index = new Index(dta.architecture);
		while (index.maxNotReached()) {
			mean += dta.getDouble(index);
			index.increase();
			c++;
		}
		mean /= c;
		return mean;
	}

	public static double getStandardDeviation(DataArray dta) {
		double mean = getMean(dta);
		double standardDeviation = 0;
		int c = 0;
		Index index = new Index(dta.architecture);
		while (index.maxNotReached()) {
			standardDeviation += Math.pow(dta.getDouble(index) - mean, 2);
			index.increase();
			c++;
		}
		standardDeviation /= c;
		standardDeviation = Math.sqrt(standardDeviation);
		return standardDeviation;
	}

}
