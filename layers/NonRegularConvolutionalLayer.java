package layers;

import general.DataArray;
import general.Index;
import general.Values;
import neural_network.Layer;

public class NonRegularConvolutionalLayer extends Layer {

	// the weights evolve for every output neuron

	// stride is always 1
	// entry height = entry width = inputFrameSize

	// the input and the output must be arrayData with one data of three dimensions

	private int inputFrameWidth;
	private int inputFrameHeight;
	private int outputFrameWidth;
	private int outputFrameHeight;
	private int padding;
	private double paddingValue;
	private int entryDepth;
	private int kernelSize;
	private int numberOfOutputFrames;

	public NonRegularConvolutionalLayer(int inputFrameHeight, int inputFrameWidth, int entryDepth,
			int numberOfOutputFrames,
			int kernelSize, double paddingValue) {
		if (kernelSize % 2 == 0) {
			System.out.println("error 95461" + 1 / 0);
		}
		this.inputFrameWidth = inputFrameWidth;
		this.inputFrameHeight = inputFrameHeight;
		this.padding = (kernelSize - 1) / 2;
		this.paddingValue = paddingValue;
		this.entryDepth = entryDepth;
		this.kernelSize = kernelSize;
		this.numberOfOutputFrames = numberOfOutputFrames;

		outputFrameWidth = (inputFrameWidth + 2 * padding - kernelSize + 1);
		outputFrameHeight = (inputFrameHeight + 2 * padding - kernelSize + 1);

		this.inputArchitecture = new int[][] { new int[] { entryDepth, inputFrameHeight, inputFrameWidth } };
		this.outputArchitecture = new int[][] {
				new int[] { numberOfOutputFrames, inputFrameHeight, outputFrameWidth } };

		this.paramArchitecture = new int[][] {
				new int[] { numberOfOutputFrames, outputFrameHeight, outputFrameWidth, entryDepth, kernelSize,
						kernelSize },
				new int[] { numberOfOutputFrames, outputFrameHeight, outputFrameWidth } };

		params = new DataArray(paramArchitecture);

	}

	// -> outputLine and outputColumn represent the coordinates of the bottom left
	// corner of the kernel, with origin the bottom left corner of the surface,
	// including padding
	// -> depthInKernel, lineInKernel and columnInKernel represent the coordinates
	// of the synapse inside the kernel

