package layers;

import general.DataArray;
import general.Index;
import general.Values;
import neural_network.Layer;

public class DuplicateLayer extends Layer {
	public DuplicateLayer(int[][] inputArchitecture) {
		this.inputArchitecture = inputArchitecture;
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(paramArchitecture);
		this.outputArchitecture = new int[inputArchitecture.length * 2][];
		for (int u = 0; u < inputArchitecture.length; u++) {
			outputArchitecture[u] = inputArchitecture[u].clone();
			outputArchitecture[u + inputArchitecture.length] = inputArchitecture[u].clone();
		}

	}

	@Override
	public DataArray compute(DataArray input) {
		// TODO Auto-generated method stub
		DataArray output = new DataArray(outputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			output.set(index.getIndexOfMultidimensionnalArray(), index.getIndexInsideMultidimensionnalArray(),
					input.getDouble(index));
			output.set(inputArchitecture.length + index.getIndexOfMultidimensionnalArray(),
					index.getIndexInsideMultidimensionnalArray(), input.getDouble(index));

			index.increase();
		}
		return output;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.DUPLICATE_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {
		DataArray inputDerivatives = new DataArray(inputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			double d1 = PDOLFWRtoOutputs.getDouble(index.getIndexOfMultidimensionnalArray(),
					index.getIndexInsideMultidimensionnalArray());
			double d2 = PDOLFWRtoOutputs.getDouble(inputArchitecture.length + index.getIndexOfMultidimensionnalArray(),
					index.getIndexInsideMultidimensionnalArray());
			inputDerivatives.set(index, d1 + d2);
			index.increase();
		}
		return inputDerivatives;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return new DataArray(paramArchitecture);
	}

}
