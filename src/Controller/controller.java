package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import Model.Board;
import Model.FindingPosition;
import Model.HistoricalInfo;
import Model.Path;
import Model.Position;
import Model.Cards.Cards;
import Model.Cards.minotayros;
import Model.Cards.mitos;
import Model.Cards.numbercard;
import Model.Findings.Finding;
import Model.Findings.Fresco;
import Model.Findings.RareFinding;
import Model.Findings.SnakeGoddess;
import Model.pawn.pawn;
import Model.pawn.thiseas;
import Model.player;
import View.Gui;

/**
 * The "brain" of the game. Holds the Model (Board + 2 players), exposes
 * methods for every possible action during a turn, and refreshes the Gui
 * after every change. The Gui itself knows nothing about the rules - it
 * just draws whatever the controller gives it and forwards the user's clicks.
 */
public class controller {

	private static final String ASSETS = "project_assets/";
	private static final String SAVE_FILE = "savegame.dat";
	private static final int HAND_SIZE = 8;

	private Gui view;
	private Board board;
	private player player1;
	private player player2;
	private player currentPlayer;
	private boolean gameOver = false;

	private HistoricalInfo historicalInfo;
	private MusicPlayer music;
	private GameTimer timer;

	public static void main(String[] args) {
		controller c = new controller();
		c.initialize();
	}

	public void initialize() {
		view = new Gui(this);
		historicalInfo = new HistoricalInfo(ASSETS + "csvFiles/csv_greek.csv");
		music = new MusicPlayer(ASSETS + "audio/player1.wav", ASSETS + "audio/player2.wav");
		timer = new GameTimer(view.getTimerLabel(), this::onTimeUp);
		newGame();
		view.show();
	}

	/**
	 * Test-support seam: lets the JUnit tests plug in a fake Gui (one that
	 * doesn't pop up real dialog windows) instead of a real one, so the
	 * turn/rule logic can be exercised headlessly. Not used by the actual game.
	 */
	public void attachTestView(Gui testView) {
		view = testView;
		historicalInfo = new HistoricalInfo(ASSETS + "csvFiles/csv_greek.csv");
		music = new MusicPlayer(ASSETS + "audio/player1.wav", ASSETS + "audio/player2.wav");
		timer = new GameTimer(testView.getTimerLabel(), this::onTimeUp);
		newGame();
	}

	// ---------------------------------------------------------------
	// Setup / new game
	// ---------------------------------------------------------------

	public void newGame() {
		board = new Board();
		player1 = new player("Παίκτης 1");
		player2 = new player("Παίκτης 2");

		dealInitialHand(player1);
		dealInitialHand(player2);

		currentPlayer = Math.random() < 0.5 ? player1 : player2;
		gameOver = false;

		refreshView();
		startTurn();
	}

	private void dealInitialHand(player p) {
		for (int i = 0; i < HAND_SIZE; i++) {
			Cards c = board.drawCard();
			if (c != null) {
				p.addCardToHand(c);
			}
		}
	}

	private void startTurn() {
		if (gameOver) {
			return;
		}
		if (currentPlayer == player1) {
			music.playForPlayer1();
		} else {
			music.playForPlayer2();
		}
		timer.restart();
		refreshView();
	}

	// ---------------------------------------------------------------
	// Phase 1: play/discard a card (called by the Gui on user clicks)
	// ---------------------------------------------------------------

	/**
	 * Left click on a card => discard it.
	 */
	public void onCardDiscard(player p, int handIndex) {
		if (!isTurnOf(p) || gameOver) {
			return;
		}
		if (handIndex < 0 || handIndex >= p.getHand().size()) {
			return;
		}
		Cards card = p.getHand().get(handIndex);
		p.useCard(card);
		board.discard(card);
		endPhaseAndSwitch();
	}

	/**
	 * Right click on a card => try to play it.
	 */
	public void onCardPlay(player p, int handIndex) {
		if (!isTurnOf(p) || gameOver) {
			return;
		}
		if (handIndex < 0 || handIndex >= p.getHand().size()) {
			return;
		}
		Cards card = p.getHand().get(handIndex);

		boolean played;
		if (card instanceof numbercard) {
			played = playNumberCard(p, (numbercard) card);
		} else if (card instanceof mitos) {
			played = playAriadneCard(p, (mitos) card);
		} else if (card instanceof minotayros) {
			played = playMinotaurCard(p, (minotayros) card);
		} else {
			played = false;
		}

		if (played) {
			p.useCard(card);
			board.discard(card);
			endPhaseAndSwitch();
		} else {
			refreshView(); // the attempt failed, just refresh (a message dialog was likely already shown)
		}
	}

