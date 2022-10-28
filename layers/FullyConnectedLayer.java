package layers;

import general.DataArray;
import general.Index;
import general.Util;
import general.Values;
import neural_network.Layer;

public class FullyConnectedLayer extends Layer {

	// CAN BE IMPROVED with a faster matrix multiplication

	// the input and the output must be arrayData with one data

	public FullyConnectedLayer(int[][] inputarchitecture, int[][] outputArchitecture) {
		this.inputArchitecture = inputarchitecture;
		this.outputArchitecture = outputArchitecture;

		if (inputarchitecture.length != 1 || outputArchitecture.length != 1) {
			System.out.println("error 58156" + 1 / 0);
		}

		this.paramArchitecture = new int[][] { Util.concat(outputArchitecture[0], inputarchitecture[0]),
				outputArchitecture[0]

		};
		this.params = new DataArray(paramArchitecture);
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.FULLY_CONNECTED_LAYER;
	}

	@Override
	public Layer clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataArray compute(DataArray inputNeurons) {

		DataArray outputNeurons = new DataArray(outputArchitecture);

		Index outputIndex = new Index(outputArchitecture);
		while (outputIndex.maxNotReached()) {
			double sum = 0;

			Index inputIndex = new Index(inputArchitecture);
			while (inputIndex.maxNotReached()) {

				sum += params.getDouble(1, outputIndex.getIndexInsideMultidimensionnalArray())
						+ inputNeurons.getDouble(inputIndex)
								* params.getDouble(0, Util.concat(outputIndex.getIndexInsideMultidimensionnalArray(),
										inputIndex.getIndexInsideMultidimensionnalArray()));
				inputIndex.increase();
			}

			outputNeurons.set(outputIndex, sum);

			outputIndex.increase();
		}
		return outputNeurons;

	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray inputNeurons,
			DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub

		DataArray inputPartialDerivatives = new DataArray(inputArchitecture);

		Index outputIndex = new Index(outputArchitecture);
		while (outputIndex.maxNotReached()) {

			Index inputIndex = new Index(inputArchitecture);
			while (inputIndex.maxNotReached()) {
				double d1 = inputPartialDerivatives.getDouble(inputIndex);
				double d2 = params.getDouble(0,
						Util.concat(outputIndex.getIndexInsideMultidimensionnalArray(),
								inputIndex.getIndexInsideMultidimensionnalArray()))
						* PDOLFWRtoOutputs.getDouble(outputIndex);

				inputPartialDerivatives.set(inputIndex, d1 + d2);
				inputIndex.increase();

			}

			outputIndex.increase();

		}

		return inputPartialDerivatives;

	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray inputNeurons,
			DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		DataArray paramsPartialDerivatives = new DataArray(paramArchitecture);

		Index outputIndex = new Index(outputArchitecture);
		while (outputIndex.maxNotReached()) {
			double j = PDOLFWRtoOutputs.getDouble(outputIndex);
			Index inputIndex = new Index(inputArchitecture);
			while (inputIndex.maxNotReached()) {
				double q = inputNeurons.getDouble(inputIndex);
				double d = q * j;
				paramsPartialDerivatives.set(0, Util.concat(outputIndex.getIndexInsideMultidimensionnalArray(),
						inputIndex.getIndexInsideMultidimensionnalArray()), d);
				inputIndex.increase();

			}

			paramsPartialDerivatives.set(1, outputIndex.getIndexInsideMultidimensionnalArray(), j);

			outputIndex.increase();

		}

		return paramsPartialDerivatives;
	}

}
