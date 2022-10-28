package layers;

import general.DataArray;
import general.Values;
import neural_network.Layer;

public class DeadLayer extends Layer {

	public DeadLayer(int[][] inputArchitecture) {
		this.inputArchitecture = inputArchitecture;
		this.outputArchitecture = new int[0][];
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(paramArchitecture);

	}

	@Override
	public DataArray compute(DataArray input) {
		return new DataArray(outputArchitecture);
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.DEAD_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return new DataArray(inputArchitecture);
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return new DataArray(paramArchitecture);
	}

}
