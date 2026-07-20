package Model;

import java.io.Serializable;

/**
 * A single square on a path. There are 9 per path, each with its own
 * points (negative for the first 3, positive afterwards). Some squares
 * also hold an archaeological finding (FindingPosition), others don't
 * (SimplePosition).
 */
public abstract class Position implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int index; // 0-8
	private final int points;

	public Position(int index, int points) {
		this.index = index;
		this.points = points;
	}

	public int getIndex() {
		return index;
	}

	public int getPoints() {
		return points;
	}

	public boolean isCheckpoint() {
		return index == Board.CHECKPOINT_INDEX;
	}

	public abstract boolean hasFinding();
}
