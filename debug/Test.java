package debug;

import general.DataArray;
import general.Index;
import general.Values;

public class Test {
	public static void testNeuralNetwork() {
		NNTEST nn = new NNTEST(null);
		int numberOfbatches = 200;
		int batchSize = 100;

		DataArray expectedOutputEveryTime = new DataArray(nn.layers[nn.layers.length - 1].outputArchitecture);
		Index index = new Index(expectedOutputEveryTime.architecture);
		while (index.maxNotReached()) {
			expectedOutputEveryTime.set(index, 0);
			index.increase();
		}

		for (int i = 0; i < numberOfbatches; i++) {
			DataArray[] expectedOutputs = new DataArray[batchSize];
			DataArray[][] neuronsValues = new DataArray[batchSize][];
			double err = 0;
			for (int j = 0; j < batchSize; j++) {
				DataArray input = new DataArray(nn.layers[0].inputArchitecture);
				Index inputIndex = new Index(input.architecture);
				while (inputIndex.maxNotReached()) {
					input.set(inputIndex, Values.random.nextDouble());
					inputIndex.increase();
				}
				DataArray[] neurons = nn.compute(input);
				DataArray finalOutput = neurons[neurons.length - 1];
				err += nn.computeLossFunction(finalOutput, expectedOutputEveryTime);

				neuronsValues[j] = neurons;
				expectedOutputs[j] = expectedOutputEveryTime;
			}
			System.out.println("loss : " + err / batchSize);

			System.out.println(nn.toString());

			nn.updateParams(neuronsValues, expectedOutputs, 0.0001);

		}

	}
}
