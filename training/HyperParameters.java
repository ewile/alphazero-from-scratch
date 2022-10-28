package training;

import general.DataArray;
import general.Index;
import general.Values;
import layers.BatchNormalizationLayer;
import layers.ConvolutionalLayer;
import layers.DeadLayer;
import layers.DuplicateLayer;
import layers.FullyConnectedLayer;
import layers.FusionLayer;
import layers.NoActionLayer;
import layers.ReluLayer;
import layers.SplitLayer;
import layers.TANHLayer;
import neural_network.Layer;

public class HyperParameters {

	public static final int BATCH_SIZE = 100;

	public static final int NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER = 70;
	public static final int NUMBER_OF_RESIDUAL_LAYERS = 2;

	public static final int NUMBER_OF_EXPLORATIONS_PER_POSITION = 1;
	public static final double EXPLORATION_COEF = 1;

	public static final double MOVE_COEF_FOR_LOSS_FUNCTION = 0;
	public static final double GAME_RESULT_COEF_FOR_LOSS_FUNCTION = 1;

	public static final double LEARNING_RATE = 0.1;

	public static final int LOSS_FUNCTION = Values.GOOGLE_LOSS_FUNCTION;
	public static final int KERNEL_SIZE = 3; // ODD NUMBER
	public static final int PADDING = (KERNEL_SIZE - 1) / 2;
	public static final int PADDING_VALUE = 0;
	public static final double RANDOM_PARAM_RANGE = 1;

	public static double computeLossFunction(DataArray finalOutput, DataArray expectedOutput) {

		double err = 0;
		Index index;
		switch (LOSS_FUNCTION) {
			case Values.GOOGLE_LOSS_FUNCTION:
				err = GAME_RESULT_COEF_FOR_LOSS_FUNCTION * Math.pow(
						expectedOutput.getDouble(1, new int[] { 0 }) - finalOutput.getDouble(1, new int[] { 0 }), 2);
				index = new Index(finalOutput.architecture);
				while (index.getIndexOfMultidimensionnalArray() == 0) {
					err -= MOVE_COEF_FOR_LOSS_FUNCTION * expectedOutput.getDouble(index)
							* Math.log(finalOutput.getDouble(index));
				}
				break;

			case Values.MEAN_SQUARE_LOSS_FUNCTION:
				index = new Index(finalOutput.architecture);
				while (index.maxNotReached()) {
					err += Math.pow(finalOutput.getDouble(index) - expectedOutput.getDouble(index), 2);
					index.increase();
				}
				err /= finalOutput.getTotalNumberOfValues();
				break;
		}

		return err;
	}

	public static double getPartialDerivativeOf_LOSS_FUNCTION_withRespectTo_OUTPUT_NEURON(Index outputNeuronIndex,
			DataArray finalOutput, DataArray expectedOutput) {

		switch (LOSS_FUNCTION) {
			case Values.GOOGLE_LOSS_FUNCTION:
				if (outputNeuronIndex.getIndexOfMultidimensionnalArray() == 1) {
					return MOVE_COEF_FOR_LOSS_FUNCTION * 2 * (finalOutput.getDouble(1, new int[] { 0 })
							- expectedOutput.getDouble(1, new int[] { 0 }));
				}

				return -GAME_RESULT_COEF_FOR_LOSS_FUNCTION * expectedOutput.getDouble(outputNeuronIndex)
						* 1 / (0.00001 + finalOutput.getDouble(outputNeuronIndex));

			case Values.MEAN_SQUARE_LOSS_FUNCTION:
				return 2 * (finalOutput.getDouble(outputNeuronIndex) - expectedOutput.getDouble(outputNeuronIndex))
						/ finalOutput.getTotalNumberOfValues();

		}
		return 1 / 0;

	}

	public static double getEdgeSignificance(int colorToPlay, double Q, double P, double N, double totalVisitCount) {
		double d1 = EXPLORATION_COEF * P * Math.sqrt(totalVisitCount + 0.01) / (1 + N);
		double d2;
		if (colorToPlay == Values.WHITE) {
			d2 = (Q + 1) / 2;
		} else {
			d2 = -(Q - 1) / 2;

		}
		return d1 + d2;
	}

