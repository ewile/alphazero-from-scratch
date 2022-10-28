package layers;

import general.DataArray;
import general.Index;
import general.Values;
import neural_network.Layer;

public class ReluLayer extends Layer {

	public ReluLayer(int[][] inputArchitecture) {
		this.inputArchitecture = inputArchitecture;
		this.outputArchitecture = inputArchitecture;
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(paramArchitecture);

	}

	@Override
	public DataArray compute(DataArray input) {
		DataArray output = new DataArray(outputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			double d = input.getDouble(index);
			if (d >= 0) {
				output.set(index, d);
			} else {
				output.set(index, 0);
			}
			index.increase();
		}
		return output;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.RELU_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {
		DataArray PDOLFWRtoInputs = new DataArray(inputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			double d = input.getDouble(index);
			if (d >= 0) {
				PDOLFWRtoInputs.set(index, PDOLFWRtoOutputs.getDouble(index));
			} else {
				PDOLFWRtoInputs.set(index, 0);
			}
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
