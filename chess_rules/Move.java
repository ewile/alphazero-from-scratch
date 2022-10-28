package chess_rules;

import general.Values;

public class Move {

	public int type = Values.NOTHING;
	public Square previousSquare;
	public Square arrivalSquare;
	public int pieceTypeForPromotion;

	public Move(int t) {
		type = t;
		previousSquare = null;
		arrivalSquare = null;
		pieceTypeForPromotion = Values.NOTHING;
	}

	public void setPromotionPiece(int pieceType) {
		pieceTypeForPromotion = pieceType;
	}

	public Move(int t, Square previous, Square arrival) {
		type = t;
		previousSquare = previous;
		arrivalSquare = arrival;
	}

	public String toString() {
		String str = "";
		switch (type) {
			case Values.BLACK_KING_SIDE_CASTLE:
				str = "BLACK_KING_SIDE_CASTLE";
				break;
			case Values.WHITE_KING_SIDE_CASTLE:
				str = "WHITE_KING_SIDE_CASTLE";
				break;
			case Values.BLACK_QUEEN_SIDE_CASTLE:
				str = "BLACK_QUEEN_SIDE_CASTLE";
				break;
			case Values.WHITE_QUEEN_SIDE_CASTLE:
				str = "WHITE_QUEEN_SIDE_CASTLE";
				break;
			case Values.SHIFT_OR_CAPTURE:
				str = "MOVE : " + previousSquare + " to " + arrivalSquare;
				break;
			case Values.PROMOTE:
				str = "PROMOTE";
				break;

		}

		return str;

	}

	public Move copy() {
		Move m = new Move(type, previousSquare, arrivalSquare);
		m.pieceTypeForPromotion = pieceTypeForPromotion;
		return m;
	}

	public boolean equals(Move m) {
		if (m.type == Values.PROMOTE) {
			if (type != Values.PROMOTE) {
				return false;
			}
			return previousSquare.equals(m.previousSquare) && arrivalSquare.equals(m.arrivalSquare)
					&& pieceTypeForPromotion == m.pieceTypeForPromotion;
		}
		if (m.type == Values.SHIFT_OR_CAPTURE) {
			if (type != Values.SHIFT_OR_CAPTURE) {
				return false;
			}
			return previousSquare.equals(m.previousSquare)
					&& arrivalSquare.equals(m.arrivalSquare);
		}

		return m.type == type;
	}

}