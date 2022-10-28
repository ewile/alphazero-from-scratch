package chess_rules;

import general.Values;

public class Square {
	public int line; // the first line is 0
	public int column; // the first column is 0

	public Square(int l, int c) {
		line = l;
		column = c;
	}

	public boolean exists() {
		return 0 <= line && line <= Values.BOARD_SIZE - 1 && 0 <= column && column <= Values.BOARD_SIZE - 1;
	}

	public boolean equals(Square square) {
		return line == square.line && column == square.column;
	}

	public String toString() {
		return Values.alphabet[column] + (line + 1);
	}
}