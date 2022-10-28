package training;

import java.util.ArrayList;

import chess_rules.Board;
import chess_rules.Move;
import chess_rules.Rules;
import general.DataArray;
import general.Values;
import neural_network.ChessNeuralNetwork;
import neural_network.Policy;

public class Util {
	public static void testCNNagainstRandomPlay(ChessNeuralNetwork cnn, int numberOfGames) {
		int victories = 0;
		int defeats = 0;
		int draws = 0;

		Board board = new Board();
		System.out.println("\n\nTesting CNN against random play : games -> ");
		for (int h = 0; h < numberOfGames; h++) {
			System.out.print(h + " ");
			board.init();
			boolean cnnIsWhite = Values.random.nextBoolean();
			while (board.gameState == Values.GAME_RUNNING) {

				Policy policy;

				if (cnnIsWhite && board.colorToMove == Values.WHITE) {

					DataArray inputVector = ChessNeuralNetwork.convertBoardIntoDataArray(board);
					DataArray[] output = cnn.compute(inputVector);

					policy = ChessNeuralNetwork.getPolicyFromOutput(output[output.length - 1], board);
					Move move = policy.selectRandomMoveAccordingToProbabilities();
					if (move == null) {
						System.out.println("err");
						// PROBELEM HERE WHEN NO LEGAL HAD A POSITIE PROBA, AND POLICY CONTAINS NO MOVE
					}
				} else {
					ArrayList<Move> legalMoves = Rules.getLegalMoves(board);
					ArrayList<Double> probabilities = new ArrayList<Double>();
					for (int i = 0; i < legalMoves.size(); i++) {
						probabilities.add(1.0 / legalMoves.size());
					}
					policy = new Policy();
					policy.probabilities = probabilities;
					policy.moves = legalMoves;
				}
				Move move = policy.selectRandomMoveAccordingToProbabilities();

				board = Rules.play(board, move);

			}

			switch (board.gameState) {
				case Values.WHITE_WON:
					if (cnnIsWhite) {
						victories += 1;
					} else {
						defeats += 1;
					}
					break;

				case Values.BLACK_WON:
					if (cnnIsWhite) {
						defeats += 1;
					} else {
						victories += 1;
					}
					break;
				case Values.DRAW:
					draws += 1;
					break;
				default:
					break;
			}
		}
		System.out.println("\n\nOn " + numberOfGames + " games, Chess neural network\nwon " + victories
				+ " games,\nlost " + defeats + " games,\nand " + draws + " games were draws.");
	}
}
