package layers;

import general.Data;
import general.DataArray;
import general.Util;
import general.Values;
import neural_network.Layer;

public class SplitLayer extends Layer {

	public Layer layers[];

	public SplitLayer(Layer layers[]) {
		this.layers = layers;

		int inputCount = 0;
		int outputCount = 0;
		int paramCount = 0;

		for (int i = 0; i < layers.length; i++) {
			inputCount += layers[i].inputArchitecture.length;
			outputCount += layers[i].outputArchitecture.length;
			paramCount += layers[i].paramArchitecture.length;
		}

		inputArchitecture = new int[inputCount][];
		outputArchitecture = new int[outputCount][];
		paramArchitecture = new int[paramCount][];

		inputCount = 0;
		outputCount = 0;
		paramCount = 0;

		for (int i = 0; i < layers.length; i++) {
			for (int u = 0; u < layers[i].inputArchitecture.length; u++) {
				inputArchitecture[inputCount] = layers[i].inputArchitecture[u].clone();
				inputCount++;
			}
			for (int u = 0; u < layers[i].outputArchitecture.length; u++) {
				outputArchitecture[outputCount] = layers[i].outputArchitecture[u].clone();
				outputCount++;
			}
			for (int u = 0; u < layers[i].paramArchitecture.length; u++) {
				paramArchitecture[paramCount] = layers[i].paramArchitecture[u].clone();
				paramCount++;
			}

		}

		params = new DataArray(paramArchitecture);
		Data[] content = params.content;

		paramCount = 0;

		for (int i = 0; i < layers.length; i++) {
			for (int u = 0; u < layers[i].paramArchitecture.length; u++) {
				content[paramCount] = layers[i].params.content[u];
				paramCount++;
			}

		}

	}

	@Override
	public DataArray compute(DataArray input) {
		DataArray[] inputs = new DataArray[layers.length];
		int inputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			inputs[i] = new DataArray(layers[i].inputArchitecture);
			for (int u = 0; u < layers[i].inputArchitecture.length; u++) {
				inputs[i].content[u] = input.content[inputCount];
				inputCount++;
			}
		}
		DataArray output = new DataArray(outputArchitecture);
		int outputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			DataArray opt = layers[i].compute(inputs[i]);
			for (int u = 0; u < layers[i].outputArchitecture.length; u++) {
				output.content[outputCount] = opt.content[u];
				outputCount++;
			}
		}
		return output;
	}

	@Override
	public int getLayerType() {
		// TODO Auto-generated method stub
		return Values.SPLIT_LAYER;
	}

	@Override
	public DataArray getPDOLFWRtoInputs(DataArray input, DataArray PDOLFWRtoOutputs) {

		DataArray[] inputsForEachLayer = new DataArray[layers.length];
		int inputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			inputsForEachLayer[i] = new DataArray(layers[i].inputArchitecture);
			for (int u = 0; u < layers[i].inputArchitecture.length; u++) {
				inputsForEachLayer[i].content[u] = input.content[inputCount];
				inputCount++;
			}
		}

		DataArray[] DPOLFWRtoOutputsForEachLayer = new DataArray[layers.length];
		int outputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			DPOLFWRtoOutputsForEachLayer[i] = new DataArray(layers[i].outputArchitecture);
			for (int u = 0; u < layers[i].outputArchitecture.length; u++) {
				DPOLFWRtoOutputsForEachLayer[i].content[u] = PDOLFWRtoOutputs.content[outputCount];
				outputCount++;
			}
		}

		DataArray PDOLFWRtoInputs = new DataArray(inputArchitecture);
		inputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			DataArray DPOLFWRtoInputsForThisLayer = layers[i].getPDOLFWRtoInputs(inputsForEachLayer[i],
					DPOLFWRtoOutputsForEachLayer[i]);
			for (int u = 0; u < layers[i].inputArchitecture.length; u++) {
				PDOLFWRtoInputs.content[inputCount] = DPOLFWRtoInputsForThisLayer.content[u];
				inputCount++;
			}
		}
		return PDOLFWRtoInputs;
	}

	@Override
	public DataArray getPDOLFWRtoParams(DataArray input, DataArray PDOLFWRtoOutputs) {

		DataArray[] inputsForEachLayer = new DataArray[layers.length];
		int inputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			inputsForEachLayer[i] = new DataArray(layers[i].inputArchitecture);
			for (int u = 0; u < layers[i].inputArchitecture.length; u++) {
				inputsForEachLayer[i].content[u] = input.content[inputCount];
				inputCount++;
			}
		}

		DataArray[] DPOLFWRtoOutputsForEachLayer = new DataArray[layers.length];
		int outputCount = 0;
		for (int i = 0; i < layers.length; i++) {
			DPOLFWRtoOutputsForEachLayer[i] = new DataArray(layers[i].outputArchitecture);
			for (int u = 0; u < layers[i].outputArchitecture.length; u++) {
				DPOLFWRtoOutputsForEachLayer[i].content[u] = PDOLFWRtoOutputs.content[outputCount];
				outputCount++;
			}
		}

		DataArray PDOLFWRtoParams = new DataArray(paramArchitecture);
		int paramsCount = 0;
		for (int i = 0; i < layers.length; i++) {
			DataArray DPOLFWRtoParamsForThisLayer = layers[i].getPDOLFWRtoParams(inputsForEachLayer[i],
					DPOLFWRtoOutputsForEachLayer[i]);
			for (int u = 0; u < layers[i].paramArchitecture.length; u++) {
				PDOLFWRtoParams.content[paramsCount] = DPOLFWRtoParamsForThisLayer.content[u];
				paramsCount++;
			}
		}
		return PDOLFWRtoParams;
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < Values.LENGTH_OF_A_LINE_IN_TERMINAL; i++) {
			str += "-";
		}
		str += "\n\n" + Values.toString(getLayerType()) + " :\n\ninput architecture :\n\n";
		str += Util.saveIntegerBidimensionalArrayToString(inputArchitecture);
		str += "\n\noutput architecture :\n\n";
		str += Util.saveIntegerBidimensionalArrayToString(outputArchitecture);

		for (int i = 0; i < layers.length; i++) {
			str += "\n\nlayer " + (i + 1) + " :\n" + layers[i].toString().replaceAll("\n", "\n  ");
		}

		str += "\n\n";

		for (int i = 0; i < Values.LENGTH_OF_A_LINE_IN_TERMINAL; i++) {
			str += "-";
		}

		return str;

	}

}