	private boolean playNumberCard(player p, numbercard card) {
		int pathIndex = board.getPath(card.getPalace()).getIndex();
		pawn existing = p.getPawnOnPath(pathIndex);

		if (existing == null) {
			pawn chosen = choosePawnToPlace(p);
			if (chosen == null) {
				return false; // cancelled, or no more pawns left to place
			}
			if (chosen instanceof thiseas && ((thiseas) chosen).isStunned()) {
				view.showMessage("Ο Θησέας είναι κουρασμένος", "Ο Θησέας δεν μπορεί να κινηθεί αυτόν τον γύρο.");
				return false;
			}
			chosen.setPath(pathIndex);
			chosen.setPosition(0);
			chosen.setLastCardValue(card.getvalue());
			return true;
		}

		if (existing instanceof thiseas && ((thiseas) existing).isStunned()) {
			view.showMessage("Ο Θησέας είναι κουρασμένος", "Ο Θησέας δεν μπορεί να κινηθεί αυτόν τον γύρο.");
			return false;
		}
		if (!card.isValidContinuation(existing.getLastCardValue())) {
			view.showMessage("Μη έγκυρη κάρτα", "Πρέπει να παίξεις κάρτα με τιμή ίση ή μεγαλύτερη από την τελευταία (" + existing.getLastCardValue() + ").");
			return false;
		}
		int newPos = existing.getPosition() + 1;
		if (newPos >= Board.PATH_LENGTH) {
			view.showMessage("Έφτασες στο ανάκτορο", "Αυτό το πιόνι έχει ήδη φτάσει στο ανάκτορο.");
			return false;
		}
		existing.setPosition(newPos);
		existing.setLastCardValue(card.getvalue());
		visitPosition(p, pathIndex, existing, newPos);
		return true;
	}

	private boolean playAriadneCard(player p, mitos card) {
		int pathIndex = board.getPath(card.getPalace()).getIndex();
		pawn existing = p.getPawnOnPath(pathIndex);
		if (existing == null) {
			view.showMessage("Μη έγκυρη κίνηση", "Δεν μπορείς να ξεκινήσεις μονοπάτι με κάρτα Μίτου Αριάδνης.");
			return false;
		}
		if (existing instanceof thiseas && ((thiseas) existing).isStunned()) {
			view.showMessage("Ο Θησέας είναι κουρασμένος", "Ο Θησέας δεν μπορεί να κινηθεί αυτόν τον γύρο.");
			return false;
		}
		if (existing.getPosition() >= Board.PATH_LENGTH - 1) {
			view.showMessage("Έφτασες στο ανάκτορο", "Αυτό το πιόνι έχει ήδη φτάσει στο ανάκτορο.");
			return false;
		}
		int from = existing.getPosition();
		int to = Math.min(from + mitos.STEPS, Board.PATH_LENGTH - 1);
		for (int idx = from + 1; idx <= to; idx++) {
			existing.setPosition(idx);
			visitPosition(p, pathIndex, existing, idx);
		}
		return true;
	}

	private boolean playMinotaurCard(player attacker, minotayros card) {
		player defenderPlayer = getOpponent(attacker);
		List<Integer> targets = new ArrayList<>();
		for (int i = 0; i < Board.PALACES.length; i++) {
			pawn defender = defenderPlayer.getPawnOnPath(i);
			if (defender != null && defender.getPosition() < Board.CHECKPOINT_INDEX) {
				targets.add(i);
			}
		}
		if (targets.isEmpty()) {
			view.showMessage("Μη έγκυρη κάρτα", "Δεν υπάρχει έγκυρος στόχος για κάρτα Μινώταυρου αυτή τη στιγμή.");
			return false;
		}
		String[] options = new String[targets.size()];
		for (int i = 0; i < targets.size(); i++) {
			options[i] = capitalize(Board.PALACES[targets.get(i)]);
		}
		String choice = view.chooseOption("Κάρτα Μινώταυρου", "Σε ποιο ανάκτορο θα επιτεθείς;", options);
		if (choice == null) {
			return false;
		}
		int pathIndex = -1;
		for (int i = 0; i < Board.PALACES.length; i++) {
			if (capitalize(Board.PALACES[i]).equals(choice)) {
				pathIndex = i;
				break;
			}
		}

		pawn defender = defenderPlayer.getPawnOnPath(pathIndex);
		defender.reveal();
		if (defender instanceof thiseas) {
			((thiseas) defender).setStunned(true);
			view.showMessage("Ο Θησέας αποκρούει!", "Ο Θησέας του αντιπάλου αποκρούει την επίθεση, αλλά κουράστηκε και δεν θα κινηθεί τον επόμενο του γύρο.");
		} else {
			int newPos = Math.max(0, defender.getPosition() - 2);
			defender.setPosition(newPos);
			view.showMessage("Επίθεση Μινώταυρου!", "Ο αρχαιολόγος του αντιπάλου αποκαλύφθηκε και πήγε 2 βήματα πίσω.");
		}
		return true;
	}

