package layers;

import general.DataArray;
import general.Index;
import general.Values;
import neural_network.Layer;

public class BatchNormalizationLayer extends Layer {

	private double mean = 0;
	private double standardDeviation = 1;

	public BatchNormalizationLayer(int[][] inputArchitecture) {
		this.inputArchitecture = inputArchitecture;
		this.outputArchitecture = inputArchitecture;
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(paramArchitecture);

	}

	public static double calculateMean(DataArray[] inputValuesHistory) {
		if (inputValuesHistory.length == 0) {
			return 0;
		}
		double m = 0;
		int t = 0;
		Index index = new Index(inputValuesHistory[0].architecture);
		for (int i = 0; i < inputValuesHistory.length; i++) {
			while (index.maxNotReached()) {
				m += inputValuesHistory[i].getDouble(index);
				index.increase();
				t++;
			}
			index.init();
		}
		return m / t;
	}

	public static double calculateStandardDeviation(DataArray[] inputValuesHistory, double mean) {
		if (inputValuesHistory.length == 0) {
			return 0;
		}
		double sd = 0;
		int t = 0;
		Index index = new Index(inputValuesHistory[0].architecture);
		for (int i = 0; i < inputValuesHistory.length; i++) {
			while (index.maxNotReached()) {
				sd += Math.pow(inputValuesHistory[i].getDouble(index) - mean, 2);
				index.increase();
				t++;
			}
			index.init();
		}
		sd = Math.sqrt(sd / t);
		return sd;
	}

	@Override
	public DataArray compute(DataArray input) {
		DataArray output = new DataArray(outputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			output.set(index, (input.getDouble(index) - mean) / standardDeviation);
			index.increase();
		}
		return output;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.BATCH_NORMALIZATION_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {
		DataArray PDOLFWRtoInputs = new DataArray(inputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			PDOLFWRtoInputs.set(index, (1 / standardDeviation) * PDOLFWRtoOutputs.getDouble(index));
			index.increase();
		}
		return PDOLFWRtoInputs;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return new DataArray(paramArchitecture);
	}
}