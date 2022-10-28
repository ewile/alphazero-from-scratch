package general;

import neural_network.ChessNeuralNetwork;
import training.Train;

// PDOLFWRto = Partial derivative of loss function with respect to

public class Main {

	public static void main(String[] args) {

		// Util.deleteAllFilesThatBeginsWith(Values.PREVIOUS_NAME_FOR_TEMP_CNN);

		ChessNeuralNetwork cnn = new ChessNeuralNetwork(10);

		int numberOfBatches = 10;
		for (int i = 1; i <= numberOfBatches; i++) {

			Train.startTraining(cnn, 1);

			System.out.println(cnn);
			cnn.storeInFile(i);

			// training.Util.testCNNagainstRandomPlay(cnn, 20);
		}

	}

}
