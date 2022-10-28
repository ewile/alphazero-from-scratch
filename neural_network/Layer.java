package neural_network;

import java.io.Serializable;

import general.DataArray;
import general.Index;
import general.Util;
import general.Values;
import training.HyperParameters;

public abstract class Layer implements Serializable {

	public int[][] inputArchitecture;
	public int[][] outputArchitecture;

	public int[][] paramArchitecture;

	public DataArray params;

	public abstract DataArray compute(DataArray input);

	public abstract int getLayerType();

	public void setRandomParams() {
		Index index = new Index(paramArchitecture);
		while (index.maxNotReached()) {
			double randomValue = HyperParameters.RANDOM_PARAM_RANGE * (Values.random.nextDouble() * 2 - 1);
			params.set(index, randomValue);
			index.increase();
		}
	}

	public abstract DataArray getPDOLFWRtoInputs(DataArray input,
			DataArray PDOLFWRtoOutputs);

	public abstract DataArray getPDOLFWRtoParams(DataArray input,
			DataArray PDOLFWRtoOutputs);

	public void updateParams(DataArray PDOLFWRtoParams, double learningRate) {
		Index index = new Index(paramArchitecture);
		while (index.maxNotReached()) {
			params.set(index, params.getDouble(index) - learningRate * PDOLFWRtoParams.getDouble(index));
			index.increase();
		}
	}

	public String toString() {

		String str = "";
		for (int i = 0; i < Values.LENGTH_OF_A_LINE_IN_TERMINAL; i++) {
			str += "-";
		}
		str += "\n\n" + Values.toString(getLayerType()) + " :\n\ninput architecture :\n\n";
		str += Util.saveIntegerBidimensionalArrayToString(inputArchitecture);
		str += "\n\noutput architecture :\n\n";
		str += Util.saveIntegerBidimensionalArrayToString(outputArchitecture);
		if (paramArchitecture.length > 0) {
			str += "\n\nparam architecture :\n\n";
			str += Util.saveIntegerBidimensionalArrayToString(paramArchitecture);
			str += "\n\nweights :\n\n";
			str += "mean :" + Util.getMean(params) + "\nstandard deviation : " + Util.getStandardDeviation(params)
					+ "\n\n";

			String temp1 = "";
			Index index = new Index(paramArchitecture);
			while (index.maxNotReached()) {
				String temp2 = "";

				temp2 += String.valueOf(params.getDouble(index)).substring(0, Values.PRECISION_FOR_DOUBLE_IN_TERMINAL)
						+ " ";

				if (temp1.length() + temp2.length() > Values.LENGTH_OF_A_LINE_IN_TERMINAL) {
					str += temp1 + "\n";
					temp1 = temp2;
				} else {
					temp1 = temp1 + temp2;
				}

				index.increase();
			}
		} else {
			str += "\n\nno weights";
		}

		str += "\n\n";

		for (int i = 0; i < Values.LENGTH_OF_A_LINE_IN_TERMINAL; i++) {
			str += "-";
		}

		return str;

	}

	public int[][] getInputArchitecture() {
		return inputArchitecture;
	}

	public int[][] getOutputArchitecture() {
		return outputArchitecture;
	}

	public int[][] getParamArchitecture() {
		return paramArchitecture;
	}

}