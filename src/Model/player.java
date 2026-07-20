package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Model.Cards.Cards;
import Model.Findings.Fresco;
import Model.Findings.RareFinding;
import Model.Findings.SnakeGoddess;
import Model.pawn.arxaiologos;
import Model.pawn.pawn;
import Model.pawn.thiseas;

/**
 * One of the 2 players. Holds their hand of cards, their 4 pawns (3
 * archaeologists + 1 Theseus) and everything they've collected.
 */
public class player implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<Cards> hand;
	private pawn[] pawns; // 0-2: archaeologists, 3: theseus

	private List<RareFinding> rareFindings = new ArrayList<>();
	private List<SnakeGoddess> statues = new ArrayList<>();
	private List<Fresco> photographedFrescoes = new ArrayList<>();

	public player(String name) {
		this.name = name;
		this.hand = new ArrayList<>();
		this.pawns = new pawn[4];
		pawns[0] = new arxaiologos();
		pawns[1] = new arxaiologos();
		pawns[2] = new arxaiologos();
		pawns[3] = new thiseas();
	}

	public void addCardsToHand(List<Cards> cards) {
		hand.addAll(cards);
	}

	public void addCardToHand(Cards card) {
		hand.add(card);
	}

	public void useCard(Cards card) {
		hand.remove(card);
	}

	public List<Cards> getHand() {
		return hand;
	}

	public pawn[] getPawns() {
		return pawns;
	}

	public thiseas getTheseus() {
		return (thiseas) pawns[3];
	}

	/**
	 * @return this player's pawn currently on the given path, or null if none of them is there.
	 */
	public pawn getPawnOnPath(int pathIndex) {
		for (pawn p : pawns) {
			if (p.getPath() == pathIndex) {
				return p;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void addRareFinding(RareFinding f) {
		rareFindings.add(f);
	}

	public void addStatue(SnakeGoddess s) {
		statues.add(s);
	}

	public void addPhotographedFresco(Fresco f) {
		if (!photographedFrescoes.contains(f)) {
			photographedFrescoes.add(f);
		}
	}

	public boolean hasPhotographed(Fresco f) {
		return photographedFrescoes.contains(f);
	}

	public List<RareFinding> getRareFindings() {
		return rareFindings;
	}

	public List<SnakeGoddess> getStatues() {
		return statues;
	}

	public List<Fresco> getPhotographedFrescoes() {
		return photographedFrescoes;
	}

	/**
	 * Score table for Snake Goddess statues, based on how many a player has
	 * collected (0..6+).
	 */
	private static final int[] STATUE_SCORE = { 0, -20, -15, 10, 15, 30, 50 };

	private int statueScore() {
		int count = statues.size();
		if (count >= STATUE_SCORE.length) {
			count = STATUE_SCORE.length - 1;
		}
		return STATUE_SCORE[count];
	}

	/**
	 * Computes the player's total score (see the "Βαθμολογία - Νικητής"
	 * section of the rules).
	 */
	public int calculateScore(Board board) {
		int score = 0;

		for (Fresco f : photographedFrescoes) {
			score += f.getValue();
		}
		for (RareFinding r : rareFindings) {
			score += r.getValue();
		}
		score += statueScore();

		for (pawn p : pawns) {
			if (p.isPlaced()) {
				Position pos = board.getPath(p.getPath()).getPosition(p.getPosition());
				score += pos.getPoints() * p.getScoreMultiplier();
			}
		}
		return score;
	}
}
