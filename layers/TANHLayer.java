package layers;

import general.DataArray;
import general.Index;
import general.Values;
import neural_network.Layer;

public class TANHLayer extends Layer {

	public TANHLayer(int[][] inputArchitecture) {
		this.inputArchitecture = inputArchitecture;
		this.outputArchitecture = inputArchitecture;
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(new int[][] {});

	}

	@Override
	public DataArray compute(DataArray input) {
		DataArray output = new DataArray(outputArchitecture);
		Index index = new Index(outputArchitecture);
		while (index.maxNotReached()) {
			double c = Math.tanh(input.getDouble(index));
			output.set(index, c);
			index.increase();
		}
		return output;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.TANH_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input,
			DataArray PDOLFWRtoOutputs) {

		DataArray derivatives = new DataArray(inputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			derivatives.set(index, (1 - Math.pow(Math.tanh(input.getDouble(index)), 2))
					* PDOLFWRtoOutputs.getDouble(index));
			index.increase();
		}

		return derivatives;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input,
			DataArray PDOLFWRtoOutputs) {
		// TODO Auto-generated method stub
		return new DataArray(new int[0][]);
	}

	@Override
	public TANHLayer clone() {
		// TODO Auto-generated method stub
		return new TANHLayer(inputArchitecture);
	}

}
