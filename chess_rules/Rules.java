package chess_rules;

import java.util.ArrayList;

import general.Values;

public class Rules {

	public static Board play(Board b, Move move) {
		Board board = b.clone();
		if (move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.BLACK_KING_SIDE_CASTLE
				|| move.type == Values.WHITE_QUEEN_SIDE_CASTLE || move.type == Values.BLACK_QUEEN_SIDE_CASTLE
				|| board.pieces[move.previousSquare.line][move.previousSquare.column].type == Values.PAWN
				|| board.pieces[move.arrivalSquare.line][move.arrivalSquare.column].type != Values.EMPTY) {

			board.noProgressCount = 0;
		} else {
			board.noProgressCount++;
		}

		if (move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.WHITE_QUEEN_SIDE_CASTLE) {
			board.castleAllowed[0] = new boolean[] { false, false };
		}
		if (move.type == Values.BLACK_KING_SIDE_CASTLE || move.type == Values.BLACK_QUEEN_SIDE_CASTLE) {
			board.castleAllowed[1] = new boolean[] { false, false };
		}

		if (!(move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.BLACK_KING_SIDE_CASTLE
				|| move.type == Values.WHITE_QUEEN_SIDE_CASTLE || move.type == Values.BLACK_QUEEN_SIDE_CASTLE)) {

			Piece piece = board.pieces[move.previousSquare.line][move.previousSquare.column];

			if ((piece.type == Values.PAWN) && Math.abs(move.previousSquare.line - move.arrivalSquare.line) == 2) {
				board.columnWhereAPawnMovedTwice = move.previousSquare.column;
			} else {
				board.columnWhereAPawnMovedTwice = Values.NOTHING;
			}

			if (piece.type == Values.KING) {
				board.castleAllowed[b.colorToMove] = new boolean[] { false, false };

			}

			Square kingSquare = Util.whereAre(board.pieces, Values.KING, board.colorToMove).get(0);
			if (piece.type == Values.ROOK && move.previousSquare.column > kingSquare.column) {
				board.castleAllowed[piece.color][1] = false;
			}
			if (piece.type == Values.ROOK && move.previousSquare.column < kingSquare.column) {
				board.castleAllowed[piece.color][0] = false;
			}
		}

		board.pieces = movePiece(board.pieces, move);

		board.colorToMove = Util.invertColor(board.colorToMove);

		if (getLegalMoves(board).size() == 0) {

			board.colorToMove = Util.invertColor(board.colorToMove);

			if (!currentPlayerCannotEatOpponentsKing(board)) {
				if (board.colorToMove == Values.WHITE) {
					board.gameState = Values.WHITE_WON;
				} else {
					board.gameState = Values.BLACK_WON;
				}
			} else {
				// pat
				board.gameState = Values.DRAW;
			}

			board.colorToMove = Values.NOTHING;
		}

		if (board.noProgressCount >= Values.MAX_PROGRESS_COUNT) {
			board.gameState = Values.DRAW;
			board.colorToMove = Values.NOTHING;

		}

		return board;

	}

