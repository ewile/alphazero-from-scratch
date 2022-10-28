package training;

import java.util.ArrayList;

import chess_rules.Board;
import general.DataArray;
import general.Index;
import general.Values;
import neural_network.ChessNeuralNetwork;
import tree_search.DataSaver;
import tree_search.Node;
import tree_search.TreeManager;

public class Train {

	// possible improvements : when a new position occur, check if a node with the
	// same position already exists

	public static void startTraining(ChessNeuralNetwork cnn, int NUMBER_OF_BATCH) {

		ArrayList<Node> path;
		int indexInBach = 0;
		int batchCount = 0;
		int x = 0;
		DataArray[] averagePDOLFWRtoParams = new DataArray[cnn.layers.length];
		for (int layerIndex = 0; layerIndex < cnn.layers.length; layerIndex++) {
			averagePDOLFWRtoParams[layerIndex] = new DataArray(cnn.layers[layerIndex].paramArchitecture);
		}

		while (batchCount < NUMBER_OF_BATCH) {

			System.out.println("number of batch : " + batchCount + ", bacthIndex : " + indexInBach);

			path = new ArrayList<Node>();
			Board board = new Board();
			board.init();
			Node node = new Node(Values.ROOT_NODE, board);
			TreeManager.expand(node, cnn);

			System.out.println("batch (on " + HyperParameters.BATCH_SIZE + ") :");
			while (node.getBoard().gameState == Values.GAME_RUNNING) {
				path.add(node);
				System.out.print(" " + x++);

				for (int i = 0; i < HyperParameters.NUMBER_OF_EXPLORATIONS_PER_POSITION; i++) {

					TreeManager.exploreFrom(node, cnn);
				}

				int edgeIndex = TreeManager.getEdgeIndexToExplore(node);

				node = node.getBelowEdge(edgeIndex).targetNode;
				if (node.type == Values.LEAF_NODE && node.getBoard().gameState == Values.GAME_RUNNING) {
					TreeManager.expand(node, cnn);

				}
				node.type = Values.ROOT_NODE;
				// setting the current node to ROOT_NODE will "cut" the three : when a new
				// position is reached, we will no longer try to find it in old positions before
				// this current node. Actually, we never try to find out if a position already
				// exist;
				// PLUS : We will no longer update W, N and Q for edges above the new root node

			}

			System.out.println("\nnode index for gradient descent : ");

			for (int nodeIndex = 0; nodeIndex < path.size(); nodeIndex++) {
				System.out.print(nodeIndex + " ");
				DataArray expectedOutput = path.get(nodeIndex).toExpectedOutput();
				DataArray[] neuronsValues = DataSaver.load(path.get(nodeIndex).savingIndex);
				DataArray[] PDOLFWRtoParams = cnn.getPDOLFWRtoParams(neuronsValues, expectedOutput);

				for (int layerIndex = 0; layerIndex < cnn.layers.length; layerIndex++) {
					Index index = new Index(cnn.layers[layerIndex].paramArchitecture);
					while (index.maxNotReached()) {
						averagePDOLFWRtoParams[layerIndex].set(index,
								averagePDOLFWRtoParams[layerIndex].getDouble(index)
										+ PDOLFWRtoParams[layerIndex].getDouble(index));
						index.increase();
					}
				}
				indexInBach++;
				if (indexInBach == HyperParameters.BATCH_SIZE) {

					indexInBach = 0;
					batchCount++;

					for (int layerIndex = 0; layerIndex < cnn.layers.length; layerIndex++) {
						Index index = new Index(cnn.layers[layerIndex].getParamArchitecture());
						while (index.maxNotReached()) {
							averagePDOLFWRtoParams[layerIndex].set(index,
									averagePDOLFWRtoParams[layerIndex].getDouble(index) / HyperParameters.BATCH_SIZE);
							index.increase();
						}
					}
					System.out.println("updating params");
					cnn.updateparams(averagePDOLFWRtoParams, HyperParameters.LEARNING_RATE);

					for (int layerIndex = 0; layerIndex < cnn.layers.length; layerIndex++) {
						averagePDOLFWRtoParams[layerIndex] = new DataArray(cnn.layers[layerIndex].paramArchitecture);
					}

				}

			}

			DataSaver.end();
		}

	}
}
