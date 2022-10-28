package layers;

import general.DataArray;
import general.Values;
import neural_network.Layer;

public class ConvolutionalLayer extends Layer {

	// a classical convolutional layer

	// stride is always 1

	// the input and the output must be arrayData with one data of three dimensions

	private int inputFrameWidth;
	private int inputFrameHeight;
	private int entryDepth;
	private int kernelSize;
	private int outputFrameWidth;
	private int outputFrameHeight;
	private int padding;
	private double paddingValue;
	private int numberOfFilters;

	public ConvolutionalLayer(int inputFrameHeight, int inputFrameWidth, int entryDepth, int numberOfFilters,
			int kernelSize, double paddingValue) {
		if (kernelSize % 2 == 0) {
			System.out.println("error 95461" + 1 / 0);
		}
		this.inputFrameWidth = inputFrameWidth;
		this.inputFrameHeight = inputFrameHeight;
		this.padding = (kernelSize - 1) / 2;
		this.paddingValue = paddingValue;
		this.numberOfFilters = numberOfFilters;
		this.entryDepth = entryDepth;
		this.kernelSize = kernelSize;

		outputFrameWidth = (inputFrameWidth + 2 * padding - kernelSize + 1);
		outputFrameHeight = (inputFrameHeight + 2 * padding - kernelSize + 1);

		this.inputArchitecture = new int[][] { new int[] { entryDepth, inputFrameHeight, inputFrameWidth } };
		this.outputArchitecture = new int[][] { new int[] { numberOfFilters, outputFrameHeight, outputFrameWidth } };

		this.paramArchitecture = new int[][] { new int[] { numberOfFilters, entryDepth, kernelSize, kernelSize },
				new int[] { numberOfFilters } };

		params = new DataArray(paramArchitecture);

	}

	// -> outputLine and outputColumn represent the coordinates of the bottom left
	// corner of the kernel, with origin the bottom left corner of the surface,
	// including padding
	// -> depthInKernel, lineInKernel and columnInKernel represent the coordinates
	// of the synapse inside the kernel (again we begin at the bottom left corner)

	@Override
	public DataArray compute(DataArray input) {
		DataArray output = new DataArray(outputArchitecture);

		for (int filterIndex = 0; filterIndex < numberOfFilters; filterIndex++) {

			double bias = params.getDouble(1, new int[] { filterIndex });

			for (int outputLine = -padding; outputLine < outputFrameHeight - padding; outputLine++) {
				for (int outputColumn = -padding; outputColumn < outputFrameWidth - padding; outputColumn++) {
					double outputValue = 0;

					// adding bias
					outputValue += bias;

					for (int depthInKernel = 0; depthInKernel < entryDepth; depthInKernel++) {
						for (int lineInKernel = 0; lineInKernel < kernelSize; lineInKernel++) {
							for (int columnInKernel = 0; columnInKernel < kernelSize; columnInKernel++) {

								double synapse = params.getDouble(0,
										new int[] { filterIndex, depthInKernel, lineInKernel, columnInKernel });

								double inputNeuron;
								int lineOnSurface = outputLine + lineInKernel;
								int columnOnSurface = outputColumn + columnInKernel;

								if (lineOnSurface < 0 || lineOnSurface >= inputFrameHeight || columnOnSurface < 0
										|| columnOnSurface >= inputFrameWidth) {
									inputNeuron = paddingValue;
								} else {
									inputNeuron = input.getDouble(0,
											new int[] { depthInKernel, lineOnSurface, columnOnSurface });
								}

								outputValue += synapse * inputNeuron;
							}
						}
					}

					output.set(0, new int[] { filterIndex, outputLine + padding, outputColumn + padding }, outputValue);

				}
			}

		}

		return output;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {

		DataArray PDOFFWRtoInputs = new DataArray(inputArchitecture);

		for (int filterIndex = 0; filterIndex < numberOfFilters; filterIndex++) {
			for (int outputLine = -padding; outputLine < outputFrameHeight - padding; outputLine++) {
				for (int outputColumn = -padding; outputColumn < outputFrameWidth - padding; outputColumn++) {

					double PDOLFWRtoOutputNeuron = PDOLFWRtoOutputs.getDouble(0,
							new int[] { filterIndex, outputLine + padding, outputColumn + padding });

					for (int depthInKernel = 0; depthInKernel < entryDepth; depthInKernel++) {
						for (int lineInKernel = 0; lineInKernel < kernelSize; lineInKernel++) {
							for (int columnInKernel = 0; columnInKernel < kernelSize; columnInKernel++) {

								double synapse = params.getDouble(0,
										new int[] { filterIndex, depthInKernel, lineInKernel, columnInKernel });

								int lineOnSurface = outputLine + lineInKernel;
								int columnOnSurface = outputColumn + columnInKernel;

								if (lineOnSurface >= 0 && lineOnSurface < inputFrameHeight && columnOnSurface >= 0
										&& columnOnSurface < inputFrameWidth) {
									double oldValue = PDOFFWRtoInputs.getDouble(0,
											new int[] { depthInKernel, lineOnSurface, columnOnSurface });
									double newValue = oldValue + synapse * PDOLFWRtoOutputNeuron;
									PDOFFWRtoInputs.set(0, new int[] { depthInKernel, lineOnSurface, columnOnSurface },
											newValue);
								}

							}
						}
					}

				}
			}

		}

		return PDOFFWRtoInputs;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {

		DataArray PDOFFWRtoParams = new DataArray(paramArchitecture);

		for (int filterIndex = 0; filterIndex < numberOfFilters; filterIndex++) {

			for (int outputLine = -padding; outputLine < outputFrameHeight - padding; outputLine++) {
				for (int outputColumn = -padding; outputColumn < outputFrameWidth - padding; outputColumn++) {

					double PDOLFWRtoOutputNeuron = PDOLFWRtoOutputs.getDouble(0,
							new int[] { filterIndex, outputLine + padding, outputColumn + padding });

					double oldValueForBias = PDOFFWRtoParams.getDouble(1, new int[] { filterIndex });
					double newValueForBias = oldValueForBias + PDOLFWRtoOutputNeuron;
					PDOFFWRtoParams.set(1, new int[] { filterIndex }, newValueForBias);

					for (int depthInKernel = 0; depthInKernel < entryDepth; depthInKernel++) {
						for (int lineInKernel = 0; lineInKernel < kernelSize; lineInKernel++) {
							for (int columnInKernel = 0; columnInKernel < kernelSize; columnInKernel++) {

								double inputNeuron;
								int lineOnSurface = outputLine + lineInKernel;
								int columnOnSurface = outputColumn + columnInKernel;

								if (lineOnSurface < 0 || lineOnSurface >= inputFrameHeight || columnOnSurface < 0
										|| columnOnSurface >= inputFrameWidth) {
									inputNeuron = paddingValue;
								} else {
									inputNeuron = input.getDouble(0,
											new int[] { depthInKernel, lineOnSurface, columnOnSurface });
								}
								double oldValueForSynapse = params.getDouble(0,
										new int[] { filterIndex, depthInKernel, lineInKernel, columnInKernel });
								double newValueForSynapse = oldValueForSynapse + inputNeuron * PDOLFWRtoOutputNeuron;
								PDOFFWRtoParams.set(0,
										new int[] { filterIndex, depthInKernel, lineInKernel, columnInKernel },
										newValueForSynapse);

							}
						}
					}

				}
			}

		}

		return PDOFFWRtoParams;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.CONVOLUTIONAL_LAYER;
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

	public int getNumberOfFilters() {
		return numberOfFilters;
	}

}