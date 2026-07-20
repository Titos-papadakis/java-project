package View;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import Controller.controller;
import Model.Board;
import Model.FindingPosition;
import Model.Path;
import Model.Position;
import Model.Cards.Cards;
import Model.Findings.Fresco;
import Model.pawn.pawn;
import Model.pawn.thiseas;
import Model.player;

/**
 * The whole Swing view. Gui does not know any game rules - it only draws
 * whatever the Model currently looks like (see refresh()) and forwards user
 * clicks to the Controller. All coordinates are absolute (JLayeredPane),
 * which keeps the code simple even if it's not the "nicest" way to lay out
 * a Swing app.
 */
public class Gui {

	private static final String ASSETS = "project_assets/";
	private static final int CARD_W = 80;
	private static final int CARD_H = 80;
	private static final int X_START = 480;
	private static final int Y_START = 220;
	private static final int X_SPACING = 10;
	private static final int Y_SPACING = 100;
	private static final Color[] PALACE_COLORS = { Color.RED, Color.ORANGE, new Color(90, 90, 90), Color.BLUE };

	private final controller ctrl;

	private JFrame frame;
	private JLabel messageLabel;
	private JLabel timerLabel;
	private JLabel deckLabel;

	private JLabel[][] boardCells = new JLabel[4][Board.PATH_LENGTH];
	private JLabel[] pawnLayer; // rebuilt on every refresh()
	private JLayeredPane layeredPane;

	private JButton[] cardButtons1 = new JButton[8];
	private JButton[] cardButtons2 = new JButton[8];
	private JLabel[] pathStatusLabels1 = new JLabel[4];
	private JLabel[] pathStatusLabels2 = new JLabel[4];
	private JLabel scoreLabel1, scoreLabel2;
	private JLabel statuesLabel1, statuesLabel2;
	private JLabel rareLabel1, rareLabel2;
	private JLabel turnLabel1, turnLabel2;
	private JLabel playerPanel1, playerPanel2;

	public Gui(controller ctrl) {
		this.ctrl = ctrl;
		buildFrame();
	}

	public void show() {
		frame.setVisible(true);
	}

	// ---------------------------------------------------------------
	// Static layout construction (runs once)
	// ---------------------------------------------------------------