	@Override
	public DataArray compute(DataArray input) {

		DataArray output = new DataArray(outputArchitecture);

		Index index = new Index(paramArchitecture);
		while (index.getIndexOfMultidimensionnalArray() == 0) {

			int outputFrameIndex = index.getIndexInsideMultidimensionnalArray()[0];
			int outputLine = index.getIndexInsideMultidimensionnalArray()[1];
			int outputColumn = index.getIndexInsideMultidimensionnalArray()[2];
			int depthInKernel = index.getIndexInsideMultidimensionnalArray()[3];
			int lineInKernel = index.getIndexInsideMultidimensionnalArray()[4];
			int columnInKernel = index.getIndexInsideMultidimensionnalArray()[5];

			double inputNeuron;

			if (outputColumn + columnInKernel < inputFrameWidth && 0 <= outputColumn + columnInKernel
					&& outputLine + lineInKernel < inputFrameHeight && 0 <= outputLine + lineInKernel) {
				inputNeuron = input.getDouble(0,
						new int[] { depthInKernel, outputLine + lineInKernel, outputColumn + columnInKernel });
			} else {
				inputNeuron = paddingValue;
			}

			double synapse = params.getDouble(index);

			output.set(0, new int[] { outputFrameIndex, outputLine, outputColumn }, inputNeuron * synapse);

			index.increase();
		}
		while (index.maxNotReached()) {

			int filterIndex = index.getIndexInsideMultidimensionnalArray()[0];
			int outputLine = index.getIndexInsideMultidimensionnalArray()[1];
			int outputColumn = index.getIndexInsideMultidimensionnalArray()[2];

			double bias = params.getDouble(index);

			double oldValue = output.getDouble(0, new int[] { filterIndex, outputLine, outputColumn });

			output.set(0, new int[] { filterIndex, outputLine, outputColumn }, oldValue + bias);

			index.increase();
		}

		return output;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray DOLFWRtoOutputs) {

		DataArray PDOFFWRtoInputs = new DataArray(inputArchitecture);

		Index index = new Index(paramArchitecture);

		while (index.getIndexOfMultidimensionnalArray() == 0) {

			int outputFrameIndex = index.getIndexInsideMultidimensionnalArray()[0];
			int outputLine = index.getIndexInsideMultidimensionnalArray()[1];
			int outputColumn = index.getIndexInsideMultidimensionnalArray()[2];
			int depthInKernel = index.getIndexInsideMultidimensionnalArray()[3];
			int lineInKernel = index.getIndexInsideMultidimensionnalArray()[4];
			int columnInKernel = index.getIndexInsideMultidimensionnalArray()[5];

			if (outputColumn + columnInKernel < inputFrameWidth && 0 <= outputColumn + columnInKernel
					&& outputLine + lineInKernel < inputFrameHeight && 0 <= outputLine + lineInKernel) {

				double oldValue = PDOFFWRtoInputs.getDouble(0,
						new int[] { depthInKernel, outputLine + lineInKernel, outputColumn + columnInKernel });

				double newValue = oldValue + params.getDouble(index)
						* DOLFWRtoOutputs.getDouble(0, new int[] { outputFrameIndex, outputLine, outputColumn });

				PDOFFWRtoInputs.set(0,
						new int[] { depthInKernel, outputLine + lineInKernel, outputColumn + columnInKernel },
						newValue);

			}

			index.increase();
		}

		return PDOFFWRtoInputs;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray DOLFWRtoOutputs) {

		DataArray PDOFFWRtoParams = new DataArray(paramArchitecture);

		Index index = new Index(paramArchitecture);

		while (index.getIndexOfMultidimensionnalArray() == 0) {

			int outputFrameIndex = index.getIndexInsideMultidimensionnalArray()[0];
			int outputLine = index.getIndexInsideMultidimensionnalArray()[1];
			int outputColumn = index.getIndexInsideMultidimensionnalArray()[2];
			int depthInKernel = index.getIndexInsideMultidimensionnalArray()[3];
			int lineInKernel = index.getIndexInsideMultidimensionnalArray()[4];
			int columnInKernel = index.getIndexInsideMultidimensionnalArray()[5];

			double outputPartialDerivative = DOLFWRtoOutputs.getDouble(0,
					new int[] { outputFrameIndex, outputLine, outputColumn });

			if (outputColumn + columnInKernel < inputFrameWidth && 0 <= outputColumn + columnInKernel
					&& outputLine + lineInKernel < inputFrameHeight && 0 <= outputLine + lineInKernel) {

				double neuronValue = input.getDouble(0,
						new int[] { depthInKernel, outputLine + lineInKernel, outputColumn + columnInKernel });

				params.set(index, neuronValue * outputPartialDerivative);
			} else {
				params.set(index, paddingValue * outputPartialDerivative);
			}

			index.increase();
		}
		while (index.maxNotReached()) {

			int filterIndex = index.getIndexInsideMultidimensionnalArray()[0];
			int outputLine = index.getIndexInsideMultidimensionnalArray()[1];
			int outputColumn = index.getIndexInsideMultidimensionnalArray()[2];

			double outputPartialDerivative = DOLFWRtoOutputs.getDouble(0,
					new int[] { filterIndex, outputLine, outputColumn });

			params.set(index, outputPartialDerivative);

			index.increase();
		}
		return PDOFFWRtoParams;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.NON_REGULAR_CONVOLUTIONAL_LAYER;
	}

	public int getInputFrameWidth() {
		return inputFrameWidth;
	}

	public int getInputFrameHeight() {
		return inputFrameHeight;
	}

	public int getEntryDepth() {
		return entryDepth;
	}

	public int getKernelSize() {
		return kernelSize;
	}

	public double getPaddingValue() {
		return paddingValue;
	}

	public int getNumberOfOutputFrames() {
		return numberOfOutputFrames;
	}

}