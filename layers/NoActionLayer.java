package layers;

import general.DataArray;
import general.Values;
import neural_network.Layer;

public class NoActionLayer extends Layer {
	// the output is the input

	public NoActionLayer(int[][] inputArchitecture) {
		this.inputArchitecture = inputArchitecture;
		this.outputArchitecture = inputArchitecture;
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(paramArchitecture);

	}

	@Override
	public DataArray compute(DataArray input) {
		// TODO Auto-generated method stub
		return input.clone();
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.NO_ACTION_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return PDOLFWRtoOutputs.clone();
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return new DataArray(paramArchitecture);
	}

}
