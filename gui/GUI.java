package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import chess_rules.Board;
import chess_rules.Move;
import chess_rules.Piece;
import chess_rules.Rules;
import chess_rules.Square;
import general.Values;

public class GUI {
	private JFrame frame;
	private JFrame nextFrame;
	public Square sq1;
	Runnable pieceClick;
	Runnable nextClick;

	// to put in main
	static GUI gui;
	static int y = 0;
	static Square sq2 = null;
	static Board guiBoard;

	public static void g1() {
		guiBoard = new Board();
		guiBoard.init();

		Runnable nextRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				ArrayList<Move> legalMoves = Rules.getLegalMoves(guiBoard);
				int u = Values.random.nextInt(legalMoves.size());
				Move move = legalMoves.get(u);

				guiBoard = Rules.play(guiBoard, move);

				System.out.println(y + "  " + move + "  -> " + Values.toString(guiBoard.gameState));
				y++;
				gui.setBoard(guiBoard.pieces);

			}
		};

		Runnable clickOnPieceRunnable = new Runnable() {

			@Override
			public void run() {

				if (sq2 == null) {
					sq2 = gui.sq2;
				} else {
					Move move = chess_rules.Util.getMoveWithClicks(guiBoard, sq2, gui.sq2);
					sq2 = null;
					ArrayList<Move> legalMoves = Rules.getLegalMoves(guiBoard);
					boolean run = true;
					for (int i = 0; i < legalMoves.size(); i++) {
						if (move.equals(legalMoves.get(i))) {
							Board nBoard = Rules.play(guiBoard, move);
							guiBoard = nBoard;

							System.out.println(y + "  " + move + "  -> " + Values.toString(nBoard.gameState));

							y++;
							run = false;
							gui.setBoard(guiBoard.pieces);
						}
					}
					if (run) {
						System.out.println("ERREUR");
					}
				}

			}
		};
		gui = new GUI(clickOnPieceRunnable, nextRunnable);
		// TODO Auto-generated method stub

		gui.setBoard(guiBoard.pieces);

	}

	public GUI(Runnable onClick, Runnable nxtClick) {

		pieceClick = onClick;
		nextClick = nxtClick;

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1000, 1000));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		nextFrame = new JFrame();
		nextFrame.setSize(200, 200);
		nextFrame.setLocation(1300, 500);
		nextFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton button = new JButton("NEXT");
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				nextClick.run();

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		nextFrame.add(button);
		nextFrame.setVisible(true);

	}

	public void setBoard(Piece[][] pieces) {
		frame.getContentPane().removeAll();
		frame.repaint();
		int boardSize = pieces.length;
		frame.setLayout(new GridLayout(boardSize, boardSize));

		for (int l = boardSize - 1; l >= 0; l--) {
			for (int c = 0; c < boardSize; c++) {
				SButton button = new SButton();

				ImageIcon icon;
				String pieceName = "";

				if (pieces[l][c].color == Values.WHITE) {
					pieceName = "white_";
				}
				if (pieces[l][c].color == Values.BLACK) {
					pieceName = "black_";
				}
				switch (pieces[l][c].color) {
					case Values.BISHOP:
						pieceName = pieceName + "bishop";
						break;
					case Values.KNIGHT:
						pieceName = pieceName + "knight";
						break;
					case Values.ROOK:
						pieceName = pieceName + "rook";
						break;
					case Values.PAWN:
						pieceName = pieceName + "pawn";
						break;
					case Values.QUEEN:
						pieceName = pieceName + "queen";
						break;
					case Values.KING:
						pieceName = pieceName + "king";
						break;
					case Values.EMPTY:
						pieceName = pieceName + "empty";
						break;
				}

				icon = new ImageIcon("/home/emile/eclipse-workspace/Chess_AI/src/pictures/" + pieceName + ".png");

				Image image = icon.getImage(); // transform it
				Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH); // scale it the smooth
																								// way
				icon = new ImageIcon(newimg); // transform it back

				button.setIcon(icon);

				button.sq = new Square(l, c);
				button.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						// TODO Auto-generated method stub
						sq1 = button.sq;
						pieceClick.run();

					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub

					}
				});
				frame.add(button);

			}
		}

		frame.pack();
		frame.setVisible(true);

	}

	private class SButton extends JButton {
		public Square sq;
	}
}
