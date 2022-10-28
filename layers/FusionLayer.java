package layers;

import general.DataArray;
import general.Index;
import general.Values;
import neural_network.Layer;

public class FusionLayer extends Layer {

	// example :
	// input architecture : [ [15, 8, 8] , [20, 8, 8], [10, 8, 8] ]
	// -> output architecture : [15 + 20 + 10, 8, 8]

	public FusionLayer(int[][] inputArchitecture) {
		this.paramArchitecture = new int[0][];
		this.params = new DataArray(paramArchitecture);

		this.inputArchitecture = inputArchitecture;
		this.outputArchitecture = new int[][] { inputArchitecture[0].clone() };
		this.outputArchitecture[0][0] = 0;
		for (int y = 0; y < inputArchitecture.length; y++) {
			this.outputArchitecture[0][0] += inputArchitecture[y][0];
		}
	}

	@Override
	public DataArray compute(DataArray input) {
		DataArray output = new DataArray(outputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			int fstIndex = 0;
			for (int u = 0; u < index.getIndexOfMultidimensionnalArray(); u++) {
				fstIndex += inputArchitecture[u][0];
			}
			fstIndex += index.getIndexInsideMultidimensionnalArray()[0];

			int[] ar = index.getIndexInsideMultidimensionnalArray().clone();
			ar[0] = fstIndex;
			output.set(0, ar, input.getDouble(index));
			index.increase();
		}
		return output;
	}

	@Override
	public int getLayerType() {
		return Values.FUSION_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {
		DataArray PDOLFWRtoInputs = new DataArray(inputArchitecture);
		Index index = new Index(inputArchitecture);
		while (index.maxNotReached()) {
			int fstIndex = 0;
			for (int u = 0; u < index.getIndexOfMultidimensionnalArray(); u++) {
				fstIndex += inputArchitecture[u][0];
			}
			fstIndex += index.getIndexInsideMultidimensionnalArray()[0];

			int[] ar = index.getIndexInsideMultidimensionnalArray().clone();
			ar[0] = fstIndex;
			PDOLFWRtoInputs.set(index, PDOLFWRtoOutputs.getDouble(0, ar));
			index.increase();
		}
		return PDOLFWRtoInputs;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {
		return new DataArray(paramArchitecture);
	}

}
