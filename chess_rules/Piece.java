package chess_rules;

import general.Values;

public class Piece {

	public int type;
	public int color;

	public Piece(int type, int color) {
		this.type = type;
		this.color = color;
	}

	public boolean equals(Piece piece) {
		return type == piece.type && color == piece.color;
	}

	public Piece clone() {
		return new Piece(type, color);
	}

	public void setEmpty() {
		type = Values.EMPTY;
		color = Values.NO_COLOR;
	}

	public String toString() {
		String c = "";
		if (color == Values.WHITE) {
			c = "white ";
		} else {
			c = "black ";
		}
		switch (type) {
			case Values.PAWN:
				return c + "pawn";
			case Values.BISHOP:
				return c + "bishop";
			case Values.KING:
				return c + "king";
			case Values.KNIGHT:
				return c + "knight";
			case Values.QUEEN:
				return c + "queen";
			case Values.ROOK:
				return c + "rook";

		}
		return null;
	}

	public String toUnicode() {
		String code = "";
		if (color == Values.WHITE) {
			if (type == Values.KING) {
				code = "2654";
			}
			if (type == Values.QUEEN) {
				code = "2655";
			}
			if (type == Values.ROOK) {
				code = "2656";
			}
			if (type == Values.BISHOP) {
				code = "2657";
			}
			if (type == Values.KNIGHT) {
				code = "2658";
			}
			if (type == Values.PAWN) {
				code = "2659";
			}

		}
		if (color == Values.BLACK) {
			if (type == Values.KING) {
				code = "265A";
			}
			if (type == Values.QUEEN) {
				code = "265B";
			}
			if (type == Values.ROOK) {
				code = "265C";
			}
			if (type == Values.BISHOP) {
				code = "265D";
			}
			if (type == Values.KNIGHT) {
				code = "265E";
			}
			if (type == Values.PAWN) {
				code = "265F";
			}

		}
		if (type == Values.EMPTY) {
			return " ";
		}
		return String.valueOf((char) (Integer.parseInt(code, 16)));
	}
}