	private void buildFrame() {
		frame = new JFrame("Αναζητώντας τα Χαμένα Μινωικά Ανάκτορα");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1500, 1000);
		frame.setJMenuBar(buildMenuBar());

		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new java.awt.Dimension(1500, 950));
		frame.setContentPane(layeredPane);

		messageLabel = new JLabel("", SwingConstants.CENTER);
		messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
		messageLabel.setBounds(X_START, 20, 700, 40);
		layeredPane.add(messageLabel, Integer.valueOf(5));

		timerLabel = new JLabel("", SwingConstants.CENTER);
		timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		timerLabel.setForeground(Color.RED);
		timerLabel.setBounds(X_START + 720, 20, 150, 40);
		layeredPane.add(timerLabel, Integer.valueOf(5));

		deckLabel = new JLabel("", SwingConstants.CENTER);
		deckLabel.setBounds(60, Y_START + 150, 140, 60);
		deckLabel.setOpaque(true);
		deckLabel.setBackground(Color.LIGHT_GRAY);
		deckLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		layeredPane.add(deckLabel, Integer.valueOf(5));

		buildBoardGrid();
		playerPanel1 = buildPlayerArea(1, 60, cardButtons1, pathStatusLabels1);
		playerPanel2 = buildPlayerArea(2, Y_START + Y_SPACING * 4 + 60, cardButtons2, pathStatusLabels2);
	}

	private JMenuBar buildMenuBar() {
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Παιχνίδι");

		JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener(e -> ctrl.newGame());

		JMenuItem save = new JMenuItem("Save Game");
		save.addActionListener(e -> ctrl.saveGame());

		JMenuItem load = new JMenuItem("Continue Saved Game");
		load.addActionListener(e -> ctrl.loadGame());

		JMenuItem exit = new JMenuItem("Exit Game");
		exit.addActionListener(e -> ctrl.exitGame());

		menu.add(newGame);
		menu.add(save);
		menu.add(load);
		menu.addSeparator();
		menu.add(exit);
		bar.add(menu);
		return bar;
	}

	private void buildBoardGrid() {
		String[] pointLabels = new String[Board.PATH_LENGTH];
		for (int i = 0; i < Board.PATH_LENGTH; i++) {
			pointLabels[i] = Board.POINTS[i] + (i == Board.CHECKPOINT_INDEX ? " (CP)" : "");
		}

		for (int row = 0; row < 4; row++) {
			int y = Y_START + row * Y_SPACING;

			for (int col = 0; col < Board.PATH_LENGTH; col++) {
				int x = X_START + col * (CARD_W + X_SPACING);

				JLabel pointsLabel = new JLabel(pointLabels[col], SwingConstants.CENTER);
				pointsLabel.setBounds(x, y - 22, CARD_W, 18);
				pointsLabel.setFont(new Font("Arial", Font.PLAIN, 10));
				layeredPane.add(pointsLabel, Integer.valueOf(2));

				JLabel cell = new JLabel("", SwingConstants.CENTER);
				cell.setBounds(x, y, CARD_W, CARD_H);
				cell.setOpaque(true);
				cell.setBackground(Color.LIGHT_GRAY);
				cell.setBorder(BorderFactory.createLineBorder(PALACE_COLORS[row], col == Board.PATH_LENGTH - 1 ? 4 : 2));
				layeredPane.add(cell, Integer.valueOf(2));
				boardCells[row][col] = cell;

				if (col == Board.PATH_LENGTH - 1) {
					final int pathIndex = row;
					cell.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							ctrl.onPalaceClicked(pathIndex);
						}
					});
				}
			}
		}
	}

	private JLabel buildPlayerArea(int playerNum, int yOffset, JButton[] cardButtons, JLabel[] pathStatusLabels) {
		JLabel panel = new JLabel();
		panel.setBounds(0, yOffset, 1480, 90);
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		layeredPane.add(panel, Integer.valueOf(1));

		int x = 10, y = yOffset + 5;
		for (int i = 0; i < cardButtons.length; i++) {
			JButton btn = new JButton();
			btn.setBounds(x, y, 65, 80);
			btn.setToolTipText("Δεξί κλικ: παίξιμο | Αριστερό κλικ: απόρριψη");
			final int slot = i;
			btn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					player p = playerNum == 1 ? ctrl.getPlayer1() : ctrl.getPlayer2();
					if (SwingUtilities.isRightMouseButton(e)) {
						ctrl.onCardPlay(p, slot);
					} else if (SwingUtilities.isLeftMouseButton(e)) {
						ctrl.onCardDiscard(p, slot);
					}
				}
			});
			layeredPane.add(btn, Integer.valueOf(2));
			cardButtons[i] = btn;
			x += 70;
		}

		x = 600;
		for (int i = 0; i < 4; i++) {
			JLabel status = new JLabel("", SwingConstants.CENTER);
			status.setBounds(x, y, 90, 35);
			status.setOpaque(true);
			status.setBackground(Color.WHITE);
			status.setBorder(BorderFactory.createLineBorder(PALACE_COLORS[i], 2));
			status.setFont(new Font("Arial", Font.PLAIN, 10));
			layeredPane.add(status, Integer.valueOf(2));
			pathStatusLabels[i] = status;
			x += 95;
		}

		JLabel score = new JLabel("", SwingConstants.CENTER);
		score.setFont(new Font("Arial", Font.BOLD, 12));
		score.setBounds(1000, y, 140, 20);
		layeredPane.add(score, Integer.valueOf(2));

		JLabel rare = new JLabel("", SwingConstants.CENTER);
		rare.setBounds(1000, y + 20, 140, 18);
		rare.setFont(new Font("Arial", Font.PLAIN, 11));
		layeredPane.add(rare, Integer.valueOf(2));

		JLabel statues = new JLabel("", SwingConstants.CENTER);
		statues.setBounds(1000, y + 38, 140, 18);
		statues.setFont(new Font("Arial", Font.PLAIN, 11));
		layeredPane.add(statues, Integer.valueOf(2));

		JButton frescoButton = new JButton("Οι τοιχογραφίες μου");
		frescoButton.setBounds(1150, y, 170, 30);
		final int pn = playerNum;
		frescoButton.addActionListener(e -> showFrescoGallery(pn == 1 ? ctrl.getPlayer1() : ctrl.getPlayer2()));
		layeredPane.add(frescoButton, Integer.valueOf(2));

		JLabel turn = new JLabel("", SwingConstants.CENTER);
		turn.setBounds(1330, y, 140, 30);
		turn.setFont(new Font("Arial", Font.BOLD, 12));
		layeredPane.add(turn, Integer.valueOf(2));

		if (playerNum == 1) {
			scoreLabel1 = score;
			rareLabel1 = rare;
			statuesLabel1 = statues;
			turnLabel1 = turn;
		} else {
			scoreLabel2 = score;
			rareLabel2 = rare;
			statuesLabel2 = statues;
			turnLabel2 = turn;
		}

		return panel;
	}

	// ---------------------------------------------------------------
	// Dynamic rendering - called by the Controller after every change
	// ---------------------------------------------------------------

	public void refresh() {
		Board board = ctrl.getBoard();
		if (board == null) {
			return;
		}
		refreshBoardCells(board);
		refreshPawns(board);
		refreshPlayerArea(1, ctrl.getPlayer1(), cardButtons1, pathStatusLabels1, scoreLabel1, rareLabel1, statuesLabel1, turnLabel1, playerPanel1);
		refreshPlayerArea(2, ctrl.getPlayer2(), cardButtons2, pathStatusLabels2, scoreLabel2, rareLabel2, statuesLabel2, turnLabel2, playerPanel2);

		deckLabel.setText("<html>Κάρτες: " + board.getDeckSize() + "</html>");
		String status = ctrl.isGameOver() ? "Το παιχνίδι τελείωσε" : "Σειρά: " + ctrl.getCurrentPlayer().getName();
		messageLabel.setText(status);

		frame.repaint();
	}

	private void refreshBoardCells(Board board) {
		for (int row = 0; row < 4; row++) {
			Path path = board.getPath(row);
			for (int col = 0; col < Board.PATH_LENGTH; col++) {
				Position pos = path.getPosition(col);
				JLabel cell = boardCells[row][col];
				String img;
				if (col == Board.PATH_LENGTH - 1) {
					img = ASSETS + "images/paths/" + path.getPalace() + "Palace.jpg";
				} else if (pos instanceof FindingPosition) {
					img = ASSETS + "images/paths/" + path.getPalace() + "2.jpg";
				} else {
					img = ASSETS + "images/paths/" + path.getPalace() + ".jpg";
				}
				setScaledIcon(cell, img, CARD_W, CARD_H);

				if (pos instanceof FindingPosition && !((FindingPosition) pos).isAvailable()) {
					cell.setBackground(new Color(210, 210, 210));
				} else {
					cell.setBackground(Color.LIGHT_GRAY);
				}
			}
		}
	}

	private void refreshPawns(Board board) {
		if (pawnLayer != null) {
			for (JLabel l : pawnLayer) {
				layeredPane.remove(l);
			}
		}
		java.util.List<JLabel> markers = new java.util.ArrayList<>();
		addPawnMarkers(markers, ctrl.getPlayer1(), Color.RED, 0);
		addPawnMarkers(markers, ctrl.getPlayer2(), Color.GREEN, 1);
		pawnLayer = markers.toArray(new JLabel[0]);
		for (JLabel l : pawnLayer) {
			layeredPane.add(l, Integer.valueOf(3));
		}
	}

	private void addPawnMarkers(java.util.List<JLabel> markers, player p, Color borderColor, int columnOffset) {
		for (pawn pw : p.getPawns()) {
			if (!pw.isPlaced()) {
				continue;
			}
			int row = pw.getPath();
			int col = pw.getPosition();
			int x = X_START + col * (CARD_W + X_SPACING) + 4 + columnOffset * 30;
			int y = Y_START + row * Y_SPACING + 4;

			String img = pw.isRevealed()
					? (pw instanceof thiseas ? ASSETS + "images/pionia/theseus.jpg" : ASSETS + "images/pionia/arch.jpg")
					: ASSETS + "images/pionia/question.jpg";

			JLabel marker = new JLabel();
			marker.setBounds(x, y, 28, CARD_H - 8);
			marker.setOpaque(true);
			marker.setBackground(Color.WHITE);
			Border border = BorderFactory.createLineBorder(borderColor, 2);
			if (pw instanceof thiseas && ((thiseas) pw).isStunned()) {
				border = BorderFactory.createLineBorder(borderColor.darker().darker(), 3);
			}
			marker.setBorder(border);
			setScaledIcon(marker, img, 26, CARD_H - 10);
			markers.add(marker);
		}
	}

	private void refreshPlayerArea(int playerNum, player p, JButton[] cardButtons, JLabel[] pathStatusLabels,
			JLabel scoreLabel, JLabel rareLabel, JLabel statuesLabel, JLabel turnLabel, JLabel panel) {
		List<Cards> hand = p.getHand();
		for (int i = 0; i < cardButtons.length; i++) {
			if (i < hand.size()) {
				Cards c = hand.get(i);
				setScaledIcon(cardButtons[i], c.getImage(), 65, 80);
				cardButtons[i].setVisible(true);
			} else {
				cardButtons[i].setIcon(null);
				cardButtons[i].setVisible(false);
			}
		}

		for (int i = 0; i < 4; i++) {
			pawn pw = p.getPawnOnPath(i);
			String text;
			if (pw == null) {
				text = Board.PALACES[i] + ": -";
			} else if (pw.getPosition() >= Board.CHECKPOINT_INDEX) {
				text = Board.PALACES[i] + ": " + pw.getPosition() + " ✓";
			} else {
				text = Board.PALACES[i] + ": " + pw.getPosition();
			}
			pathStatusLabels[i].setText(text);
		}

		scoreLabel.setText("Σκορ: " + p.calculateScore(ctrl.getBoard()));
		rareLabel.setText("Σπάνια ευρήματα: " + p.getRareFindings().size());
		statuesLabel.setText("Αγαλματάκια: " + p.getStatues().size());

		boolean active = p == ctrl.getCurrentPlayer() && !ctrl.isGameOver();
		turnLabel.setText(active ? "▶ Παίζει τώρα" : "");
		panel.setBorder(BorderFactory.createLineBorder(active ? new Color(218, 165, 32) : Color.GRAY, active ? 4 : 2));
	}

	private void showFrescoGallery(player p) {
		List<Fresco> frescoes = p.getPhotographedFrescoes();
		if (frescoes.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Δεν έχεις φωτογραφίσει καμία τοιχογραφία ακόμα.", "Οι τοιχογραφίες μου", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		StringBuilder text = new StringBuilder("<html>");
		for (int i = 0; i < frescoes.size(); i++) {
			Fresco f = frescoes.get(i);
			text.append(f.getName()).append(" (").append(f.getValue()).append(" πόντοι)<br>");
		}
		text.append("</html>");
		JOptionPane.showMessageDialog(frame, new JLabel(text.toString()), "Οι τοιχογραφίες μου", JOptionPane.INFORMATION_MESSAGE);
	}

	// ---------------------------------------------------------------
	// Dialog helpers used by the Controller
	// ---------------------------------------------------------------

	public void showMessage(String title, String text) {
		JOptionPane.showMessageDialog(frame, wrap(text), title, JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean confirm(String title, String text) {
		int result = JOptionPane.showConfirmDialog(frame, wrap(text), title, JOptionPane.YES_NO_OPTION);
		return result == JOptionPane.YES_OPTION;
	}

	public String chooseOption(String title, String text, String[] options) {
		return (String) JOptionPane.showInputDialog(frame, wrap(text), title, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}

	private static String wrap(String text) {
		return "<html><body style='width: 320px'>" + text + "</body></html>";
	}

	private static void setScaledIcon(JLabel label, String path, int w, int h) {
		Image scaled = loadScaled(path, w, h);
		label.setIcon(scaled != null ? new ImageIcon(scaled) : null);
	}

	private static void setScaledIcon(JButton button, String path, int w, int h) {
		Image scaled = loadScaled(path, w, h);
		button.setIcon(scaled != null ? new ImageIcon(scaled) : null);
	}

	private static Image loadScaled(String path, int w, int h) {
		ImageIcon icon = new ImageIcon(path);
		if (icon.getIconWidth() <= 0) {
			return null; // missing image file - fail silently, gameplay isn't affected
		}
		return icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}

	public JLabel getTimerLabel() {
		return timerLabel;
	}
}
