package debug;

import general.DataArray;
import general.Index;
import layers.ConvolutionalLayer;
import layers.DuplicateLayer;
import layers.SplitLayer;
import neural_network.Layer;
import neural_network.NeuralNetwork;

public class NNTEST extends NeuralNetwork {

	public NNTEST(Layer[] layers) {
		super(layers);

		ConvolutionalLayer l1 = new ConvolutionalLayer(5, 6, 3, 4, 3, 0);
		DuplicateLayer l2 = new DuplicateLayer(l1.outputArchitecture);
		ConvolutionalLayer l3 = new ConvolutionalLayer(5, 6, 4, 2, 3, 0);
		ConvolutionalLayer l4 = new ConvolutionalLayer(5, 6, 4, 2, 3, 0);

		SplitLayer l5 = new SplitLayer(new Layer[] { l3, l4 });

		this.layers = new Layer[] { l1, l2, l5 };

		setRandomParams();
	}

	@Override
	public double computeLossFunction(DataArray finalOutput, DataArray expectedOutput) {
		// TODO Auto-generated method stub
		Index index = new Index(layers[layers.length - 1].getOutputArchitecture());
		double s = 0;
		while (index.maxNotReached()) {
			s += Math.pow(finalOutput.getDouble(index) - expectedOutput.getDouble(index), 2);
			index.increase();
		}
		return s;
	}

	@Override
	public double getPDOLFWRtoOutputNeuron(Index outputNeuronIndex, DataArray finalOutput, DataArray expectedOutput) {
		// TODO Auto-generated method stub
		return 2 * (finalOutput.getDouble(outputNeuronIndex) - expectedOutput.getDouble(outputNeuronIndex));
	}

}