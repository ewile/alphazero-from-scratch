package tree_search;
import chess_rules.Board;
import chess_rules.Move;
import chess_rules.Rules;
import general.DataArray;
import general.Values;
import neural_network.ChessNeuralNetwork;
import neural_network.Policy;
import training.HyperParameters;

public class TreeManager {

	public static void exploreFrom(Node node, ChessNeuralNetwork cnn) {
		if (node.type == Values.LEAF_NODE) {

			if (node.getBoard().gameState == Values.GAME_RUNNING) {
				double v = expand(node, cnn);
				retroPropagate(node, v);
			} else {

				double z = 0;
				switch (node.getBoard().gameState) {
					case Values.WHITE_WON:
						z = 1;
						break;
					case Values.BLACK_WON:
						z = -1;
						break;
					case Values.DRAW:
						z = 0;
						break;
				}

				retroPropagate(node, z);
			}

		} else {
			// System.out.println(node);

			int index = getEdgeIndexToExplore(node);

			// System.out.println("move chosen : "+node.belowEdges.get(index).move);
			exploreFrom(node.getBelowEdge(index).targetNode, cnn);

		}

	}

	public static Node getRootNode(Node node) {
		if (node.type == Values.ROOT_NODE) {
			return node;
		} else {
			return getRootNode(node.getUpwardEdge(0).originNode);
		}
	}

	public static int getEdgeIndexToExplore(Node node) {
		int numberOfMoves = node.getBelowEdgeCount();
		int totalVisitCount = 0;

		for (int i = 0; i < numberOfMoves; i++) {
			totalVisitCount += node.getBelowEdge(i).N;
		}

		double[] probabilities = new double[numberOfMoves];
		double total = 0;

		for (int i = 0; i < numberOfMoves; i++) {
			Edge edge = node.getBelowEdge(i);
			double probability = HyperParameters.getEdgeSignificance(node.getBoard().colorToMove, edge.Q, edge.P,
					edge.N, totalVisitCount);

			total += probability;
			probabilities[i] = probability;
		}

		for (int i = 0; i < numberOfMoves; i++) {
			probabilities[i] = probabilities[i] / total;
		}
		double d = Values.random.nextDouble();

		double sum = 0;
		for (int i = 0; i < numberOfMoves; i++) {
			sum += probabilities[i];
			if (d <= sum) {
				return i;
			}
		}
		return Values.NOTHING;
	}

	public static double expand(Node leafNode, ChessNeuralNetwork cnn) {
		if (leafNode.type != Values.LEAF_NODE || leafNode.getBoard().gameState != Values.GAME_RUNNING) {
			System.err.println("error : not a LEAF NODE");
		}

		if (leafNode.type == Values.LEAF_NODE) {
			leafNode.type = Values.INTERNAL_NODE;
		}

		if (leafNode.getBelowEdgeCount() != 0) {
			System.out.println(1 / 0);
		}

		DataArray input = ChessNeuralNetwork.convertBoardIntoDataArray(leafNode.getBoard());
		DataArray[] neurons = cnn.compute(input);
		leafNode.savingIndex = DataSaver.save(neurons);

		Policy policy = ChessNeuralNetwork.getPolicyFromOutput(neurons[neurons.length - 1], leafNode.getBoard());

		double realResult = Values.NOTHING;

		for (int i = 0; i < policy.getNumberOfMoves(); i++) {
			Move move = policy.moves.get(i);

			Board newBoard = Rules.play(leafNode.getBoard(), move);
			double probability = policy.probabilities.get(i);

			Node node = new Node(Values.LEAF_NODE, newBoard);

			Edge edge = new Edge(leafNode, node, move, probability);

			node.addUpwardEdge(edge);
			;
			leafNode.addBelowEdge(edge);
			;

			if (newBoard.gameState == Values.WHITE_WON) {
				realResult = 1;
			}
			if (newBoard.gameState == Values.BLACK_WON) {
				realResult = -1;
			}
		}

		if (realResult == Values.NOTHING) {
			return policy.v;
		} else {
			return realResult;
		}
	}

	public static void retroPropagate(Node leafNode, double v) {
		for (int i = 0; i < leafNode.getUpwardEdgeCount(); i++) {
			leafNode.getUpwardEdge(i).N += 1;
			leafNode.getUpwardEdge(i).W += v;
			leafNode.getUpwardEdge(i).Q += leafNode.getUpwardEdge(i).W / leafNode.getUpwardEdge(i).N;

			if (leafNode.type != Values.ROOT_NODE) {
				retroPropagate(leafNode.getUpwardEdge(i).originNode, v);
			}
		}

	}

}