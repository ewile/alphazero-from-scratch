package chess_rules;

import java.util.ArrayList;

import general.Values;

public class Debug {
	public static String getRandomGamePGN() {

		String str = "";
		Board board = new Board();
		board.init();

		int h = 1;

		while (board.gameState == Values.GAME_RUNNING) {

			ArrayList<Move> legalMoves = Rules.getLegalMoves(board);

			int u = Values.random.nextInt(legalMoves.size());
			Move move = legalMoves.get(u);

			if (board.colorToMove == Values.WHITE) {
				str = str + " " + h + ".";
			} else {
				h++;
			}
			str = str + " " + chess_rules.Util.getPGNForMove(board, move);
			board = Rules.play(board, move);
		}
		System.out.println(Values.toString(board.gameState) + " " + h + " moves");
		return str;
	}

}
