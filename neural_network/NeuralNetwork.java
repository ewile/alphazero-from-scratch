package neural_network;

import general.DataArray;
import general.Index;
import general.Util;
import general.Values;
import layers.BatchNormalizationLayer;
import layers.SplitLayer;

public abstract class NeuralNetwork {

	public Layer[] layers;

	public abstract double computeLossFunction(DataArray finalOutput, DataArray expectedOutput);

	public abstract double getPDOLFWRtoOutputNeuron(Index outputNeuronIndex,
			DataArray finalOutput, DataArray expectedOutput);

	public NeuralNetwork(Layer[] layers) {
		this.layers = layers;
	}

	public void setRandomParams() {
		for (int i = 0; i < layers.length; i++) {
			layers[i].setRandomParams();
		}
	}

	// return all neuron values inside the neural network, to make gradient descent
	// later
	public DataArray[] compute(DataArray input) {
		DataArray[] neuronsValues = new DataArray[layers.length + 1];
		neuronsValues[0] = input.clone(); // REALLY NECESSARY ??
		for (int i = 0; i < layers.length; i++) {
			if (Util.equals(layers[i].inputArchitecture, neuronsValues[i].architecture)) {
				neuronsValues[i + 1] = layers[i].compute(neuronsValues[i]);
			} else {
				System.out.println("architecture issue");
			}
		}
		return neuronsValues;
	}

	public DataArray[] getPDOLFWRtoParams(DataArray[] neuronsValues, DataArray expectedOutput) {

		DataArray[] PDOLFWRtoParams = new DataArray[layers.length];
		for (int l = 0; l < layers.length; l++) {
			PDOLFWRtoParams[l] = new DataArray(layers[l].getParamArchitecture());
		}

		DataArray[] PDOLFWRtoNeurons = new DataArray[layers.length + 1];
		PDOLFWRtoNeurons[layers.length] = getPDOLFWRtoOutputNeurons(
				neuronsValues[layers.length], expectedOutput);

		for (int j = layers.length - 1; j >= 0; j--) {
			PDOLFWRtoNeurons[j] = layers[j].getPDOLFWRtoInputs(neuronsValues[j],
					PDOLFWRtoNeurons[j + 1]);

			PDOLFWRtoParams[j] = layers[j].getPDOLFWRtoParams(neuronsValues[j],
					PDOLFWRtoNeurons[j + 1]);

		}
		return PDOLFWRtoParams;
	}

	public void updateParams(DataArray[][] neuronsValues, DataArray[] expectedOutputs, double learningRate) {
		int batchSize = neuronsValues.length;

		DataArray[] AVERAGE_PDOLFWRto_PARAM = new DataArray[layers.length];

		for (int l = 0; l < layers.length; l++) {
			AVERAGE_PDOLFWRto_PARAM[l] = new DataArray(layers[l].getParamArchitecture());
		}

		for (int batchIndex = 0; batchIndex < batchSize; batchIndex++) {
			DataArray[] PDOLFWRtoParams = getPDOLFWRtoParams(neuronsValues[batchIndex], expectedOutputs[batchIndex]);
			for (int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
				Index index = new Index(layers[layerIndex].getParamArchitecture());
				while (index.maxNotReached()) {
					AVERAGE_PDOLFWRto_PARAM[layerIndex].set(index, AVERAGE_PDOLFWRto_PARAM[layerIndex].getDouble(index)
							+ PDOLFWRtoParams[layerIndex].getDouble(index));
					index.increase();
				}

			}

		}

		for (int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
			Index index = new Index(layers[layerIndex].getParamArchitecture());
			while (index.maxNotReached()) {
				AVERAGE_PDOLFWRto_PARAM[layerIndex].set(index,
						AVERAGE_PDOLFWRto_PARAM[layerIndex].getDouble(index) / batchSize);
				index.increase();
			}
		}

		updateparams(AVERAGE_PDOLFWRto_PARAM, learningRate);

	}

	public void updateparams(DataArray[] AVERAGE_PDOLFWRto_PARAM, double learningRate) {
		for (int j = 0; j < layers.length; j++) {
			layers[j].updateParams(AVERAGE_PDOLFWRto_PARAM[j], learningRate);
		}
	}

	public void updateBatchNormalizationLayers(DataArray[][] neuronsValues) {
		int batchSize = neuronsValues.length;

		DataArray[][] inputValuesHistoryOfLayer = new DataArray[layers.length][];
		for (int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
			inputValuesHistoryOfLayer[layerIndex] = new DataArray[batchSize];

			for (int h = 0; h < batchSize; h++) {
				inputValuesHistoryOfLayer[layerIndex][h] = neuronsValues[h][layerIndex].clone();
			}
		}
		for (int q = 0; q < layers.length; q++) {
			updateLayer(layers[q], inputValuesHistoryOfLayer[q]);
		}
	}

	private void updateLayer(Layer layer, DataArray[] inputValuesHistory) {

		if (layer.getLayerType() == Values.BATCH_NORMALIZATION_LAYER) {
			double mean = BatchNormalizationLayer.calculateMean(inputValuesHistory);
			double standardDeviation = BatchNormalizationLayer.calculateStandardDeviation(inputValuesHistory, mean);
			((BatchNormalizationLayer) layer).setMean(mean);
			((BatchNormalizationLayer) layer).setStandardDeviation(standardDeviation);

		}

		if (layer.getLayerType() == Values.SPLIT_LAYER) {
			Layer[] subLayers = ((SplitLayer) layer).layers;

			int index = 0;

			for (int subLayerIndex = 0; subLayerIndex < subLayers.length; subLayerIndex++) {
				DataArray[] inputValuesHistoryTHISSubLayer = new DataArray[inputValuesHistory.length];
				for (int inputValuesIndex = 0; inputValuesIndex < inputValuesHistory.length; inputValuesIndex++) {
					inputValuesHistoryTHISSubLayer[inputValuesIndex] = new DataArray(
							subLayers[subLayerIndex].inputArchitecture);
					for (int k = 0; k < subLayers[subLayerIndex].inputArchitecture.length; k++) {
						inputValuesHistoryTHISSubLayer[inputValuesIndex].content[k] = inputValuesHistory[inputValuesIndex].content[index
								+ k];
					}
				}
				index += subLayers[subLayerIndex].inputArchitecture.length;

				updateLayer(subLayers[subLayerIndex], inputValuesHistoryTHISSubLayer);

			}
		}
	}

	public DataArray getPDOLFWRtoOutputNeurons(DataArray finalOutput,
			DataArray expectedOutput) {
		DataArray parDer = new DataArray(layers[layers.length - 1].outputArchitecture);
		Index index = new Index(layers[layers.length - 1].outputArchitecture);
		while (index.maxNotReached()) {
			parDer.set(index, getPDOLFWRtoOutputNeuron(index, finalOutput,
					expectedOutput));
			index.increase();
		}

		return parDer;
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < layers.length; i++) {
			str += "layer " + (i + 1) + " of the neural network : \n" + layers[i].toString() + "\n\n\n";
		}
		return str;
	}

}