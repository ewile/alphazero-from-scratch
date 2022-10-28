package chess_rules;

import java.util.ArrayList;

import general.Values;

public class Util {

	public static Board invertBoardColor(Board board) {
		Board b = new Board();
		switch (board.gameState) {
			case Values.WHITE_WON:
				b.gameState = Values.BLACK_WON;
				break;
			case Values.BLACK_WON:
				b.gameState = Values.WHITE_WON;
				break;
			default:
				b.gameState = board.gameState;
				break;
		}
		if (board.columnWhereAPawnMovedTwice == Values.NOTHING) {
			b.columnWhereAPawnMovedTwice = Values.NOTHING;
		} else {
			b.columnWhereAPawnMovedTwice = Values.BOARD_SIZE - 1 - board.columnWhereAPawnMovedTwice;
		}
		b.colorToMove = Util.invertColor(board.colorToMove);
		b.noProgressCount = board.noProgressCount;
		b.castleAllowed = new boolean[2][2];
		b.castleAllowed[0][0] = board.castleAllowed[1][1];
		b.castleAllowed[0][1] = board.castleAllowed[1][0];
		b.castleAllowed[1][0] = board.castleAllowed[0][1];
		b.castleAllowed[1][1] = board.castleAllowed[0][0];

		b.pieces = new Piece[Values.BOARD_SIZE][Values.BOARD_SIZE];

		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				Piece p = board.pieces[Values.BOARD_SIZE - 1 - l][Values.BOARD_SIZE - 1 - c];
				b.pieces[l][c] = new Piece(p.type, invertColor(p.color));
			}
		}

		return b;

	}

	public static ArrayList<Move> invertMovesColor(ArrayList<Move> moves) {
		ArrayList<Move> invertedMoves = new ArrayList<Move>();
		for (int i = 0; i < moves.size(); i++) {
			invertedMoves.add(invertMoveColor(moves.get(i)));
		}
		return invertedMoves;
	}

	public static Move invertMoveColor(Move move) {
		if (move.type == Values.WHITE_KING_SIDE_CASTLE) {
			return new Move(Values.BLACK_QUEEN_SIDE_CASTLE);
		}
		if (move.type == Values.WHITE_QUEEN_SIDE_CASTLE) {
			return new Move(Values.BLACK_KING_SIDE_CASTLE);
		}
		if (move.type == Values.BLACK_KING_SIDE_CASTLE) {
			return new Move(Values.WHITE_QUEEN_SIDE_CASTLE);
		}
		if (move.type == Values.BLACK_QUEEN_SIDE_CASTLE) {
			return new Move(Values.WHITE_KING_SIDE_CASTLE);
		}
		Square newPreviousSquare = new Square(Values.BOARD_SIZE - 1 - move.previousSquare.line,
				Values.BOARD_SIZE - 1 - move.previousSquare.column);
		Square newArrivalSquare = new Square(Values.BOARD_SIZE - 1 - move.arrivalSquare.line,
				Values.BOARD_SIZE - 1 - move.arrivalSquare.column);

		Move invertedMove = new Move(move.type, newPreviousSquare, newArrivalSquare);
		invertedMove.pieceTypeForPromotion = move.pieceTypeForPromotion;
		return invertedMove;
	}

	public static String getPGNForMove(Board board, Move move) {

		// do not put + when there is a check

		String columNames[] = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "e", "f", "g", "h", "i", "j" };
		String str = "";
		switch (move.type) {
			case Values.WHITE_QUEEN_SIDE_CASTLE:
				str = "O-O-O";
				break;
			case Values.BLACK_QUEEN_SIDE_CASTLE:
				str = "O-O-O";
				break;
			case Values.WHITE_KING_SIDE_CASTLE:
				str = "O-O";
				break;
			case Values.BLACK_KING_SIDE_CASTLE:
				str = "O-O";
				break;
			case Values.PROMOTE:
				if (move.arrivalSquare.column != move.previousSquare.column) {
					str = columNames[move.previousSquare.column] + "x" + columNames[move.arrivalSquare.column]
							+ (move.arrivalSquare.line + 1);
				} else {
					str = columNames[move.arrivalSquare.column] + (move.arrivalSquare.line + 1);
				}
				switch (move.pieceTypeForPromotion) {
					case Values.BISHOP:
						str = str + "=B";
						break;
					case Values.ROOK:
						str = str + "=R";
						break;
					case Values.KNIGHT:
						str = str + "=N";
						break;
					case Values.QUEEN:
						str = str + "=Q";
						break;

				}
				break;
			case Values.SHIFT_OR_CAPTURE:

				switch (board.pieces[move.previousSquare.line][move.previousSquare.column].type) {
					case Values.BISHOP:
						str = "B";
						break;
					case Values.KNIGHT:
						str = "N";
						break;
					case Values.ROOK:
						str = "R";
						break;
					case Values.QUEEN:
						str = "Q";
						break;
					case Values.KING:
						str = "K";
						break;
					case Values.PAWN:
						if (move.arrivalSquare.column != move.previousSquare.column) {
							str = columNames[move.previousSquare.column];
						}

						break;
				}

				if (board.pieces[move.previousSquare.line][move.previousSquare.column].type == Values.BISHOP
						|| board.pieces[move.previousSquare.line][move.previousSquare.column].type == Values.KNIGHT
						|| board.pieces[move.previousSquare.line][move.previousSquare.column].type == Values.ROOK
						|| board.pieces[move.previousSquare.line][move.previousSquare.column].type == Values.QUEEN) {
					str = str + columNames[move.previousSquare.column] + (move.previousSquare.line + 1);
				}

				if (board.pieces[move.arrivalSquare.line][move.arrivalSquare.column].type != Values.EMPTY
						|| (board.pieces[move.previousSquare.line][move.previousSquare.column].type == Values.PAWN
								&& move.arrivalSquare.column != move.previousSquare.column)) {
					str = str + "x";

				}

				str = str + columNames[move.arrivalSquare.column] + (move.arrivalSquare.line + 1);

				break;

			default:
				break;
		}

		return str;
	}

	public static Move getMoveWithClicks(Board board, Square clickedSquare1, Square clickedSquare2) {

		Piece[][] pieces = board.pieces;

		Piece piece1 = pieces[clickedSquare1.line][clickedSquare1.column];
		Piece piece2 = pieces[clickedSquare2.line][clickedSquare2.column];

		int lastLine;
		if (piece1.color == Values.WHITE) {
			lastLine = Values.BOARD_SIZE - 1;
		} else {
			lastLine = 0;
		}

		if (piece1.type == Values.PAWN && clickedSquare2.line == lastLine) {
			// PROMOTION
			// TO DO
			Move m = new Move(Values.PROMOTE, clickedSquare1, clickedSquare2);
			switch (Values.random.nextInt(4)) {
				case 0:
					m.setPromotionPiece(Values.QUEEN);
					break;
				case 1:
					m.setPromotionPiece(Values.BISHOP);
					break;
				case 2:
					m.setPromotionPiece(Values.ROOK);
					break;
				case 3:
					m.setPromotionPiece(Values.KNIGHT);
					break;
			}

			return m;

		}

		if (piece2.color == Values.NO_COLOR || piece1.color != piece2.color) {
			return new Move(Values.SHIFT_OR_CAPTURE, clickedSquare1, clickedSquare2);
		}

		// castling
		if (clickedSquare1.column < clickedSquare2.column) {
			if (piece1.color == Values.WHITE) {
				return new Move(Values.WHITE_KING_SIDE_CASTLE);
			} else {
				return new Move(Values.BLACK_KING_SIDE_CASTLE);

			}
		} else {
			if (piece1.color == Values.WHITE) {
				return new Move(Values.WHITE_QUEEN_SIDE_CASTLE);
			} else {
				return new Move(Values.BLACK_QUEEN_SIDE_CASTLE);

			}
		}

	}

	public static int invertColor(int color) {
		if (color == Values.WHITE) {
			return Values.BLACK;
		} else if (color == Values.BLACK) {
			return Values.WHITE;
		} else {
			return Values.NO_COLOR;
		}
	}

	public static ArrayList<Square> whereAre(Piece[][] pieces, int type, int color) {
		ArrayList<Square> squares = new ArrayList<Square>();
		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				if (pieces[l][c].type == type && pieces[l][c].color == color) {
					squares.add(new Square(l, c));
				}
			}
		}
		return squares;
	}
}
