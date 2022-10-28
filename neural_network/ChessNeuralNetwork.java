package neural_network;

import java.math.BigInteger;
import java.util.ArrayList;

import chess_rules.Board;
import chess_rules.Move;
import chess_rules.Rules;
import chess_rules.Square;
import chess_rules.Util;
import general.DataArray;
import general.Index;
import general.Values;
import training.HyperParameters;

public class ChessNeuralNetwork extends NeuralNetwork {

	/*
	 * In this architecture, it's always at white to play
	 */

	@Override
	public double computeLossFunction(DataArray finalOutput, DataArray expectedOutput) {
		return HyperParameters.computeLossFunction(finalOutput, expectedOutput);
	}

	@Override
	public double getPDOLFWRtoOutputNeuron(Index outputNeuronIndex,
			DataArray finalOutput, DataArray expectedOutput) {
		return HyperParameters.getPartialDerivativeOf_LOSS_FUNCTION_withRespectTo_OUTPUT_NEURON(outputNeuronIndex,
				finalOutput, expectedOutput);
	}

	// randoms weights
	public ChessNeuralNetwork() {
		super(HyperParameters.getLayersforCNN());
		updateBatchNormalizationLayers(5, 10);
	}

	public ChessNeuralNetwork(Layer[] layers) {
		super(layers);
	}

	// return a data array of size one
	// inverts the board if it is at black to move
	public static DataArray convertBoardIntoDataArray(Board b) {
		Board board;
		if (b.colorToMove == Values.WHITE) {
			board = b.clone();
		} else {
			board = chess_rules.Util.invertBoardColor(b);
		}
		DataArray dataArray = new DataArray(new int[][] { new int[] { 15, Values.BOARD_SIZE, Values.BOARD_SIZE } });
		int planeIndex = 0;

		for (int cl = 0; cl < 1; cl++) {
			int color;
			if (cl == 0) {
				color = Values.WHITE;
			} else {
				color = Values.BLACK;
			}
			for (int pieceIndex = 0; pieceIndex < 6; pieceIndex++) {
				int pieceType = Values.ALL_PIECE_TYPES[pieceIndex];
				ArrayList<Square> squares = Util.whereAre(board.pieces, pieceType, color);
				for (int squareIndex = 0; squareIndex < squares.size(); squareIndex++) {
					dataArray.set(0,
							new int[] { planeIndex, squares.get(squareIndex).line, squares.get(squareIndex).column },
							1);
				}
				planeIndex++;
			}
		}

		if (board.columnWhereAPawnMovedTwice != Values.NOTHING) {
			for (int l = 0; l < Values.BOARD_SIZE; l++) {
				dataArray.set(0, new int[] { 12, l, board.columnWhereAPawnMovedTwice }, 1);
			}
		}
		for (int cstl = 0; cstl < 1; cstl++) {
			if (board.castleAllowed[0][cstl]) {
				for (int l = 0; l < Values.BOARD_SIZE; l++) {
					for (int c = 0; c < Values.BOARD_SIZE; c++) {
						dataArray.set(0, new int[] { 13 + cstl, l, c }, 1);
					}
				}
			}
		}
		return dataArray;
	}

	public void updateBatchNormalizationLayers(int numberOfBatches, int numberOfBoardsPerBatch) {

		Board board = new Board();
		board.init();
		System.out.print("\nupdating batch normalition layers : (on " + numberOfBatches + ") : ");

		for (int e = 0; e < numberOfBatches; e++) {
			DataArray[][] neuronsValues = new DataArray[numberOfBoardsPerBatch][];

			for (int h = 0; h < numberOfBoardsPerBatch; h++) {

				DataArray input = ChessNeuralNetwork.convertBoardIntoDataArray(board);
				neuronsValues[h] = compute(input);

				if (board.gameState == Values.GAME_RUNNING) {

					ArrayList<Move> legalMoves = Rules.getLegalMoves(board);

					Move move = legalMoves.get(Values.random.nextInt(legalMoves.size()));

					board = Rules.play(board, move);
				} else {
					board.init();
				}
			}

			updateBatchNormalizationLayers(neuronsValues);

			System.out.print((e + 1) + " ");
		}
		System.out.println();
	}

	// output must contains 2 pieces of data : the first contains the estimated
	// probabilities for each move and the second contains [v] with v in [-1, 1]
	// represents how white is estimated to win

	// the policy contains all the legal moves, even if there is a (prior)
	// probability
	public static Policy getPolicyFromOutput(DataArray output, Board b) {
		Board board;

		if (b.colorToMove == Values.WHITE) {
			board = b.clone();
		} else {
			board = Util.invertBoardColor(b);
		}
		ArrayList<Move> legalMoves = Rules.getLegalMoves(board);

		Policy result = new Policy();
		result.v = output.getDouble(1, new int[] { 0 });
		result.moves = new ArrayList<Move>();
		result.probabilities = new ArrayList<Double>();

		for (int moveIndex = 0; moveIndex < legalMoves.size(); moveIndex++) {
			Move m = legalMoves.get(moveIndex);
			result.moves.add(m);

			double proba = output.getDouble(0, getMoveAdress(m, board));

			result.probabilities.add(proba);
		}

		if (b.colorToMove == Values.BLACK) {
			result.moves = Util.invertMovesColor(result.moves);
			result.v = -result.v;
		}

		result.resizeProbabilitiesForASummOfOne();
		return result;
	}

