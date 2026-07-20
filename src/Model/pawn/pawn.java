package Model.pawn;

import java.io.Serializable;

/**
 * Base class for a game piece (either an archaeologist or Theseus).
 * A pawn lives on one of the 4 palace paths at a time. position == -1
 * means the pawn has not been placed on a path yet.
 */
public abstract class pawn implements Serializable {

	private static final long serialVersionUID = 1L;

	private String idiotita; // "arxaiologos" or "thiseas" - used by the view to pick the right icon
	private int position = -1; // step on the path (0-8), -1 = not placed yet
	private int path = -1; // which path (0=knossos,1=malia,2=phaistos,3=zakros), -1 = none
	private int lastCardValue = 0; // last numbercard value played for this pawn
	private boolean revealed = false; // has it been revealed to the opponent

	public pawn(String idiotita) {
		this.idiotita = idiotita;
	}

	public String getidiotita() {
		return idiotita;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPath() {
		return path;
	}

	public void setPath(int path) {
		this.path = path;
	}

	public boolean isPlaced() {
		return path != -1;
	}

	public int getLastCardValue() {
		return lastCardValue;
	}

	public void setLastCardValue(int lastCardValue) {
		this.lastCardValue = lastCardValue;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void reveal() {
		this.revealed = true;
	}

	/**
	 * How many points this pawn scores per point printed on the square it
	 * sits on (Theseus counts double).
	 */
	public abstract int getScoreMultiplier();
}