	public static ArrayList<Move> getLegalMoves(Board board) {
		Piece[][] pieces = board.pieces;
		int colorToMove = board.colorToMove;
		boolean[][] castleAllowed = board.castleAllowed;
		int columnWhereAPawnMovedTwice = board.columnWhereAPawnMovedTwice;

		ArrayList<Move> potentialMoves = new ArrayList<>();

		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				Piece piece = pieces[l][c];
				if (piece.type != Values.EMPTY && piece.color == colorToMove) {

					if (piece.type == Values.BISHOP || piece.type == Values.ROOK || piece.type == Values.QUEEN) {
						int[][] cn = null;
						switch (piece.type) {
							case Values.BISHOP:
								cn = Values.BISHOP_MOVE_DIRECTIONS;
								break;
							case Values.ROOK:
								cn = Values.ROOK_MOVE_DIRECTIONS;
								break;
							case Values.QUEEN:
								cn = Values.QUEEN_MOVE_DIRECTIONS;
								break;
						}

						for (int cn_counter = 0; cn_counter < cn.length; cn_counter++) {
							int range = 1;
							boolean run = true;
							while (new Square(l + cn[cn_counter][0] * range, c + cn[cn_counter][1] * range).exists()
									&& run) {
								Piece p = pieces[l + cn[cn_counter][0] * range][c + cn[cn_counter][1] * range];
								if (p.type != Values.EMPTY) {
									run = false;

								}
								if (p.type == Values.EMPTY || p.color == Util.invertColor(colorToMove)) {
									potentialMoves.add(new Move(Values.SHIFT_OR_CAPTURE, new Square(l, c),
											new Square(l + cn[cn_counter][0] * range, c + cn[cn_counter][1] * range)));

								}

								range++;
							}
						}

					}

					if (piece.type == Values.KING || piece.type == Values.KNIGHT) {
						int[][] cn = null;
						switch (piece.type) {
							case Values.KING:
								cn = Values.KING_MOVE_DIRECTIONS;
								break;
							case Values.KNIGHT:
								cn = Values.KNIGHT_MOVE_DIRECTIONS;
								break;
						}

						for (int cn_counter = 0; cn_counter < cn.length; cn_counter++) {
							if (new Square(l + cn[cn_counter][0], c + cn[cn_counter][1]).exists()) {
								Piece p = pieces[l + cn[cn_counter][0]][c + cn[cn_counter][1]];
								if (p.type == Values.EMPTY || p.color == Util.invertColor(colorToMove)) {
									potentialMoves.add(new Move(Values.SHIFT_OR_CAPTURE, new Square(l, c),
											new Square(l + cn[cn_counter][0], c + cn[cn_counter][1])));

								}

							}
						}

					}

					if (piece.type == Values.PAWN) {
						ArrayList<Move> pawnMoves = new ArrayList<>();

						// take left or right
						int[][] cn = null;
						if (colorToMove == Values.WHITE) {

							cn = new int[][] { new int[] { 1, -1 }, new int[] { 1, 1 } };
						} else {
							cn = new int[][] { new int[] { -1, -1 }, new int[] { -1, 1 } };
						}
						for (int cn_counter = 0; cn_counter < cn.length; cn_counter++) {
							if (new Square(l + cn[cn_counter][0], c + cn[cn_counter][1]).exists()
									&& pieces[l + cn[cn_counter][0]][c + cn[cn_counter][1]].type != Values.EMPTY
									&& pieces[l + cn[cn_counter][0]][c + cn[cn_counter][1]].color == Util
											.invertColor(colorToMove)) {
								pawnMoves.add(new Move(Values.SHIFT_OR_CAPTURE, new Square(l, c),
										new Square(l + cn[cn_counter][0], c + cn[cn_counter][1])));
							}
						}

						int lineForPriseEnPassant;
						int initialPawnLine;
						int lastLine;

						if (colorToMove == Values.WHITE) {
							initialPawnLine = 1;
							lastLine = Values.BOARD_SIZE - 1;
							lineForPriseEnPassant = Values.BOARD_SIZE - 4;
							cn = new int[][] { new int[] { 1, Values.NOTHING } };
						} else {
							initialPawnLine = Values.BOARD_SIZE - 2;
							lastLine = 0;
							lineForPriseEnPassant = 3;
							cn = new int[][] { new int[] { -1, Values.NOTHING } };
						}

						// move one square ahead
						if (pieces[l + cn[0][0]][c].type == Values.EMPTY) {
							pawnMoves.add(
									new Move(Values.SHIFT_OR_CAPTURE, new Square(l, c), new Square(l + cn[0][0], c)));

						}

						// move 2 squares ahead
						if (l == initialPawnLine && pieces[l + 2 * cn[0][0]][c].type == Values.EMPTY
								&& pieces[l + cn[0][0]][c].type == Values.EMPTY) {
							pawnMoves.add(new Move(Values.SHIFT_OR_CAPTURE, new Square(l, c),
									new Square(l + 2 * cn[0][0], c)));
						}

						// prise en passant
						if (Math.abs(columnWhereAPawnMovedTwice - c) == 1 && l == lineForPriseEnPassant) {
							pawnMoves.add(new Move(Values.SHIFT_OR_CAPTURE, new Square(l, c),
									new Square(l + cn[0][0], columnWhereAPawnMovedTwice)));

						}

						// promotions
						for (int i = 0; i < pawnMoves.size(); i++) {
							Move pawnMove = pawnMoves.get(i);
							if (pawnMove.arrivalSquare.line == lastLine) {
								for (int z = 0; z < Values.PROMOTION_PIECE_TYPES.length; z++) {
									Move m = new Move(Values.PROMOTE, pawnMove.previousSquare, pawnMove.arrivalSquare);
									m.setPromotionPiece(Values.PROMOTION_PIECE_TYPES[z]);
									potentialMoves.add(m);
								}
							} else {
								potentialMoves.add(pawnMove);

							}
						}
					}
				}
			}
		}

		ArrayList<Move> realMoves = new ArrayList<>();

		for (int i = 0; i < potentialMoves.size(); i++) {
			Move potentialMove = potentialMoves.get(i);
			Board newBoard = board.clone();
			newBoard.colorToMove = Util.invertColor(colorToMove);
			newBoard.pieces = movePiece(newBoard.pieces, potentialMove);

			if (currentPlayerCannotEatOpponentsKing(newBoard)) {
				realMoves.add(potentialMove);
			}
		}

		Square kingSquare = Util.whereAre(pieces, Values.KING, colorToMove).get(0);

		int directions[] = new int[] { -1, 1 };

		for (int dirIndex = 0; dirIndex < 2; dirIndex++) {

			boolean canCastle = castleAllowed[colorToMove][dirIndex];
			int rookColumn = Values.NOTHING;

			for (int q = kingSquare.column + directions[dirIndex]; q < Values.BOARD_SIZE && q >= 0
					&& rookColumn == Values.NOTHING; q += directions[dirIndex]) {
				if (pieces[kingSquare.line][q].type == Values.ROOK && pieces[kingSquare.line][q].color == colorToMove) {
					rookColumn = q;
				}
			}
			canCastle = canCastle && rookColumn != Values.NOTHING;

			boolean emptySpace = true;

			for (int q = kingSquare.column + directions[dirIndex]; Math.abs(rookColumn - q) > 0 && emptySpace
					&& canCastle; q += directions[dirIndex]) {
				if (pieces[kingSquare.line][q].type != Values.EMPTY) {
					emptySpace = false;
				}
			}
			canCastle = canCastle && emptySpace;

			boolean noChecksCrossed = true;

			for (int q = kingSquare.column; Math.abs(kingSquare.column - q) <= 2 && noChecksCrossed
					&& canCastle; q += directions[dirIndex]) {

				pieces[kingSquare.line][kingSquare.column].setEmpty();

				pieces[kingSquare.line][q] = new Piece(Values.KING, colorToMove);
				board.colorToMove = Util.invertColor(board.colorToMove);

				if (!currentPlayerCannotEatOpponentsKing(board)) {
					noChecksCrossed = false;
				}
				board.colorToMove = Util.invertColor(board.colorToMove);

				pieces[kingSquare.line][q].setEmpty();

				pieces[kingSquare.line][kingSquare.column] = new Piece(Values.KING, colorToMove);

			}
			canCastle = canCastle && noChecksCrossed;

			if (canCastle) {
				if (colorToMove == Values.WHITE) {
					if (dirIndex == 0) {
						if (castleAllowed[0][0]) {
							realMoves.add(new Move(Values.WHITE_QUEEN_SIDE_CASTLE));
						}
					} else {
						if (castleAllowed[0][1]) {
							realMoves.add(new Move(Values.WHITE_KING_SIDE_CASTLE));
						}
					}
				} else {
					if (dirIndex == 0) {
						if (castleAllowed[1][0]) {
							realMoves.add(new Move(Values.BLACK_QUEEN_SIDE_CASTLE));
						}
					} else {
						if (castleAllowed[1][1]) {
							realMoves.add(new Move(Values.BLACK_KING_SIDE_CASTLE));
						}

					}

				}

			}
		}

		return realMoves;
	}

	private static Piece[][] movePiece(Piece[][] pieces, Move move) {

		Piece[][] newPieces = pieces.clone();

		if (move.type == Values.SHIFT_OR_CAPTURE) {

			Piece movingPiece = newPieces[move.previousSquare.line][move.previousSquare.column].clone();

			if (movingPiece.type == Values.PAWN && move.arrivalSquare.column != move.previousSquare.column
					&& newPieces[move.arrivalSquare.line][move.arrivalSquare.column].type == Values.EMPTY) {
				// "prise en passant"
				newPieces[move.previousSquare.line][move.arrivalSquare.column].setEmpty();

			}

			newPieces[move.previousSquare.line][move.previousSquare.column].setEmpty();

			newPieces[move.arrivalSquare.line][move.arrivalSquare.column] = movingPiece.clone();

		}

		if (move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.BLACK_KING_SIDE_CASTLE
				|| move.type == Values.WHITE_QUEEN_SIDE_CASTLE || move.type == Values.BLACK_QUEEN_SIDE_CASTLE) {

			Square kingSquare;

			if (move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.WHITE_QUEEN_SIDE_CASTLE) {
				kingSquare = Util.whereAre(newPieces, Values.KING, Values.WHITE).get(0);
			} else {
				kingSquare = Util.whereAre(newPieces, Values.KING, Values.BLACK).get(0);
			}

			int direction;

			if (move.type == Values.WHITE_KING_SIDE_CASTLE || move.type == Values.BLACK_KING_SIDE_CASTLE) {
				direction = 1;
			} else {
				direction = -1;
			}

			int rookColumn = Values.NOTHING;

			for (int u = kingSquare.column; rookColumn == Values.NOTHING; u = u + direction) {
				if (pieces[kingSquare.line][u].type == Values.ROOK) {
					rookColumn = u;
				}
			}
			int colorCasting = pieces[kingSquare.line][kingSquare.column].color;

			newPieces[kingSquare.line][kingSquare.column].setEmpty();
			newPieces[kingSquare.line][rookColumn].setEmpty();

			newPieces[kingSquare.line][kingSquare.column + 2 * direction] = new Piece(Values.KING, colorCasting);
			newPieces[kingSquare.line][kingSquare.column + 1 * direction] = new Piece(Values.ROOK, colorCasting);
		}
		if (move.type == Values.PROMOTE) {
			newPieces[move.arrivalSquare.line][move.arrivalSquare.column] = new Piece(move.pieceTypeForPromotion,
					pieces[move.previousSquare.line][move.previousSquare.column].color);
			newPieces[move.previousSquare.line][move.previousSquare.column].setEmpty();
		}
		return newPieces;
	}

	private static boolean currentPlayerCannotEatOpponentsKing(Board board) {

		Piece[][] pieces = board.pieces;
		Square kingSquare = Util.whereAre(pieces, Values.KING, Util.invertColor(board.colorToMove)).get(0);
		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				if (pieces[l][c].color == board.colorToMove) {
					ArrayList<Square> edibleSquares = whereCouldAKingBeEaten(pieces, new Square(l, c));
					for (int i = 0; i < edibleSquares.size(); i++) {
						if (edibleSquares.get(i).equals(kingSquare)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private static ArrayList<Square> whereCouldAKingBeEaten(Piece[][] pieces, Square square) {

		int line = square.line;
		int column = square.column;

		Piece piece = pieces[line][column];
		int type = piece.type;
		int color = piece.color;

		ArrayList<Square> edibleSquares = new ArrayList<Square>();

		if (type == Values.PAWN || type == Values.KNIGHT || type == Values.KING) {
			int[][] cn = null;

			switch (type) {
				case Values.PAWN:
					if (color == Values.WHITE) {
						cn = new int[][] { new int[] { 1, -1 }, new int[] { 1, 1 } };
					} else {
						cn = new int[][] { new int[] { -1, -1 }, new int[] { -1, 1 } };

						cn[1] = new int[] { -1, 1 };
					}
					break;
				case Values.KNIGHT:
					cn = Values.KNIGHT_MOVE_DIRECTIONS;
					break;
				case Values.KING:
					cn = Values.KING_MOVE_DIRECTIONS;
					break;

			}

			for (int cn_counter = 0; cn_counter < cn.length; cn_counter++) {
				int l = line + cn[cn_counter][0];
				int c = column + cn[cn_counter][1];
				if (new Square(l, c).exists() && pieces[l][c].type != Values.EMPTY) {
					edibleSquares.add(new Square(l, c));
				}
			}
		}

		if (type == Values.ROOK || type == Values.BISHOP || type == Values.QUEEN) {
			int[][] cn = null;

			switch (type) {
				case Values.BISHOP:
					cn = Values.BISHOP_MOVE_DIRECTIONS;
					break;
				case Values.ROOK:
					cn = Values.ROOK_MOVE_DIRECTIONS;
					break;
				case Values.QUEEN:
					cn = Values.QUEEN_MOVE_DIRECTIONS;
					break;

			}

			for (int cn_counter = 0; cn_counter < cn.length; cn_counter++) {
				int i = 1;
				boolean run = true;
				while (new Square(line + cn[cn_counter][0] * i, column + cn[cn_counter][1] * i).exists() && run) {
					Piece p = pieces[line + cn[cn_counter][0] * i][column + cn[cn_counter][1] * i];
					if (p.type != Values.EMPTY) {
						run = false;
						edibleSquares.add(new Square(line + cn[cn_counter][0] * i, column + cn[cn_counter][1] * i));

					}

					i++;
				}
			}
		}

		return edibleSquares;

	}

}