package chess_rules;

import general.Values;

public class Board {

	/*
	 * Piece [ line ] [ column ] We begin to count at the BOTTOM LEFT CORNER
	 */
	public Piece[][] pieces;

	// castleAllowed[0] [ ... ] --> WHITE
	// castleAllowed[1] [ ... ] --> BLACK

	// castleAllowed[ ... ] [ 0 ] --> QUEEN SIDE
	// castleAllowed[ ... ] [ 1 ] --> KING SIDE
	public boolean[][] castleAllowed;

	public int gameState;
	public int colorToMove;
	public int columnWhereAPawnMovedTwice;
	public int noProgressCount;

	public Board() {
	}

	public Board clone() {
		Board b = new Board();
		b.gameState = gameState;
		b.colorToMove = colorToMove;
		b.columnWhereAPawnMovedTwice = columnWhereAPawnMovedTwice;
		b.noProgressCount = noProgressCount;
		b.castleAllowed = new boolean[2][2];
		for (int l = 0; l < 2; l++) {
			for (int c = 0; c < 2; c++) {
				b.castleAllowed[l][c] = castleAllowed[l][c];
			}
		}

		b.pieces = new Piece[Values.BOARD_SIZE][Values.BOARD_SIZE];
		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				b.pieces[l][c] = new Piece(pieces[l][c].type, pieces[l][c].color);
			}
		}

		return b;
	}

	public String toString() {
		String s = "";

		for (int l = Values.BOARD_SIZE - 1; l >= 0; l--) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				s = s + "[" + pieces[l][c].toUnicode() + "]";
			}
			s = s + "\n";
		}

		return s;
	}

	// do not consider no progress count
	public boolean equals(Board board) {
		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				if (!pieces[l][c].equals(board.pieces[l][c])) {
					return false;
				}
			}
		}

		if (castleAllowed[0][0] != board.castleAllowed[0][0] || castleAllowed[0][1] != board.castleAllowed[0][1]
				|| castleAllowed[1][0] != board.castleAllowed[1][0] || castleAllowed[1][1] != board.castleAllowed[1][1]
				|| gameState != board.gameState || columnWhereAPawnMovedTwice != board.columnWhereAPawnMovedTwice
				|| colorToMove != board.colorToMove) {
			return false;
		}

		return true;
	}

	public void init() {

		gameState = Values.GAME_RUNNING;
		colorToMove = Values.WHITE;
		columnWhereAPawnMovedTwice = Values.NOTHING;
		noProgressCount = 0;

		pieces = new Piece[Values.BOARD_SIZE][];
		for (int l = 0; l < Values.BOARD_SIZE; l++) {
			pieces[l] = new Piece[Values.BOARD_SIZE];
			for (int c = 0; c < Values.BOARD_SIZE; c++) {
				pieces[l][c] = new Piece(Values.EMPTY, Values.NO_COLOR);
			}
		}

		switch (Values.BOARD_CONFIGURATION) {
		case Values.CLASSICAL_BOARD:
			castleAllowed = new boolean[][] { new boolean[] { true, true }, new boolean[] { true, true } };

			for (int c = 0; c < 8; c++) {
				pieces[1][c] = new Piece(Values.PAWN, Values.WHITE);
				pieces[6][c] = new Piece(Values.PAWN, Values.BLACK);
			}

			pieces[0][0] = new Piece(Values.ROOK, Values.WHITE);
			pieces[0][7] = new Piece(Values.ROOK, Values.WHITE);
			pieces[7][0] = new Piece(Values.ROOK, Values.BLACK);
			pieces[7][7] = new Piece(Values.ROOK, Values.BLACK);

			pieces[0][1] = new Piece(Values.KNIGHT, Values.WHITE);
			pieces[0][6] = new Piece(Values.KNIGHT, Values.WHITE);
			pieces[7][1] = new Piece(Values.KNIGHT, Values.BLACK);
			pieces[7][6] = new Piece(Values.KNIGHT, Values.BLACK);

			pieces[0][2] = new Piece(Values.BISHOP, Values.WHITE);
			pieces[0][5] = new Piece(Values.BISHOP, Values.WHITE);
			pieces[7][2] = new Piece(Values.BISHOP, Values.BLACK);
			pieces[7][5] = new Piece(Values.BISHOP, Values.BLACK);

			pieces[0][3] = new Piece(Values.QUEEN, Values.WHITE);
			pieces[7][3] = new Piece(Values.QUEEN, Values.BLACK);

			pieces[0][4] = new Piece(Values.KING, Values.WHITE);
			pieces[7][4] = new Piece(Values.KING, Values.BLACK);

			break;

		case Values.DEBUG_8_TIMES_8:
			castleAllowed = new boolean[][] { new boolean[] { true, true }, new boolean[] { false, false } };

			pieces[0][0] = new Piece(Values.ROOK, Values.WHITE);
			pieces[0][7] = new Piece(Values.ROOK, Values.WHITE);
			pieces[7][0] = new Piece(Values.EMPTY, Values.NO_COLOR);
			pieces[7][7] = new Piece(Values.EMPTY, Values.NO_COLOR);

			pieces[0][1] = new Piece(Values.KNIGHT, Values.WHITE);
			pieces[0][6] = new Piece(Values.KNIGHT, Values.WHITE);
			pieces[7][1] = new Piece(Values.EMPTY, Values.NO_COLOR);
			pieces[7][6] = new Piece(Values.EMPTY, Values.NO_COLOR);

			pieces[0][2] = new Piece(Values.BISHOP, Values.WHITE);
			pieces[0][5] = new Piece(Values.BISHOP, Values.WHITE);
			pieces[7][2] = new Piece(Values.EMPTY, Values.NO_COLOR);
			pieces[7][5] = new Piece(Values.EMPTY, Values.NO_COLOR);

			pieces[0][3] = new Piece(Values.QUEEN, Values.WHITE);
			pieces[7][3] = new Piece(Values.EMPTY, Values.NO_COLOR);

			pieces[0][4] = new Piece(Values.KING, Values.WHITE);
			pieces[7][4] = new Piece(Values.KING, Values.BLACK);

			pieces[2][3] = new Piece(Values.QUEEN, Values.WHITE);
			pieces[2][5] = new Piece(Values.QUEEN, Values.WHITE);

			break;

		case Values.DEBUG_6_TIMES_6:
			castleAllowed = new boolean[][] { new boolean[] { false, false }, new boolean[] { false, false } };

			for (int c = 0; c < 6; c++) {
				pieces[1][c] = new Piece(Values.PAWN, Values.WHITE);
				pieces[4][c] = new Piece(Values.PAWN, Values.BLACK);
			}

			pieces[0][0] = new Piece(Values.ROOK, Values.WHITE);
			pieces[0][5] = new Piece(Values.ROOK, Values.WHITE);
			pieces[5][0] = new Piece(Values.ROOK, Values.BLACK);
			pieces[5][5] = new Piece(Values.ROOK, Values.BLACK);

			pieces[0][1] = new Piece(Values.KNIGHT, Values.WHITE);
			pieces[5][1] = new Piece(Values.KNIGHT, Values.BLACK);

			pieces[0][4] = new Piece(Values.BISHOP, Values.WHITE);
			pieces[5][4] = new Piece(Values.BISHOP, Values.BLACK);

			pieces[0][2] = new Piece(Values.QUEEN, Values.WHITE);
			pieces[5][2] = new Piece(Values.QUEEN, Values.BLACK);

			pieces[0][3] = new Piece(Values.KING, Values.WHITE);
			pieces[5][3] = new Piece(Values.KING, Values.BLACK);

			break;

		case Values.DEBUG_5_TIMES_5:
			castleAllowed = new boolean[][] { new boolean[] { false, false }, new boolean[] { false, false } };

			pieces[1][0] = new Piece(Values.PAWN, Values.WHITE);
			pieces[1][1] = new Piece(Values.PAWN, Values.WHITE);

			pieces[3][3] = new Piece(Values.PAWN, Values.BLACK);
			pieces[3][4] = new Piece(Values.PAWN, Values.BLACK);

			pieces[0][0] = new Piece(Values.KING, Values.WHITE);
			pieces[4][4] = new Piece(Values.KING, Values.BLACK);

			pieces[0][1] = new Piece(Values.QUEEN, Values.WHITE);
			pieces[4][3] = new Piece(Values.QUEEN, Values.BLACK);

			break;

		}

	}

}