	/**
	 * Called every time a pawn passes through (or lands on) a square. If the
	 * square has a finding that hasn't been resolved yet, asks the player
	 * what they want to do with it.
	 */
	private void visitPosition(player p, int pathIndex, pawn mover, int posIndex) {
		Position pos = board.getPath(pathIndex).getPosition(posIndex);
		if (!(pos instanceof FindingPosition)) {
			return;
		}
		FindingPosition fp = (FindingPosition) pos;
		if (!fp.isAvailable()) {
			return;
		}

		if (mover instanceof thiseas) {
			thiseas th = (thiseas) mover;
			if (!th.canDestroyMore()) {
				return; // already destroyed 3, silently ignores this one
			}
			boolean destroy = view.confirm("Ανασκαφή", "Βρέθηκε κάτι θαμμένο εδώ! Να το καταστρέψεις;");
			if (destroy) {
				fp.consume(p);
				th.registerDestroyedFinding();
				mover.reveal();
				view.showMessage("Καταστροφή ευρήματος", "Ο Θησέας κατέστρεψε το εύρημα.");
			}
			return;
		}

		boolean open = view.confirm("Ανασκαφή", "Βρέθηκε κάτι θαμμένο εδώ! Θέλεις να κάνεις ανασκαφή;");
		if (!open) {
			return;
		}
		mover.reveal();
		Finding finding = fp.getFinding();
		if (finding instanceof RareFinding) {
			p.addRareFinding((RareFinding) finding);
			fp.consume(p);
		} else if (finding instanceof SnakeGoddess) {
			p.addStatue((SnakeGoddess) finding);
			fp.consume(p);
		} else if (finding instanceof Fresco) {
			p.addPhotographedFresco((Fresco) finding);
			// the fresco doesn't disappear - stays available for the other player too
		}

		String[] info = historicalInfo.lookup(finding.getImage());
		String title = info != null ? info[0] : "Ανακάλυψες: " + finding.getName();
		String description = info != null ? info[1] : finding.getDescription();
		view.showMessage(title, description);
	}

