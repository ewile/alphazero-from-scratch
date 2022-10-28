package tree_search;

import chess_rules.Move;

public class Edge {

	public Node originNode;
	public Node targetNode;

	Move move;

	public int N; // visit count
	public int W; // total value of next state
	public int Q; // mean value of next state
	public double P; // prior probability of selecting next state

	public Edge(Node originNode, Node targetNode, Move move, double priorProbability) {
		this.originNode = originNode;
		this.targetNode = targetNode;
		this.move = move;

		P = priorProbability;
		N = 0;
		W = 0;
		Q = 0;

	}

}