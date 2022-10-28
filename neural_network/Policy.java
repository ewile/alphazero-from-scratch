package neural_network;

import java.util.ArrayList;

import chess_rules.Move;
import general.Values;

public class Policy {

	// the policy represents the output of the neural network

	public double v; // estimation of the game result : v = 1 -> white will win, v = 0 -> draw, v =
						// -1 -> black will win
	public ArrayList<Move> moves;
	public ArrayList<Double> probabilities; // probabilities.get(i) is the estimated probability that move.get(i) should
											// be played

	public int getNumberOfMoves() {
		return moves.size();
	}

	public void resizeProbabilitiesForASummOfOne() {
		double s = 0;
		for (int i = 0; i < getNumberOfMoves(); i++) {
			s += probabilities.get(i);
		}
		if (s > 0) {
			for (int i = 0; i < getNumberOfMoves(); i++) {
				probabilities.set(i, probabilities.get(i) / s);
			}
		} else {
			for (int i = 0; i < getNumberOfMoves(); i++) {
				probabilities.set(i, 1.0 / getNumberOfMoves());
			}
		}
	}

	public Move selectRandomMoveAccordingToProbabilities() {
		double d = Values.random.nextDouble();
		double s = 0;
		for (int i = 0; i < getNumberOfMoves(); i++) {
			s += probabilities.get(i);
			if (d <= s) {
				return moves.get(i);
			}
		}
		return null;
	}

	// delta is the error allowed hen comparing 2 probas
	public boolean equals(Policy p) {
		double delta = 0.001;
		if (v != p.v || p.getNumberOfMoves() != getNumberOfMoves()) {
			System.out.println("policiy moves " + p.getNumberOfMoves() + " vs " + getNumberOfMoves());
			return false;
		}

		for (int i = 0; i < getNumberOfMoves(); i++) {
			boolean contains = false;
			for (int y = 0; y < getNumberOfMoves(); y++) {
				if (moves.get(i).equals(p.moves.get(y))) {
					contains = true;
					if (Math.abs(probabilities.get(i) - p.probabilities.get(y)) > delta) {

						return false;
					}
				}
			}
			if (!contains) {
				return false;
			}
		}
		return true;
	}
}