	public static Layer[] getLayersforCNN() {
		Layer[] layers = new Layer[11 + 8 * NUMBER_OF_RESIDUAL_LAYERS];
		layers[0] = new DuplicateLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } });
		layers[1] = new SplitLayer(new Layer[] {
				new ConvolutionalLayer(Values.BOARD_SIZE, Values.BOARD_SIZE, 15,
						NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, KERNEL_SIZE, PADDING_VALUE),
				new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

		layers[2] = new SplitLayer(new Layer[] { new BatchNormalizationLayer(new int[][] {
				new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
				new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
		layers[3] = new SplitLayer(new Layer[] { new ReluLayer(new int[][] {
				new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
				new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

		int index = 4;
		for (int i = 0; i < NUMBER_OF_RESIDUAL_LAYERS; i++) {
			layers[index] = new SplitLayer(new Layer[] { new NoActionLayer(new int[][] {
					new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
					new DuplicateLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

			layers[index
					+ 1] = new SplitLayer(
							new Layer[] {
									new FusionLayer(new int[][] {
											new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE,
													Values.BOARD_SIZE },
											{ 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
									new NoActionLayer(
											new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

			layers[index + 2] = new SplitLayer(new Layer[] {
					new ConvolutionalLayer(Values.BOARD_SIZE, Values.BOARD_SIZE,
							15 + NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER,
							KERNEL_SIZE, PADDING_VALUE),
					new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
			layers[index + 3] = new SplitLayer(new Layer[] { new BatchNormalizationLayer(new int[][] {
					new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
					new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
			layers[index + 4] = new SplitLayer(new Layer[] { new ReluLayer(new int[][] {
					new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
					new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

			layers[index + 5] = new SplitLayer(new Layer[] {
					new ConvolutionalLayer(Values.BOARD_SIZE, Values.BOARD_SIZE,
							NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER,
							KERNEL_SIZE, PADDING_VALUE),
					new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
			layers[index + 6] = new SplitLayer(new Layer[] { new BatchNormalizationLayer(new int[][] {
					new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
					new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
			layers[index + 7] = new SplitLayer(new Layer[] { new ReluLayer(new int[][] {
					new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
					new NoActionLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

			index += 8;
		}
		layers[index] = new SplitLayer(new Layer[] { new NoActionLayer(new int[][] {
				new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
				new DeadLayer(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
		layers[index + 1] = new DuplicateLayer(new int[][] {
				new int[] { NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER, Values.BOARD_SIZE, Values.BOARD_SIZE } });

		layers[index + 2] = new SplitLayer(new Layer[] {
				new ConvolutionalLayer(Values.BOARD_SIZE, Values.BOARD_SIZE, NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER,
						Values.NUMBER_OF_PLANES_IN_CNN_OUTPUT, KERNEL_SIZE, PADDING_VALUE),
				new ConvolutionalLayer(Values.BOARD_SIZE, Values.BOARD_SIZE, NUMBER_OF_FILTERS_PER_CONVOLUTIONAL_LAYER,
						1, KERNEL_SIZE, PADDING_VALUE) });
		layers[index + 3] = new SplitLayer(new Layer[] {
				new BatchNormalizationLayer(new int[][] {
						new int[] { Values.NUMBER_OF_PLANES_IN_CNN_OUTPUT, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
				new BatchNormalizationLayer(new int[][] { new int[] { 1, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });
		layers[index + 4] = new SplitLayer(
				new Layer[] { new ReluLayer(new int[][] {
						new int[] { Values.NUMBER_OF_PLANES_IN_CNN_OUTPUT, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
						new ReluLayer(new int[][] { new int[] { 1, Values.BOARD_SIZE, Values.BOARD_SIZE } }) });

		layers[index + 5] = new SplitLayer(
				new Layer[] { new NoActionLayer(new int[][] {
						new int[] { Values.NUMBER_OF_PLANES_IN_CNN_OUTPUT, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
						new FullyConnectedLayer(new int[][] { new int[] { 1, Values.BOARD_SIZE, Values.BOARD_SIZE } },
								new int[][] { new int[] { 1 } }) });
		layers[index + 6] = new SplitLayer(
				new Layer[] { new NoActionLayer(new int[][] {
						new int[] { Values.NUMBER_OF_PLANES_IN_CNN_OUTPUT, Values.BOARD_SIZE, Values.BOARD_SIZE } }),
						new TANHLayer(new int[][] { new int[] { 1 } }) });

		for (int i = 0; i < layers.length; i++) {
			layers[i].setRandomParams();
		}

		return layers;
	}

}
