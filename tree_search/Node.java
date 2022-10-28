package tree_search;

import java.util.ArrayList;

import chess_rules.Board;
import general.DataArray;
import neural_network.ChessNeuralNetwork;
import neural_network.Policy;
import training.HyperParameters;

public class Node {

	public int type; // root, internal or final

	private ArrayList<Edge> upwardEdges;
	private ArrayList<Edge> belowEdges;

	private Board board;

	public int savingIndex; // for Neural Network

	public Node(int type, Board board) {
		this.type = type;
		this.board = board;
		belowEdges = new ArrayList<>();
		upwardEdges = new ArrayList<>();
	}

	public void addUpwardEdge(Edge edge) {
		upwardEdges.add(edge);
		Board b = edge.originNode.getBoard();
		if (b.colorToMove == board.colorToMove) {
			System.out.println();
		}
	}

	public void addBelowEdge(Edge edge) {
		belowEdges.add(edge);
	}

	public int getUpwardEdgeCount() {
		return upwardEdges.size();
	}

	public int getBelowEdgeCount() {
		return belowEdges.size();
	}

	public Edge getUpwardEdge(int index) {
		return upwardEdges.get(index);
	}

	public Edge getBelowEdge(int index) {
		return belowEdges.get(index);
	}

	public Board getBoard() {
		return board;
	}

	public DataArray toExpectedOutput() {
		Policy policy = new Policy();
		policy.moves = new ArrayList<>();
		policy.probabilities = new ArrayList<>();

		if (upwardEdges.size() > 0) {
			policy.v = upwardEdges.get(0).Q;
		} else {
			// first position of chess board, there is no upward Edge that count Q,
			// so we find Q with the Q of below edges
			for (int h = 0; h < belowEdges.size(); h++) {
				policy.v += belowEdges.get(h).Q;
			}
			policy.v /= belowEdges.size();
		}

		for (int h = 0; h < belowEdges.size(); h++) {
			policy.moves.add(belowEdges.get(h).move);
			policy.probabilities.add((double) belowEdges.get(h).Q);
		}
		policy.resizeProbabilitiesForASummOfOne();

		return ChessNeuralNetwork.getOutputVectorFromPolicy(policy, board);
	}

	public String toString() {
		String str = board.toString();

		int numberOfMoves = belowEdges.size();
		int totalVisitCount = 0;

		for (int i = 0; i < numberOfMoves; i++) {
			totalVisitCount += belowEdges.get(i).N;
		}

		double[] probabilities = new double[numberOfMoves];
		double total = 0;

		for (int i = 0; i < numberOfMoves; i++) {
			Edge edge = belowEdges.get(i);
			double probability = HyperParameters.getEdgeSignificance(board.colorToMove, edge.Q, edge.P, edge.N,
					totalVisitCount);
			total += probability;
			probabilities[i] = probability;
		}

		for (int i = 0; i < numberOfMoves; i++) {
			probabilities[i] = probabilities[i] / total;
		}

		for (int i = 0; i < numberOfMoves; i++) {
			str += belowEdges.get(i).move.toString() + " : " + probabilities[i] + "  ";
		}
		return str;
	}
}