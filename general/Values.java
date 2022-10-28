package general;

import java.util.Random;

public class Values {

	public static final int BOARD_SIZE = 5;

	public static final int NUMBER_OF_PLANES_IN_CNN_OUTPUT = 8 * (BOARD_SIZE - 1) + 8 + 9;

	public static final int BOARD_CONFIGURATION = Values.DEBUG_5_TIMES_5;

	public static final int CLASSICAL_BOARD = 847521;
	public static final int DEBUG_5_TIMES_5 = 847522;
	public static final int DEBUG_6_TIMES_6 = 847523;
	public static final int DEBUG_8_TIMES_8 = 847524;

	public static final int MAX_PROGRESS_COUNT = 15;

	public static final String MEMORY_LOCATION = "/home/emile/Documents/eclipse_util/";

	public static final String PREVIOUS_NAME_FOR_TEMP_CNN = "tempCNN";
	public static final String PREVIOUS_NAME_FOR_LAYERS = "layers";

	public static Random random = new Random();

	public static final String alphabet[] = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

	public static final int[] ALL_PIECE_TYPES = new int[] { Values.BISHOP, Values.KNIGHT, Values.QUEEN, Values.ROOK,
			Values.KING, Values.PAWN };

	public static final int[] PROMOTION_PIECE_TYPES = new int[] { Values.BISHOP, Values.KNIGHT, Values.QUEEN,
			Values.ROOK };

	public final static int[][] PAWN_MOVE_DIRECTIONS = new int[][] { new int[] { 1, -1 }, new int[] { 1, 0 },
			new int[] { 1, 1 } };

	public final static int[][] KNIGHT_MOVE_DIRECTIONS = new int[][] { new int[] { 2, 1 }, new int[] { 2, -1 },
			new int[] { -2, 1 }, new int[] { -2, -1 }, new int[] { 1, 2 }, new int[] { -1, 2 }, new int[] { 1, -2 },
			new int[] { -1, -2 } };

	public final static int[][] KING_MOVE_DIRECTIONS = new int[][] { new int[] { -1, -1 }, new int[] { -1, 0 },
			new int[] { -1, 1 }, new int[] { 0, -1 }, new int[] { 0, 1 }, new int[] { 1, -1 }, new int[] { 1, 0 },
			new int[] { 1, 1 } };

	public final static int[][] QUEEN_MOVE_DIRECTIONS = new int[][] { new int[] { 1, 0 }, new int[] { 1, 1 },
			new int[] { 0, 1 }, new int[] { -1, 1 }, new int[] { -1, 0 }, new int[] { -1, -1 }, new int[] { 0, -1 },
			new int[] { 1, -1 } };

	public final static int[][] BISHOP_MOVE_DIRECTIONS = new int[][] { new int[] { 1, 1 }, new int[] { 1, -1 },
			new int[] { -1, 1 }, new int[] { -1, -1 } };

	public final static int[][] ROOK_MOVE_DIRECTIONS = new int[][] { new int[] { 0, 1 }, new int[] { 0, -1 },
			new int[] { 1, 0 }, new int[] { -1, 0 } };

	public static final int NOTHING = -100000;

	public static final int LENGTH_OF_A_LINE_IN_TERMINAL = 80;
	public static final int PRECISION_FOR_DOUBLE_IN_TERMINAL = 7;

	// piece colors
	public static final int WHITE = 0; // NOT TO CHANGE
	public static final int BLACK = 1; // NOT TO CHANGE
	public static final int NO_COLOR = 3;

	// game states
	public static final int GAME_RUNNING = 4;
	public static final int WHITE_WON = 5;
	public static final int BLACK_WON = 6;
	public static final int DRAW = 7;

	// move types
	public static final int BLACK_KING_SIDE_CASTLE = 8;
	public static final int WHITE_KING_SIDE_CASTLE = 9;
	public static final int BLACK_QUEEN_SIDE_CASTLE = 10;
	public static final int WHITE_QUEEN_SIDE_CASTLE = 11;
	public static final int SHIFT_OR_CAPTURE = 12;
	public static final int PROMOTE = 13;

	// piece types
	public static final int EMPTY = 14;
	public static final int PAWN = 15;
	public static final int ROOK = 16;
	public static final int KNIGHT = 17;
	public static final int BISHOP = 18;
	public static final int QUEEN = 19;
	public static final int KING = 20;

	// layer types
	public static final int BATCH_NORMALIZATION_LAYER = 21;
	public static final int NON_REGULAR_CONVOLUTIONAL_LAYER = 22;
	public static final int NO_ACTION_LAYER = 23;
	public static final int RELU_LAYER = 24;
	public static final int SPLIT_LAYER = 25;
	public static final int DUPLICATE_LAYER = 26;
	public static final int DEAD_LAYER = 27;
	public static final int FULLY_CONNECTED_LAYER = 28;
	public static final int TANH_LAYER = 29;
	public static final int FUSION_LAYER = 30;
	public static final int PROBABILITY_DISTRIBUTION_LAYER = 31;
	public static final int CONVOLUTIONAL_LAYER = 32;

	// loss functions
	public static final int GOOGLE_LOSS_FUNCTION = 50;
	public static final int MEAN_SQUARE_LOSS_FUNCTION = 51;

	// pipe types
	public static final int INITIAL_PIPE = 70;
	public static final int FINAL_PIPE = 71;
	public static final int INTERMEDIATE_PIPE = 72;

	// node types
	public static final int ROOT_NODE = 80;
	public static final int INTERNAL_NODE = 81;
	public static final int LEAF_NODE = 82;

	public static String toString(int n) {
		switch (n) {
			case WHITE:
				return "WHITE";
			case BLACK:
				return "BLACK";
			case NOTHING:
				return "NOTHING";
			case GAME_RUNNING:
				return "GAME RUNNING";
			case WHITE_WON:
				return "WHITE WON";
			case BLACK_WON:
				return "BLACK WON";
			case DRAW:
				return "DRAW";
			case BATCH_NORMALIZATION_LAYER:
				return "BATCH NORMALIZATION_LAYER";
			case NON_REGULAR_CONVOLUTIONAL_LAYER:
				return "NON REGULAR CONVOLUTIONAL LAYER";
			case NO_ACTION_LAYER:
				return "NO ACTION LAYER";
			case RELU_LAYER:
				return "RELU LAYER";
			case SPLIT_LAYER:
				return "SPLIT LAYER";
			case DUPLICATE_LAYER:
				return "DUPLICATE LAYER";
			case DEAD_LAYER:
				return "DEAD LAYER";
			case FULLY_CONNECTED_LAYER:
				return "FULLY CONNECTED LAYER";
			case TANH_LAYER:
				return "TANH LAYER";
			case FUSION_LAYER:
				return "FUSION LAYER";
			case PROBABILITY_DISTRIBUTION_LAYER:
				return "PROBABILITY DISTRIBUTION LAYER";
			case CONVOLUTIONAL_LAYER:
				return "CONVOLUTIONAL LAYER";

		}
		return "";
	}

}