	/**
	 * Asks the player which of their unplaced pawns they want to use to
	 * start a new path.
	 */
	private pawn choosePawnToPlace(player p) {
		List<pawn> unused = new ArrayList<>();
		for (pawn pw : p.getPawns()) {
			if (!pw.isPlaced()) {
				unused.add(pw);
			}
		}
		if (unused.isEmpty()) {
			view.showMessage("Δεν υπάρχουν πιόνια", "Έχεις ήδη τοποθετήσει και τα 4 πιόνια σου σε μονοπάτια.");
			return null;
		}
		String[] options = new String[unused.size()];
		for (int i = 0; i < unused.size(); i++) {
			pawn pw = unused.get(i);
			options[i] = (pw instanceof thiseas) ? "Θησέας" : "Αρχαιολόγος " + (i + 1);
		}
		String choice = view.chooseOption("Επιλογή πιονιού", "Ποιο πιόνι θέλεις να τοποθετήσεις;", options);
		if (choice == null) {
			return null;
		}
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(choice)) {
				return unused.get(i);
			}
		}
		return null;
	}

	// ---------------------------------------------------------------
	// Phase 2 + switching turns
	// ---------------------------------------------------------------

	private void endPhaseAndSwitch() {
		Cards drawn = board.drawCard();
		if (drawn != null) {
			currentPlayer.addCardToHand(drawn);
		}
		if (currentPlayer.getTheseus().isStunned()) {
			currentPlayer.getTheseus().setStunned(false);
		}
		if (isEndOfGame()) {
			finishGame();
			return;
		}
		switchTurn();
	}

	private void switchTurn() {
		currentPlayer = getOpponent(currentPlayer);
		startTurn();
	}

	private void onTimeUp() {
		view.showMessage("Ο χρόνος τελείωσε!", currentPlayer.getName() + ", ο χρόνος σου τελείωσε. Η σειρά περνάει στον αντίπαλο.");
		switchTurn();
	}

	private boolean isTurnOf(player p) {
		return p == currentPlayer && !gameOver;
	}

	private player getOpponent(player p) {
		return p == player1 ? player2 : player1;
	}

	// ---------------------------------------------------------------
	// End of game
	// ---------------------------------------------------------------

	/**
	 * True either when the deck is empty, or when at least 4 pawns (of
	 * either player, on any path) have reached or passed the checkpoint.
	 */
	public boolean isEndOfGame() {
		if (board.isDeckEmpty()) {
			return true;
		}
		int atCheckpoint = 0;
		for (player p : new player[] { player1, player2 }) {
			for (pawn pw : p.getPawns()) {
				if (pw.isPlaced() && pw.getPosition() >= Board.CHECKPOINT_INDEX) {
					atCheckpoint++;
				}
			}
		}
		return atCheckpoint >= 4;
	}

	/**
	 * Works out who currently has the higher score, applying the tie-break
	 * chain from the rules (rare findings, then frescoes, then statues).
	 * Can be called at any point in the game, not only once it has ended.
	 * @return the leading player, or null if it's an exact tie.
	 */
	public player getWinner() {
		int score1 = player1.calculateScore(board);
		int score2 = player2.calculateScore(board);
		if (score1 != score2) {
			return score1 > score2 ? player1 : player2;
		}
		if (player1.getRareFindings().size() != player2.getRareFindings().size()) {
			return player1.getRareFindings().size() > player2.getRareFindings().size() ? player1 : player2;
		}
		if (player1.getPhotographedFrescoes().size() != player2.getPhotographedFrescoes().size()) {
			return player1.getPhotographedFrescoes().size() > player2.getPhotographedFrescoes().size() ? player1 : player2;
		}
		if (player1.getStatues().size() != player2.getStatues().size()) {
			return player1.getStatues().size() > player2.getStatues().size() ? player1 : player2;
		}
		return null; // tie
	}

	private void finishGame() {
		gameOver = true;
		timer.stop();
		music.stop();

		player winner = getWinner();
		String winnerText = winner != null ? winner.getName() + " κερδίζει!" : "Ισοπαλία!";

		StringBuilder sb = new StringBuilder();
		sb.append("Το παιχνίδι τελείωσε!\n\n");
		sb.append(player1.getName()).append(": ").append(player1.calculateScore(board)).append(" πόντοι\n");
		sb.append(player2.getName()).append(": ").append(player2.calculateScore(board)).append(" πόντοι\n\n");
		sb.append(winnerText);

		refreshView();
		view.showMessage("Τέλος Παιχνιδιού", sb.toString());
	}

	// ---------------------------------------------------------------
	// Save / Load (Bonus 3)
	// ---------------------------------------------------------------

	public void saveGame() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
			out.writeObject(new GameSaveData(board, player1, player2, currentPlayer == player1));
			view.showMessage("Αποθήκευση", "Το παιχνίδι αποθηκεύτηκε επιτυχώς.");
		} catch (Exception e) {
			view.showMessage("Σφάλμα Αποθήκευσης", "Δεν ήταν δυνατή η αποθήκευση: " + e.getMessage());
		}
	}

	public void loadGame() {
		File f = new File(SAVE_FILE);
		if (!f.exists()) {
			view.showMessage("Δεν βρέθηκε αποθηκευμένο παιχνίδι", "Δεν υπάρχει αρχείο αποθήκευσης (" + SAVE_FILE + ").");
			return;
		}
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
			GameSaveData data = (GameSaveData) in.readObject();
			this.board = data.board;
			this.player1 = data.player1;
			this.player2 = data.player2;
			this.currentPlayer = data.player1Turn ? player1 : player2;
			this.gameOver = false;
			startTurn();
		} catch (Exception e) {
			view.showMessage("Σφάλμα Φόρτωσης", "Δεν ήταν δυνατή η φόρτωση: " + e.getMessage());
		}
	}

	public void exitGame() {
		System.exit(0);
	}

	// ---------------------------------------------------------------
	// Historical info (click on a palace)
	// ---------------------------------------------------------------

	public void onPalaceClicked(int pathIndex) {
		Path path = board.getPath(pathIndex);
		String key = ASSETS + "images/paths/" + path.getPalace() + "Palace.jpg";
		String[] info = historicalInfo.lookup(key);
		if (info != null) {
			view.showMessage(info[0], info[1]);
		}
	}

	private static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	// ---------------------------------------------------------------
	// Getters for the Gui
	// ---------------------------------------------------------------

	public Board getBoard() {
		return board;
	}

	public player getPlayer1() {
		return player1;
	}

	public player getPlayer2() {
		return player2;
	}

	public player getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	private void refreshView() {
		if (view != null) {
			view.refresh();
		}
	}
}