	// planes (example with a board size of ) :
	//
	// queen move direction 0 range 1, queen move direction 0 range 2, ... queen
	// move direction 0 range 7 ,
	// queen move direction 1 range 1, queen move direction 1 range 2, ... queen
	// move direction 1 range 7 ,
	// ...
	// queen move direction 7 range 1, queen move direction 7 range 2, ... queen
	// move direction 7 range 7 ,

	// knight move direction 0, knight move direction 1, ... , knight move direction
	// 7

	// promotion left to rook, promotion left to knight, promotion left to bishop,
	// promotion "in front" to rook, promotion "in front" to knight, promotion "in
	// front" to bishop,
	// promotion right to rook, promotion right to knight, promotion right to
	// bishop,

	private static int[] getMoveAdress(Move m, Board board) {
		if (board.colorToMove != Values.WHITE) {
			System.out.println(1 / 0);
		}

		Move move = m.copy();

		int id = 0;

		if (move.type == Values.PROMOTE && move.pieceTypeForPromotion != Values.QUEEN) {
			switch (move.arrivalSquare.column - move.previousSquare.column) {
				case -1:
					id = (Values.BOARD_SIZE - 1) * 8 + 8;
					break;
				case 0:
					id = (Values.BOARD_SIZE - 1) * 8 + 8 + 3;
					break;
				case 1:
					id = (Values.BOARD_SIZE - 1) * 8 + 8 + 6;
					break;
				default:
					System.out.println("error 8539108325");
					break;
			}

			switch (move.pieceTypeForPromotion) {
				case Values.ROOK:
					id += 0;
					break;
				case Values.KNIGHT:
					id += 1;
					break;
				case Values.BISHOP:
					id += 2;
					break;
			}

		} else {
			if (move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.WHITE_QUEEN_SIDE_CASTLE) {
				Square previousSquare = Util.whereAre(board.pieces, Values.KING, Values.WHITE).get(0);
				Square arrivalSquare;
				if (move.type == Values.WHITE_KING_SIDE_CASTLE) {
					arrivalSquare = new Square(previousSquare.line, previousSquare.column + 2);
				} else {
					arrivalSquare = new Square(previousSquare.line, previousSquare.column - 2);
				}

				move = new Move(Values.SHIFT_OR_CAPTURE, previousSquare, arrivalSquare);
			}

			int diffLine = move.arrivalSquare.line - move.previousSquare.line;
			int diffColumn = move.arrivalSquare.column - move.previousSquare.column;

			int gcd = BigInteger.valueOf(diffLine).gcd(BigInteger.valueOf(diffColumn)).intValue();

			for (int u = 0; u < Values.QUEEN_MOVE_DIRECTIONS.length; u++) {
				if (diffLine / gcd == Values.QUEEN_MOVE_DIRECTIONS[u][0]
						&& diffColumn / gcd == Values.QUEEN_MOVE_DIRECTIONS[u][1]) {
					id = u * (Values.BOARD_SIZE - 1);
					id += (gcd - 1);

				}
			}

			for (int u = 0; u < Values.KNIGHT_MOVE_DIRECTIONS.length; u++) {
				if (diffLine == Values.KNIGHT_MOVE_DIRECTIONS[u][0]
						&& diffColumn == Values.KNIGHT_MOVE_DIRECTIONS[u][1]) {
					id = ((Values.BOARD_SIZE - 1) * 8 + u);
				}
			}

		}

		return new int[] { id, move.previousSquare.line, move.previousSquare.column };

	}

	public static DataArray getOutputVectorFromPolicy(Policy p, Board b) {

		Board reOrientedBoard;
		Policy reOrientedPolicy = new Policy();
		reOrientedPolicy.probabilities = (ArrayList<Double>) p.probabilities.clone();

		if (b.colorToMove == Values.WHITE) {

			reOrientedBoard = b.clone();
			reOrientedPolicy.v = p.v;
			reOrientedPolicy.moves = (ArrayList<Move>) p.moves.clone();
		} else {

			reOrientedBoard = Util.invertBoardColor(b);
			reOrientedPolicy.v = -p.v;
			reOrientedPolicy.moves = Util.invertMovesColor(p.moves);

		}

		int dataCount = 8 * (Values.BOARD_SIZE - 1) + 8 + 3 * 3;
		DataArray outputVector = new DataArray(
				new int[][] { new int[] { dataCount, Values.BOARD_SIZE, Values.BOARD_SIZE }, new int[] { 1 } });
		outputVector.set(1, new int[] { 0 }, reOrientedPolicy.v);

		for (int moveIndex = 0; moveIndex < reOrientedPolicy.moves.size(); moveIndex++) {
			Move move = reOrientedPolicy.moves.get(moveIndex).copy();
			double probability = reOrientedPolicy.probabilities.get(moveIndex);
			int[] moveAdress = getMoveAdress(move, reOrientedBoard);

			outputVector.set(0, moveAdress, probability);

		}

		return outputVector;
	}

	public void storeInFile(int savingIndex) {
		String s = general.Util.saveLayerArrayToString(layers);
		general.Util.writeStringInFile(Values.PREVIOUS_NAME_FOR_LAYERS + savingIndex, s);
	}

	public ChessNeuralNetwork(int savingIndex) {
		super(general.Util.loadLayerArrayFromString(
				general.Util.readStringFromFile(Values.PREVIOUS_NAME_FOR_LAYERS + savingIndex)));

	}

}